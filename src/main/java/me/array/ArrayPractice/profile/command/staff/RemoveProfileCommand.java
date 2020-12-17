

package me.array.ArrayPractice.profile.command.staff;

import me.array.ArrayPractice.Array;
import org.bson.Document;
import org.bukkit.ChatColor;
import com.qrakn.honcho.command.CPL;
import org.bukkit.command.CommandSender;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "removeprofile" }, permission = "practice.admin")
public class RemoveProfileCommand
{
    public void execute(final CommandSender player, @CPL("name") final String name) {
        if (name == null) {
            player.sendMessage(ChatColor.RED + "That name is not valid");
        }
        try {
            Array.get().getMongoDatabase().getCollection("profiles").deleteOne(new Document("name", name));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.RED + "Deleted: " + name);
    }
}
