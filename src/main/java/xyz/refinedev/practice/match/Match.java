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
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.events.match.*;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.meta.RatingType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.task.*;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.kit.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.chat.ChatHelper;
import xyz.refinedev.practice.util.chat.Clickable;
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
        TaskUtil.runTimerAsync(new MatchPearlCooldownTask(), 2L, 2L);
        TaskUtil.runTimerAsync(new MatchBowCooldownTask(), 2L, 2L);
        TaskUtil.runTimerAsync(new MatchSnapshotCleanupTask(), 20L * 5, 20L * 5);

        TaskUtil.runTimer(() -> Bukkit.getWorlds().forEach(world -> {
            world.setStorm(false);
            world.setThundering(false);
        }), 20, 20);
    }


    /**
     * Clear up the match remnants
     */
    public static void cleanup() {
        for (Match match : matches) {
            match.getPlacedBlocks().forEach(location -> location.getBlock().setType(Material.AIR));
            match.getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
            match.getEntities().forEach(Entity::remove);
        }
    }

    /**
     * Get amount of players in fight from a certain
     * queue
     *
     * @param queue {@link Queue}
     * @return amount of players in fight with the queue
     */
    public static int getInFights(Queue queue) {
        int i = 0;

        for (Match match : matches) {
            if (match.getQueue() == null || !match.getQueue().equals(queue)) return i;
            if (!match.isFighting() && !match.isStarting()) return i;

            i += match.getTeamPlayers().size();
        }
        return i;
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
     * Initiate and start the {@link Match}
     * This method sets up the players, teleports them
     * starts the countdown tasks, handles visibility
     */
    public void start() {
        for (Player player : getPlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_FIGHT);
            profile.setMatch(this);

            for ( Player otherPlayer : Bukkit.getOnlinePlayers() ) {
                profile.handleVisibility(player, otherPlayer);
            }

            //So that chunks are properly visible
            if (!this.arena.getSpawn1().getChunk().isLoaded() || !this.arena.getSpawn2().getChunk().isLoaded()) {
                this.arena.getSpawn1().getChunk().load();
                this.arena.getSpawn2().getChunk().load();
            }
            new MatchPlayerSetupEvent(player, this).call();
            this.setupPlayer(player);
        }

        TaskUtil.run(this::onStart);

        for (Player player : this.getPlayers()) {
            Profile profile = Profile.getByPlayer(player);
            if (!profile.getSentDuelRequests().isEmpty()) {
                profile.getSentDuelRequests().clear();
            }
        }

        this.state = MatchState.STARTING;
        this.startTimestamp = -1;
        this.arena.setActive(true);

        this.sendStartMessage();

        if (getKit() != null) {
            if (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isParkour() || getKit().getGameRules().isSumo()) {
                this.matchWaterCheck = new MatchWaterCheckTask(this).runTaskTimer(plugin, 20L, 20L);
            }
        }

        this.task = new MatchStartTask(this).runTaskTimer(plugin, 20L, 20L);
        getPlayers().forEach(player -> new MatchPotionTrackTask(player).runTaskTimerAsynchronously(plugin, 0L, 5L));

        new MatchStartEvent(this).call();
    }

    /**
     * End the {@link Match}
     * This resets the players, updates the match's state
     * Created the Match inventories for each player
     * Resets their knockback and hit delay
     * and finally sends them rating message if enabled
     */
    public void end() {
        if (onEnd()) {
            state = MatchState.ENDING;
        } else {
            return;
        }

        if (getKit().getGameRules().isBuild() || getKit().getGameRules().isShowHealth()) {
            for ( Player otherPlayerTeam : getPlayers() ) {
                Objective objective = otherPlayerTeam.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null) objective.unregister();
            }
        }

        snapshots.forEach(matchInventory -> {
                    matchInventory.setCreated(System.currentTimeMillis());
                    MatchSnapshot.getSnapshots().put(matchInventory.getTeamPlayer().getUuid(), matchInventory);
        });

        for ( Player player : getPlayers() ) {
            plugin.getKnockbackManager().resetKnockback(player);
            player.setMaximumNoDamageTicks(20);
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

        if (Array.getInstance().getConfigHandler().isRATINGS_ENABLED()) {
            this.getPlayers().stream().map(Profile::getByPlayer).forEach(profile ->  {
                profile.setCanIssueRating(true);
                profile.setRatingArena(arena);
                this.sendRatingMessage(profile.getPlayer(), this.getArena());
            });
        }

        this.getPlayers().forEach(p -> this.removePearl(p, true));
        this.getPlayers().stream().map(Profile::getByPlayer).map(Profile::getPlates).forEach(List::clear);

        entities.forEach(Entity::remove);
        droppedItems.forEach(Entity::remove);

        arena.setActive(false);

        if (matchWaterCheck != null) matchWaterCheck.cancel();

        new MatchEndEvent(this).call();
        new MatchResetTask(this).runTask(Array.getInstance());

        matches.remove(this);
    }

    public String replace(String input) {
        input = input.replace("<arena>", this.getArena().getName())
                     .replace("<kit>", this.getKit().getName());
        return input;
    }

    /**
     * Send Match start message
     * This isn't used in solo matches
     * because we get their message in our queue thread
     * or duel handler
     */
    public void sendStartMessage() {
        if (this.isFreeForAllMatch() || this.isTeamMatch()) {
            Locale.MATCH_TEAM_STARTMESSAGE.toList().forEach(this::broadcastMessage);
        } else if (isHCFMatch()) {
            Locale.MATCH_HCF_STARTMESSAGE.toList().forEach(this::broadcastMessage);
        }
    }

    /**
     * Send the player our rating message and allow them to
     * rate the arena provided in their match
     *
     * @param player {@link Player} the player sending the rating message
     * @param arena {@link Arena} the arena being rated
     */
    public void sendRatingMessage(Player player, Arena arena) {
        Profile profile = Profile.getByPlayer(player);
        profile.setCanIssueRating(true);

        String key = "&7Click to rate &a" + arena.getDisplayName();
        Clickable clickable =
        new Clickable("&c&l[1⭐]", key + " &7as &cTerrible&7.", "/rate " + arena.getName() + " " + RatingType.TERRIBLE.name());
        clickable.add("&6&l[2⭐]", key + " &7as &6Okay&7.", "/rate " + arena.getName() + " " + RatingType.OKAY.name());
        clickable.add("&e&l[3⭐]", key + " &7as &eAverage&7.", "/rate " + arena.getName() + " " + RatingType.AVERAGE.name());
        clickable.add("&2&l[4⭐]", key + " &7as &2Decent&7.", "/rate " + arena.getName() + " " + RatingType.DECENT.name());
        clickable.add("&a&l[5⭐]", key + " &7as &aGood&7.", "/rate " + arena.getName() + " " + RatingType.GOOD.name());

        player.sendMessage("");
        player.sendMessage(CC.translate("&aPlease give us feedback on the Arena, How was it?"));
        clickable.sendToPlayer(player);
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
        if (PlayerUtil.getLastDamager(player) instanceof CraftPlayer) {
            Player killer = (Player) PlayerUtil.getLastDamager(player);
            handleDeath(player, killer, false);
        } else if (player.getKiller() != null) {
            handleDeath(player, player.getKiller(), false);
        } else {
            handleDeath(player, null, false);
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

        for ( Player player : getPlayersAndSpectators() ) {
            TeamPlayer otherTeamPlayer = getTeamPlayer(player);
            if (otherTeamPlayer == null || otherTeamPlayer.isDisconnected()) continue;

            if (teamPlayer.isDisconnected()) {
                player.sendMessage(Locale.MATCH_DISCONNECTED.toString()
                        .replace("<relation_color>", getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));
                continue;
            }
            if (!isHCFMatch() && getKit().getGameRules().isParkour() && killerPlayer != null) {
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
            this.handleKillEffect(deadPlayer, killerPlayer);
        }
        this.onDeath(deadPlayer, killerPlayer);
        //Respawn the player jic
        deadPlayer.spigot().respawn();
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
    public void addSpectator(Player player, Player target) {
        spectators.add(player.getUniqueId());
        new MatchSpectatorJoinEvent(player, this).call();

        PlayerUtil.spectator(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setMatch(this);
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
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
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
        spectators.remove(player.getUniqueId());
        new MatchSpectatorLeaveEvent(player, this).call();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setMatch(null);
        profile.refreshHotbar();
        profile.handleVisibility();
        profile.teleportToSpawn();

        player.setAllowFlight(false);
        player.setFlying(false);
        player.spigot().setCollidesWithEntities(true);
        player.updateInventory();

        if (state == MatchState.ENDING) return;
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
    public List<Player> getPlayersAndSpectators() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    protected static String getHoverEvent(TeamPlayer teamPlayer) {
        return Locale.MATCH_INVENTORY_HOVER.toString().replace("<inventory_name>", teamPlayer.getUsername());
    }

    protected static String getClickEvent(TeamPlayer teamPlayer) {
        return "/viewinv " + teamPlayer.getUuid().toString();
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
    public static BaseComponent[] generateInventoriesComponents(String prefix, TeamPlayer participant) {
        return generateInventoriesComponents(prefix, Collections.singletonList(participant));
    }

    /**
     * Generate match inventory click messages
     *
     * @param prefix {@link String} prefix of the message, either winner or loser
     * @param participants {@link List<TeamPlayer>} the list of teamPlayers whose message we are displaying
     * @return {@link BaseComponent}
     */
    public static BaseComponent[] generateInventoriesComponents(String prefix, List<TeamPlayer> participants) {
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

    public boolean isSoloMatch() {
        return false;
    }

    public boolean isTeamMatch(){
        return false;
    }

    public boolean isFreeForAllMatch(){
        return false;
    }

    public boolean isHCFMatch() {
        return false;
    }

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

    public abstract void handleKillEffect(Player deadPlayer, Player killerPlayer);

    public abstract void onDeath(Player player, Player killer);

    public abstract void onRespawn(Player player);

    public abstract Player getWinningPlayer();

    public abstract Team getWinningTeam();

    public abstract TeamPlayer getTeamPlayerA();

    public abstract TeamPlayer getTeamPlayerB();

    public abstract List<TeamPlayer> getTeamPlayers();

    public abstract List<Player> getPlayers();

    public abstract List<Player> getAlivePlayers();

    public abstract Team getTeamA();

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