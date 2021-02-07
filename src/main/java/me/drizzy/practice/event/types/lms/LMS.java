package me.drizzy.practice.event.types.lms;

import org.bukkit.Location;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.lms.player.LMSPlayer;
import me.drizzy.practice.event.types.lms.player.LMSPlayerState;
import me.drizzy.practice.event.types.lms.task.LMSRoundEndTask;
import me.drizzy.practice.event.types.lms.task.LMSRoundStartTask;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarItem;
import me.drizzy.practice.util.Circle;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.Teleporter;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LMS {

    protected static String EVENT_PREFIX =CC.translate("&8[&bLMS&8] &r");
    @Getter
    @Setter
    static private Kit kit;
    private final String name;
    @Setter
    private LMSState state = LMSState.WAITING;
    private LMSTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, LMSPlayer> eventPlayers = new LinkedHashMap<>();
    @Getter
    private final List<UUID> spectators = new ArrayList<>();
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


    public LMS(Player player, Kit kit) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        maxPlayers = 100;
        LMS.kit = kit;
    }

    public List<String> getLore() {
        List<String> toReturn=new ArrayList<>();

        LMS LMS = Array.getInstance().getLMSManager().getActiveLMS();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&bHost: &r" + LMS.getName()));
        toReturn.add(CC.translate("&bKit: &r" + kit.getName()));

        if (LMS.isWaiting()) {
            toReturn.add("&bPlayers: &r" + LMS.getEventPlayers().size() + "/" + LMS.getMaxPlayers());
            toReturn.add("");

            if (LMS.getCooldown() == null) {
                toReturn.add(CC.translate("&fWaiting for players..."));
            } else {
                String remaining=TimeUtil.millisToSeconds(LMS.getCooldown().getRemaining());

                if (remaining.startsWith("-")) {
                    remaining="0.0";
                }

                toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
            }
        } else {
            toReturn.add("&bPlayers: &r" + LMS.getRemainingPlayers().size() + "/" + LMS.getTotalPlayers());
            toReturn.add("&bDuration: &r" + LMS.getRoundDuration());
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
            eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
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
        if (this.eventPlayers.size() >= this.maxPlayers) {
            player.sendMessage(CC.RED + "The event is full");
            return;
        }

        eventPlayers.put(player.getUniqueId(), new LMSPlayer(player));

        broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bLMS Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
        player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bLMS Event&8!"));
        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(Array.getInstance().getLMSManager().getLmsSpectator());

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
        if (state != LMSState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == LMSState.WAITING) {
            broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bLMS Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
            player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bLMS Event&8!"));
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setLms(null);
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
        LMSPlayer loser = getEventPlayer(player);
        loser.setState(LMSPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public void end() {
        Array.getInstance().getLMSManager().setActiveLMS(null);
        Array.getInstance().getLMSManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The LMS event has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "LMS Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "LMS Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "LMS Event" + CC.GRAY + "!");
        }

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setLms(null);
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
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.AQUA + getHost().getPlayer().getName() + CC.translate("&7 is hosting a &b&lLMS Event&7. ") + CC.GREEN + "[Click to join]")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GRAY + "Click to join the LMS event.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lms join"))
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
        if (getKit().getKnockbackProfile() != null && KnockbackModule.INSTANCE.profiles.containsKey(getKit().getKnockbackProfile())) {
            kbprofile = KnockbackModule.INSTANCE.profiles.get(getKit().getKnockbackProfile());
        }
        ((CraftPlayer) player).getHandle().setKnockback(kbprofile);
    }
    public void onLeave(Player player) {
        KnockbackProfile kbprofile = KnockbackModule.getDefault();
        ((CraftPlayer) player).getHandle().setKnockback(kbprofile);
    }

    public void onRound() {
        setState(LMSState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
                List<Location> locs = Circle.getCircle(Array.getInstance().getLMSManager().getLmsSpectator(),5, this.eventPlayers.size());
                Location spawn = Array.getInstance().getLMSManager().getLmsSpectator();
            if (player != null) {
                Location center = spawn.clone();
                Location loc = locs.get(0);
                Location target = loc.setDirection(center.subtract(loc).toVector());
                if(Teleporter.teleport(player, target, false)) {
                    locs.remove(0);
                }
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInLMS()) {
                PlayerUtil.reset(player, false);
        profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }

            assert player != null;
            Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(0, Hotbar.getItems().get(HotbarItem.DEFAULT_KIT)));
        }
        setEventTask(new LMSRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + killer.getName() + "&7!");
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
        if (getState() == LMSState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == LMSState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
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
        player.setFlying(true);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(Array.getInstance().getLMSManager().getLmsSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(null);
        player.setFlying(false);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        Array.getInstance().getEssentials().teleportToSpawn(player);
    }
}
