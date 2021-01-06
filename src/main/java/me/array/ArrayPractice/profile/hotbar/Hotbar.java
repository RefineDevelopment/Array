package me.array.ArrayPractice.profile.hotbar;

import java.util.HashMap;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class Hotbar
{
    private static Map<HotbarItem, ItemStack> items;
    
    private Hotbar() {
    }
    
    public static void init() {
        Hotbar.items.put(HotbarItem.QUEUE_JOIN_UNRANKED, new ItemBuilder(Material.IRON_SWORD).name(CC.AQUA + "Join Unranked Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.QUEUE_JOIN_RANKED, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.AQUA + "Join Ranked Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.QUEUE_JOIN_KITPVP, new ItemBuilder(Material.GOLD_SWORD).name(CC.AQUA + "Join KitPvP" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.QUEUE_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Queue" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_EVENTS, new ItemBuilder(Material.GOLD_AXE).name(CC.AQUA + "Party Events" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_CREATE, new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Create Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_DISBAND, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Disband Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_SETTINGS, new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Party Settings" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_LEAVE, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Party" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARTY_INFO, new ItemBuilder(Material.PAPER).durability(3).name(CC.AQUA + "Party Information" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.OTHER_PARTIES, new ItemBuilder(Material.REDSTONE_TORCH_ON).name(CC.AQUA + "Fight Other Parties" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.LEADERBOARDS_MENU, new ItemBuilder(Material.EMERALD).name(CC.AQUA + "Main Menu" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.SETTINGS_MENU, new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Settings" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.KIT_EDITOR, new ItemBuilder(Material.BOOK).name(CC.AQUA + "Kit Editor" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.SPECTATE_STOP, new ItemBuilder(Material.INK_SACK).durability(1).name(CC.AQUA + CC.BOLD + "Stop Spectating" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.VIEW_INVENTORY, new ItemBuilder(Material.BOOK).name(CC.AQUA + "View Inventory" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.EVENT_JOIN, new ItemBuilder(Material.NETHER_STAR).name(CC.AQUA + "Join Event" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.SUMO_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave Sumo" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.BRACKETS_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave Brackets" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.LMS_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave FFA" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.PARKOUR_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave Parkour" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.SKYWARS_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave Skywars" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.SPLEEF_LEAVE, new ItemBuilder(Material.INK_SACK).name(CC.RED + "Leave Spleef" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.REMATCH_REQUEST, new ItemBuilder(Material.BLAZE_POWDER).name(CC.AQUA + "Request Rematch" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.REMATCH_ACCEPT, new ItemBuilder(Material.DIAMOND).name(CC.AQUA + "Accept Rematch" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.DEFAULT_KIT, new ItemBuilder(Material.BOOK).name(CC.AQUA + "Default Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.DIAMOND_KIT, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.AQUA + "Diamond Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.BARD_KIT, new ItemBuilder(Material.BLAZE_POWDER).name(CC.AQUA + "Bard Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.ROGUE_KIT, new ItemBuilder(Material.GOLD_SWORD).name(CC.AQUA + "Rogue Kit" + CC.GRAY + " (Right-Click)").build());
        Hotbar.items.put(HotbarItem.ARCHER_KIT, new ItemBuilder(Material.BOW).name(CC.AQUA + "Archer Kit" + CC.GRAY + " (Right-Click)").build());
    }
    
    public static ItemStack[] getLayout(final HotbarLayout layout, final Profile profile) {
        final ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);
        switch (layout) {
            case LOBBY: {
                if (profile.getParty() == null) {
                    final boolean activeEvent = (Practice.get().getSumoManager().getActiveSumo() != null && Practice.get().getSumoManager().getActiveSumo().isWaiting()) || (Practice.get().getBracketsManager().getActiveBrackets() != null && Practice.get().getBracketsManager().getActiveBrackets().isWaiting()) || (Practice.get().getLMSManager().getActiveLMS() != null && Practice.get().getLMSManager().getActiveLMS().isWaiting()) || (Practice.get().getParkourManager().getActiveParkour() != null && Practice.get().getParkourManager().getActiveParkour().isWaiting()) || (Practice.get().getSpleefManager().getActiveSpleef() != null && Practice.get().getSpleefManager().getActiveSpleef().isWaiting());
                    toReturn[0] = Hotbar.items.get(HotbarItem.QUEUE_JOIN_UNRANKED);
                    toReturn[1] = Hotbar.items.get(HotbarItem.QUEUE_JOIN_RANKED);
                    toReturn[2] = Hotbar.items.get(HotbarItem.QUEUE_JOIN_KITPVP);
                    if (!activeEvent) {
                        toReturn[4] = Hotbar.items.get(HotbarItem.PARTY_CREATE);
                    }
                    else {
                        toReturn[3] = Hotbar.items.get(HotbarItem.EVENT_JOIN);
                        toReturn[5] = Hotbar.items.get(HotbarItem.PARTY_CREATE);
                    }
                    toReturn[6] = Hotbar.items.get(HotbarItem.LEADERBOARDS_MENU);
                    toReturn[7] = Hotbar.items.get(HotbarItem.SETTINGS_MENU);
                    toReturn[8] = Hotbar.items.get(HotbarItem.KIT_EDITOR);
                    break;
                }
                if (profile.getParty().isLeader(profile.getUuid())) {
                    toReturn[0] = Hotbar.items.get(HotbarItem.PARTY_EVENTS);
                    toReturn[1] = Hotbar.items.get(HotbarItem.PARTY_INFO);
                    toReturn[4] = Hotbar.items.get(HotbarItem.OTHER_PARTIES);
                    toReturn[6] = Hotbar.items.get(HotbarItem.KIT_EDITOR);
                    toReturn[7] = Hotbar.items.get(HotbarItem.PARTY_SETTINGS);
                    toReturn[8] = Hotbar.items.get(HotbarItem.PARTY_DISBAND);
                    break;
                }
                toReturn[0] = Hotbar.items.get(HotbarItem.PARTY_INFO);
                toReturn[4] = Hotbar.items.get(HotbarItem.OTHER_PARTIES);
                toReturn[7] = Hotbar.items.get(HotbarItem.KIT_EDITOR);
                toReturn[8] = Hotbar.items.get(HotbarItem.PARTY_LEAVE);
                break;
            }
            case QUEUE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.QUEUE_LEAVE);
                break;
            }
            case SUMO_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.SUMO_LEAVE);
                break;
            }
            case BRACKETS_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.BRACKETS_LEAVE);
                break;
            }
            case LMS_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.LMS_LEAVE);
                break;
            }
            case PARKOUR_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.PARKOUR_LEAVE);
                break;
            }
            case SPLEEF_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.SPLEEF_LEAVE);
                break;
            }
            case MATCH_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.SPECTATE_STOP);
                break;
            }
            case SKYWARS_SPECTATE: {
                toReturn[0] = Hotbar.items.get(HotbarItem.SKYWARS_LEAVE);
            }
        }
        return toReturn;
    }
    
    public static HotbarItem fromItemStack(final ItemStack itemStack) {
        for (final Map.Entry<HotbarItem, ItemStack> entry : getItems().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals((Object)itemStack)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public static Map<HotbarItem, ItemStack> getItems() {
        return Hotbar.items;
    }
    
    static {
        Hotbar.items = new HashMap<HotbarItem, ItemStack>();
    }
}
