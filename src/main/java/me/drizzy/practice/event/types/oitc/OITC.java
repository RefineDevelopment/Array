package me.drizzy.practice.event.types.oitc;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.event.types.oitc.player.OITCPlayer;
import me.drizzy.practice.event.types.oitc.player.OITCPlayerState;
import me.drizzy.practice.event.types.oitc.task.OITCRoundEndTask;
import me.drizzy.practice.event.types.oitc.task.OITCRoundStartTask;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.Circle;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class OITC {

    protected static String EVENT_PREFIX =CC.translate("&8[&bOITC&8] &r");
    @Getter @Setter private Kit kit;
    private final String name;
    @Setter private OITCState state = OITCState.WAITING;
    private OITCTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, OITCPlayer> eventPlayers = new LinkedHashMap<>();
    @Getter private final List<UUID> spectators = new ArrayList<>();
    @Getter @Setter public static int maxPlayers;
    @Getter @Setter private int totalPlayers;
    @Setter private Cooldown cooldown;
    @Setter private long roundStart;
    @Getter @Setter private static boolean enabled = true;


    public OITC(Player player, Kit kit) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        maxPlayers = 100;
        this.kit = kit;
    }

    public List<String> getLore() {
        List<String> toReturn=new ArrayList<>();

        OITC OITC = Array.getInstance().getOITCManager().getActiveOITC();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&bHost: &r" + OITC.getName()));
        toReturn.add(CC.translate("&bKit: &r" + kit.getName()));

        if (OITC.isWaiting()) {
            toReturn.add("&bPlayers: &r" + OITC.getEventPlayers().size() + "/" + getMaxPlayers());
            toReturn.add("");

            if (OITC.getCooldown() == null) {
                toReturn.add(CC.translate("&fWaiting for players..."));
            } else {
                String remaining=TimeUtil.millisToSeconds(OITC.getCooldown().getRemaining());

                if (remaining.startsWith("-")) {
                    remaining="0.0";
                }

                toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
            }
        } else {
            toReturn.add("&bPlayers: &r" + OITC.getRemainingPlayers().size() + "/" + OITC.getTotalPlayers());
            toReturn.add("&bDuration: &r" + OITC.getRoundDuration());
        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

        public void setEventTask(OITCTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == OITCState.WAITING;
    }

    public boolean isFighting() {
        return state == OITCState.ROUND_FIGHTING;
    }

    public OITCPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for ( me.drizzy.practice.event.types.oitc.player.OITCPlayer OITCPlayer : eventPlayers.values()) {
            Player player = OITCPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for ( me.drizzy.practice.event.types.oitc.player.OITCPlayer OITCPlayer : eventPlayers.values()) {
            if (OITCPlayer.getState() == OITCPlayerState.WAITING) {
                Player player = OITCPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        if (this.eventPlayers.size() >= maxPlayers) {
            player.sendMessage(CC.RED + "The event is full");
            return;
        }

        eventPlayers.put(player.getUniqueId(), new OITCPlayer(player));

        broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bOITC Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
        player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bOITC Event&8!"));
        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setOITC(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(Array.getInstance().getOITCManager().getOITCSpectator());

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
        if (state != OITCState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == OITCState.WAITING) {
            broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bOITC Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
            player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bOITC Event&8!"));
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setOITC(null);
        profile.refreshHotbar();

        Essentials.teleportToSpawn(player);

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
        OITCPlayer loser = getEventPlayer(player);
        loser.setState(OITCPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public void end() {
        Array.getInstance().getOITCManager().setActiveOITC(null);
        Array.getInstance().getOITCManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The OITC event has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "OITC Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "OITC Event" + CC.GRAY + "!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "OITC Event" + CC.GRAY + "!");
        }

        for ( me.drizzy.practice.event.types.oitc.player.OITCPlayer OITCPlayer : eventPlayers.values()) {
            Player player = OITCPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setOITC(null);
            PlayerUtil.reset(player, false);
        profile.refreshHotbar();

                Essentials.teleportToSpawn(player);
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public boolean canEnd() {
        int remaining = 0;

        for ( me.drizzy.practice.event.types.oitc.player.OITCPlayer OITCPlayer : eventPlayers.values()) {
            if (OITCPlayer.getState() == OITCPlayerState.WAITING) {
                remaining++;
            }
        }

        return remaining == 1;
    }

    public Player getWinner() {
        for ( me.drizzy.practice.event.types.oitc.player.OITCPlayer OITCPlayer : eventPlayers.values()) {
            if (OITCPlayer.getState() != OITCPlayerState.ELIMINATED) {
                return OITCPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        List<String> strings=new ArrayList<>();
        strings.add(CC.translate(" "));
        strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
        strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&b&l[OITC Event]"));
        strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + ""));
        strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&fA &bOITC &fevent is being hosted by &b" + this.host.getUsername()));
        strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
        strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
        strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
        strings.add(CC.translate(" "));
        for ( String string : strings ) {
            Clickable message = new Clickable(string, "Click to join OITC event", "/oitc join");
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
        Profile.setKb(player, Array.getInstance().getOITCManager().getOITCKnockbackProfile());
    }
    public void onLeave(Player player) {
        Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);
    }

    public void onRound() {
        setState(OITCState.ROUND_STARTING);

        int i=0;
        for (Player player : this.getRemainingPlayers()) {
                Location midSpawn = Array.getInstance().getOITCManager().getOITCSpectator();
                List<Location> circleLocations = Circle.getCircle(midSpawn, 7, this.getPlayers().size());
                Location center = midSpawn.clone();
                Location loc = circleLocations.get(i);
                Location target = loc.setDirection(center.subtract(loc).toVector());
                player.teleport(target.add(0, 0.5, 0));
                circleLocations.remove(i);
                i++;
                player.getInventory().setContents(getKit().getKitInventory().getContents());
                player.getInventory().setArmorContents(getKit().getKitInventory().getArmor());
        }
        setEventTask(new OITCRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + killer.getName() + "&7!");
        }


        if (canEnd()) {
            setState(OITCState.ROUND_ENDING);
            setEventTask(new OITCRoundEndTask(this));
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
        if (getState() == OITCState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == OITCState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public boolean isFighting(Player player) {
        if (this.getState().equals(OITCState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setOITC(this);
        player.setFlying(true);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(Array.getInstance().getOITCManager().getOITCSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setOITC(null);
        player.setFlying(false);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        Essentials.teleportToSpawn(player);
    }
}
