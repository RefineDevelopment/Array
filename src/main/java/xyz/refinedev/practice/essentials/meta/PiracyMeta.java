package xyz.refinedev.practice.essentials.meta;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.essentials.Essentials;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PiracyMeta {
    
    private final Array plugin;
    private final String key;

    public PiracyMeta(Array plugin, String key) {
        this.plugin = plugin;
        this.key = key;
        register();
    }

    public void register() {
        Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
        Array.logger("Checking license....");

        try {
            URL url = new URL("http://backend.refinedev.xyz:8080/api/checklicense?license=" + key + "&pluginname=" + plugin.getName() + "&ip=" + getMachineIp());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() == 200) {
                Array.logger("&aLicense valid!");
                Array.logger("&aBy using this plugin you agree to our TOS!");
                Array.logger("&c" + plugin.getName() + " &7is made by &c&lRefine Development");
                Array.logger(" ");
                Array.logger("&7Note:");
                Array.logger("&7If this is a cracked copy then please don't use it,");
                Array.logger("&7We work very hard to make this plugin affordable!");
                Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
                Essentials.setupEssentials();
            } else {
                Array.logger("&c&lLicense could not be Validated!");
                Array.logger("&cLicense key was NOT found");
                Array.logger(" ");
                Array.logger("&4Plugin is probably leaked, Disabling Array!");
                Array.logger("&7Contact support at our discord for license issues");
                Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
                Bukkit.shutdown();
            }
        } catch (IOException e) {
            Array.logger("&c&lLicense could not be Validated!");
            Array.logger("&cLicense server offline?");
            Array.logger(" ");
            Array.logger("&4Plugin is probably leaked, Disabling Array!");
            Array.logger("&7Contact support at our discord for license issues");
            Array.logger("&7&m---------------&8[&c" + plugin.getName() + "&7-&cLicense&8]&7&m----------------");
            e.printStackTrace();
            System.exit(0);
            Bukkit.shutdown();
        }
    }

    public static String getMachineIp() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        return new BufferedReader(new InputStreamReader(whatismyip.openStream())).readLine();
    }
}

