package me.drizzy.practice.array.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bson.Document;
import org.bukkit.entity.Player;

@CommandMeta(label={"array reset", "array resetstats"}, permission="array.dev")
public class ArrayResetStatsCommand {
    public void execute(Player p, @CPL("profile") String name) {
        if (name == null) {
            p.sendMessage(CC.RED + "Either that player does not exist or you did not specify a name!");
        }
        try {
            Array.getInstance().getMongoDatabase().getCollection("profiles").deleteOne(new Document("name", name));
            PlayerUtil.getPlayer(name).kickPlayer("Your Profile was reset by an Admin, Please Rejoin!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
