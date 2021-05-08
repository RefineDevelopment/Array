package me.drizzy.practice.essentials.meta;

import me.drizzy.practice.Array;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Nick_0251 @ColdDev
 * PiracyMeta System Created By Nick
 * Redistribution of this is PROHIBITED
 */
public class PiracyMeta {
    
    private final Plugin plugin;
    private String key;

    public PiracyMeta(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = key;
        register();
    }

    public void register() {
        Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
        Array.logger("Checking license....");
        try {
            if (key.equals("")) key = "XXXX-XXXX-XXXX";
            
            String LICENSE_CHECK_URL="https://licenses.colddev.cf/api/validate";
            URL url = new URL(LICENSE_CHECK_URL + "/" + key + "/" + plugin.getName() + "/");
            
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            InputStream is = con.getInputStream();
            
            Scanner s = new Scanner((new InputStreamReader(is)));
            if (s.hasNext()) {
                String response = s.next();
                s.close();
                if (response.equalsIgnoreCase("Success")) {
                    Array.logger("&aLicense valid!");
                    Array.logger("&aBy using this plugin you agree to our TOS!");
                    Array.logger("&cArray &7is made by &c&lPurge Development &7at &c&ohttps://discord.gg/VXzUMfBefZ");
                    Array.logger(" ");
                    Array.logger("&7&oNote");
                    Array.logger("&7&oIf this is a cracked copy then please don't use it,");
                    Array.logger("&7&oWe work very hard to make this plugin affordable!");
                    Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
                    } else {
                    Array.logger("&c&lLicense is NOT Valid!");
                    Array.logger("&cFailed as a result of:" + response);
                    Array.logger(" ");
                    Array.logger("&4Plugin is probably leaked, Disabling Array!");
                    Array.logger("&7Contact support at our discord for license issues");
                    Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
                    Bukkit.getScheduler().cancelTasks(plugin);
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    }
            } else {
                s.close();
                Array.logger("&c&lLicense is NOT Valid!");
                Array.logger("&cFailed as a result of: Licensing server offline?");
                Array.logger(" ");
                Array.logger("&7Contact support at our discord for license issues");
                Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
                Bukkit.getScheduler().cancelTasks(plugin);
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        } catch (IOException e) {
            Array.logger("&c&lLicense is NOT Valid!");
            Array.logger("&cFailed as a result of: Licensing server offline?");
            Array.logger(" ");
            Array.logger("&7Contact support at our discord for license issues");
            Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
            Bukkit.getScheduler().cancelTasks(plugin);
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}

