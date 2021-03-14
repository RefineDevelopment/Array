package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "kit save", permission = "array.dev")
public class KitSaveCommand {

    public void execute(CommandSender sender) {
        for ( Kit kit : Kit.getKits() ) {
            kit.save();
        }
        sender.sendMessage(CC.translate("&8[&b&lArray&8] &7You saved &b" + Kit.getKits().size() +  " the kits!"));
    }
}
