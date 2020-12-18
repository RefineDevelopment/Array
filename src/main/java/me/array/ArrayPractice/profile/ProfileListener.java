package me.array.ArrayPractice.profile;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.TaskUtil;
import me.array.ArrayPractice.profile.meta.option.button.AllowSpectatorsOptionButton;
import me.array.ArrayPractice.profile.meta.option.button.DuelRequestsOptionButton;
import me.array.ArrayPractice.profile.meta.option.button.ShowScoreboardOptionButton;
import me.array.ArrayPractice.util.essentials.event.SpawnTeleportEvent;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.profile.option.event.OptionsOpenedEvent;
import net.mineaus.lunar.LunarClientAPI;
import net.mineaus.lunar.event.impl.AuthenticateEvent;
import net.mineaus.lunar.util.type.StaffModule;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class ProfileListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p=e.getPlayer();
        if (p.hasPermission("practice.color")) {
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
        Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isBusy(event.getPlayer()) && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            profile.refreshHotbar();
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getState() == ProfileState.IN_LOBBY) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }



    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!(profile.isInSomeSortOfFight())) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight() && !profile.isInSpleef()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInLobby()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    Array.get().getEssentials().teleportToSpawn((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onOptionsOpenedEvent(OptionsOpenedEvent event) {
        event.getButtons().add(new ShowScoreboardOptionButton());
        event.getButtons().add(new AllowSpectatorsOptionButton());
        event.getButtons().add(new DuelRequestsOptionButton());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().performCommand("help");
        event.getPlayer().performCommand("spawn");
        TaskUtil.runAsync(() -> {
            Player p = event.getPlayer();
            Profile profile = new Profile(p.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {
                e.printStackTrace();
                event.getPlayer().kickPlayer("Failed to load your profile");
                return;
            }

            Profile.getProfiles().put(p.getUniqueId(), profile);

            profile.setName(p.getName());

            Array.get().getEssentials().teleportToSpawn(p);

            profile.refreshHotbar();
            profile.handleVisibility();

            for (Profile otherProfile : Profile.getProfiles().values()) {
                otherProfile.handleVisibility(otherProfile.getPlayer(), p);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        TaskUtil.runAsync(() -> {
            Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

            profile.save();

            if (profile.getRematchData() != null) {
                Player target = Array.get().getServer().getPlayer(profile.getRematchData().getTarget());

                if (target != null && target.isOnline()) {
                    Profile.getByUuid(target.getUniqueId()).checkForHotbarUpdate();
                }
            }
        });
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressurePlate(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAuth(AuthenticateEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("practice.staff")) {
            try {
                LunarClientAPI.getInstance().sendTitle(player, false, CC.AQUA + "MoonNight " + CC.AQUA + "Practice", 1f, 6, 3, 3);
                LunarClientAPI.getInstance().sendTitle(player, true, CC.GREEN + "LCAPI " + CC.AQUA + "Authenticated!", 1f, 6, 3, 3);
            } catch (IOException e) {
                //ignore
            }
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                if(player.isOp()) {
                    try {
                        LunarClientAPI.getInstance().toggleStaffModule(player, StaffModule.BUNNY_HOP, true);
                        LunarClientAPI.getInstance().toggleStaffModule(player, StaffModule.NAME_TAGS, true);
                        LunarClientAPI.getInstance().toggleStaffModule(player, StaffModule.XRAY, true);
                    } catch (Exception e){
                        //ignore
                    }
                }
            }
        }.runTaskLater(Array.get(), 20L);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLogin(org.bukkit.event.player.AsyncPlayerPreLoginEvent event){
        if (!Bukkit.getServer().hasWhitelist()){
            return;
        }

        for (OfflinePlayer player : Bukkit.getServer().getWhitelistedPlayers()){
            if (player.hasPlayedBefore()) {
                if (player.getUniqueId().equals(event.getUniqueId())) {
                    return;
                }
            } else {
                if (player.getName().equals(event.getName())) {
                    return;
                }
            }
        }
        event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatColor.WHITE + "You are not whitelisted.");
    }
}
