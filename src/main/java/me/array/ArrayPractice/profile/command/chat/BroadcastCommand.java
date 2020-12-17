

package me.array.ArrayPractice.profile.command.chat;

import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Bukkit;
import com.qrakn.honcho.command.CPL;
import org.bukkit.command.CommandSender;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "bc", "broadcast" }, permission = "essentials.broadcast")
public class BroadcastCommand
{
    public void execute(final CommandSender sender, @CPL("message") final String message) {
        Bukkit.broadcastMessage(CC.translate("&c&l(Broadcast) &f" + message));
    }
}
