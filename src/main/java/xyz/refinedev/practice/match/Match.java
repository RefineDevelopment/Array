package xyz.refinedev.practice.match;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.events.match.*;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.task.*;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.match.types.HCFMatch;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.match.types.kit.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.chat.ChatHelper;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public abstract class Match {

    private final Array plugin = Array.getInstance();

    @Getter protected static List<Match> matches = new ArrayList<>();

    private final Map<UUID, EnderPearl> pearlMap = new HashMap<>();
    private final List<MatchSnapshot> snapshots = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> droppedItems = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final List<BlockState> changedBlocks = new ArrayList<>();

    private final UUID matchId = UUID.randomUUID();
    private final Queue queue;
    private final Kit kit;
    private final Arena arena;
    private final QueueType queueType;

    public MatchState state = MatchState.STARTING;
    public BukkitTask task;
    private BukkitTask matchWaterCheck;

    private long startTimestamp;

    /**
     * Construct a match using the given details
     *
     * @param queue {@link Queue} if match is started from queue, then we provide it
     * @param kit {@link Kit} The kit that will be given to all players in the match
     * @param arena {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public Match(Queue queue, Kit kit, Arena arena, QueueType queueType) {
        this.queue = queue;
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;

        matches.add(this);
    }

    /**
     * Preload all normal match tasks
     */
    public static void preload() {
        final Array plugin = Array.getInstance();

        new MatchPearlCooldownTask(plugin).runTaskTimerAsynchronously(plugin, 2L, 2L);
        new MatchBowCooldownTask(plugin).runTaskTimerAsynchronously(plugin, 2L, 2L);
        new MatchSnapshotCleanupTask().runTaskTimerAsynchronously(plugin, 20L * 5, 20L * 5);

        TaskUtil.runTimer(() -> Bukkit.getWorlds().forEach(world -> {
            world.setStorm(false);
            world.setThundering(false);
        }), 20, 20);
    }


    /**
     * Clear up the {@link Match} leftovers and remnants
     * and rollback the {@link Arena} to its original state
     */
    public void cleanup() {
        if (kit.getGameRules().isBuild() && this.placedBlocks.size() > 0) {
            this.placedBlocks.forEach(l -> l.getBlock().setType(Material.AIR));
            this.placedBlocks.clear();
        }
        if (kit.getGameRules().isBuild() && this.changedBlocks.size() > 0) {
            this.changedBlocks.forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
            this.changedBlocks.clear();
        }
        this.arena.setActive(false);
        this.entities.forEach(Entity::remove);
        this.droppedItems.forEach(Item::remove);
    }

    /**
     * Initiate and start the {@link Match}
     * This method sets up the players, teleports them
     * starts the countdown tasks, handles visibility
     */
    public void start() {
        //So that chunks are properly visible
        //My old dumb ass put this in the loop below causing massive lag
        if (!this.arena.getSpawn1().getChunk().isLoaded() || !this.arena.getSpawn2().getChunk().isLoaded()) {
            this.arena.getSpawn1().getChunk().load();
            this.arena.getSpawn2().getChunk().load();
        }

        for (Player player : this.getPlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_FIGHT);
            profile.setMatch(this);
            profile.handleVisibility();

            if (!profile.getSentDuelRequests().isEmpty()) {
                profile.getSentDuelRequests().clear();
            }

            MatchPlayerSetupEvent event = new MatchPlayerSetupEvent(player, this);
            event.call();

            this.setupPlayer(player);
        }

        this.onStart();

        this.state = MatchState.STARTING;
        this.startTimestamp = -1;
        this.arena.setActive(true);

        this.sendStartMessage();
        this.initiateTasks();

        new MatchStartEvent(this).call();
    }

    /**
     * Start all the usual Match tasks that
     * track and execute the match's logic
     */
    public void initiateTasks() {
        if (kit.getGameRules().isWaterKill() || kit.getGameRules().isParkour() || kit.getGameRules().isSumo()) {
            this.matchWaterCheck = new MatchWaterCheckTask(this).runTaskTimer(plugin, 20L, 20L);
        }

        this.task = new MatchStartTask(this).runTaskTimer(plugin, 20L, 20L);
        this.getPlayers().forEach(player -> new MatchPotionTrackTask(player).runTaskTimerAsynchronously(plugin, 0L, 5L));
    }

    /**
     * End the {@link Match}
     * This resets the players, updates the match's state
     * Created the Match inventories for each player
     * Resets their knockback and hit delay
     * and finally sends them rating message if enabled
     */
    public void end() {
        if (!onEnd()) return;

        this.state = MatchState.ENDING;

        if (kit.getGameRules().isBuild() || kit.getGameRules().isShowHealth()) {
            for ( Player otherPlayerTeam : getPlayers() ) {
                Objective objective = otherPlayerTeam.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null) objective.unregister();
            }
        }

        snapshots.forEach(matchInventory -> {
            matchInventory.setCreated(System.currentTimeMillis());
            MatchSnapshot.getSnapshots().put(matchInventory.getTeamPlayer().getUniqueId(), matchInventory);
        });

        for ( Player player : this.getPlayers() ) {
            plugin.getKnockbackManager().resetKnockback(player);
            player.setMaximumNoDamageTicks(20);

            if (kit.getGameRules().isParkour()) {
                Profile profile = Profile.getByPlayer(player);
                profile.getPlates().clear();
            }

            this.removePearl(player, true);
        }

        for ( TeamPlayer gamePlayer : getTeamPlayers()) {
            Player player = gamePlayer.getPlayer();
            if (gamePlayer.isDisconnected() || player == null) continue;
            for ( BaseComponent[] components : generateEndComponents(player) ) {
                player.spigot().sendMessage(components);
            }
        }

        for (Player player : getSpectators()) {
            if (player == null) continue;
            for (BaseComponent[] components : generateEndComponents(player)) {
                player.spigot().sendMessage(components);
            }
            this.removeSpectator(player);
        }

        if (plugin.getConfigHandler().isRATINGS_ENABLED()) {
            this.getPlayers().stream().map(Profile::getByPlayer).forEach(profile ->  {
                profile.setIssueRating(true);
                profile.setRatingArena(arena);
                plugin.getRatingsManager().sendRatingMessage(profile.getPlayer(), this.getArena());
            });
        }

        this.cleanup();

        if (matchWaterCheck != null) matchWaterCheck.cancel();

        MatchEndEvent event = new MatchEndEvent(this);
        event.call();

        matches.remove(this);
    }

    /**
     * Send Match start message
     * This isn't used in solo matches
     * because we get their message in our queue thread
     * or duel handler
     */
    public void sendStartMessage() {
        if (this.isFreeForAllMatch() || this.isTeamMatch()) {
            Locale.MATCH_TEAM_STARTMESSAGE.toList().stream().map(s -> s.replace("<arena>", this.arena.getDisplayName()).replace("<kit>", this.kit.getDisplayName())).forEach(this::broadcastMessage);
        } else if (isHCFMatch()) {
            Locale.MATCH_HCF_STARTMESSAGE.toList().stream().map(s -> s.replace("<arena>", this.arena.getDisplayName()).replace("<kit>", this.kit.getDisplayName())).forEach(this::broadcastMessage);
        }
    }

    /**
     * Add the pearl to our map to track it
     *
     * @param player {@link Player} the player pearling
     * @param pearl {@link EnderPearl} the enderpearl used
     */
    public void onPearl(Player player, EnderPearl pearl) {
        this.pearlMap.put(player.getUniqueId(), pearl);
    }

    /**
     * Remove the pearl from our map because we have tracked it
     *
     * @param player {@link Player} the player that threw the pearl
     * @param resetCooldown {@link Boolean} should we reset their pearl cooldown
     */
    public void removePearl(Player player, boolean resetCooldown) {
        if (player == null) return;

        if (resetCooldown) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setEnderpearlCooldown(new Cooldown(0L));
        }
        EnderPearl pearl = this.pearlMap.get(player.getUniqueId());
        if (pearl != null) pearl.remove();
    }

    /**
     * Handle the player's respawn
     *
     * @param player {@link Player} the player respawning
     */
    public void handleRespawn(Player player) {
        player.setVelocity(player.getLocation().getDirection().setY(1));
        this.onRespawn(player);
    }

    /**
     * Handle the player's death, this method auto detects
     * any killer if preset or if the player disconnected or not
     *
     * @param player {@link Player} The player dying
     */
    public void handleDeath(Player player) {
        if (PlayerUtil.getLastAttacker(player) instanceof CraftPlayer) {
            Player killer = (Player) PlayerUtil.getLastAttacker(player);
            this.handleDeath(player, killer, false);
        } else if (player.getKiller() != null) {
            this.handleDeath(player, player.getKiller(), false);
        } else {
            this.handleDeath(player, null, false);
        }
    }

    /**
     * Main method for handling a player's death while in match
     * or a player disconnecting while in match
     *
     * @param deadPlayer {@link Player} the player that died or disconnected
     * @param killerPlayer {@link Player} the killer of the player if there is one or else null
     * @param disconnected {@link Boolean} disconnected
     */
    public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
        deadPlayer.teleport(deadPlayer.getLocation().add(0.0, 1.0, 0.0));

        TeamPlayer teamPlayer = this.getTeamPlayer(deadPlayer);

        if (teamPlayer == null) return;

        teamPlayer.setDisconnected(disconnected);

        if (!teamPlayer.isAlive()) return;

        teamPlayer.setAlive(false);
        teamPlayer.setParkourCheckpoint(null);

        for ( Player player : getAllPlayers() ) {
            TeamPlayer otherTeamPlayer = getTeamPlayer(player);
            if (otherTeamPlayer == null || otherTeamPlayer.isDisconnected()) continue;

            if (teamPlayer.isDisconnected()) {
                player.sendMessage(Locale.MATCH_DISCONNECTED.toString()
                        .replace("<relation_color>", getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));
                continue;
            }
            if (kit.getGameRules().isParkour() && killerPlayer != null) {
                player.sendMessage(Locale.MATCH_WON.toString()
                        .replace("<relation_color>", getRelationColor(player, killerPlayer).toString())
                        .replace("<participant_name>", killerPlayer.getName()));
            } else if (killerPlayer == null) {
                player.sendMessage(Locale.MATCH_DIED.toString()
                        .replace("<relation_color>", getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));
            } else {
                player.sendMessage(Locale.MATCH_KILLED.toString()
                        .replace("<relation_color_dead>", getRelationColor(player, deadPlayer).toString())
                        .replace("<relation_color_killer>", getRelationColor(player, killerPlayer).toString())
                        .replace("<dead_name>", deadPlayer.getName())
                        .replace("<killer_name>", killerPlayer.getName()));
            }
        }

        this.handleKillEffect(deadPlayer, killerPlayer);
        this.onDeath(deadPlayer, killerPlayer);
    }

    /**
     * Lightning through Protocol Lib cuz we care about the
     * environment
     *
     * @param location {@link Location} where the lightning should spawn
     * @return {@link PacketContainer}
     */
    public PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int)(location.getX() * 32.0));
        lightningPacket.getIntegers().write(2, (int)(location.getY() * 32.0));
        lightningPacket.getIntegers().write(3, (int)(location.getZ() * 32.0));

        return lightningPacket;
    }

    public void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {}
    }

    /**
     * Get match's duration in string form
     *
     * @return {@link String}
     */
    public String getDuration() {
        switch (state) {
            case STARTING:
                return "Starting";
            case ENDING:
                return "Ending";
            default:
                return TimeUtil.millisToTimer(getElapsedDuration());
        }
    }

    /**
     * Get Elapsed Duration of the {@link Match}
     *
     * @return {@link Long} the time passed in long
     */
    public long getElapsedDuration() {
        return System.currentTimeMillis() - startTimestamp;
    }

    /**
     * Broadcast a {@link String} message to all match participants
     *
     * @param message {@link String}
     */
    public void broadcastMessage(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
        getSpectators().forEach(player -> player.sendMessage(message));
    }

    /**
     * Broadcast a {@link Sound} to all match participants
     *
     * @param sound {@link Sound}
     */
    public void broadcastSound(Sound sound) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
        getSpectators().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
    }

    /**
     * Get all spectators currently in the match
     *
     * @return {@link List}
     */
    public List<Player> getSpectators() {
        return spectators.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Add a player as a spectator for this match
     *
     * @param player {@link Player} being added
     * @param target {@link Player} target that the player is spectating
     */
    public void addSpectator(Player player, @Nullable Player target) {
        //This could happen mane
        if (this.isEnding()) {
            player.sendMessage(CC.translate("&cThat match has just ended, failed to add you as a spectator!"));
            return;
        }

        this.spectators.add(player.getUniqueId());

        MatchSpectatorJoinEvent event = new MatchSpectatorJoinEvent(player, this);
        event.call();

        PlayerUtil.spectator(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setMatch(this);
        profile.setSpectating(target);
        profile.setState(ProfileState.SPECTATING);
        profile.handleVisibility();
        profile.refreshHotbar();

        player.teleport(this.getMidSpawn());
        player.spigot().setCollidesWithEntities(false);
        player.updateInventory();

        if (!profile.getPlayer().hasPermission("array.profile.silent")) {
            for (Player otherPlayer : getPlayers()) {
                otherPlayer.sendMessage(Locale.MATCH_SPECTATE.toString().replace("<spectator>", player.getName()));
            }
        }

        // I'll probably change this later onwards
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            this.getPlayers().forEach(p -> p.hidePlayer(player));

            if (profile.getSettings().isShowSpectator()) {
                getSpectators().forEach(spectator -> {
                    spectator.showPlayer(player);
                    player.showPlayer(spectator);
                });
            }
        }, 5L);

    }

    /**
     * Toggle spectator visibility for specified player
     *
     * @param player {@link Player}
     */
    public void toggleSpectators(Player player) {
        Profile profile = Profile.getByPlayer(player);
        profile.getSettings().setShowSpectator(!profile.getSettings().isShowSpectator());
        profile.refreshHotbar();

        if (profile.getSettings().isShowSpectator()) {
            getSpectators().forEach(spectator -> {
                spectator.showPlayer(player);
                player.showPlayer(spectator);
            });
            player.sendMessage(CC.translate("&aShowing spectators."));
        } else {
            getSpectators().forEach(spectator -> {
                spectator.hidePlayer(player);
                player.hidePlayer(spectator);
            });
            player.sendMessage(CC.translate("&cHiding spectators."));
        }
    }

    /**
     * Remove the specified spectator and teleport
     * them back to spawn with their visibility and hotbar being reset
     *
     * @param player {@link Player} leaving spectating
     */
    public void removeSpectator(Player player) {
        this.spectators.remove(player.getUniqueId());

        MatchSpectatorLeaveEvent event = new MatchSpectatorLeaveEvent(player, this);
        event.call();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setMatch(null);
        profile.setSpectating(null);
        profile.refreshHotbar();
        profile.handleVisibility();
        profile.teleportToSpawn();

        player.setAllowFlight(false);
        player.setFlying(false);
        player.spigot().setCollidesWithEntities(true);
        player.updateInventory();

        if (this.isEnding()) return;
        for ( Player otherPlayer : getPlayers() ) {
            if (!profile.getPlayer().hasPermission("array.profile.silent")) {
                otherPlayer.sendMessage(Locale.MATCH_STOPSPEC.toString().replace("<spectator>", player.getName()));
            }
        }
    }

    /**
     * Get both players and spectators in a {@link List}
     *
     * @return {@link List}
     */
    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    /**
     * Get hover event for Clickable Inventories of
     * a specified {@link TeamPlayer}
     *
     * @param teamPlayer {@link TeamPlayer} the player whose hover event we are getting
     * @return {@link String} the hover string of the hover event
     */
    protected static String getHoverEvent(TeamPlayer teamPlayer) {
        return Locale.MATCH_INVENTORY_HOVER.toString().replace("<inventory_name>", teamPlayer.getUsername());
    }

    /**
     * Get click event for Clickable Inventories of
     * a specified {@link TeamPlayer}
     *
     * @param teamPlayer {@link TeamPlayer} the player whose click event we are getting
     * @return {@link String} the command string of the click event
     */
    protected static String getClickEvent(TeamPlayer teamPlayer) {
        return "/viewinv " + teamPlayer.getUniqueId().toString();
    }

    /**
     * Get the middle spawn of the current arena
     *
     * @return {@link Location} middle spawn
     */
    public Location getMidSpawn() {
        Location spawn = getArena().getSpawn1();
        Location spawn2 = getArena().getSpawn2();
        Location midSpawn = getArena().getSpawn1();

        midSpawn.setX(getAverage(spawn.getX(), spawn2.getX()));
        midSpawn.setZ(getAverage(spawn.getZ(), spawn2.getZ()));

        return midSpawn;
    }

    /**
     * Average between two numbers
     *
     * @param one {@link Double} first number
     * @param two {@link Double} second number
     * @return {@link Double} average of both numbers
     */
    public double getAverage(double one, double two) {
        double three = one + two;
        three = three / 2;
        return three;
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
     * Returns true if the match starting
     *
     * @return {@link Boolean}
     */
    public boolean isStarting() {
        return state == MatchState.STARTING;
    }

    /**
     * Returns true if the match is in fight
     *
     * @return {@link Boolean}
     */
    public boolean isFighting() {
        return state == MatchState.FIGHTING;
    }

    /**
     * Returns true if the match is ending
     *
     * @return {@link Boolean}
     */
    public boolean isEnding() {
        return state == MatchState.ENDING;
    }

    /**
     * This method is returns true if the
     * current match related to {@link SoloMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isSoloMatch() {
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link TeamMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isTeamMatch(){
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link FFAMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isFreeForAllMatch(){
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link HCFMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isHCFMatch() {
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link SoloBridgeMatch}
     * or {@link TeamBridgeMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isTheBridgeMatch() {
        return this instanceof SoloBridgeMatch || this instanceof TeamBridgeMatch;
    }

    /**
     * Setup the player according to {@link Kit},
     * {@link KitGameRules} and {@link Arena}
     * <p>
     * This also teleports the player to the specified arena,
     * set's their parkour checkpoint if kit is parkour and
     * gives special potion effects if specified
     *
     * @param player {@link Player} being setup
     */
    public abstract void setupPlayer(Player player);

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    public abstract void onStart();

    /**
     * Execute match end tasks through this method
     * This method is called to check if the match can end or not
     * and if it can then the method itself clears up a bit of ending the match
     *
     * @return {@link Boolean} Whether the match successfully ended or not
     */
    public abstract boolean onEnd();

    /**
     * Returns true if the match is ready to end
     *
     * @return {@link Boolean} Where the match can statistically end or not
     */
    public abstract boolean canEnd();

    /**
     * Execute the Kill Effect for a specified Player
     *
     * @param deadPlayer {@link Player} the player being killed
     * @param killerPlayer {@link Player} the player killing
     */
    public abstract void handleKillEffect(Player deadPlayer, Player killerPlayer);

    /**
     * Execute tasks upon a player's death
     *
     * @param player {@link Player} the player being killed
     * @param killer {@link Player} the player killing
     */
    public abstract void onDeath(Player player, Player killer);

    /**
     * Execute tasks upon a player's respawn
     *
     * @param player {@link Player} the player being respawned
     */
    public abstract void onRespawn(Player player);

    /**
     * Get the winning {@link Player} of a Match
     *
     * @return {@link Player}
     */
    public abstract Player getWinningPlayer();

    /**
     * Get the winning {@link Team} of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getWinningTeam();

    /**
     * Get teamPlayerA of a Match
     *
     * @return {@link TeamPlayer}
     */
    public abstract TeamPlayer getTeamPlayerA();

    /**
     * Get teamPlayerB of a Match
     *
     * @return {@link TeamPlayer}
     */
    public abstract TeamPlayer getTeamPlayerB();

    /**
     * Get a List of all TeamPlayers of a Match
     *
     * @return {@link List<TeamPlayer>}
     */
    public abstract List<TeamPlayer> getTeamPlayers();

    /**
     * Get a List of all Players of a Match
     *
     * @return {@link List<Player>}
     */
    public abstract List<Player> getPlayers();

    /**
     * Get a List of all Alive Players of a Match
     *
     * @return {@link List<Player>}
     */
    public abstract List<Player> getAlivePlayers();

    /**
     * Get Team A of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getTeamA();

    /**
     * Get Team B of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getTeamB();

    public abstract Team getTeam(Player player);

    public abstract TeamPlayer getTeamPlayer(Player player);

    public abstract Team getOpponentTeam(Team Team);

    public abstract Team getOpponentTeam(Player player);

    public abstract TeamPlayer getOpponentTeamPlayer(Player player);

    public abstract Player getOpponentPlayer(Player player);

    public abstract List<BaseComponent[]> generateEndComponents(Player player);

    public abstract ChatColor getRelationColor(Player viewer, Player target);
}