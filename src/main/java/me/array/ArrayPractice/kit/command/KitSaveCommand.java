package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "kit save", permission = "practice.kit.create")
public class KitSaveCommand {

    public void execute(CommandSender sender) {
        for ( Kit kit : Kit.getKits() ) {
            kit.save();
        }
        sender.sendMessage(CC.GREEN + "You saved all the kits!");
    }
}
