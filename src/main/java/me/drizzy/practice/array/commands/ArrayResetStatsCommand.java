package me.drizzy.practice.array.commands;

import com.mongodb.client.model.Filters;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = {"array reset", "array resetstats"}, permission = "array.dev")
public class ArrayResetStatsCommand {

    public void execute(Player player, @CPL("profile") String name) {

        if (name == null) {
            player.sendMessage(CC.RED + "Either that player does not exist or you did not specify a name!");
            return;
        }

        UUID uuid = ArrayCache.getUUID(name);
        
        if (uuid == null) {
            player.sendMessage(CC.RED + "That player is not in our database!");
            return;
        }

        try {
            Array.getInstance().getMongoDatabase().getCollection("profiles").deleteOne(Filters.eq("uuid", uuid.toString()));
            PlayerUtil.getPlayer(name).kickPlayer("Your Profile was reset by an Admin, Please Rejoin!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
