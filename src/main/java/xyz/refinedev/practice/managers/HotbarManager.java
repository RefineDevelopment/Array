package xyz.refinedev.practice.managers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.hotbar.HotbarItem;
import xyz.refinedev.practice.profile.hotbar.HotbarLayout;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/27/2021
 * Project: Array
 */

@Getter
public class HotbarManager {

    private final Array plugin;
    private final BasicConfigurationFile config;
    private final List<HotbarItem> items = new ArrayList<>();

    public HotbarManager(Array plugin) {
        this.plugin = plugin;
        this.config = plugin.getHotbarConfig();
    }

    public void init() {
        for ( HotbarType item : HotbarType.values() ) {
            String path = "HOTBAR_ITEMS." + item.name() + ".";

            if (item == HotbarType.CUSTOM) continue;

            final ItemBuilder builder = new ItemBuilder(Material.valueOf(config.getString(path + "MATERIAL")));

            builder.name(CC.translate(config.getString(path + "NAME")));
            if (config.getInteger(path + "DURABILITY") != 0) builder.durability(config.getInteger(path + "DURABILITY"));
            if (config.getStringList(path + "LORE") != null || !config.getStringList(path + "LORE").isEmpty()) builder.lore(config.getStringList(path + "LORE"));

            int slot = config.getInteger(path + "SLOT");
            String command = config.getString(path + "COMMAND");
            HotbarLayout layout;

            try {
                layout = HotbarLayout.valueOf(config.getString(path + "LAYOUT_TYPE"));
            } catch (Exception ignored) {
                plugin.logger("&cInvalid Layout specified for " + item.name() + ".");
                continue;
            }

            HotbarItem hotbarItem = new HotbarItem(item, layout, builder.build(), slot);
            hotbarItem.setEnabled(config.getBoolean(path + "ENABLED"));
            if (command != null && !command.equalsIgnoreCase("")) {
                if (!command.contains("/")) {
                    plugin.logger("&cInvalid Command setup for " + item.name() + ".");
                    return;
                }
                hotbarItem.setCommand(command);
            }
            items.add(hotbarItem);
        }

        if (config.getConfiguration().getConfigurationSection("CUSTOM_ITEMS") == null) return;
        for ( String key : config.getConfiguration().getConfigurationSection("CUSTOM_ITEMS").getKeys(false) ) {
            if (key == null) return;

            String path = "CUSTOM_ITEMS." + key + ".";
            
            final ItemBuilder builder = new ItemBuilder(Material.valueOf(config.getString(path + "MATERIAL")));
            builder.name(CC.translate(config.getString(path + "NAME")));

            if (config.getInteger(path + "DURABILITY") != 0) builder.durability(config.getInteger(path + "DURABILITY"));
            if (config.getStringList(path + "LORE") != null || !config.getStringList(path + "LORE").isEmpty()) builder.lore(config.getStringList(path + "LORE"));

            int slot = config.getInteger(path + "SLOT");
            String command = config.getString(path + "COMMAND");
            HotbarLayout layout;

            try {
                layout = HotbarLayout.valueOf(config.getString(path + "LAYOUT_TYPE"));
            } catch (Exception ignored) {
                plugin.logger("&cInvalid Layout specified for " + key + ".");
                continue;
            }

            HotbarItem hotbarItem = new HotbarItem(HotbarType.CUSTOM, layout, builder.build(), slot);
            hotbarItem.setEnabled(config.getBoolean(path + "ENABLED"));
            if (command != null && !command.equalsIgnoreCase("")) {
                if (!command.contains("/")) {
                    plugin.logger("&cInvalid Command setup for " + key + ".");
                    continue;
                }
                hotbarItem.setCommand(command);
            }
            this.items.add(hotbarItem);
        }
    }

    public ItemStack[] getLayout(HotbarLayout layout, Profile profile) {
        final ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);

