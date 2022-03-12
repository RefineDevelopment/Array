package xyz.refinedev.practice.util.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.party.menu.PartyEventMenu;
import xyz.refinedev.practice.party.menu.PartySettingsMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.menu.KillEffectMenu;
import xyz.refinedev.practice.profile.menu.ProfileMenu;
import xyz.refinedev.practice.profile.settings.menu.ProfileSettingsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.config.impl.MenuConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.custom.ButtonData;
import xyz.refinedev.practice.util.menu.custom.CustomMenu;
import xyz.refinedev.practice.util.menu.custom.CustomPaginatedMenu;
import xyz.refinedev.practice.util.menu.custom.MenuData;
import xyz.refinedev.practice.util.menu.custom.action.ActionData;
import xyz.refinedev.practice.util.menu.custom.action.ActionType;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class MenuHandler {

    private final Array plugin;
    private final BasicConfigurationFile config;

    private final Map<String, FoldersConfigurationFile> configs = new HashMap<>();
    private final Map<UUID, Menu> openedMenus = new HashMap<>();
    private final List<MenuData> menuData = new ArrayList<>();

    public void init() {
        //Async cuz yknow we don't wanna diturb the main thread and besides this stuff gets updated way too often
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new MenuUpdateTask(plugin), 5L, 5L);

        //Custom Menus :p
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/menu");
        File[] files = file.listFiles();

        if (files == null || files.length == 0) return;

        for ( File menuFile : files ) {
            MenuConfigurationFile menuConfig = new MenuConfigurationFile(menuFile.getName().replace(".yml", ""), plugin);
            MenuData menu = new MenuData();

            menu.setName(menuFile.getName());
            menu.setTitle(menuConfig.getString("TITLE"));
            menu.setSize(menuConfig.getInteger("SIZE"));
            menu.setPaginated(menuConfig.getBoolean("PAGINATED"));

            if (menuConfig.getBoolean("PLACEHOLDER")) {
                Material material = ButtonUtil.getPlaceholderMaterial(menuConfig, menu.getName());
                if (material == null) {
                    menu.setPlaceholder(false);
                    return;
                }

                ItemBuilder itemBuilder = new ItemBuilder(material);
                itemBuilder.name(menuConfig.getString("PLACEHOLDER_BUTTON.NAME"));
                itemBuilder.durability(menuConfig.getInteger("PLACEHOLDER_BUTTON.DATA"));
                itemBuilder.lore(menuConfig.getStringList("PLACEHOLDER_BUTTON.LORE"));
                itemBuilder.clearFlags();

                menu.setPlaceholderItem(itemBuilder.build());
                menu.setPlaceholder(true);
            }

            menu.setButtons(this.loadCustomButtons(menuConfig));
            this.menuData.add(menu);
        }
    }

    public void loadMenu(Menu menu, String key) {
        List<ButtonData> custom = plugin.getMenuHandler().loadCustomButtons(key);
        if (custom != null && !custom.isEmpty()) menu.getCustomButtons().addAll(custom);

        menu.setPlaceholder(config.getBoolean(key + ".PLACEHOLDER"));

        if (menu.isPlaceholder()) {
            Material material = ButtonUtil.getPlaceholderMaterial(config, key, key);
            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.name(config.getString(key + ".PLACEHOLDER_BUTTON.NAME"));
            itemBuilder.durability(config.getInteger(key + ".PLACEHOLDER_BUTTON.DATA"));
            itemBuilder.lore(config.getStringList(key + ".PLACEHOLDER_BUTTON.LORE"));
            itemBuilder.clearFlags();
            menu.setPlaceholderButton(Button.placeholder(itemBuilder.build()));
        }
    }

    /**
     * Get the menu that is opened to the player by
     * their UniqueId
     *
     * @param player {@link Player}
     * @return {@link Menu} queried menu
     */
    public Menu getByPlayer(Player player) {
       return this.openedMenus.get(player.getUniqueId());
    }

    /**
     * Get the menu that is opened to the player by
     * their UniqueId
     *
     * @param player {@link Player}
     * @return {@link Menu} queried menu
     */
    public Menu getByPlayer(UUID player) {
        return this.openedMenus.get(player);
    }

    /**
     * Load custom buttons from this menu config
     *
     * @return {@link List} of {@link ButtonData}
     */
    public List<ButtonData> loadCustomButtons(String menuKey) {
        List<ButtonData> customButtons = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(menuKey + ".CUSTOM_BUTTONS");
        if (section == null || section.getKeys(false).isEmpty()) return null;

        for ( String key : section.getKeys(false) ){
            ButtonData buttonData = new ButtonData();

            Material material = ButtonUtil.getCustomButton(config, menuKey + ".CUSTOM_BUTTONS." + key + ".MATERIAL", key);
            if (material == null) continue;

            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.name(section.getString(key + ".NAME"));
            itemBuilder.durability(section.getInt(key + ".DATA"));
            itemBuilder.lore(section.getStringList(key + ".LORE"));
            itemBuilder.clearFlags();

            ItemStack itemStack = itemBuilder.build();
            buttonData.setItem(itemStack);
            buttonData.setSlot(section.getInt(key + ".SLOT"));
            buttonData.getActions().addAll(this.loadActionData(key, menuKey));

            customButtons.add(buttonData);
        }
        return customButtons;
    }

    /**
     * Load custom buttons from a menu config
     *
     * @param config) {@link MenuConfigurationFile} menu config
     * @return {@link List} of {@link ButtonData}
     */
    public List<ButtonData> loadCustomButtons(MenuConfigurationFile config) {
        List<ButtonData> customButtons = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("CUSTOM_BUTTONS");
        if (section == null || section.getKeys(false).isEmpty()) return null;

        for ( String key : section.getKeys(false) ){
            ButtonData buttonData = new ButtonData();

            Material material = ButtonUtil.getCustomButton(config, "CUSTOM_BUTTONS." + key + ".MATERIAL", key);
            if (material == null) continue;

            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.name(section.getString(key + ".NAME"));
            itemBuilder.durability(section.getInt(key + ".DATA"));
            itemBuilder.lore(section.getStringList(key + ".LORE"));
            itemBuilder.clearFlags();

            ItemStack itemStack = itemBuilder.build();
            buttonData.setItem(itemStack);
            buttonData.setSlot(section.getInt(key + ".SLOT"));
            buttonData.getActions().addAll(this.loadActionData(key, config));

            customButtons.add(buttonData);
        }
        return customButtons;
    }


    /**
     * Open the menu for the Player
     *
     * @param menu The Menu player is supposed to get displayed
     * @param player Player viewing the menu
     */
    public void openMenu(Menu menu, Player player) {
        menu.loadConfig(config);
        menu.setButtons(menu.getButtons(this.plugin, player));

        if (!menu.getCustomButtons().isEmpty() && menu.getCustomButtons().size() != 0) {
            for ( ButtonData customButton : menu.getCustomButtons() ) {
                menu.getButtons().put(customButton.getSlot(), new CustomButton(customButton));
            }
        }

        String title = CC.translate(menu.getTitle(this.plugin, player));
        Menu previousMenu =  this.getOpenedMenus().get(player.getUniqueId());
        Inventory inventory = null;

        int size = menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
        boolean update = false;

        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (player.getOpenInventory() != null) {
            if (previousMenu == null) {
                player.closeInventory();
            } else {
                int previousSize = player.getOpenInventory().getTopInventory().getSize();

                if (previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    player.closeInventory();
                }
            }
        }

        if (inventory == null) {
            inventory = plugin.getServer().createInventory(player, size, title);
        }

        inventory.setContents(new ItemStack[inventory.getSize()]);

        this.getOpenedMenus().put(player.getUniqueId(), menu);
        for (Map.Entry<Integer, Button> buttonEntry : menu.getButtons().entrySet()) {
            inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(plugin, player));
        }

        if (menu.isPlaceholder()) {
            for (int index = 0; index < size; index++) {
                if (menu.getButtons().get(index) == null) {
                    menu.getButtons().put(index, menu.getPlaceholderButton());
                    inventory.setItem(index, menu.getPlaceholderButton().getButtonItem(plugin, player));
                }
            }
        }

        if (update) {
            player.updateInventory();
        } else {
            player.openInventory(inventory);
        }

        menu.onOpen(this.plugin, player);
        menu.setClosedByMenu(false);
    }

    /**
     * Load a {@link ButtonData}'s list of ActionData
     *
     * @param buttonKey The key of the button in the config
     * @param config The config file of the menu
     * @return {@link List} of ActionData
     */
    public List<ActionData> loadActionData(String buttonKey, String menuKey) {
        ConfigurationSection configurationSection = config.getConfigurationSection(menuKey + ".CUSTOM_BUTTONS." + buttonKey + ".ACTIONS");
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return null;

        List<ActionData> actionData = new ArrayList<>();

        for ( String key : configurationSection.getKeys(false) ) {
            String path = menuKey + ".CUSTOM_BUTTONS." + buttonKey + ".ACTIONS." + key;
            String clickType = config.getString(path + ".CLICK_TYPE");
            String action = config.getString(path + ".ACTION");

            ActionType actionType = ButtonUtil.getAction(config, config.getString(path + ".ACTION_TYPE"), buttonKey);
            if (actionType == null) continue;

            ActionData data = new ActionData(actionType, clickType, action);
            actionData.add(data);
        }
        return actionData;
    }



    /**
     * Load a {@link ButtonData}'s list of ActionData
     *
     * @param buttonKey The key of the button in the config
     * @param config The config file of the menu
     * @return {@link List} of ActionData
     */
    public List<ActionData> loadActionData(String buttonKey, MenuConfigurationFile config) {
        ConfigurationSection configurationSection = config.getConfigurationSection("CUSTOM_BUTTONS." + buttonKey + ".ACTIONS");
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return null;

        List<ActionData> actionData = new ArrayList<>();

        for ( String key : configurationSection.getKeys(false) ) {
            String path = "CUSTOM_BUTTONS." + buttonKey + ".ACTIONS." + key;
            String clickType = config.getString(path + ".CLICK_TYPE");
            String action = config.getString(path + ".ACTION");

            ActionType actionType = ButtonUtil.getAction(config, config.getString(path + ".ACTION_TYPE"), buttonKey);
            if (actionType == null) continue;

            ActionData data = new ActionData(actionType, clickType, action);
            actionData.add(data);
        }
        return actionData;
    }

    /**
     * Get a Custom Menu Data by Name
     *
     * @param name {@link String} name of the menu
     * @return {@link MenuData} menuData of the menu
     */
    public MenuData getMenuDataByName(String name) {
        return menuData.stream()
                .filter(menu -> menu.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**-
     * Get a menu config by menu's name
     * This is used for default built in menus
     *
     * @param name {@link String} menu name
     * @return {@link FoldersConfigurationFile} config
     */
    public FoldersConfigurationFile getConfigByName(String name) {
        return this.configs.get(name);
    }

    /**
     * Get a Custom Menu by Name
     *
     * @param name {@link String} menu name
     * @return {@link Menu}
     */
    public Menu findMenu(Player player, String name) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        switch (name) {
            case "kill_effects": return new KillEffectMenu(plugin);
            case "party_events": return new PartyEventMenu(plugin);
            case "party_settings": return new PartySettingsMenu(plugin);
            case "profile_menu": return new ProfileMenu(player);
            case "profile_settings": return new ProfileSettingsMenu();
            default: {
                MenuData menuData = this.getMenuDataByName(name);
                if (menuData == null) {
                    player.sendMessage(Locale.ERROR_MENU.toString());
                    return null;
                }
                return menuData.isPaginated() ?  new CustomPaginatedMenu(menuData) : new CustomMenu(menuData);
            }
        }
    }

}
