package xyz.refinedev.practice.util.menu;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import xyz.refinedev.practice.util.config.AbstractConfigurationFile;
import xyz.refinedev.practice.util.menu.custom.action.ActionType;

import java.util.logging.Level;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/29/2022
 * Project: Array
 */

@UtilityClass
public class ButtonUtil {

    public Material getMaterial(AbstractConfigurationFile config, String path) {
        Material material;
        try {
            material = Material.valueOf(config.getString(path + ".MATERIAL"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Error in " + path + " configuration, please fix syntax errors!");
            return null;
        }
        return material;
    }

    public Material getMaterial(AbstractConfigurationFile config, String path, String key) {
        Material material;
        try {
            material = Material.valueOf(config.getString(path + ".MATERIAL"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Button " + key + "'s Material is invalid, ignoring button...");
            return null;
        }
        return material;
    }

    public Material getCustomButton(AbstractConfigurationFile config, String path, String key) {
        Material material;
        try {
            material = Material.valueOf(config.getString(path));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Button " + key + "'s Material is invalid, ignoring button...");
            return null;
        }
        return material;
    }

    public Material getPlaceholderMaterial(AbstractConfigurationFile config, String menu) {
        Material material;
        try {
            material = Material.valueOf(config.getString("PLACEHOLDER_BUTTON.MATERIAL"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid Placeholder Button on Menu " + menu + ", turning off placeholder mode.");
            return null;
        }
        return material;
    }

    public Material getPlaceholderMaterial(AbstractConfigurationFile config, String menu, String key) {
        Material material;
        try {
            material = Material.valueOf(config.getString(key + ".PLACEHOLDER_BUTTON.MATERIAL"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid Placeholder Button on Menu " + menu + ", turning off placeholder mode.");
            return null;
        }
        return material;
    }

    public ActionType getAction(AbstractConfigurationFile config, String path, String key) {
        ActionType actionType;
        try {
            actionType = ActionType.valueOf(config.getString(path + ".ACTION_TYPE"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Button " + key + "'s Action Type is invalid, ignoring action...");
            return null;
        }
        return actionType;
    }
}
