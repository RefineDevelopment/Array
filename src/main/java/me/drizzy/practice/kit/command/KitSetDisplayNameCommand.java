package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit setdisplayname", permission= "array.dev")
public class KitSetDisplayNameCommand {
    public void execute(Player player, @CPL("kit") String kit, @CPL("displayname") String display) {
        Kit dakit = Kit.getByName(kit);
        if (dakit == null) {
            player.sendMessage(CC.translate("&8[&bArray&8] &7A Kit with that name does not exist."));
            return;
        }
        dakit.setDisplayName(display);
        dakit.save();
        player.sendMessage(CC.translate("&8[&bArray&8] &7Successfully updated the kit &b" + dakit.getName() + "'s &7display name."));

    }
}
