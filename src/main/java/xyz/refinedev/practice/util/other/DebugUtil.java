package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/6/2021
 * Project: Array
 */

@UtilityClass
public class DebugUtil {

    protected final List<String> developers = Arrays.asList(
            "0272c116-0ff8-4b7f-b40a-eee4749dcec4",
            "2c847402-0dd0-4376-a206-3d3256394e4d",
            "c65c09b0-2405-411f-81d3-d5827a682a84",
            "1634ecf3-22a3-4c1b-8fa0-580ae7aed8a4",
            "d21b096d-2438-4112-a6fd-cfac67775014");

    public void sendDebug(String debug) {
        for ( String developer : developers ) {
            Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().toString().equalsIgnoreCase(developer)).forEach(p -> p.sendMessage(CC.translate(debug)));
        }
    }

    public void sendRawDebug(String debug) {
        for ( String developer : developers ) {
            Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().toString().equalsIgnoreCase(developer)).forEach(p -> p.sendMessage(CC.translate(debug)));
        }
    }

    public boolean isDeveloper(UUID uuid) {
        return developers.contains(uuid.toString());
    }

    public void sendJoinMessage(Array plugin, Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&fThis server is running &c&lArray &fon version &c&l2.0 &f."));
        player.sendMessage(CC.translate("&fLicense: &c" + plugin.getConfigHandler().getLICENSE()));

        if (!Description.getAuthor().contains("RefineDevelopment") || !Description.getAuthor().contains("Nick_0251")) {
            player.sendMessage(CC.translate("&fAuthors have been changed to &c" + Description.getAuthor()));
        }
        if (!Description.getName().equals("Array")) {
            player.sendMessage(CC.translate("&fName has been changed to &c" + Description.getName()));
        }
        player.sendMessage(CC.CHAT_BAR);
    }
}