        switch (layout) {
            case LOBBY: {
                Collection<HotbarItem> lobbyItems = items.stream().filter(HotbarItem::isEnabled).filter(item -> item.getLayout().equals(HotbarLayout.LOBBY) && !item.getType().equals(HotbarType.PARTY_CREATE) && !item.getType().equals(HotbarType.EVENT_JOIN) && !item.getType().equals(HotbarType.REMATCH_ACCEPT) && !item.getType().equals(HotbarType.REMATCH_REQUEST)).collect(Collectors.toList());
                lobbyItems.addAll(getCustomItems().stream().filter(item -> item.getLayout().equals(HotbarLayout.LOBBY)).collect(Collectors.toList()));

                for ( HotbarItem item : lobbyItems ) toReturn[item.getSlot()] = item.getItem();

                HotbarItem rematchAccept = getHotbarItem(HotbarType.REMATCH_ACCEPT);
                HotbarItem rematchRequest = getHotbarItem(HotbarType.REMATCH_REQUEST);
                HotbarItem eventJoin = getHotbarItem(HotbarType.EVENT_JOIN);
                HotbarItem partyCreate = getHotbarItem(HotbarType.PARTY_CREATE);

                boolean rematchEnabled = rematchAccept.isEnabled() && rematchRequest.isEnabled();
                boolean eventJoinEnabled = eventJoin.isEnabled();
                boolean partyCreateEnabled = partyCreate.isEnabled();

                boolean activeRematch = profile.getRematchData() != null && rematchEnabled;
                boolean activeEvent = plugin.getEventManager().getActiveEvent() != null && eventJoinEnabled;

                int eventRematchAddSlot = config.getInteger("HOTBAR_ITEMS.EVENT_JOIN.MOVE_SLOT_TO.REMATCH_ADDED");
                int partyEventSlot = config.getInteger("HOTBAR_ITEMS.PARTY_CREATE.MOVE_SLOT_TO.EVENT_JOIN_ADDED");
                int partyRematchSlot = config.getInteger("HOTBAR_ITEMS.PARTY_CREATE.MOVE_SLOT_TO.REMATCH_ADDED");
                int partybothSlot = config.getInteger("HOTBAR_ITEMS.PARTY_CREATE.MOVE_SLOT_TO.EVENT_JOIN_AND_REMATCH_ADDED");

                if (activeRematch && activeEvent) {
                    if (profile.getRematchData().isReceive()) {
                        toReturn[rematchAccept.getSlot()] = rematchAccept.getItem();
                    } else {
                        toReturn[rematchRequest.getSlot()] = rematchRequest.getItem();
                    }
                    toReturn[eventRematchAddSlot] = eventJoin.getItem();
                    if (partyCreateEnabled) toReturn[partybothSlot] = partyCreate.getItem();
                } else if (activeEvent) {
                    toReturn[eventJoin.getSlot()] = eventJoin.getItem();
                    toReturn[partyEventSlot] = partyCreate.getItem();
                } else if (activeRematch) {
                    if (profile.getRematchData().isReceive()) {
                        toReturn[rematchAccept.getSlot()] = rematchAccept.getItem();
                    } else {
                        toReturn[rematchRequest.getSlot()] = rematchRequest.getItem();
                    }
                    if (partyCreateEnabled) toReturn[partyRematchSlot] = partyCreate.getItem();
                } else {
                    if (partyCreateEnabled) toReturn[partyCreate.getSlot()] = partyCreate.getItem();
                }
                break;
            }
            case PARTY: {
                Collection<HotbarItem> partyItems = items.stream().filter(HotbarItem::isEnabled).filter(item -> item.getLayout().equals(HotbarLayout.PARTY) || item.getLayout().equals(HotbarLayout.PARTY_LEADER) || item.getLayout().equals(HotbarLayout.PARTY_MEMBER)).collect(Collectors.toList());
                partyItems.addAll(getCustomItems().stream().filter(item -> item.getLayout().equals(HotbarLayout.PARTY) || item.getLayout().equals(HotbarLayout.PARTY_LEADER) || item.getLayout().equals(HotbarLayout.PARTY_MEMBER)).collect(Collectors.toList()));

                for ( HotbarItem item : partyItems ) {
                    if (item.getLayout().equals(HotbarLayout.PARTY_LEADER)) {
                        if (profile.getParty().isLeader(profile.getUuid())) {
                            toReturn[item.getSlot()] = item.getItem();
                        }
                    } else if (item.getLayout().equals(HotbarLayout.PARTY_MEMBER)) {
                        if (!profile.getParty().isLeader(profile.getUuid())) {
                            toReturn[item.getSlot()] = item.getItem();
                        }
                    } else if (item.getLayout().equals(HotbarLayout.PARTY)) {
                        toReturn[item.getSlot()] = item.getItem();
                    }
                }
                break;
            }
            case QUEUE: {
                Collection<HotbarItem> queueItems = items.stream().filter(HotbarItem::isEnabled).filter(item -> item.getLayout().equals(HotbarLayout.QUEUE)).collect(Collectors.toList());
                queueItems.addAll(getCustomItems().stream().filter(item -> item.getLayout().equals(HotbarLayout.QUEUE)).collect(Collectors.toList()));

                for ( HotbarItem item : queueItems ) toReturn[item.getSlot()] = item.getItem();

                break;
            }
            case EVENT: {
                Collection<HotbarItem> eventItems = items.stream().filter(HotbarItem::isEnabled).filter(item -> (item.getLayout().equals(HotbarLayout.EVENT) || item.getLayout().equals(HotbarLayout.EVENT_SPECTATE) || item.getLayout().equals(HotbarLayout.EVENT_WAITING))).collect(Collectors.toList());
                eventItems.addAll(getCustomItems().stream().filter(item -> item.getLayout().equals(HotbarLayout.EVENT) || item.getLayout().equals(HotbarLayout.EVENT_SPECTATE) || item.getLayout().equals(HotbarLayout.EVENT_WAITING)).collect(Collectors.toList()));

                for ( HotbarItem item : eventItems ) {
                    switch (item.getLayout()) {
                        case EVENT_WAITING: {
                            if (profile.getEvent().isWaiting()) {
                                if (item.getType().equals(HotbarType.EVENT_TEAM) && profile.getEvent().isTeam()) {
                                    toReturn[item.getSlot()] = item.getItem();
                                } else if (!item.getType().equals(HotbarType.EVENT_TEAM)) {
                                    toReturn[item.getSlot()] = item.getItem();
                                }
                            }
                            break;
                        }
                        case EVENT_SPECTATE: {
                            if (profile.isSpectating() && profile.isInEvent()) {
                                toReturn[item.getSlot()] = item.getItem();
                            }
                            break;
                        }
                        case EVENT: {
                            if ((profile.isSpectating() && profile.isInEvent()) || profile.getEvent().isWaiting()) {
                                toReturn[item.getSlot()] = item.getItem();
                            }
                            break;
                        }
                    }
                }
            }
            case MATCH_SPECTATE: {
                Collection<HotbarItem> matchItems = items.stream().filter(HotbarItem::isEnabled).filter(item -> item.getLayout().equals(HotbarLayout.MATCH_SPECTATE)).collect(Collectors.toList());
                matchItems.addAll(getCustomItems().stream().filter(item -> item.getLayout().equals(HotbarLayout.MATCH_SPECTATE)).collect(Collectors.toList()));

                HotbarItem spectatorShow = getHotbarItem(HotbarType.SPECTATOR_SHOW);
                HotbarItem spectatorHide = getHotbarItem(HotbarType.SPECTATOR_HIDE);

                boolean spectatorEnable = spectatorHide.isEnabled() && spectatorShow.isEnabled();

                if (spectatorEnable) {
                    if (profile.getSettings().isShowSpectator()) {
                        toReturn[spectatorHide.getSlot()] = spectatorHide.getItem();
                    } else {
                        toReturn[spectatorShow.getSlot()] = spectatorShow.getItem();
                    }
                }
                for ( HotbarItem item : matchItems ) {
                    toReturn[item.getSlot()] = item.getItem();
                }
                PlayerUtil.spectator(profile.getPlayer());
                break;
            }
        }
        return toReturn;
    }
    
    public final List<HotbarItem> getCustomItems() {
        return items.stream().filter(hotbarItem -> hotbarItem.isEnabled() && hotbarItem.getType().equals(HotbarType.CUSTOM)).collect(Collectors.toList());
    }

    public final HotbarItem getHotbarItem(HotbarType hotbarType) {
        return items.stream().filter(hotbarItem -> hotbarItem.isEnabled() && hotbarItem.getType().equals(hotbarType)).findFirst().orElse(null);
    }

    public HotbarType fromItemStack(ItemStack itemStack) {
        HotbarItem hotbarItem = this.items.stream().filter(item -> item.getItem().equals(itemStack)).findFirst().orElse(null);
        if (hotbarItem == null) return null;

        return hotbarItem.getType();
    }

    public HotbarItem getItem(ItemStack stack) {
        return items.stream().filter(item -> item.getItem().equals(stack)).findFirst().orElse(null);
    }

}
