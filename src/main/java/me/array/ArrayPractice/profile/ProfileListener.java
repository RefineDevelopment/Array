package me.array.ArrayPractice.profile;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.events.MatchEvent;
import me.array.ArrayPractice.match.events.MatchStartEvent;
import me.array.ArrayPractice.profile.meta.option.button.AllowSpectatorsOptionButton;
import me.array.ArrayPractice.profile.meta.option.button.DuelRequestsOptionButton;
import me.array.ArrayPractice.profile.meta.option.button.ShowScoreboardOptionButton;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.TaskUtil;
import me.array.ArrayPractice.util.essentials.event.SpawnTeleportEvent;
import me.array.ArrayPractice.util.external.profile.option.event.OptionsOpenedEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.List;

public class ProfileListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMatchStart(MatchEvent e) {
        if (e instanceof MatchStartEvent) {
            Match match = e.getMatch();
            Bukkit.getScheduler().runTaskLaterAsynchronously(Practice.getInstance(), () -> match.getPlayers().forEach(player -> {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                List<Player> followers = profile.getFollower();
                for (Player follower : followers) {
                    if (follower != null) {
                        follower.chat("/spec " + profile.getName());
                    }
                }
            }), 20L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getClickedBlock() == null) {
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem().getType() == Material.PAINTING) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().isOp())
                        e.setCancelled(true);
                }
            }

            if (e.getClickedBlock().getState() instanceof ItemFrame) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().isOp()) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if (e.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (sign.getLine(1) != null && sign.getLine(1).contains("[Click Here]")) {
                    if (sign.getLine(2).toLowerCase().contains("back to spawn")) {
                        Practice.getInstance().getEssentials().teleportToSpawn(e.getPlayer());
                    }
                }
            }

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
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!(profile.isInSomeSortOfFight())) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInSkyWars() && !profile.isInFight() && !profile.isInSpleef()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInSkyWars() && !profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInSkyWars() && !profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
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
                    Practice.getInstance().getEssentials().teleportToSpawn((Player) event.getEntity());
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

            Practice.getInstance().getEssentials().teleportToSpawn(p);

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
                Player target = Practice.getInstance().getServer().getPlayer(profile.getRematchData().getTarget());

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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        List<String> completions = (List<String>) event.getTabCompletions();
        completions.clear();
        String token = event.getLastToken();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (StringUtil.startsWithIgnoreCase(p.getName(), token)) {
                completions.add(p.getName());
            }
        }
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC().getEntity().getType() == EntityType.ZOMBIE) {
            TaskUtil.runAsync(() -> {
                Profile profile = Profile.getByUuid(event.getClicker());

                ItemStack[] items = PlayerUtil.getNextSet(profile.getPlayer());

                ItemStack hand = items[0];
                ItemStack helm = items[1];
                ItemStack chest = items[2];
                ItemStack legs = items[3];
                ItemStack boot = items[4];

                PacketPlayOutEntityEquipment helmP = new PacketPlayOutEntityEquipment(event.getNPC().getEntity().getEntityId(), 4, CraftItemStack.asNMSCopy(helm));
                PacketPlayOutEntityEquipment chestP = new PacketPlayOutEntityEquipment(event.getNPC().getEntity().getEntityId(), 3, CraftItemStack.asNMSCopy(chest));
                PacketPlayOutEntityEquipment legsP = new PacketPlayOutEntityEquipment(event.getNPC().getEntity().getEntityId(), 2, CraftItemStack.asNMSCopy(legs));
                PacketPlayOutEntityEquipment bootP = new PacketPlayOutEntityEquipment(event.getNPC().getEntity().getEntityId(), 1, CraftItemStack.asNMSCopy(boot));
                PacketPlayOutEntityEquipment swordP = new PacketPlayOutEntityEquipment(event.getNPC().getEntity().getEntityId(), 0, CraftItemStack.asNMSCopy(hand));

                ((CraftPlayer) event.getClicker()).getHandle().playerConnection.sendPacket(helmP);
                ((CraftPlayer) event.getClicker()).getHandle().playerConnection.sendPacket(chestP);
                ((CraftPlayer) event.getClicker()).getHandle().playerConnection.sendPacket(legsP);
                ((CraftPlayer) event.getClicker()).getHandle().playerConnection.sendPacket(bootP);
                ((CraftPlayer) event.getClicker()).getHandle().playerConnection.sendPacket(swordP);

            });

            event.getClicker().playSound(event.getNPC().getEntity().getLocation(), Sound.IRONGOLEM_HIT, 10.0F, 0.5F);
        }
    }

}
