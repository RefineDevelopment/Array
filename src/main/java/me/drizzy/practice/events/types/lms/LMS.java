package me.drizzy.practice.events.types.lms;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.lms.player.LMSPlayer;
import me.drizzy.practice.events.types.lms.player.LMSPlayerState;
import me.drizzy.practice.events.types.lms.task.LMSRoundEndTask;
import me.drizzy.practice.events.types.lms.task.LMSRoundStartTask;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.location.Circle;
import me.drizzy.practice.util.other.*;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LMS {

    @Getter @Setter private static boolean enabled = true;
    protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "LMS");
    private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

    private static Array plugin = Array.getInstance();

    private final LinkedHashMap<UUID, LMSPlayer> eventPlayers = new LinkedHashMap<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();

    private final String name;
    private final PlayerSnapshot host;
    private Kit kit;
    private Cooldown cooldown;
    private LMSTask eventTask;
    private LMSState state = LMSState.WAITING;

    @Getter @Setter public static int maxPlayers;
    private int totalPlayers;
    private long roundStart;

    public LMS(Player player, Kit kit) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.kit = kit;
        maxPlayers = 100;
    }

    public List<String> getLore() {
        List<String> toReturn=new ArrayList<>();

        LMS lms = plugin.getLMSManager().getActiveLMS();

        toReturn.add(CC.MENU_BAR);
        if (lms.isWaiting()) {

            String status;
            if (lms.getCooldown() == null) {

                status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_WAITING")
                        .replace("<lms_host_name>", lms.getName())
                        .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                        .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

            } else {
                String remaining=TimeUtil.millisToSeconds(lms.getCooldown().getRemaining());
                if (remaining.startsWith("-")) {
                    remaining="0.0";
                }
                String finalRemaining = remaining;

                status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_COUNTING")
                        .replace("<lms_host_name>", lms.getName())
                        .replace("<remaining>", finalRemaining)
                        .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                        .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

            }

            config.getStringList("SCOREBOARD.EVENT.LMS.WAITING").forEach(line -> toReturn.add(CC.translate(line
                    .replace("<lms_host_name>", lms.getName())
                    .replace("<status>", status)
                    .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                    .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

        } else {

            config.getStringList("SCOREBOARD.EVENT.LMS.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
                    .replace("<lms_host_name>", lms.getName())
                    .replace("<lms_duration>", lms.getRoundDuration())
                    .replace("<lms_players_alive>", String.valueOf(lms.getRemainingPlayers().size()))
                    .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                    .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

        public void setEventTask(LMSTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == LMSState.WAITING;
    }

    public boolean isFighting() {
        return state == LMSState.ROUND_FIGHTING;
    }

    public LMSPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() == LMSPlayerState.WAITING) {
                Player player = LMSPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        if (this.eventPlayers.size() >= maxPlayers) {
            player.sendMessage(Locale.EVENT_FULL.toString());
            return;
        }

        eventPlayers.put(player.getUniqueId(), new LMSPlayer(player));

        broadcastMessage(Locale.EVENT_JOIN.toString()
                .replace("<event_name>", "LMS")
                .replace("<joined>", player.getName())
                .replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
                .replace("<event_max_players>", String.valueOf(getMaxPlayers())));

        player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "LMS"));
        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(plugin.getLMSManager().getLmsSpawn());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                    NameTags.color(player, otherPlayer, plugin.getEssentials().getNametagMeta().getEventColor(), getKit().getGameRules().isShowHealth() || getKit().getGameRules().isBuild());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void handleLeave(Player player) {
        if (state != LMSState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == LMSState.WAITING) {
            broadcastMessage(Locale.EVENT_LEAVE.toString()
                    .replace("<event_name>", "LMS")
                    .replace("<left>", player.getName())
                    .replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
                    .replace("<event_max_players>", String.valueOf(getMaxPlayers())));
        }
        player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", "LMS"));

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                    NameTags.reset(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(plugin);

        profile.setState(ProfileState.IN_LOBBY);
        profile.setLms(null);
        profile.refreshHotbar();
        profile.teleportToSpawn();
    }

    protected List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void handleDeath(Player player, Player killer) {
        LMSPlayer loser = getEventPlayer(player);
        loser.setState(LMSPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public int getMaxBuildHeight() {
        int highest = (int) plugin.getLMSManager().getLmsSpawn().getY();
        return highest + 5;
    }

    public void end() {
        plugin.getLMSManager().setActiveLMS(null);
        plugin.getLMSManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "LMS"));
        } else {
            String win = Locale.EVENT_WON.toString().replace("<winner_name>", winner.getName())
                    .replace("<event_name>", "LMS")
                    .replace("<event_prefix>", EVENT_PREFIX);

            Bukkit.broadcastMessage(win);
            Bukkit.broadcastMessage(win);
            Bukkit.broadcastMessage(win);
        }
        
        placedBlocks.forEach(location -> location.getBlock().setType(Material.AIR));
        
        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setLms(null);
                profile.refreshHotbar();
                profile.teleportToSpawn();
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public boolean canEnd() {
        int remaining = 0;

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() == LMSPlayerState.WAITING) {
                remaining++;
            }
        }

        return remaining == 1;
    }

    public Player getWinner() {
        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() != LMSPlayerState.ELIMINATED) {
                return LMSPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
            String main = string
                    .replace("<event_name>", "LMS")
                    .replace("<event_host>", this.getHost().getUsername())
                    .replace("<event_prefix>", EVENT_PREFIX);

            Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "LMS"), "/lms join");

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                if (!eventPlayers.containsKey(player.getUniqueId())) {
                    message.sendToPlayer(player);
                }
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(EVENT_PREFIX + CC.translate(message));
        }
    }

    public void onJoin(Player player) {
        plugin.getNMSManager().getKnockbackType().applyKnockback(player, plugin.getLMSManager().getLmsKnockbackProfile());
    }
    public void onLeave(Player player) {
        plugin.getNMSManager().getKnockbackType().applyDefaultKnockback(player);
    }

    public void onRound() {
        setState(LMSState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {

            Location midSpawn = plugin.getLMSManager().getLmsSpawn();

            List<Location> circleLocations=Circle.getCircle(midSpawn, plugin.getEssentials().getMeta().getFfaSpawnRadius(), this.getPlayers().size());

            Location center = midSpawn.clone();
            Location loc = circleLocations.get(i);
            Location target = loc.setDirection(center.subtract(loc).toVector());

            player.teleport(target.add(0, 0.5, 0));
            circleLocations.remove(i);
            i++;

            TaskUtil.runLater(() ->
                    Profile.getByUuid(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) ->
                            player.getInventory().setItem(integer, itemStack)), 10L);
        }
        setEventTask(new LMSRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage(Locale.EVENT_ELIMINATED.toString()
                    .replace("<eliminated_name>", player.getName())
                    .replace("<eliminator_name>", killer.getPlayer().getName()));
        }


        if (canEnd()) {
            setState(LMSState.ROUND_ENDING);
            setEventTask(new LMSRoundEndTask(this));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(plugin);
        profile.refreshHotbar();
    }

    public String getRoundDuration() {
        switch (getState()) {
            case ROUND_STARTING:
                return "00:00";
            case ROUND_FIGHTING:
                return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
            default:
                return "Ending";
        }
    }

    public boolean isFighting(Player player) {
        if (this.getState().equals(LMSState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.SPECTATING);
        profile.refreshHotbar();
        profile.handleVisibility();
        player.teleport(plugin.getLMSManager().getLmsSpawn());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        eventPlayers.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();
        profile.teleportToSpawn();
    }
}
