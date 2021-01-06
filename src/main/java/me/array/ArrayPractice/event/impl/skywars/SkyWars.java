package me.array.ArrayPractice.event.impl.skywars;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayer;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayerState;
import me.array.ArrayPractice.event.impl.skywars.task.SkyWarsRoundEndTask;
import me.array.ArrayPractice.event.impl.skywars.task.SkyWarsRoundStartTask;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.*;
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
import rip.verse.jupiter.knockback.KnockbackModule;
import rip.verse.jupiter.knockback.KnockbackProfile;

import java.util.*;

@Getter
public class SkyWars {

    protected static String EVENT_PREFIX = CC.RED + CC.BOLD + "* " + CC.RESET;

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
    private final int maxPlayers;
    @Getter
    @Setter
    private int totalPlayers;
    @Setter
    private Cooldown cooldown;
    @Setter
    private long roundStart;


    public SkyWars(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = Practice.get().getSkyWarsManager().getSkyWarsSpectators().size();

        for (SkyWarsChest chest : SkyWarsChest.chests.values()) {
            Inventory cInv = chest.getBlock().getInventory();
            cInv.clear();
            chest.getBlock().update();
        }

    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        SkyWars skyWars = Practice.get().getSkyWarsManager().getActiveSkyWars();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&bHost: &r" + skyWars.getName()));

        if (skyWars.isWaiting()) {
            toReturn.add("bPlayers: &r" + skyWars.getEventPlayers().size() + "/" + skyWars.getMaxPlayers());
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
            toReturn.add("&bRemaining: &r" + skyWars.getRemainingPlayers().size() + "/" + skyWars.getTotalPlayers());
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
            eventTask.runTaskTimer(Practice.get(), 0L, 20L);
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

        broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the skywars event. " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSkyWars(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(LocationUtil.deserialize(Practice.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Practice.get());
    }

    public void handleLeave(Player player) {
        if (state != SkyWarsState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == SkyWarsState.WAITING) {
            broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the event " + CC.GRAY +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setSkyWars(null);
        profile.refreshHotbar();

        Practice.get().getEssentials().teleportToSpawn(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Practice.get());
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
        Practice.get().getSkyWarsManager().setActiveSkyWars(null);
        Practice.get().getSkyWarsManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        new SkyWarsResetTask(this).runTask(Practice.get());

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The skywars has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the skywars event!");
            Bukkit.broadcastMessage(CC.GRAY + "");
        }

        for (SkyWarsPlayer skyWarsPlayer : eventPlayers.values()) {
            Player player = skyWarsPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setSkyWars(null);
                profile.refreshHotbar();

                Practice.get().getEssentials().teleportToSpawn(player);
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
                .parse(EVENT_PREFIX + CC.RED + getHost().getUsername() + CC.GRAY + " is hosting a skywars event! " + CC.GREEN + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GREEN + "Click to join.").create()))
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
        KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get("Practice");
        ((CraftPlayer)player).getHandle().setKnockback(profile);
    }
    public void onLeave(Player player) {
        KnockbackProfile profile = KnockbackModule.INSTANCE.profiles.get("Practice");
        ((CraftPlayer)player).getHandle().setKnockback(profile);
    }

    public void onRound() {
        setState(SkyWarsState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(LocationUtil.deserialize(Practice.get().getSkyWarsManager().getSkyWarsSpectators().get(i)));
                i++;

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInSkyWars()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }
        }
        setEventTask(new SkyWarsRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&c" + player.getName() + "&7 was eliminated by &c" + killer.getName() + "&7!");
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
        }.runTaskAsynchronously(Practice.get());

        new BukkitRunnable() {
            @Override
            public void run() {
                profile.refreshHotbar();
            }
        }.runTask(Practice.get());
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

        player.teleport(LocationUtil.deserialize(Practice.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSkyWars(null);
        player.setFlying(false);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        Practice.get().getEssentials().teleportToSpawn(player);
    }
}
