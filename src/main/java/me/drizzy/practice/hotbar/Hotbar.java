package me.drizzy.practice.hotbar;

import java.util.HashMap;

import me.drizzy.practice.Array;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class Hotbar
{
    private static Map<HotbarType, ItemStack> items;
    
    public Hotbar() {
        preload();
    }
    
    public static void preload() {
        Hotbar.items.put(HotbarType.QUEUE_JOIN_UNRANKED, new ItemBuilder(Material.IRON_SWORD).name(CC.AQUA + "Join Unranked Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.QUEUE_JOIN_RANKED, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.AQUA + "Join Ranked Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.QUEUE_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_EVENTS, new ItemBuilder(Material.DIAMOND_AXE).name(CC.AQUA + "Party Events" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_CREATE, new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Create Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_DISBAND, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Disband Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_SETTINGS, new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Party Settings" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARTY_INFO, new ItemBuilder(Material.PAPER).name(CC.AQUA + "Party Information" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.OTHER_PARTIES, new ItemBuilder(Material.REDSTONE_TORCH_ON).name(CC.AQUA + "Duel Other Parties" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.LEADERBOARDS_MENU, new ItemBuilder(Material.EMERALD).name(CC.AQUA + "Main Menu" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.SETTINGS_MENU, new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Settings" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.KIT_EDITOR, new ItemBuilder(Material.BOOK).name(CC.AQUA + "Kit Editor" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.SPECTATE_STOP, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.AQUA + CC.BOLD + "Stop Spectating" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.SPLEEF_MATCH, new ItemBuilder(Material.DIAMOND_SPADE).name(CC.AQUA + "Spleef Shovel" + CC.GRAY + " (Left-Click)").build());
        Hotbar.items.put(HotbarType.VIEW_INVENTORY, new ItemBuilder(Material.BOOK).name(CC.AQUA + "View Inventory" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.EVENT_JOIN, new ItemBuilder(Material.NETHER_STAR).name(CC.AQUA + "Join Event" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.SUMO_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Sumo" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.GULAG_GUN, new ItemBuilder(Material.DIAMOND_HOE).name(CC.AQUA + "Glock 19" + CC.GRAY + " (Loaded)").build());
        Hotbar.items.put(HotbarType.GULAG_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Gulag" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.BRACKETS_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Brackets" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.LMS_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave LMS" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARKOUR_SPAWN, new ItemBuilder(Material.ARROW).name(CC.GREEN + "Back to Checkpoint" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.PARKOUR_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Parkour" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.SPLEEF_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Spleef" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.REMATCH_REQUEST, new ItemBuilder(Material.BLAZE_POWDER).name(CC.AQUA + "Request Rematch" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.REMATCH_ACCEPT, new ItemBuilder(Material.DIAMOND).name(CC.AQUA + "Accept Rematch" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.OITC_KIT, new ItemBuilder(Material.BLAZE_POWDER).name(CC.AQUA + "OITC Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.DEFAULT_KIT, new ItemBuilder(Material.BOOK).name(CC.AQUA + "Default Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.DIAMOND_KIT, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.AQUA + "Diamond Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.BARD_KIT, new ItemBuilder(Material.BLAZE_POWDER).name(CC.AQUA + "Bard Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.ROGUE_KIT, new ItemBuilder(Material.GOLD_SWORD).name(CC.AQUA + "Rogue Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarType.ARCHER_KIT, new ItemBuilder(Material.BOW).name(CC.AQUA + "Archer Kit" + CC.GRAY + " (Right-Click)").build());
    }
    
    public static ItemStack[] getLayout(final HotbarLayout layout, final Profile profile) {
        final ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);
        switch (layout) {
            case LOBBY: {
                if (profile.getParty() == null) {
                    final boolean activeEvent = (Array.getInstance().getSumoManager().getActiveSumo() != null && Array.getInstance().getSumoManager().getActiveSumo().isWaiting()) || (Array.getInstance().getBracketsManager().getActiveBrackets() != null && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting()) || (Array.getInstance().getLMSManager().getActiveLMS() != null && Array.getInstance().getLMSManager().getActiveLMS().isWaiting()) || (Array.getInstance().getParkourManager().getActiveParkour() != null && Array.getInstance().getParkourManager().getActiveParkour().isWaiting()) || (Array.getInstance().getSpleefManager().getActiveSpleef() != null && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting());
                    toReturn[0] = Hotbar.items.get(HotbarType.QUEUE_JOIN_UNRANKED);
                    toReturn[1] = Hotbar.items.get(HotbarType.QUEUE_JOIN_RANKED);
                    if (!activeEvent) {
                        toReturn[4] = Hotbar.items.get(HotbarType.PARTY_CREATE);
                    }
                    else {
                        toReturn[3] = Hotbar.items.get(HotbarType.EVENT_JOIN);
                        toReturn[5] = Hotbar.items.get(HotbarType.PARTY_CREATE);
                    }
                    toReturn[7] = Hotbar.items.get(HotbarType.LEADERBOARDS_MENU);
                    toReturn[8] = Hotbar.items.get(HotbarType.KIT_EDITOR);
                    break;
                }
                if (profile.getParty().isLeader(profile.getUuid())) {
                    toReturn[0] = Hotbar.items.get(HotbarType.PARTY_EVENTS);
                    toReturn[1] = Hotbar.items.get(HotbarType.PARTY_INFO);
                    toReturn[4] = Hotbar.items.get(HotbarType.OTHER_PARTIES);
                    toReturn[6] = Hotbar.items.get(HotbarType.KIT_EDITOR);
                    toReturn[7] = Hotbar.items.get(HotbarType.PARTY_SETTINGS);
                    toReturn[8] = Hotbar.items.get(HotbarType.PARTY_DISBAND);
                    break;
                }
                toReturn[0] = Hotbar.items.get(HotbarType.PARTY_INFO);
                toReturn[4] = Hotbar.items.get(HotbarType.OTHER_PARTIES);
                toReturn[7] = Hotbar.items.get(HotbarType.KIT_EDITOR);
                toReturn[8] = Hotbar.items.get(HotbarType.PARTY_LEAVE);
                break;
            }
            case QUEUE: {
                toReturn[0] = Hotbar.items.get(HotbarType.QUEUE_LEAVE);
                break;
            }
            case SUMO_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.SUMO_LEAVE);
                break;
            }
            case BRACKETS_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.BRACKETS_LEAVE);
                break;
            }
            case GULAG_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.GULAG_LEAVE);
                break;
            }
            case OITC_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.OITC_LEAVE);
                break;
            }
            case LMS_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.LMS_LEAVE);
                break;
            }
            case PARKOUR_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.PARKOUR_SPAWN);
                toReturn[8] = Hotbar.items.get(HotbarType.PARKOUR_LEAVE);
                break;
            }
            case SPLEEF_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.SPLEEF_LEAVE);
                break;
            }
            case MATCH_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarType.SPECTATE_STOP);
                break;
            }
        }
        return toReturn;
    }
    
    public static HotbarType fromItemStack(final ItemStack itemStack) {
        for (final Map.Entry<HotbarType, ItemStack> entry : getItems().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(itemStack)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public static Map<HotbarType, ItemStack> getItems() {
        return Hotbar.items;
    }
    
    static {
        Hotbar.items = new HashMap<>();
    }
}
