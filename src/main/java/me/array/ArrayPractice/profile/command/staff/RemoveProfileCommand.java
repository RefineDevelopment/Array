package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "removeprofile", permission = "practice.admin")
public class RemoveProfileCommand {

    public void execute(CommandSender player, @CPL("name") String name) {
        if (name == null) {
            player.sendMessage(ChatColor.RED + "That name is not valid");
        }
        try {
            Practice.getInstance().getMongoDatabase().getCollection("profiles").deleteOne(new Document("name", name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.RED + "Deleted: " + name);
    }

}
