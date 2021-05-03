package me.drizzy.practice.match;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.events.*;
import me.drizzy.practice.match.task.*;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.ChatComponentBuilder;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.other.TaskUtil;
import me.drizzy.practice.util.other.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public abstract class Match {

    @Setter private long startTimestamp;
    @Setter public MatchState state = MatchState.STARTING;
    @Getter protected static List<Match> matches = new ArrayList<>();
    private final UUID matchId = UUID.randomUUID();
    private final Queue queue;
    private final Kit kit;
    public BukkitTask task;
    private BukkitTask matchWaterCheck;
    public Map<UUID, EnderPearl> pearlMap = new HashMap<>();
    private final Arena arena;
    private final QueueType queueType;
    private final List<MatchSnapshot> snapshots = new ArrayList<>();
    public final List<UUID> spectators = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> droppedItems = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final List<BlockState> changedBlocks = new ArrayList<>();
    public final List<Player> catcher = new ArrayList<>();

    public Match(Queue queue, Kit kit, Arena arena, QueueType queueType) {
        this.queue = queue;
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;

        matches.add(this);
    }

    public static void preload() {
        new MatchPearlCooldownTask().runTaskTimerAsynchronously(Array.getInstance(), 2L, 2L);
        new MatchSnapshotCleanupTask().runTaskTimerAsynchronously(Array.getInstance(), 20L * 5, 20L * 5);
        new BukkitRunnable(){
            @Override
            public void run() {
                for(World world : Bukkit.getWorlds()){
                    world.setStorm(false);
                    world.setThundering(false);
                }
            }
        }.runTaskTimer(Array.getInstance() , 20 , 20);
    }

    public static void cleanup() {
        for (Match match : matches) {
            match.getPlacedBlocks().forEach(location -> location.getBlock().setType(Material.AIR));
            match.getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
            match.getEntities().forEach(Entity::remove);
        }
    }

    public static int getInFights(Queue queue) {
        int i = 0;

        for (Match match : matches) {
            if (match.getQueue() != null && (match.isFighting() || match.isStarting())) {
                if (match.getQueue() != null && match.getQueue().equals(queue)) {
                    i = i + match.getTeamPlayers().size();
                }
            }
        }

        return i;
    }

    public boolean isStarting() {
        return state == MatchState.STARTING;
    }

    public boolean isFighting() {
        return state == MatchState.FIGHTING;
    }

    public boolean isEnding() {
        return state == MatchState.ENDING;
    }

    public void start() {
        for (Player player : getPlayers()) {

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_FIGHT);
            profile.setMatch(this);

            for ( Player otherPlayer : Bukkit.getOnlinePlayers() ) {
                profile.handleVisibility(player, otherPlayer);
            }

            if (!getArena().getSpawn1().getChunk().isLoaded() || !getArena().getSpawn2().getChunk().isLoaded()) {
                getArena().getSpawn1().getChunk().load();
                getArena().getSpawn2().getChunk().load();
            }

            new MatchPlayerSetupEvent(player, this).call();

            setupPlayer(player);

        }

        onStart();

        for (Player player : this.getPlayers()) {
            Profile profile = Profile.getByPlayer(player);
            if (!profile.getSentDuelRequests().isEmpty()) {
                profile.getSentDuelRequests().clear();
            }
        }

        state = MatchState.STARTING;
        startTimestamp = -1;
        arena.setActive(true);

        if (!this.isSoloMatch() || !isHCFMatch() || !this.isTheBridgeMatch()) {
            if (this.isFreeForAllMatch() || this.isTeamMatch()) {
                for ( String string : Locale.MATCH_TEAM_STARTMESSAGE.toList()) {
                    this.broadcastMessage(CC.translate(this.replace(string)));
                }
            }
        }
        if (isHCFMatch()) {
            for ( String string : Locale.MATCH_HCF_STARTMESSAGE.toList()) {
                this.broadcastMessage(CC.translate(string));
            }
        }

        if (getKit() != null) {
            if (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isParkour() || getKit().getGameRules().isSumo()) {
                matchWaterCheck = new MatchWaterCheckTask(this).runTaskTimer(Array.getInstance(), 20L, 20L);
            }
        }

        task = new MatchStartTask(this).runTaskTimer(Array.getInstance(), 20L, 20L);
        for (Player shooter : getPlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Profile shooterData = Profile.getByPlayer(shooter);

                    if (shooterData.isInFight()) {
                        int potions = 0;
                        for (ItemStack item : shooter.getInventory().getContents()) {
                            if (item == null)
                                continue;
                            if (item.getType() == Material.AIR)
                                continue;
                            if (item.getType() != Material.POTION)
                                continue;
                            if (item.getDurability() != (short) 16421)
                                continue;
                            potions++;
                        }
                        shooterData.getMatch().getTeamPlayer(shooter).setPotions(potions);
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(Array.getInstance(), 0L, 5L);
        }
        final MatchStartEvent event = new MatchStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

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

        getPlayers().forEach(this::removePearl);

        getPlayers().stream().map(Profile::getByPlayer).map(Profile::getPlates).forEach(List::clear);

        getSpectators().forEach(this::removeSpectator);
        entities.forEach(Entity::remove);
        droppedItems.forEach(Entity::remove);


        if (matchWaterCheck != null) {
            matchWaterCheck.cancel();
        }

        final MatchEndEvent event = new MatchEndEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        new MatchResetTask(this).runTask(Array.getInstance());

        getArena().setActive(false);

        matches.remove(this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> getPlayers().forEach(player -> ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0)), 2L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> getPlayers().forEach(player -> ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0)), 2L);
    }

    public void handleRespawn(Player player) {
        player.setVelocity(new Vector());

        onRespawn(player);
    }

    public String replace(String input) {
        input = input.replace("<arena>", this.getArena().getName())
                     .replace("<kit>", this.getKit().getName());

        return input;
    }

    public void onPearl(Player player, EnderPearl pearl) {
        this.pearlMap.put(player.getUniqueId(), pearl);
    }

    public void removePearl(Player player) {
        final EnderPearl pearl;
        if (player != null) {
            if ((pearl = this.pearlMap.remove(player.getUniqueId())) != null) {
                pearl.remove();
            }
        }
    }

    public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
        TeamPlayer teamPlayer=this.getTeamPlayer(deadPlayer);

        if (teamPlayer == null) return;

        teamPlayer.setDisconnected(disconnected);

        if (!teamPlayer.isAlive()) return;

        teamPlayer.setAlive(false);

        teamPlayer.setParkourCheckpoint(null);

        List<Player> playersAndSpectators=getPlayersAndSpectators();


        for ( Player player : playersAndSpectators ) {
            if (teamPlayer.isDisconnected()) {

                player.sendMessage(Locale.MATCH_DISCONNECTED.toString()
                        .replace("<relation_color>", getRelationColor(player, deadPlayer).toString())
                        .replace("<participant_name>", deadPlayer.getName()));

                continue;
            }
            if (!isTheBridgeMatch()) {
                if ((!isHCFMatch()) && getKit().getGameRules().isParkour() && killerPlayer != null) {
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
                            .replace("<dead_name>", deadPlayer.getName())
                            .replace("<relation_color_killer>", getRelationColor(player, killerPlayer).toString())
                            .replace("<killer_name>", killerPlayer.getName()));
                }
            }
        }

        catcher.remove(deadPlayer);

        if (!isTheBridgeMatch()) {
            final PacketContainer lightningPacket=this.createLightningPacket(deadPlayer.getLocation());
            float thunderSoundPitch = 0.8f + ThreadLocalRandom.current().nextFloat() * 0.2f;
            float explodeSoundPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;
            for ( final Player onlinePlayer : this.getPlayers() ) {
                Profile profile=Profile.getByPlayer(onlinePlayer);
                //PotPvP aka Lunar Death Animation
                PlayerUtil.animateDeath(deadPlayer);
                if (profile.getSettings().isLightning()) {
                    onlinePlayer.playSound(deadPlayer.getLocation(), Sound.AMBIENCE_THUNDER, 10000.0f, thunderSoundPitch);
                    if (killerPlayer != null) {
                        onlinePlayer.playSound(killerPlayer.getLocation(), Sound.AMBIENCE_THUNDER, 10000.0f, thunderSoundPitch);
                        onlinePlayer.playSound(killerPlayer.getLocation(), Sound.EXPLODE, 2.0f, explodeSoundPitch);
                    }
                    this.sendLightningPacket(onlinePlayer, lightningPacket);
                }
            }
        }

        onDeath(deadPlayer, killerPlayer);

        if (isTheBridgeMatch() && disconnected) {
            end();
            return;
        }

        if (!isTheBridgeMatch()) {
            if (canEnd()) {
                end();
            } else {
                PlayerUtil.spectator(deadPlayer);
                for ( Player player : getPlayersAndSpectators() ) {
                    TaskUtil.runLater(() -> Profile.getByUuid(player.getUniqueId()).handleVisibility(player, deadPlayer), 7L);
                }
                TaskUtil.runLater(() ->  {
                    getSpectators().forEach(spectator -> spectator.showPlayer(deadPlayer));
                    getSpectators().forEach(deadPlayer::showPlayer);
                }, 8L);
            }
        } else {
            if (canEnd()) {
                end();
            }
        }
    }

    private PacketContainer createLightningPacket(Location location) {
        final PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);
        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int)(location.getX() * 32.0));
        lightningPacket.getIntegers().write(2, ((int)(location.getY() * 32.0)));
        lightningPacket.getIntegers().write(3, ((int)(location.getZ() * 32.0)));
        return lightningPacket;
    }

    private void sendLightningPacket(final Player target, final PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        }
        catch (InvocationTargetException ex) {
            //empty catch
        }
    }

    public String getDuration() {
        if (isStarting()) {
            return "Starting";
        } else if (isEnding()) {
            return "Ending";
        } else {
            return TimeUtil.millisToTimer(getElapsedDuration());
        }
    }

    public long getElapsedDuration() {
        return System.currentTimeMillis() - startTimestamp;
    }

    public void broadcastMessage(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
        getSpectators().forEach(player -> player.sendMessage(message));
    }

    public void broadcastSound(Sound sound) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
        getSpectators().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
    }

    public List<Player> getSpectators() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void addSpectator(Player player, Player target) {
        spectators.add(player.getUniqueId());
        PlayerUtil.spectator(player);
        new MatchSpectatorJoinEvent(player, this).call();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setMatch(this);
        profile.setState(ProfileState.SPECTATING);
        profile.refreshHotbar();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(this.getMidSpawn());
        player.spigot().setCollidesWithEntities(false);

        if (!profile.getPlayer().hasPermission("array.staff")) {
            for (Player otherPlayer : getPlayers()) {
                otherPlayer.sendMessage(Locale.MATCH_SPECTATE.toString().replace("<spectator>", player.getName()));
            }
        }


        Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> {
            if (this.isSoloMatch()) {
                target.hidePlayer(player);
            }
            else if (this.isTheBridgeMatch()) {
                target.hidePlayer(player);
                this.getOpponentPlayer(target).hidePlayer(player);
            }
            else if (this.isTeamMatch()) {
                for ( Player targetPlayers : this.getTeam(target).getPlayers() ) {
                    targetPlayers.hidePlayer(player);
                }
                for ( Player targetPlayers : this.getOpponentTeam(target).getPlayers() ) {
                    targetPlayers.hidePlayer(player);
                }
            }
            else if (this.isHCFMatch()) {
                for ( Player targetPlayers : this.getTeam(target).getPlayers() ) {
                    targetPlayers.hidePlayer(player);
                }
                for ( Player targetPlayers : this.getOpponentTeam(target).getPlayers() ) {
                    targetPlayers.hidePlayer(player);
                }
            }
            else if (this.isFreeForAllMatch()) {
                for (  Player targetPlayers : this.getPlayers()) {
                    targetPlayers.hidePlayer(player);
                }
            }
        }, 10L);
    }

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

        if (state != MatchState.ENDING) {
            for (Player otherPlayer : getPlayers()) {
                if (!profile.getPlayer().hasPermission("array.staff")) {
                    otherPlayer.sendMessage(Locale.MATCH_STOPSPEC.toString().replace("<spectator>", player.getName()));
                }
            }
        }
    }

    public List<Player> getPlayersAndSpectators() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    protected HoverEvent getHoverEvent(TeamPlayer teamPlayer) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                .parse(Locale.MATCH_INVENTORY_HOVER.toString().replace("<inventory_name>", teamPlayer.getUsername())).create());
    }

    protected ClickEvent getClickEvent(TeamPlayer teamPlayer) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinv " + teamPlayer.getUuid().toString());
    }

    public double getAverage(double one, double two) {
        double three = one + two;
        three = three / 2;
        return three;
    }

    public Location getMidSpawn() {

        //Get Both Spawns
        Location spawn = getArena().getSpawn1();
        Location spawn2 = getArena().getSpawn2();
        //Initialize the mid spawn
        Location midSpawn = getArena().getSpawn1();

        //Get Average from both spawns to get the middle of the arena
        midSpawn.setX(getAverage(spawn.getX(), spawn2.getX()));
        midSpawn.setZ(getAverage(spawn.getZ(), spawn2.getZ()));

        //Return it as the middle spawn
        return midSpawn;
    }

    public abstract boolean isSoloMatch();

    public abstract boolean isTeamMatch();

    public abstract boolean isFreeForAllMatch();

    public abstract boolean isHCFMatch();

    public abstract boolean isTheBridgeMatch();

    public abstract void setupPlayer(Player player);

    public abstract void onStart();

    public abstract boolean onEnd();

    public abstract boolean canEnd();

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

    public abstract ChatColor getRelationColor(Player viewer, Player target);
}