package xyz.refinedev.practice.profile.hotbar;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Hotbar {

    @Getter private static final Map<HotbarType, ItemStack> items = new HashMap<>();
    private static final BasicConfigurationFile config = Array.getInstance().getHotbarConfig();

    public static void preload() {
        for (HotbarType hotbarItem : HotbarType.values()) {
            try {
                String path = "HOTBAR_ITEMS." + hotbarItem.name() + ".";

                //if (!config.getBoolean(path + "ENABLED")) return;

                ItemBuilder builder = new ItemBuilder(Material.valueOf(config.getString(path + "MATERIAL")));

                try {
                    builder.durability(config.getInteger(path + "DURABILITY"));
                } catch (Exception ignored) {}

                builder.name(CC.translate(config.getString(path + "NAME")));

                try {
                    builder.lore(config.getStringList(path + "LORE"));
                } catch (Exception ignored) {}

                items.put(hotbarItem, builder.build());
            } catch (Exception ignored) {}
        }
    }
    
    public static ItemStack[] getLayout(HotbarLayout layout, Profile profile) {
        final ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);

        boolean activeRematch = profile.getRematchData() != null;

        switch (layout) {
            case LOBBY: {
                if (profile.getParty() == null) {

                    boolean activeEvent = (
                            (Array.getInstance().getSumoManager().getActiveSumo() != null
                            && Array.getInstance().getSumoManager().getActiveSumo().isWaiting())
                            || (Array.getInstance().getBracketsManager().getActiveBrackets() != null
                            && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting())
                            || (Array.getInstance().getLMSManager().getActiveLMS() != null
                            && Array.getInstance().getLMSManager().getActiveLMS().isWaiting())
                            || (Array.getInstance().getParkourManager().getActiveParkour() != null
                            && Array.getInstance().getParkourManager().getActiveParkour().isWaiting())
                            || (Array.getInstance().getGulagManager().getActiveGulag() != null
                            && Array.getInstance().getGulagManager().getActiveGulag().isWaiting())
                            || (Array.getInstance().getSpleefManager().getActiveSpleef() != null
                            && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting()));

                    toReturn[0] = items.get(HotbarType.QUEUE_JOIN_UNRANKED);
                    toReturn[1] = items.get(HotbarType.QUEUE_JOIN_RANKED);
                    toReturn[2] = items.get(HotbarType.QUEUE_JOIN_CLAN);

                    if (activeRematch && activeEvent) {
                        if (profile.getRematchData().isReceive())  {
                            toReturn[2] = items.get(HotbarType.REMATCH_ACCEPT);
                        } else {
                            toReturn[2] = items.get(HotbarType.REMATCH_REQUEST);
                        }
                        toReturn[4] = items.get(HotbarType.EVENT_JOIN);
                        toReturn[5] = items.get(HotbarType.PARTY_CREATE);
                    } else if (activeEvent){
                        toReturn[3] = items.get(HotbarType.EVENT_JOIN);
                        toReturn[5] = items.get(HotbarType.PARTY_CREATE);
                    } else if (activeRematch) {
                        if (profile.getRematchData().isReceive())  {
                            toReturn[3] = items.get(HotbarType.REMATCH_ACCEPT);
                        } else {
                            toReturn[3] = items.get(HotbarType.REMATCH_REQUEST);
                        }
                        toReturn[5] = items.get(HotbarType.PARTY_CREATE);
                    } else {
                        toReturn[4] = items.get(HotbarType.PARTY_CREATE);
                    }

                    toReturn[7] = items.get(HotbarType.MAIN_MENU);
                    toReturn[8] = items.get(HotbarType.KIT_EDITOR);
                    break;
                }
                if (profile.getParty().isLeader(profile.getUuid())) {
                    toReturn[0] = items.get(HotbarType.PARTY_EVENTS);
                    toReturn[1] = items.get(HotbarType.PARTY_INFO);
                    if (profile.getParty().getPlayers().size() > 3) {
                        toReturn[2] = items.get(HotbarType.PARTY_CLASSES);
                    }
                    toReturn[4] = items.get(HotbarType.OTHER_PARTIES);
                    toReturn[6] = items.get(HotbarType.KIT_EDITOR);
                    toReturn[7] = items.get(HotbarType.PARTY_SETTINGS);
                    toReturn[8] = items.get(HotbarType.PARTY_DISBAND);
                    break;
                }
                toReturn[0] = items.get(HotbarType.PARTY_INFO);
                toReturn[4] = items.get(HotbarType.OTHER_PARTIES);
                toReturn[7] = items.get(HotbarType.KIT_EDITOR);
                toReturn[8] = items.get(HotbarType.PARTY_LEAVE);
                break;
            }
            case QUEUE: {
                toReturn[0] = items.get(HotbarType.QUEUE_LEAVE);
                break;
            }
            case SUMO_SPECTATE: {
                toReturn[0] = items.get(HotbarType.SUMO_LEAVE);
                break;
            }
            case BRACKETS_SPECTATE: {
                toReturn[0] = items.get(HotbarType.BRACKETS_LEAVE);
                break;
            }
            case GULAG_SPECTATE: {
                toReturn[0] = items.get(HotbarType.GULAG_LEAVE);
                break;
            }
            case LMS_SPECTATE: {
                toReturn[0] = items.get(HotbarType.LMS_LEAVE);
                break;
            }
            case PARKOUR_SPECTATE: {
                toReturn[0] = items.get(HotbarType.PARKOUR_SPAWN);
                toReturn[8] = items.get(HotbarType.PARKOUR_LEAVE);
                break;
            }
            case SPLEEF_SPECTATE: {
                toReturn[0] = items.get(HotbarType.SPLEEF_LEAVE);
                break;
            }
            case MATCH_SPECTATE: {
                if (profile.getSettings().isShowSpectator()) {
                     toReturn[0] = items.get(HotbarType.SPECTATOR_HIDE);
                } else {
                    toReturn[0] = items.get(HotbarType.SPECTATOR_SHOW);
                }
                toReturn[8] = items.get(HotbarType.SPECTATE_STOP);
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
}
