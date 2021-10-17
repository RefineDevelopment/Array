package xyz.refinedev.practice.util.other;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Listener;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.CommandsManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

@UtilityClass
public class ClassUtil {

    private final Array plugin = Array.getInstance();

    public void registerListeners(String packageName) {
        for (Class<?> clazz : getClassesInPackage(packageName)) {
            if (isListener(clazz)) {
                try {
                    Constructor<?> constructor = clazz.getDeclaredConstructor(Array.class);
                    constructor.setAccessible(true);
                    Listener listener = (Listener) constructor.newInstance(plugin);
                    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                    // production code should handle these exceptions more gracefully
                } catch (ReflectiveOperationException x) {
                    x.printStackTrace();
                }
            }
        }
        plugin.logger("&7Registering listeners...");
    }

    ///TODO: Change to reflection
    public void registerCommands(String packageName) {
        CommandsManager commandsManager = new CommandsManager(plugin, plugin.getDrink());
        commandsManager.init();

        plugin.logger("&7Registering commands...");
    }

    public boolean isListener(Class<?> clazz) {
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (interfaze == Listener.class) {
                return true;
            }
        }
        return false;
    }

    public Collection<Class<?>> getClassesInPackage(String packageName) {
        JarFile jarFile;
        Collection<Class<?>> classes = new ArrayList<>();
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();

        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;
            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }
            if (className != null) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ImmutableSet.copyOf(classes);
    }
}
