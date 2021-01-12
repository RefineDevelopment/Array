package me.array.ArrayPractice.event.impl.lms;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.lms.player.LMSPlayer;
import me.array.ArrayPractice.event.impl.lms.player.LMSPlayerState;
import me.array.ArrayPractice.event.impl.lms.task.LMSRoundEndTask;
import me.array.ArrayPractice.event.impl.lms.task.LMSRoundStartTask;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LMS {

    protected static String EVENT_PREFIX = CC.AQUA + CC.BOLD + "(LMS) " + CC.RESET;
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
    private final int maxPlayers;
    @Getter
    @Setter
    private int totalPlayers;
    @Setter
    private Cooldown cooldown;
    @Setter
    private long roundStart;


    public LMS(Player player, Kit kit) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
        LMS.kit = kit;
    }

    public List<String> getLore() {
        List<String> toReturn=new ArrayList<>();

        LMS LMS = Practice.getInstance().getLMSManager().getActiveLMS();

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
            toReturn.add("&bRemaining: &r" + LMS.getRemainingPlayers().size() + "/" + LMS.getTotalPlayers());
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
            eventTask.runTaskTimer(Practice.getInstance(), 0L, 20L);
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
        eventPlayers.put(player.getUniqueId(), new LMSPlayer(player));

        broadcastMessage(CC.GOLD + player.getName() + CC.YELLOW + " joined the LMS " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(Practice.getInstance().getLMSManager().getLmsSpectator());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Practice.getInstance());
    }

    public void handleLeave(Player player) {
        if (state != LMSState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == LMSState.WAITING) {
            broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the LMS " + CC.GRAY +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setLms(null);
        profile.refreshHotbar();

        Practice.getInstance().getEssentials().teleportToSpawn(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(Practice.getInstance());
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
        Practice.getInstance().getLMSManager().setActiveLMS(null);
        Practice.getInstance().getLMSManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The LMS has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the ffa!");
        }

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setLms(null);
                profile.refreshHotbar();

                Practice.getInstance().getEssentials().teleportToSpawn(player);
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
                .parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting LMS " + CC.GRAY + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GRAY + "Click to join the LMS.").create()))
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
        ((CraftPlayer)player).getHandle().setKnockback(KnockbackModule.INSTANCE.profiles.get("Practice"));
    }

    public void onLeave(Player player) {
        ((CraftPlayer)player).getHandle().setKnockback(KnockbackModule.INSTANCE.profiles.get("Practice"));
    }

    public void onRound() {
        setState(LMSState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(Practice.getInstance().getLMSManager().getLmsSpectator());

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInLMS()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }

            Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }
        setEventTask(new LMSRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&c" + player.getName() + "&e was eliminated by &c" + killer.getName() + "&e!");
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
        }.runTaskAsynchronously(Practice.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                profile.refreshHotbar();
            }
        }.runTask(Practice.getInstance());
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

        player.teleport(Practice.getInstance().getLMSManager().getLmsSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(null);
        player.setFlying(false);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        Practice.getInstance().getEssentials().teleportToSpawn(player);
    }
}
