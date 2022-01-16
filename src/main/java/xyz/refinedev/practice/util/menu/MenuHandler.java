package xyz.refinedev.practice.util.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.menu.EventSelectMenu;
import xyz.refinedev.practice.event.menu.EventTeamMenu;
import xyz.refinedev.practice.party.menu.PartyEventMenu;
import xyz.refinedev.practice.party.menu.PartySettingsMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.menu.KEMenu;
import xyz.refinedev.practice.profile.killeffect.menu.KEPaginatedMenu;
import xyz.refinedev.practice.profile.menu.ProfileMenu;
import xyz.refinedev.practice.profile.settings.menu.ProfileSettingsMenu;
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

    private final String[] configNames = {"general", "profile_menu", "profile_settings", "profile_killeffects",
                                          "profile_history", "party_events", "event_size", "event_host"};

    private final Map<String, FoldersConfigurationFile> configs = new HashMap<>();
    private final Map<UUID, Menu> openedMenus = new HashMap<>();
    private final List<MenuData> menuData = new ArrayList<>();

    public void init() {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/menu");
        File[] files = file.listFiles();

        if (files == null) {
            this.generateDefaultMenus();
            return;
        }

        this.checkMissingConfigs(files);

        for ( File menuFile : files ) {
            MenuConfigurationFile menuConfig = new MenuConfigurationFile(menuFile.getName().replace(".yml", ""), plugin);
            MenuData menu = new MenuData();

            menu.setName(menuFile.getName());
            menu.setTitle(menuConfig.getString("TITLE"));
            menu.setSize(menuConfig.getInteger("SIZE"));
            menu.setPaginated(menuConfig.getBoolean("PAGINATED"));
            menu.setPlaceholder(menuConfig.getBoolean("PLACEHOLDER"));

            if (menu.isPlaceholder()) {
                Material material;

                try {
                    material = Material.valueOf(menuConfig.getString("PLACEHOLDER_BUTTON.MATERIAL"));
                } catch (Exception e) {
                    plugin.logger("Invalid Placeholder Button on Menu " + menu.getName() + ", turning off placeholder mode.");
                    menu.setPlaceholder(false);
                    return;
                }

                ItemBuilder itemBuilder = new ItemBuilder(material);
                itemBuilder.name(menuConfig.getString("PLACEHOLDER_BUTTON.NAME"));
                if (menuConfig.getInteger("PLACEHOLDER_BUTTON.DATA") != 0) itemBuilder.durability(menuConfig.getInteger("PLACEHOLDER_BUTTON.DATA"));
                itemBuilder.lore(menuConfig.getStringList("PLACEHOLDER_BUTTON.LORE"));
                itemBuilder.clearFlags();

                menu.setPlaceholderItem(itemBuilder.build());
            }

            menu.setButtons(this.loadCustomButtons(menuConfig));

            menuData.add(menu);
        }

        //Async cuz yknow we don't wanna diturb the main thread and besides this stuff gets updated way too often
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new MenuUpdateTask(plugin), 5L, 5L);
    }

    /**
     * Generate the default configs inside our jar
     */
    public void generateDefaultMenus() {
        for ( String config : configNames ) {
            FoldersConfigurationFile loadedConfig = new FoldersConfigurationFile(plugin,"menu", config);
            this.configs.put(config, loadedConfig);
        }
    }

    /**
     * Check which default configs are missing from
     * the menu folder and create them again
     *
     * @param files {@link File} list of files from the menu folder
     */
    public void checkMissingConfigs(File[] files) {
        List<String> fileNames = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        for ( String name : configNames ) {
            if (fileNames.contains(name)) continue;

            FoldersConfigurationFile loadedConfig = new FoldersConfigurationFile(plugin, "menu", name);
            this.configs.put(name, loadedConfig);
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
     * Load custom buttons from a menu config
     *
     * @param config) {@link BasicConfigurationFile} menu config
     * @return {@link List} of {@link ButtonData}
     */
    public List<ButtonData> loadCustomButtons(FoldersConfigurationFile config) {
        List<ButtonData> customButtons = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("CUSTOM_BUTTONS");
        if (section == null || section.getKeys(false).isEmpty()) return null;

        for ( String key : section.getKeys(false) ){
            ButtonData buttonData = new ButtonData();

            Material material;
            try {
                material = Material.valueOf(section.getString(key + ".MATERIAL"));
            } catch (Exception e) {
                System.out.println("Button " + key + "'s Material is invalid, ignoring button...");
                continue;
            }
            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.name(section.getString(key + ".NAME"));
            itemBuilder.clearFlags();
            if (section.getInt(key + ".DATA") != 0) {
                itemBuilder.durability(section.getInt(key + ".DATA"));
            }
            if (section.getStringList(key + ".LORE") != null && !section.getStringList(key + ".LORE").isEmpty()) {
                itemBuilder.lore(section.getStringList(key + ".LORE"));
            }
            ItemStack itemStack = itemBuilder.build();
            buttonData.setItem(itemStack);
            buttonData.setSlot(section.getInt(key + ".SLOT"));
            buttonData.getActions().addAll(this.loadActionData(key, config));

            customButtons.add(buttonData);
        }

        return customButtons;
    }

    /**
     * Load a {@link ButtonData}'s list of ActionData
     *
     * @param buttonKey The key of the button in the config
     * @param config The config file of the menu
     * @return {@link List} of ActionData
     */
    public List<ActionData> loadActionData(String buttonKey, FoldersConfigurationFile config) {
        ConfigurationSection configurationSection = config.getConfigurationSection("CUSTOM_BUTTONS." + buttonKey + ".ACTIONS");
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return null;

        List<ActionData> actionData = new ArrayList<>();

        for ( String key : configurationSection.getKeys(false) ) {
            String path = "CUSTOM_BUTTONS." + buttonKey + ".ACTIONS." + key;

            ActionType actionType;
            try {
                actionType = ActionType.valueOf(config.getString(path + ".ACTION_TYPE"));
            } catch (Exception e) {
                plugin.getLogger().info("Button " + buttonKey + "'s Action Type is invalid, ignoring action...");
                continue;
            }
            ActionData data = new ActionData(actionType, config.getString(path + ".CLICK_TYPE"), config.getString(path + ".ACTION"));
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
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        switch (name) {
            case "event_team": {
                if (profile.isInEvent()) {
                    Event event = this.plugin.getEventManager().getEventByUUID(profile.getEvent());
                    return new EventTeamMenu(event);
                }
                break;
            }
            case "kill_effects": {
                if (this.getConfigByName("kill_effects").getBoolean("PAGINATED")) {
                    return new KEPaginatedMenu(plugin);
                } else {
                    return new KEMenu(plugin);
                }
            }
            case "event_host": return new EventSelectMenu(plugin);
            case "party_events": return new PartyEventMenu(plugin);
            case "party_settings": return new PartySettingsMenu(plugin);
            case "profile_menu": return new ProfileMenu(plugin, player);
            case "profile_settings": return new ProfileSettingsMenu(plugin);
            default: {
                MenuData menuData = getMenuDataByName(name);
                if (menuData == null) return null;
                if (menuData.isPaginated()) {
                    return new CustomPaginatedMenu(menuData);
                } else {
                    return new CustomMenu(menuData);
                }
            }
        }
        return null;
    }

}
