package me.drizzy.practice.event.types.skywars;

import me.drizzy.practice.util.external.ChatComponentBuilder;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.LocationUtil;
import me.drizzy.practice.util.external.TimeUtil;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.skywars.player.SkyWarsPlayer;
import me.drizzy.practice.event.types.skywars.player.SkyWarsPlayerState;
import me.drizzy.practice.event.types.skywars.task.SkyWarsRoundEndTask;
import me.drizzy.practice.event.types.skywars.task.SkyWarsRoundStartTask;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import pt.foxspigot.jar.practice.util.external.*;

import java.util.*;

@Getter
public class SkyWars {

    protected static String EVENT_PREFIX=CC.translate("&8[&bSkywars&8] &r");

    private final String name;
    @Setter
    private SkyWarsState state = SkyWarsState.WAITING;
    private SkyWarsTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, SkyWarsPlayer> eventPlayers = new LinkedHashMap<>();
    @Getter
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final Collection<Item> drops = new ArrayList<>();
    private final List<BlockState> changedBlocks = new ArrayList<>();
    @Getter
    @Setter
    public static int maxPlayers;
    @Getter
    @Setter
    private int totalPlayers;
    @Setter
    private Cooldown cooldown;
    @Setter
    private long roundStart;
    @Getter
    @Setter
    private static boolean enabled = true;


    public SkyWars(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        maxPlayers = Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().size();

        for ( SkywarsChests chest : SkywarsChests.chests.values()) {
            Inventory cInv = chest.getBlock().getInventory();
            cInv.clear();
            chest.getBlock().update();
        }

    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        SkyWars skyWars = Array.getInstance().getSkyWarsManager().getActiveSkyWars();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&bHost: &r" + skyWars.getName()));

        if (skyWars.isWaiting()) {
            toReturn.add("bPlayers: &r" + skyWars.getEventPlayers().size() + "/" + SkyWars.getMaxPlayers());
            toReturn.add("");

            if (skyWars.getCooldown() == null) {
                toReturn.add(CC.translate("&fWaiting for players..."));
            } else {
                String remaining = TimeUtil.millisToSeconds(skyWars.getCooldown().getRemaining());

                if (remaining.startsWith("-")) {
                    remaining = "0.0";
                }

                toReturn.add(CC.translate("&7Match starting in &c" + remaining + "s"));
            }
        } else {
            toReturn.add("&bPlayers: &r" + skyWars.getRemainingPlayers().size() + "/" + skyWars.getTotalPlayers());
            toReturn.add("&bDuration: &r" + skyWars.getRoundDuration());
        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(SkyWarsTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == SkyWarsState.WAITING;
    }

    public boolean isFighting() {
        return state == SkyWarsState.ROUND_FIGHTING;
    }

    public boolean isFighting(Player player) {
        if (state.equals(SkyWarsState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public SkyWarsPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            Player player = skyWarsPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            if (skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
                Player player = skyWarsPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        if (this.eventPlayers.size() >= this.maxPlayers) {
            player.sendMessage(CC.RED + "The event is full");
            return;
        }

        eventPlayers.put(player.getUniqueId(), new SkyWarsPlayer(player));

        broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bSkywars Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
        player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bSkywars Event&8!"));
        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSkyWars(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(LocationUtil.deserialize(Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().get(0)));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Array.getInstance());
    }

    public void handleLeave(Player player) {
        if (state != SkyWarsState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == SkyWarsState.WAITING) {
            broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bSkywars Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
            player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bSkywars Event&8!"));
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setSkyWars(null);
        profile.refreshHotbar();

        Array.getInstance().getEssentials().teleportToSpawn(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Array.getInstance());
    }

    protected List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void handleDeath(Player player, Player killer) {
        SkyWarsPlayer loser = getEventPlayer(player);
        loser.setState(SkyWarsPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public void end() {
        Array.getInstance().getSkyWarsManager().setActiveSkyWars(null);
        Array.getInstance().getSkyWarsManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        new SkyWarsResetTask(this).runTask(Array.getInstance());

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The skywars event has been canceled.");
        } else {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "SkyWars Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "SkyWars Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "SkyWars Event" + CC.GRAY + "!");
        }

        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            Player player = skyWarsPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setSkyWars(null);
            PlayerUtil.reset(player, false);
        profile.refreshHotbar();

                Array.getInstance().getEssentials().teleportToSpawn(player);
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public boolean canEnd() {
        int remaining = 0;

        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            if (skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
                remaining++;
            }
        }

        return remaining == 1;
    }

    public Player getWinner() {
        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            if (skyWarsPlayer.getState() != SkyWarsPlayerState.ELIMINATED) {
                return skyWarsPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.AQUA + getHost().getPlayer().getName() + CC.translate("&7 is hosting a &b&lSkywars Event&7. ") + CC.GREEN + "[Click to join]")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GRAY + "Click to join the skywars event.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skywars join"))
                .create();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!eventPlayers.containsKey(player.getUniqueId())) {
                player.sendMessage("");
                player.spigot().sendMessage(components);
                player.sendMessage("");
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(EVENT_PREFIX + CC.translate(message));
        }
    }

    public void onJoin(Player player) {
        KnockbackProfile kbprofile = KnockbackModule.getDefault();
        ((CraftPlayer) player).getHandle().setKnockback(kbprofile);
    }
    public void onLeave(Player player) {
        KnockbackProfile kbprofile = KnockbackModule.getDefault();
        ((CraftPlayer) player).getHandle().setKnockback(kbprofile);
    }

    public void onRound() {
        setState(SkyWarsState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(LocationUtil.deserialize(Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().get(i)));
                i++;

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInSkyWars()) {
                PlayerUtil.reset(player, false);
        profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
                PlayerUtil.allowMovement(player);
            }
        }
        setEventTask(new SkyWarsRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + killer.getName() + "&7!");
        }


        if (canEnd()) {
            setState(SkyWarsState.ROUND_ENDING);
            setEventTask(new SkyWarsRoundEndTask(this));
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
        }.runTaskAsynchronously(Array.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
            PlayerUtil.reset(player, false);
        profile.refreshHotbar();
            }
        }.runTask(Array.getInstance());
    }

    public String getRoundDuration() {
        if (getState() == SkyWarsState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == SkyWarsState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSkyWars(this);
        player.setFlying(true);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(LocationUtil.deserialize(Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().get(0)));
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSkyWars(null);
        player.setFlying(false);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        Array.getInstance().getEssentials().teleportToSpawn(player);
    }
}
