package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.events.match.*;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.match.types.kit.BattleRushMatch;
import xyz.refinedev.practice.match.types.kit.BoxingMatch;
import xyz.refinedev.practice.match.types.kit.MLGRushMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.task.match.MatchCooldownTask;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.chat.ChatHelper;
import xyz.refinedev.practice.util.location.LightningUtil;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.EffectUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TimeUtil;
import xyz.refinedev.practice.util.storage.TimerHashMap;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class MatchManager {

    private final Array plugin;

    private final Map<UUID, MatchSnapshot> snapshotMap = new TimerHashMap<>(TimeUnit.SECONDS, 60);
    private final List<Match> matches = new ArrayList<>();

    public void init() {
        new MatchCooldownTask(plugin).runTaskTimerAsynchronously(plugin, 1L, 1L);

        plugin.getServer().getWorlds().forEach(world -> {
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");

            world.setStorm(false);
            world.setThundering(false);
        });
    }
    
    /**
     * Initiate and start the {@link Match}
     * This method sets up the players, teleports them
     * starts the countdown tasks, handles visibility
     *
     * @param match {@link Match} the match starting
     */
    public void start(Match match) {
        for (Player player : match.getPlayers()) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            profile.setState(ProfileState.IN_FIGHT);
            profile.setMatch(match);

            this.plugin.getProfileManager().handleVisibility(profile);

            if (!profile.getDuelRequests().isEmpty()) {
                profile.getDuelRequests().clear();
            }

            MatchPlayerSetupEvent event = new MatchPlayerSetupEvent(player, match);
            this.plugin.getServer().getPluginManager().callEvent(event);

            match.setupPlayer(plugin, player);
        }

        match.setState(MatchState.STARTING);
        match.onStart(plugin);
        match.setStartTimestamp(-1);
        match.getArena().setActive(true);
        match.initiateTasks(this.plugin);

        this.sendStartMessage(match);

        MatchStartEvent event = new MatchStartEvent(match);
        this.plugin.getServer().getPluginManager().callEvent(event);
    }


    /**
     * End the {@link Match}
     * This resets the players, updates the match's state
     * Created the Match inventories for each player
     * Resets their knockback and hit delay
     * and finally sends them rating message if enabled
     * 
     * @param match {@link Match} the match ending
     */
    public void end(Match match) {
        if (!match.onEnd(plugin)) return;

        match.setState(MatchState.ENDING);

        Kit kit = match.getKit();
        Arena arena = match.getArena();

        if (kit.getGameRules().isBuild() || kit.getGameRules().isShowHealth()) {
            for ( Player otherPlayerTeam : match.getPlayers() ) {
                Objective objective = otherPlayerTeam.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null)
                    objective.unregister();
            }
        }

        for ( MatchSnapshot snapshot : match.getSnapshots() ) {
            snapshot.setCreated(System.currentTimeMillis());
            plugin.getMatchManager().getSnapshotMap().put(snapshot.getTeamPlayer().getUniqueId(), snapshot);
        }

        for ( Player player : match.getPlayers() ) {
            Profile profile = plugin.getProfileManager().getProfileByPlayer(player);

            plugin.getSpigotHandler().resetKnockback(player);
            player.setMaximumNoDamageTicks(20);

            if (kit.getGameRules().isParkour()) {
                profile.getParkourCheckpoints().clear();
            }
        }

        for ( TeamPlayer gamePlayer : match.getTeamPlayers()) {
            if (gamePlayer == null) return;
            Player player = gamePlayer.getPlayer();
            if (gamePlayer.isDisconnected() || player == null) continue;
            for ( BaseComponent[] components :  match.generateEndComponents(plugin, player) ) {
                player.spigot().sendMessage(components);
            }
        }

        for (Player player : match.getSpectators()) {
            if (player == null) return;
            for (BaseComponent[] components : match.generateEndComponents(plugin, player)) {
                player.spigot().sendMessage(components);
            }
            this.removeSpectator(match, player);
        }

        if (plugin.getConfigHandler().isRATINGS_ENABLED()) {
            for ( Player player : match.getPlayers() ) {
                Profile profile = this.plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

                profile.setIssueRating(true);
                profile.setRatingArena(arena);

                plugin.getArenaManager().sendRatingMessage(player, arena);
            }
        }

        if (match.getArena().isStandalone()) match.getArena().restoreSnapshot();
        match.getArena().setActive(false);
        match.getEntities().forEach(Entity::remove);

        if (match.getWaterTask() != null) {
            match.getWaterTask().cancel();
        }

        MatchEndEvent event = new MatchEndEvent(match);
        plugin.getServer().getPluginManager().callEvent(event);

        this.matches.remove(match);
    }


    /**
     * Handle the player's respawn
     *
     * @param match  {@link Match} the match of player respawning
     * @param player {@link Player} the player respawning
     */
    public void handleRespawn(Match match, Player player) {
        player.setVelocity(player.getLocation().getDirection().setY(1));
        match.onRespawn(plugin, player);
    }

    /**
     * Handle the player's death, this method auto detects
     * any killer if preset or if the player disconnected or not
     *
     * @param match  {@link Match} The match of the player dying
     * @param player {@link Player} The player dying
     */
    public void handleDeath(Match match, Player player) {
        if (PlayerUtil.getLastAttacker(player) instanceof CraftPlayer) {
            Player killer = (Player) PlayerUtil.getLastAttacker(player);
            this.handleDeath(match, player, killer, false);
        } else if (player.getKiller() != null) {
            this.handleDeath(match, player, player.getKiller(), false);
        } else {
            this.handleDeath(match, player, null, false);
        }
    }

    /**
     * Main method for handling a player's death while in match
     * or a player disconnecting while in match
     *
     * @param match  {@link Match} the match of player dying
     * @param deadPlayer {@link Player} the player that died or disconnected
     * @param killerPlayer {@link Player} the killer of the player if there is one or else null
     * @param disconnected {@link Boolean} disconnected
     */
    public void handleDeath(Match match, Player deadPlayer, Player killerPlayer, boolean disconnected) {
        TeamPlayer teamPlayer = match.getTeamPlayer(deadPlayer);

        if (teamPlayer == null) return;

        teamPlayer.setDisconnected(disconnected);

        if (!teamPlayer.isAlive()) return;

        teamPlayer.setAlive(false);
        teamPlayer.setParkourCheckpoint(null);

        for ( Player player : match.getAllPlayers() ) {
            TeamPlayer otherTeamPlayer = match.getTeamPlayer(player);
            if (otherTeamPlayer == null || otherTeamPlayer.isDisconnected()) continue;

            if (teamPlayer.isDisconnected()) {
                player.sendMessage(Locale.MATCH_DISCONNECTED.toString()
                        .replace("<relation_color>", match.getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));
                continue;
            }
            if (match.getKit().getGameRules().isParkour() && killerPlayer != null) {
                player.sendMessage(Locale.MATCH_WON.toString()
                        .replace("<relation_color>", match.getRelationColor(player, killerPlayer).toString())
                        .replace("<participant_name>", killerPlayer.getName()));
            } else if (killerPlayer == null) {
                player.sendMessage(Locale.MATCH_DIED.toString()
                        .replace("<relation_color>", match.getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));
            } else {
                player.sendMessage(Locale.MATCH_KILLED.toString()
                        .replace("<relation_color_dead>", match.getRelationColor(player, deadPlayer).toString())
                        .replace("<relation_color_killer>", match.getRelationColor(player, killerPlayer).toString())
                        .replace("<dead_name>", deadPlayer.getName())
                        .replace("<killer_name>", killerPlayer.getName()));
            }
        }

        this.handleKillEffect(match, deadPlayer, killerPlayer);
        match.onDeath(plugin, deadPlayer, killerPlayer);
    }

    /**
     * Execute the Kill Effect for a specified Player
     *
     * @param deadPlayer {@link Player} the player being killed
     * @param killerPlayer {@link Player} the player killing
     */
    public void handleKillEffect(Match match, Player deadPlayer, Player killerPlayer) {
        if (killerPlayer == null) return;
        Profile profile = plugin.getProfileManager().getProfileByPlayer(killerPlayer);
        KillEffect killEffect = plugin.getKillEffectManager().getByUUID(profile.getKillEffect());

        deadPlayer.teleport(deadPlayer.getLocation().add(0.0, 1.0, 0.0));

        if (killEffect == null) {
            killEffect = plugin.getKillEffectManager().getDefault();
        }

        if (killEffect.getEffect() != null) {
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 1.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 1.0f);
        }

        if (killEffect.isLightning() && !(match.isTheBridgeMatch() || match.isBedwarsMatch() || match.isMLGRushMatch() || match.isBattleRushMatch())) {
            for ( Player player : match.getPlayers() ) {
                LightningUtil.spawnLightning(player, deadPlayer.getLocation());
            }
        }

        if (killEffect.isDropsClear()) {
            match.getEntities().forEach(Entity::remove);
        }

        if (killEffect.isAnimateDeath())
            PlayerUtil.animateDeath(deadPlayer);

        if (!killEffect.getKillEffectSounds().isEmpty()) {
            float randomPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;
            for ( KillEffectSound killEffectSound : killEffect.getKillEffectSounds()) {
                match.getPlayers().forEach(player -> player.playSound(deadPlayer.getLocation(), killEffectSound.getSound(), killEffectSound.getPitch(), randomPitch));
            }
        }
    }

    /**
     * Add a player as a spectator for this match
     *
     * @param player {@link Player} being added
     * @param target {@link Player} target that the player is spectating
     */
    public void addSpectator(Match match, Player player, @Nullable Player target) {
        //This could happen mane
        if (match.isEnding()) {
            player.sendMessage(CC.translate("&cThat match has just ended, failed to add you as a spectator!"));
            return;
        }

        match.getSpectatorList().add(player.getUniqueId());

        MatchSpectatorJoinEvent event = new MatchSpectatorJoinEvent(player, match);
        plugin.getServer().getPluginManager().callEvent(event);

        PlayerUtil.spectator(player);

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        profile.setMatch(match);
        profile.setSpectating(target);
        profile.setState(ProfileState.SPECTATING);

        plugin.getProfileManager().handleVisibility(profile);
        plugin.getProfileManager().refreshHotbar(profile);

        player.teleport(match.getMidSpawn());
        player.spigot().setCollidesWithEntities(false);
        player.updateInventory();

        for ( Player matchPlayer : match.getAllPlayers() ) {
            Profile matchProfile = plugin.getProfileManager().getProfileByPlayer(matchPlayer);
            plugin.getProfileManager().handleVisibility(matchProfile, player);
        }

        if (profile.isSilent()) return;

        match.getPlayers().forEach(otherPlayer -> otherPlayer.sendMessage(Locale.MATCH_SPECTATE.toString().replace("<spectator>", player.getName())));

    }

    /**
     * Remove the specified spectator and teleport
     * them back to spawn with their visibility and hotbar being reset
     *
     * @param match  {@link Match} the match, player is leaving
     * @param player {@link Player} leaving spectating
     */
    public void removeSpectator(Match match, Player player) {
        match.getSpectatorList().remove(player.getUniqueId());

        MatchSpectatorLeaveEvent event = new MatchSpectatorLeaveEvent(player, match);
        plugin.getServer().getPluginManager().callEvent(event);

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setMatch(null);
        profile.setSpectating(null);

        plugin.getProfileManager().teleportToSpawn(profile);

        player.setAllowFlight(false);
        player.setFlying(false);
        player.spigot().setCollidesWithEntities(true);
        player.updateInventory();

        if (match.isEnding()) return;
        if (profile.isSilent()) return;

        match.getPlayers().forEach(otherPlayer -> otherPlayer.sendMessage(Locale.MATCH_STOPSPEC.toString().replace("<spectator>", player.getName())));
    }

    /**
     * Toggle spectator visibility for specified player
     *
     * @param match  {@link Match} being used
     * @param player {@link Player} for whom we are toggling
     */
    public void toggleSpectators(Match match, Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);

        profile.setVisibilityCooldown(new Cooldown(TimeUtil.parseTime("5s")));

        if (profile.getVisibilityCooldown() != null && !profile.getVisibilityCooldown().hasExpired()) {
            player.sendMessage(CC.translate("&cYou are currently on cooldown for " + profile.getVisibilityCooldown().getTimeLeft()));
            return;
        }

        profile.getSettings().setShowSpectator(!profile.getSettings().isShowSpectator());
        plugin.getProfileManager().refreshHotbar(profile);

        if (profile.getSettings().isShowSpectator()) {
            match.getSpectators().forEach(player::showPlayer);
            player.sendMessage(CC.translate("&aShowing spectators."));
        } else {
            match.getSpectators().forEach(player::hidePlayer);
            player.sendMessage(CC.translate("&cHiding spectators."));
        }
    }

    /**
     * Clear up the {@link Match} leftovers, remnants
     * and rollback the {@link Arena} to its original state
     *
     * @param match {@link Match} being cleaned up
     */
    public void cleanup(Match match) {
        if (match.getArena().isStandalone()) match.getArena().restoreSnapshot();
    }

    /**
     * Get amount of players in fight from a certain
     * queue
     *
     * @return amount of players in fight with the queue
     */
    public int getInFights() {
        int i = 0;

        for ( Match match : matches) {
            if (!match.isFighting() && !match.isStarting()) continue;

            i += match.getTeamPlayers().size();
        }
        return i;
    }

    /**
     * Get hover event for Clickable Inventories of
     * a specified {@link TeamPlayer}
     *
     * @param teamPlayer {@link TeamPlayer} the player whose hover event we are getting
     * @return {@link String} the hover string of the hover event
     */
    protected String getHoverEvent(TeamPlayer teamPlayer) {
        return Locale.MATCH_INVENTORY_HOVER.toString().replace("<inventory_name>", teamPlayer.getUsername());
    }

    /**
     * Get click event for Clickable Inventories of
     * a specified {@link TeamPlayer}
     *
     * @param teamPlayer {@link TeamPlayer} the player whose click event we are getting
     * @return {@link String} the command string of the click event
     */
    protected String getClickEvent(TeamPlayer teamPlayer) {
        return "/viewinv " + teamPlayer.getUniqueId().toString();
    }

    /**
     * Generate match inventory click messages
     *
     * @param prefix {@link String} prefix of the message, either winner or loser
     * @param participant {@link TeamPlayer} the teamPlayer whose inventory message we are displaying
     * @return {@link BaseComponent}
     */
    public BaseComponent[] generateInventoriesComponents(String prefix, TeamPlayer participant) {
        return generateInventoriesComponents(prefix, Collections.singletonList(participant));
    }

    /**
     * Generate match inventory click messages
     *
     * @param prefix {@link String} prefix of the message, either winner or loser
     * @param participants {@link List<TeamPlayer>} the list of teamPlayers whose message we are displaying
     * @return {@link BaseComponent}
     */
    public BaseComponent[] generateInventoriesComponents(String prefix, List<TeamPlayer> participants) {
        ChatComponentBuilder builder = new ChatComponentBuilder(prefix);

        int totalPlayers = 0;
        int processedPlayers = 0;

        totalPlayers += participants.size();

        for (TeamPlayer gamePlayer : participants) {
            processedPlayers++;

            ChatComponentBuilder current = new ChatComponentBuilder(gamePlayer.getUsername())
                    .attachToEachPart(ChatHelper.hover(CC.translate(getHoverEvent(gamePlayer))))
                    .attachToEachPart(ChatHelper.click(getClickEvent(gamePlayer)));

            builder.append(current.create());

            if (processedPlayers != totalPlayers) {
                builder.append(", ");
                builder.getCurrent().setClickEvent(null);
                builder.getCurrent().setHoverEvent(null);
            }
        }

        return builder.create();
    }

    /**
     * Send Match start message
     * This isn't used in solo matches
     * because we get their message in our queue thread
     * or duel manager
     */
    public void sendStartMessage(Match match) {
        if (match.isFreeForAllMatch() || match.isTeamMatch()) {
            Locale.MATCH_TEAM_STARTMESSAGE.toList().stream().map(s -> s
                    .replace("<arena>", match.getArena().getDisplayName())
                    .replace("<kit>", match.getKit().getDisplayName()))
                    .forEach(match::broadcastMessage);

        } else if (match.isHCFMatch()) {
            Locale.MATCH_HCF_STARTMESSAGE.toList().stream().map(s -> s
                    .replace("<arena>", match.getArena().getDisplayName())
                    .replace("<kit>", match.getKit().getDisplayName()))
                    .forEach(match::broadcastMessage);
        }
    }

    public MatchSnapshot getByString(String name) {
        try {
            UUID uuid = UUID.fromString(name);
            return snapshotMap.get(uuid);
        } catch (Exception e) {
            for ( MatchSnapshot snapshot : snapshotMap.values()) {
                TeamPlayer teamPlayer = snapshot.getTeamPlayer();
                if (teamPlayer.getUsername().equalsIgnoreCase(name))
                    return snapshot;
            }
            return null;
        }
    }

    public Match createSoloKitMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        Match match;

        if (kit.getGameRules().isBridge()) {
            match = new SoloBridgeMatch(queue, playerA, playerB, kit, arena, queueType);
        } else if (kit.getGameRules().isBedwars()) {
            match = new SoloBedwarsMatch(queue, playerA, playerB, kit, arena, queueType);
        } else if (kit.getGameRules().isMlgRush()) {
            match = new MLGRushMatch(queue, playerA, playerB, kit, arena, queueType);
        } else if (kit.getGameRules().isBoxing()) {
            match = new BoxingMatch(queue, playerA, playerB, kit, arena, queueType);
        } else if (kit.getGameRules().isBattleRush()) {
            match = new BattleRushMatch(queue, playerA, playerB, kit, arena, queueType);
        } else {
            match = new SoloMatch(queue, playerA, playerB, kit, arena, queueType);
        }
        this.matches.add(match);
        return match;
    }

    public Match createTeamKitMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        Match match;
        if (kit.equals(this.plugin.getKitManager().getTeamFight())) {
            match = new TeamMatch(teamA, teamB, kit, arena);
        } else if (kit.getGameRules().isBridge()) {
            match = new TeamBridgeMatch(teamA, teamB, kit, arena);
        } else {
            match = new TeamMatch(teamA, teamB, kit, arena);
        }
        this.matches.add(match);
        return match;
    }
}
