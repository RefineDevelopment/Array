package xyz.refinedev.practice.util.command;

import com.google.common.base.Preconditions;
import xyz.refinedev.practice.util.command.command.DrinkCommandService;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is the main class of Drink
 * Drink can be shaded or used as a plugin
 * This class provides the plugin functionality
 * As well, this class can be used to get an instance of a
 * {@link CommandService} for your plugin to registerCommands cmds via.
 *
 * @author jonahseguin
 */
public class Drink {

    private static final ConcurrentMap<String, CommandService> services = new ConcurrentHashMap<>();

    /**
     * Get a {@link CommandService} instance to registerCommands cmds via
     * - JavaPlugin specific (one per plugin instance)
     *
     * @param javaPlugin {@link Nonnull} your {@link JavaPlugin} instance
     * @return The {@link CommandService} instance
     */
    public static CommandService get(@Nonnull JavaPlugin javaPlugin) {
        Preconditions.checkNotNull(javaPlugin, "JavaPlugin cannot be null");
        return services.computeIfAbsent(javaPlugin.getName(), name -> new DrinkCommandService(javaPlugin));
    }

}
