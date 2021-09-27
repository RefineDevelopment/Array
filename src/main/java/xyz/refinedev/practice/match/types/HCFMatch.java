package xyz.refinedev.practice.match.types;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.PvPClassManager;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.other.EffectUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class HCFMatch extends Match {

    private final Array plugin = Array.getInstance();

    private final Team teamA;
    private final Team teamB;

    public HCFMatch(Team teamA, Team teamB, Arena arena) {
        super(null, Kit.getHCFTeamFight(), arena, QueueType.UNRANKED);

        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    public boolean isHCFMatch() {
        return true;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        player.setNoDamageTicks(Kit.getHCFTeamFight().getGameRules().getHitDelay());

        Team team = getTeam(player);

        Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Party party = profile.getParty();
        String kit = party.getKits().get(player.getUniqueId());

        switch (kit) {
            case "bard":
            case "Bard":
                PvPClassManager.giveBardKit(player);
                break;
            case "archer":
            case "Archer":
                PvPClassManager.giveArcherKit(player);
                break;
            case "rogue":
            case "Rogue":
                PvPClassManager.giveRogueKit(player);
                break;
            default:
                PvPClassManager.giveDiamondKit(player);
                break;
        }

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.getPlayers().forEach(player -> plugin.getSpigotHandler().kitKnockback(player, Kit.getHCFTeamFight()));
    }

    @Override
    public boolean onEnd() {
        for ( TeamPlayer teamPlayer : getTeamPlayers() ) {
            if (teamPlayer.isDisconnected() || !teamPlayer.isAlive()) continue;
            Player player = teamPlayer.getPlayer();
            if (player == null) continue;

            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.handleVisibility();

            this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer teamPlayer : getTeamPlayers()) {
                    if (teamPlayer.isDisconnected()) continue;

                    Player player = teamPlayer.getPlayer();
                    if (player == null) continue;

                    for ( TeamPlayer secondTeamPlayer : getTeamPlayers() ) {
                        if (secondTeamPlayer.isDisconnected()) continue;

                        if (secondTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                            continue;
                        }

                        Player secondPlayer = secondTeamPlayer.getPlayer();
                        if (secondPlayer == null) continue;

                        player.hidePlayer(secondPlayer);
                    }

                    if (teamPlayer.isAlive()) {
                        getSnapshots().add(new MatchSnapshot(teamPlayer));
                    }

                    player.setFireTicks(0);
                    player.updateInventory();
                    player.getActivePotionEffects().clear();

                    plugin.getSpigotHandler().resetKnockback(player);

                    Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
                    profile.setState(ProfileState.IN_LOBBY);
                    profile.setMatch(null);
                    profile.refreshHotbar();
                    profile.handleVisibility();
                    profile.teleportToSpawn();
                }
            }
        }.runTaskLater(plugin, TimeUtil.parseTime(plugin.getConfigHandler().getTELEPORT_DELAY()  + "s"));

        Team winningTeam = getWinningTeam();
        Team losingTeam = getOpponentTeam(winningTeam);

        winningTeam.getPlayers().stream().map(Profile::getByPlayer).forEach(profile -> {
            profile.getStatisticsData().get(getKit()).incrementWon();
            TaskUtil.runAsync(profile::save);
        });
        losingTeam.getPlayers().stream().map(Profile::getByPlayer).forEach(profile -> {
            profile.getStatisticsData().get(getKit()).incrementLost();
            TaskUtil.runAsync(profile::save);
        });
        return true;
    }

    @Override
    public boolean canEnd() {
        return teamA.getAliveTeamPlayers().isEmpty() || teamB.getAliveTeamPlayers().isEmpty();
    }

    @Override
    public void handleKillEffect(Player deadPlayer, Player killerPlayer) {
        if (killerPlayer == null) return;
        Profile profile = plugin.getProfileManager().getByPlayer(killerPlayer);
        KillEffect killEffect = profile.getKillEffect();

        if (killEffect == null) {
            killEffect = plugin.getKillEffectManager().getDefault();
        }

        if (killEffect.getEffect() != null) {
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 1.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 1.0f);
        }

        if (killEffect.isLightning()) {
            for ( Player player : this.getPlayers() ) {
                PacketContainer packetContainer = this.createLightningPacket(deadPlayer.getLocation());
                this.sendLightningPacket(player, packetContainer);
            }
        }

        if (killEffect.isDropsClear()) {
            this.getDroppedItems().forEach(Item::remove);
        }

        if (killEffect.isAnimateDeath()) PlayerUtil.animateDeath(deadPlayer);

        if (!killEffect.getKillEffectSounds().isEmpty()) {
            float randomPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;
            for ( KillEffectSound killEffectSound : killEffect.getKillEffectSounds()) {
                this.getPlayers().forEach(player -> player.playSound(deadPlayer.getLocation(), killEffectSound.getSound(), killEffectSound.getPitch(), randomPitch));
            }
        }
    }

    @Override
    public void onDeath(Player deadPlayer, Player killer) {
        TeamPlayer teamPlayer = getTeamPlayer(deadPlayer);

        this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        PlayerUtil.reset(deadPlayer);

        for ( Player otherPlayer : getPlayers() ) {
            Profile profile = plugin.getProfileManager().getByUUID(otherPlayer.getUniqueId());
            TaskUtil.runLater(() -> profile.handleVisibility(otherPlayer, deadPlayer), 2L);
        }

        if (this.canEnd()) {
            this.end();
        } else {
            PlayerUtil.spectator(deadPlayer);
            if (!teamPlayer.isDisconnected()) {
                deadPlayer.teleport(getMidSpawn());

                Profile profile = plugin.getProfileManager().getByUUID(deadPlayer.getUniqueId());
                profile.refreshHotbar();
                profile.setState(ProfileState.SPECTATING);
            }
            TaskUtil.runLater(() -> {
                //Then handle spectator visibility
                this.getSpectators().forEach(spectator -> {
                    if (plugin.getProfileManager().getByPlayer(spectator).getSettings().isShowSpectator()) spectator.showPlayer(deadPlayer);
                    if (plugin.getProfileManager().getByPlayer(deadPlayer).getSettings().isShowSpectator()) deadPlayer.showPlayer(spectator);
                });
            }, 8L);
        }
    }

    @Override
    public void onRespawn(Player player) {
    }

    @Override
    public Player getWinningPlayer() {
        throw new UnsupportedOperationException("Cannot getInstance solo winning player from a TeamMatch");
    }

    @Override
    public Team getWinningTeam() {
        if (this.teamA.getAliveCount() == 0) {
            return this.teamB;
        }
        if (this.teamB.getAliveCount() == 0) {
            return this.teamA;
        }
        return null;
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        throw new UnsupportedOperationException("Cannot getInstance solo match player from a TeamMatch");
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        throw new UnsupportedOperationException("Cannot getInstance solo match player from a TeamMatch");
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        List<TeamPlayer> TeamPlayers = new ArrayList<>();
        TeamPlayers.addAll(teamA.getTeamPlayers());
        TeamPlayers.addAll(teamB.getTeamPlayers());
        return TeamPlayers;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        teamA.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        });

        teamB.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        });

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        teamA.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                if (TeamPlayer.isAlive()) {
                    players.add(player);
                }
            }
        });

        teamB.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                if (TeamPlayer.isAlive()) {
                    players.add(player);
                }
            }
        });

        return players;
    }

    @Override
    public Team getTeamA() {
        return teamA;
    }

    @Override
    public Team getTeamB() {
        return teamB;
    }

    @Override
    public Team getTeam(Player player) {
        for (TeamPlayer teamTeamPlayer : teamA.getTeamPlayers()) {
            if (teamTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamA;
            }
        }

        for (TeamPlayer teamTeamPlayer : teamB.getTeamPlayers()) {
            if (teamTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamB;
            }
        }

        return null;
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        for (TeamPlayer teamPlayer : teamA.getTeamPlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        for (TeamPlayer teamPlayer : teamB.getTeamPlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        return null;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        if (teamA.equals(team)) {
            return teamB;
        } else if (teamB.equals(team)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public Team getOpponentTeam(Player player) {
        if (teamA.containsPlayer(player)) {
            return teamB;
        } else if (teamB.containsPlayer(player)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance solo opponent player from TeamMatch");
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance solo opponent match player from TeamMatch");
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        boolean[] booleans = new boolean[]{
                getTeamA().containsPlayer(viewer),
                getTeamB().containsPlayer(viewer),
                getTeamA().containsPlayer(target),
                getTeamB().containsPlayer(target)
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer)) {
            return getTeamA().containsPlayer(target) ?  org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.AQUA;
        }
    }

    @Override
    public List<BaseComponent[]> generateEndComponents(Player player) {
        List<BaseComponent[]> componentsList = new ArrayList<>();

        for ( String line : Locale.MATCH_INVENTORY_MESSAGE.toList() ) {
            if (line.equalsIgnoreCase("<inventories>")) {

                BaseComponent[] winners = generateInventoriesComponents(Locale.MATCH_INVENTORY_WINNERS.toString(), getWinningTeam().getTeamPlayers());
                BaseComponent[] losers = generateInventoriesComponents(Locale.MATCH_INVENTORY_LOSERS.toString(), getOpponentTeam(getWinningTeam()).getTeamPlayers());

                componentsList.add(winners);
                componentsList.add(losers);

                continue;
            }

            if (line.equalsIgnoreCase("<elo_changes>")) {
                continue;
            }

            componentsList.add(new ChatComponentBuilder("").parse(line).create());
        }

        return componentsList;
    }

}
