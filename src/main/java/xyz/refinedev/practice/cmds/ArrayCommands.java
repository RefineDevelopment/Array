package xyz.refinedev.practice.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.listeners.GHeadListener;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.menu.WorldsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.command.annotation.Text;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.other.TaskUtil;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class ArrayCommands {
    
    private final Array plugin = Array.getInstance();
    private final String[] HELP_MESSAGE = {
            CC.CHAT_BAR,
            CC.translate("&cArray &7» Essential Commands"),
            CC.CHAT_BAR,
            CC.translate(" &7* &c/array setlobby &8(&7&oSets the lobby to player's location&8)"),
            CC.translate(" &7* &c/array reload &8(&7&oReload All Configurations&8)"),
            CC.translate(" &7* &c/array goldenhead &8(&7&oReceive a pre-made G-Head&8)"),
            CC.translate(" &7* &c/array refill &8(&7&oRefill your Inventory with potions or soup&8)"),
            CC.translate(" &7* &c/array update &8(&7&oSave and Update all leaderboards&8)"),
            CC.translate(" &7* &c/array hcf &8(&7&oHelp on how to setup HCF&8)"),
            CC.translate(" &7* &c/array worlds &8(&7&oShow a Worlds Menu&8)"),
            CC.translate(" &7* &c/array resetstats &8<&7name&8> &8(&7&oResets a profile&8)"),
            CC.translate(" &7* &c/array clearloadouts &8<&7kit|all&8> &8<&7global|name&8> &8(&7&oResets a profile&8)"),
            CC.translate(" &7* &c/array rename &8<&7name&8> &8(&7&oRenames item in hand&8)"),
            CC.translate(" &7* &c/array spawn &8(&7&oRefresh Profile & Teleport to spawn&8)"),
            CC.CHAT_BAR
    };
    private final String[] INFO_MESSAGE = {
            CC.CHAT_BAR,
            CC.translate("&fThis server is currently running &cArray &fv&c" + Description.getVersion()),
            CC.translate("&fDeveloped By &cRefine Development Team&7."),
            CC.translate(""),
            CC.translate("&7 * &cDiscord: &fhttps://dsc.gg/refine"),
            CC.translate("&7 * &cTwitter: &fhttps://twitter.com/RefineDev"),
            CC.translate("&7 * &cWebsite: &fhttps://www.refinedev.xyz"),
            CC.translate("&7 * &cContact: &frefinedevelopment@gmail.com"),
            CC.translate(""),
            CC.translate("&7&oYou can buy our products and issue commisions at our discord."),
            CC.CHAT_BAR
    };

    @Command(name = "", aliases = "help", desc = "View Array Commands")
    public void help(@Sender CommandSender sender) {
        if (sender.hasPermission("array.listeners.admin")) {
            sender.sendMessage(HELP_MESSAGE);
        } else {
            sender.sendMessage(INFO_MESSAGE);
        }
    }
    
    @Command(name = "hcf", aliases = {"teamfight","pvpclasses"}, desc = "View Help on how to setup HCF")
    @Require("array.listeners.admin")
    public void HCF(@Sender CommandSender player) {
        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            player.sendMessage(CC.translate("&c&lHow to Setup HCF "));
            player.sendMessage("");
            player.sendMessage(CC.translate("&7In order to setup HCF, first of make sure a Kit"));
            player.sendMessage(CC.translate("&7named &cHCFTeamFight &7is created, normal this kit will automatically"));
            player.sendMessage(CC.translate("&7create itself but if it doesn't make sure to use that correct"));
            player.sendMessage(CC.translate("&7capitalization to create it. To setup arenas for HCF, make a shared or standalone"));
            player.sendMessage(CC.translate("&7arena and add the kit HCFTeamFight to it. The PvP Classes are Built-In to the plugin,"));
            player.sendMessage(CC.translate("&7So you don't need to worry about setting them up as they will be automatically provided"));
            player.sendMessage(CC.CHAT_BAR);
        } else {
            player.sendMessage(CC.translate("&cHCFTeamFight has been disabled by an Admin."));
        }
    }
    
    @Command(name = "rename", desc = "Rename the Item you are currently holding")
    @Require("array.listeners.admin")
    public void rename(@Sender Player player, @Text String name) {
        if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold something in your hand.");
            return;
        }
        
        ItemStack hand = player.getItemInHand();
        ItemMeta meta = hand.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        hand.setItemMeta(meta);
        
        player.getInventory().setItemInHand(hand);
        player.updateInventory();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &aThe Item in your hand has been renamed!"));
    }
    
    @Command(name = "reload", aliases = "refresh", desc = "Reload Array's Configurations")
    @Require("array.listeners.admin")
    public void reload(@Sender CommandSender player) {
        plugin.getDivisionsConfig().reload();
        plugin.getArenasConfig().reload();
        plugin.getKitsConfig().reload();
        plugin.getMainConfig().reload();
        plugin.getTablistConfig().reload();
        plugin.getScoreboardConfig().reload();

        player.sendMessage(CC.translate("&8[&c&lArray&8] &aSuccessfully reloaded all configurations."));
    }

    @Command(name = "update", aliases = "save", desc = "Update and Save all data related to Array")
    @Require("array.listeners.admin")
    public void update(@Sender CommandSender player) {
        TaskUtil.runAsync(() -> {
            Profile.getProfiles().values().forEach(Profile::save);
            Profile.getProfiles().values().forEach(Profile::load);
            Kit.getKits().forEach(Kit::save);
            Arena.getArenas().forEach(Arena::save);

            plugin.getLeaderboardsManager().loadGlobalLeaderboards();
            Kit.getKits().forEach(plugin.getLeaderboardsManager()::loadKitLeaderboards);
        });
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7&oReloaded all Stats and Leaderboards!"));
    }

    @Command(name = "spawn", aliases = "reset", desc = "Reset your profile and Teleport to Spawn")
    @Require("array.listeners.admin")
    public void spawn(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        profile.teleportToSpawn();
    }

    @Command(name = "version", aliases = "ver", desc = "View Array's Build Version")
    public void version(@Sender CommandSender sender) {
        sender.sendMessage(INFO_MESSAGE);
    }

    @Command(name = "worlds", aliases = "world", desc = "Open a Worlds GUI to Teleport to Different Worlds")
    @Require("array.listeners.admin")
    public void worlds(@Sender Player player) {
        new WorldsMenu().openMenu(player);
    }

    @Command(name = "goldenhead", aliases = {"ghead", "goldh", "head"}, desc = "Receive a pre-made Golden Head")
    @Require("array.listeners.admin")
    public void goldenHead(@Sender Player player) {
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7You received a &cGolden head&7."));
        player.getInventory().addItem(GHeadListener.getGoldenHeadApple());
    }

    @Command(name = "refill", aliases = "cheat", desc = "Refill your Inventory with Soup or Potions Quitely")
    @Require("array.listeners.admin")
    public void refill(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (profile.isInFight()) {
            if (player.getInventory().contains(Material.POTION)) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(this.getPotion());
                }
            }
            if (player.getInventory().contains(Material.MUSHROOM_SOUP)) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(this.getSoup());
                }
            }
            player.sendMessage(CC.translate("&7Refilled ;)"));
        }
    }

    @Command(name = "resetstats", aliases = "clearstats", usage = "<target>", desc = "Reset a player's statistics")
    public void resetStats(@Sender Player player, Profile profile) {
        TaskUtil.runAsync(() -> {
            profile.getStatisticsData().values().forEach(stats -> {
                stats.setElo(1000);
                stats.setWon(0);
                stats.setLost(0);
            });
            profile.setGlobalElo(1000);
            profile.save();
        });

        player.sendMessage(CC.translate("&aSuccessfully wiped statistics of " + profile.getName() + "."));
        if (profile.getPlayer() != null) {
            profile.getPlayer().kickPlayer(CC.RED + "You were kicked because your profile was reset by an Admin!");
        }
    }

    @Command(name = "setlobby", aliases = "setspawn", desc = "Set your current location as the Lobby Spawn")
    public void setLobby(@Sender Player player) {
        plugin.getConfigHandler().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7You have set the &cnew &7lobby &cspawn &7!"));
    }












    /**
     * THIS IS THE WORST CODE I HAVE EVER WRITTEN IN THE PAST 10 MONTHS
     * THIS WILL BE RECODED, SORRY!
     */
    @Command(name = "clearloadouts", aliases = {"clearinventories", "clearkits"}, desc = "Clear Loadouts of a Certain Kit or All", usage = "<kit/all> <profile/global>")
    @Require("array.listeners.admin")
    public void clearLoadouts(@Sender CommandSender player, String type, String reach) {
        if (reach.equalsIgnoreCase("global")) {
            Kit kitType = Kit.getByName(type);
            if (kitType != null) {
                for ( Profile profile : Profile.getProfiles().values() ) {
                    for ( KitInventory kitInventory : profile.getStatisticsData().get(kitType).getLoadouts() ) {
                        profile.getStatisticsData().get(kitType).deleteKit(kitInventory);
                    }
                    profile.save();
                    if (profile.getPlayer().isOnline()) {
                        profile.getPlayer().kickPlayer("Please re-log due to your kit loadouts being reset by an Admin.");
                    }
                }
            } else {
                if (type.equalsIgnoreCase("all")) {
                    for ( Kit kit : Kit.getKits() ) {
                        for ( Profile profile : Profile.getProfiles().values() ) {
                            for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                                profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                            }
                            profile.save();
                            if (profile.getPlayer().isOnline()) {
                                profile.getPlayer().kickPlayer("Please re-log due to your kit loadouts being reset by an Admin.");
                            }
                        }
                    }
                } else {
                    player.sendMessage(CC.translate("&7Invalid Type!"));
                    return;
                }
            }
            player.sendMessage(CC.translate("&8[&cArray&8] &7Succesfully deleted kit loadouts for &cEveryone!"));
        } else if (Bukkit.getPlayer(reach) == null || !Bukkit.getPlayer(reach).isOnline()) {
            player.sendMessage(CC.translate("&8[&cArray&8] &7That player is offline or does not exist."));
        } else {
            Kit kitType = Kit.getByName(type);
            if (kitType != null) {
                Player target = Bukkit.getPlayer(reach);
                Profile profile = Profile.getByPlayer(target);
                for ( KitInventory kitInventory : profile.getStatisticsData().get(kitType).getLoadouts() ) {
                    profile.getStatisticsData().get(kitType).deleteKit(kitInventory);
                }
                profile.save();
                player.sendMessage(CC.translate("&8[&cArray&8] &7Successfully deleted kitloadouts for &c" + reach));
            } else {
                if (type.equalsIgnoreCase("all")) {
                    for ( Kit kit : Kit.getKits() ) {
                        Player target = Bukkit.getPlayer(reach);
                        Profile profile = Profile.getByPlayer(target);
                        for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                            profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                        }
                        profile.save();
                    }
                } else {
                    player.sendMessage(CC.translate("&7Invalid Type!"));
                }
            }
        }
    }


    public ItemStack getPotion() {
        return new ItemBuilder(Material.POTION).durability(16421).build();
    }

    public ItemStack getSoup() {
        return new ItemBuilder(Material.MUSHROOM_SOUP).build();
    }
}