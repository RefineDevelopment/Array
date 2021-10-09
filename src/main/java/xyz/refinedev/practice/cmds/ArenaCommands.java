package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.impl.SharedArena;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.arena.impl.BridgeArena;
import xyz.refinedev.practice.arena.rating.Rating;
import xyz.refinedev.practice.arena.runnables.StandalonePasteRunnable;
import xyz.refinedev.practice.arena.runnables.TheBridgePasteRunnable;
import xyz.refinedev.practice.arena.selection.Selection;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.other.TaskUtil;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/21/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ArenaCommands {

    private final Array plugin;

    @Command(name = "", aliases = "help", desc = "View Arena Commands")
    @Require("array.arena.admin")
    public void help(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Arena Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &7* &c/arena create &8<&7name&8> &8<&7Shared|Standalone|TheBridge&8> &8(&7&oCreate an Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena remove &8<&7name&8> &8(&7&oDelete an Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena save &8(&7&oSave Arenas&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena wand &8(&7&oReceive a wand to select the cuboids for Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena pearls &8(&7&oEnable or Disable the ability for players to pearl on the arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena kitlist &8<&7arena&8> &8(&7&oLists all the kits of an arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena seticon &8<&7arena&8> &8(&7&oSets the item your holding as Arena Icon&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setcuboid &8<&7red|blue&8> &8(&7&oSets red/blue cuboid from your selection&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setportal &8<&7red|blue&8> &8(&7&oSets red/blue portal from your selection&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setbridgespawn &8<&7red|blue&8> &8(&7&oSet red/blue spawn of arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setduplicatespawn &8<&7arena&8> &8<&71/2&8> &8(&7&oSet red/blue spawn of arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setspawn &8<&7arena&8> &8<&71/2&8> &8<&7duplicate-id&8> &8(&7&oSet 1/2 spawn of a duplicate arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setmax &8(&7&oSet Max Location of a Standalone Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setmin &8(&7&oSet Min Location of a Standalone Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena setfalldeathheight &8<&7int&8> &8(&7&oSet Fall Death Height of the Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena generate &8<&7arena&8> &8<&7Amount&8> &8(&7&oCopy and paste Standalone Arenas automatically&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena addkit &8<&7arena&8> &8<&7kit&8> &8(&7&oAdd a kit to the arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena addnormalkits &8<&7arena&8> &8<&7kit&8> &8(&7&oAdd all the normal kits to the arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena addbuildkits &8<&7arena&8> &8<&7kit&8> &8(&7&oAdd all the build kits to the arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/arena removekit &8<&7arena&8> <&7Kit&8> &8(&7&oRemove a kit from the arena&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "create", desc = "Create an Arena According to its Type", usage = "<name> <shared|standalone|bridge|duplicate>")
    @Require("array.arena.admin")
    public void arenaCreate(@Sender Player player, String name, ArenaType type) {
        if (name == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please provide a name."));
            return;
        }

        Arena duplicate = plugin.getArenaManager().getByName(name);
        if (duplicate != null && plugin.getArenaManager().getArenas().contains(duplicate) && type.equals(ArenaType.DUPLICATE)) {
            if (duplicate.getType() != ArenaType.STANDALONE) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7You can't convert a Shared arena to a duped one."));
                return;
            }

            StandaloneArena standaloneArena = new StandaloneArena(plugin, name);
            StandaloneArena sarena = (StandaloneArena) duplicate;
            sarena.getDuplicates().add(standaloneArena);

            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Saved a duplicate arena from &c" + name + "&8(&7#&c" + sarena.getDuplicates().size() + "&8)"));
            player.sendMessage(CC.translate("&8[&cTIP&8] &7Please note the &cDuplicate ID&7 of the arena for later use to setup its spawn points. " + "&8(&7#&c" + sarena.getDuplicates().size() + "&8)"));
        } else {
            if (plugin.getArenaManager().getArenas().contains(duplicate)) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name already exists!"));
                return;
            }

            Arena arena;
            switch (type) {
                case SHARED:
                    arena = new SharedArena(plugin, name);
                    break;
                case BRIDGE:
                    arena = new BridgeArena(plugin, name);
                    break;
                default:
                    arena = new StandaloneArena(plugin, name);
            }
            arena.setActive(false);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully created an Arena called &c" + name + "&7 of type &c" + type));
        }
        plugin.getArenaManager().getArenas().forEach(Arena::save);
    }

    @Command(name = "", usage = "<arena> <int>",desc = "Set an arena's fall death height")
    public void fallDeathHeight(@Sender CommandSender sender, Arena arena, int amount) {
        arena.setDeathHeight(amount);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set &c" + arena.getDisplayName() + "'s &7fall death height to &c" + amount + "&7."));
        sender.sendMessage(CC.translate("&8[&cTIP&8] &7&oPlease bare in mind, this amount is subtracted from the y-level of your spawn 1 to get the y level for death height."));
    }

    @Command(name = "save", aliases = "export", desc = "Save Arenas to Config")
    @Require("array.arena.admin")
    public void arenaSave(@Sender CommandSender sender) {
        plugin.getArenaManager().getArenas().forEach(Arena::save);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully saved &c" + plugin.getArenaManager().getArenas().size() + " &7arenas!"));
    }

    @Command(name = "remove", aliases = "delete", desc = "Remove an Arena", usage = "<arena>")
    @Require("array.arena.admin")
    public void arenaRemove(@Sender CommandSender player, Arena arena) {
        if (arena != null) {
            if (arena.isActive()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is currently active, please try again later!"));
                return;
            }

            plugin.getArenaManager().delete(arena);
            plugin.getArenaManager().getArenas().remove(arena);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully removed the arena &c" + arena.getDisplayName()));
        }
    }

    @Command(name = "generate", aliases = "copy", usage = "<arena> <amount>", desc = "Generate/Copy a standalone Arena")
    @Require("array.arena.admin")
    public void arenaGenerate(@Sender CommandSender player, Arena arena, int amount) {

        if (!Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit") && !Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ) {
            player.sendMessage(CC.translate("&7World Edit or FAWE not found, Arena Generating will not work!"));
            return;
        }

        if (amount > 15) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That amount is too high, you can only place &c15 &7arenas at a time due to performance issues."));
            return;
        }

        if (plugin.getArenaManager().isPasting()) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7The grid is already pasting arenas, please wait!"));
            return;
        }

        if (arena.getType() == ArenaType.SHARED || arena.isDuplicate() || arena.getType() == ArenaType.BRIDGE) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7You can't paste that type of Arena!"));
            return;
        }

        plugin.getArenaManager().setPasting(true);

        if (arena.getType() == ArenaType.BRIDGE) {
            TaskUtil.run(new TheBridgePasteRunnable(plugin, (BridgeArena) arena, amount));
        } else {
            TaskUtil.run(new StandalonePasteRunnable(plugin, (StandaloneArena) arena, amount));
        }

        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Pasting...."));
        plugin.getArenaManager().getArenas().forEach(Arena::save);
    }

    @Command(name = "reload", aliases = "refresh", desc = "Reload arenas")
    @Require("array.arena.admin")
    public void arenaReload(@Sender CommandSender player) {
        long st = System.currentTimeMillis();

        Match.getMatches().forEach(Match::cleanup);
        plugin.getArenaManager().getArenas().clear();
        plugin.getArenaManager().init();

        long et = System.currentTimeMillis();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Arenas were reloaded in &c" + (et - st) + " ms&7."));
    }

    @Command(name = "list", desc = "Arena List Command")
    @Require("array.arena.admin")
    public void arenaList(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» All Arenas"));
        player.sendMessage(CC.CHAT_BAR);

        if (plugin.getArenaManager().getArenas().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.ITALIC + "There are no arenas setup.");
            player.sendMessage("");
            player.sendMessage(CC.CHAT_BAR);
            return;
        }

        for (Arena arena : plugin.getArenaManager().getArenas()) {

            String type;
            switch (arena.getType()) {
                case STANDALONE:
                    type = "Standalone";
                    break;
                case BRIDGE:
                    type = "TheBridge";
                    break;
                default:
                    type = "Shared";
            }

            if (arena.isDuplicate()) continue;

            if (arena.getType().equals(ArenaType.STANDALONE)) {
                StandaloneArena standaloneArena = (StandaloneArena) arena;
                player.sendMessage(CC.DARK_GRAY + " • " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + " (" + standaloneArena.getDuplicates().size() + ") " + CC.translate((arena.isActive() ? " &8[&eIn-Match&8]" : " &8[&aFree&8]") + " &8[&7" + type + "&8]"));
            } else {
                player.sendMessage(CC.DARK_GRAY + " • " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.translate((arena.isActive() ? " &8[&eIn-Match&8]" : " &8[&aFree&8]") + " &8[&7" + type + "&8]"));
            }
        }
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "disablepearls", aliases = "pearls", desc = "Disable pearling in an arena", usage = "<arena>")
    @Require("array.arena.admin")
    public void arenaDisablePearls(@Sender CommandSender player, Arena arena) {
        arena.setDisablePearls(!arena.isDisablePearls());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (arena.isDisablePearls() ?  "&cdisabled" : "&aenabled") + " &7pearls in the arena &c" + arena.getName() + "&7."));
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to an Arena", usage = "<arena>")
    @Require("array.arena.admin")
    public void arenaTeleport(@Sender Player player, Arena arena) {
        player.teleport(arena.getSpawn1());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully &cteleported &7to the arena &c" + arena.getName() + "&7!"));

    }

    @Command(name = "rating", aliases = "poll", desc = "View Ratings of an Arena from Survey", usage = "<arena>")
    @Require("array.arena.admin")
    public void arenaRating(@Sender CommandSender sender, Arena arena) {
        Rating rating = arena.getRating();
        int total = rating.getAverage() + rating.getGood() + rating.getDecent() + rating.getOkay() + rating.getAverage() + rating.getTerrible();

        String terrible = plugin.getArenaManager().getBar(rating.getTerrible(), total);
        String average = plugin.getArenaManager().getBar(rating.getAverage(), total);
        String okay = plugin.getArenaManager().getBar(rating.getOkay(), total);
        String decent = plugin.getArenaManager().getBar(rating.getDecent(), total);
        String good = plugin.getArenaManager().getBar(rating.getGood(), total);

        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&cArray &7» " + arena.getDisplayName() + "'s Ratings"));
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage("");
        sender.sendMessage(CC.translate("&7There have been &c" + total + " &7ratings of arena &c" + arena.getDisplayName() + "&7."));
        sender.sendMessage(CC.translate("&7These are small survey graph bars of our ratings analysis, these can help you decide"));
        sender.sendMessage(CC.translate("&7 which arena was favoured by your community and playerbase in general."));
        sender.sendMessage("");
        sender.sendMessage(CC.translate("&4Terrible: &8[&r" + terrible + "&8]"));
        sender.sendMessage(CC.translate("&6Okay: &8[&r" + okay + "&8]"));
        sender.sendMessage(CC.translate("&eAverage: &8[&r" + average + "&8]"));
        sender.sendMessage(CC.translate("&2Decent: &8[&r" + decent + "&8]"));
        sender.sendMessage(CC.translate("&aGood: &8[&r" + good + "&8]"));
        sender.sendMessage(CC.CHAT_BAR);
    }


    @Command(name = "wand", aliases = {"portalwand", "selection", "portal"}, desc = "Receive Arena selection wand")
    @Require("array.arena.setup")
    public void arenaWand(@Sender Player player) {
        if (player.getInventory().first(Selection.SELECTION_WAND) != -1) {
            player.getInventory().remove(Selection.SELECTION_WAND);
        } else {
            player.getInventory().addItem(Selection.SELECTION_WAND);
            player.sendMessage(CC.translate("&8[&cTIP&8] &7&oLeft-Click to select first position and Right-Click to select second position."));
            player.sendMessage(CC.translate("&7&oTo setup the cuboids, please select one position as the lower corner and one position as upper corner."));
        }

        player.updateInventory();
    }

    @Command(name = "setspawn", desc = "Set an arena's spawn", usage = "<arena> <1/2>")
    @Require("array.arena.setup")
    public void arenaSpawn(@Sender Player player, Arena arena, int pos) {
        
        if (arena.getType() != ArenaType.BRIDGE) {
            Location loc=player.getLocation().clone();

            if (pos == 1) {
                arena.setSpawn1(loc);
            } else if (pos == 2) {
                arena.setSpawn2(loc);
            } else {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Invalid Position."));
                return;
            }
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the position of &c" + arena.getName() + "&8&o (&7&oPosition: " + pos + "&8)"));
            arena.save();

        } else {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please use the command /arena setbridgespawn to set the spawn for a bridge arena."));
        }
    }

    @Command(name = "setbridgespawn", desc = "Set a bridge arena's spawn", usage = "<arena> <red/blue>")
    @Require("array.arena.setup")
    public void arenaBridgeSpawn(@Sender Player player, Arena arena, String pos) {
        
        if (arena.getType() == ArenaType.BRIDGE) {
            Location loc = player.getLocation().clone();

            if (pos.equals("red")) {
                arena.setSpawn1(loc);
            } else if (pos.equals("blue")) {
                arena.setSpawn2(loc);
            } else {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Invalid argument."));
                return;
            }
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the position of &c" + arena.getName() + "&8&o (&7&oPosition: " + pos + "&8)"));
            arena.save();

        } else {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please use the command /arena setspawn to set the spawn for a non-bridge arena."));
        }
    }

    @Command(name = "setduplicatespawn", usage = "<arena> <1/2> <duplicate-id>", desc = "Set a duplicate arena's spawn")
    @Require("array.arena.setup")
    public void arenaDuplicateSpawn(@Sender Player player, Arena arena, int pos, int number) {
                final Arena darena = ((StandaloneArena)arena).getDuplicates().get(number - 2);
        if (darena != null) {
            Location loc = player.getLocation().clone();
            if (pos == 1) {
                darena.setSpawn1(loc);
            }
            else if (pos == 2) {
                darena.setSpawn2(loc);
            }
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the position of &c" + arena.getName() + "&8&o (&7&oPosition: " + pos + "&8) (&7&oDupe Arena #" + (number - 1) + "&8)"));
            arena.save();
        }
    }

    @Command(name = "setcuboid", usage = "<arena> <blue/red>", desc = "Set a bridge arena's team cuboid")
    @Require("array.arena.setup")
    public void arenaCuboid(@Sender Player player, Arena arena, String color) {
        if (!color.equals("blue") && !color.equals("red")) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That is an invalid team."));
            return;
        }

        if (arena.getType() != ArenaType.BRIDGE) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is not a &cTheBridge &7arena."));
            return;
        }

        BridgeArena bridgeArena = (BridgeArena) arena;

        if (color.equalsIgnoreCase("blue")) {
            Selection selection = Selection.createOrGetSelection(player);
            if (!selection.isFullObject()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
                return;
            }
            bridgeArena.setBlueCuboid(selection.getCuboid());
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cBlue Cuboid&7!"));
        }

        if (color.equalsIgnoreCase("red")) {
            Selection selection = Selection.createOrGetSelection(player);
            if (!selection.isFullObject()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
                return;
            }
            bridgeArena.setRedCuboid(selection.getCuboid());
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cRed Cuboid&7!"));
        }
    }

    @Command(name = "setportal", usage = "<arena> <blue/red>", desc = "Set a bridge arena's team portal")
    @Require("array.arena.setup")
    public void arenaBridgePortal(@Sender Player player, Arena arena,  String color) {
        if (!color.equals("blue") && !color.equals("red")) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That is an invalid team."));
            return;
        }

        if (arena.getType() != ArenaType.BRIDGE) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is not a &cTheBridge &7arena."));
            return;
        }

        BridgeArena bridgeArena = (BridgeArena) arena;

        if (color.equalsIgnoreCase("blue")) {
            Selection selection = Selection.createOrGetSelection(player);
            if (!selection.isFullObject()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
                return;
            }
            bridgeArena.setBluePortal(selection.getCuboid());
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cBlue Portal&7!"));
        }
        if (color.equalsIgnoreCase("red")) {
            Selection selection = Selection.createOrGetSelection(player);
            if (!selection.isFullObject()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
                return;
            }
            bridgeArena.setRedPortal(selection.getCuboid());
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cRed Portal&7!"));
        }
    }

    @Command(name = "setmax", usage = "<arena>", desc = "Set an arena's maximum position")
    @Require("array.arena.setup")
    public void arenaMax(@Sender Player player, Arena arena) {
        arena.setMax(player.getLocation().clone());
        arena.save();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cMax &7Position for the arena &c" + arena.getDisplayName() + "&7!"));
    }

    @Command(name = "setmin", usage = "<arena>", desc = "Set an arena's minimum position")
    @Require("array.arena.setup")
    public void arenaMin(@Sender Player player, Arena arena) {
        arena.setMin(player.getLocation().clone());
        arena.save();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cMin &7Position for the arena &c" + arena.getDisplayName() + "&7!"));
    }

    @Command(name = "seticon", usage = "<arena>", desc = "Set an arena's icon")
    @Require("array.arena.setup")
    public void arenaIcon(@Sender Player player, Arena arena) {
        ItemStack item = player.getItemInHand();

        if (item == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please hold a valid item in your hand!"));
        } else if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
        } else {
            arena.setDisplayIcon(item);
            arena.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &carena icon &7to the &citem&7 in your hand."));
        }
    }

    @Command(name = "setdisplayname", usage = "<arena> <displayname>", desc = "Set an arena's display name")
    @Require("array.arena.setup")
    public void arenaDisplayname(@Sender Player player, Arena arena, String displayname) {
        arena.setDisplayName(displayname);
        arena.save();
        player.sendMessage(CC.translate("&8[&cArray&8] &7Successfully updated the arena &c" + arena.getName() + "'s &7display name."));
    }

    @Command(name = "addkit", aliases = "kits add", usage = "<arena> <kit>", desc = "Add a kit to an arena")
    @Require("array.arena.kit")
    public void arenaKit(@Sender CommandSender player, Arena arena, Kit kit) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Arena does not exist"));
            return;
        }

        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Kit does not exist"));
            return;
        }

        if (arena.getType() == ArenaType.SHARED && kit.getGameRules().isBuild()) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7The arena is set to type shared and you can't add build kits to it!"));
            return;
        }

        if (!arena.getKits().contains(kit)) {
            arena.getKits().add(kit);
        }

        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully added the kit &c" + kit.getName() + "&7 to &c" + arena.getName()));
        arena.save();
    }

    @Command(name = "addbuildkits", aliases = "kits addbuild", usage = "<arena>", desc = "Add build kits to an arena")
    @Require("array.arena.kit")
    public void arenaBuildKit(@Sender CommandSender player, Arena arena) {
        for ( Kit kit : plugin.getKitManager().getKits() ) {
            if (kit == null) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7There are no kits setup."));
                return;
            }
            if (kit.getGameRules().isBuild()) {
                if (!arena.getKits().contains(kit)) {
                    arena.getKits().add(kit);
                }
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully added the kit &c" + kit.getName() + "&7 to &c" + arena.getName()));
            }
        }
        arena.save();
    }

    @Command(name = "addnormalkits", aliases = "kits addnormal", usage = "<arena>", desc = "Add normal kits to an arena")
    @Require("array.arena.kit")
    public void arenaNormalKit(@Sender CommandSender player, Arena arena) {

        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Arena does not exist."));
            return;
        }

        for ( Kit kit : plugin.getKitManager().getKits() ) {
            if (kit == null) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7There are no kits."));
                return;
            }
            if (kit.getGameRules().isBuild() || kit.getGameRules().isBoxuhc() || kit.getGameRules().isSpleef() || kit.getGameRules().isSumo() || kit.getGameRules().isParkour() || kit.getGameRules().isWaterKill()) {
                return;
            }

            if (!arena.getKits().contains(kit.getName())) {
                arena.getKits().add(kit);
            }

            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully added the kit &c" + kit.getName() + "&7 to &c" + arena.getName() + "&7."));
        }
        arena.save();

    }

    @Command(name = "removekit", aliases = {"deletekit", "wipekit"}, usage = "<arena> <kit>", desc = "Remove a kit from an Arena")
    @Require("array.arena.kit")
    public void arenaRemoveKit(@Sender CommandSender player, Arena arena, Kit kit) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist"));
            return;
        }

        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7A kit with that name does not exist."));
            return;
        }

        if (arena.getKits().contains(kit.getName())) {
            arena.getKits().remove(kit.getName());

            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully removed the kit &c" + kit.getName() + " &7from &c" + arena.getName()));
            arena.save();
        }
    }

    @Command(name = "kitlist", aliases = {"listkits", "kits"}, usage = "<arena>", desc = "View an Arena's kits")
    @Require("array.arena.kit")
    public void arenaKitList(@Sender CommandSender player, Arena arena) {

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» " + arena.getName() + "'s kits"));
        player.sendMessage(CC.CHAT_BAR);

        if (arena.getKits().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.ITALIC + "There are no kits for this arena.");
            player.sendMessage("");
            return;
        }

        for ( Kit kit : arena.getKits() ) {
            player.sendMessage(CC.GRAY + " • " + kit.getName());
        }

        player.sendMessage(CC.CHAT_BAR);
    }


}
