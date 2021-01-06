package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

@CommandMeta(label = "getloc", permission = "smok.staff.location")
public class GetLocationCommand {

    public void execute(Player player) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        double yaw = player.getLocation().getYaw();
        double pitch = player.getLocation().getPitch();

        player.sendMessage(CC.translate("&cX: " + player.getLocation().getX()));
        player.sendMessage(CC.translate("&cY: " + player.getLocation().getY()));
        player.sendMessage(CC.translate("&cZ: " + player.getLocation().getZ()));
        player.sendMessage(CC.translate("&cYAW: " + player.getLocation().getYaw()));
        player.sendMessage(CC.translate("&cPITCH: " + player.getLocation().getPitch()));
        player.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a(Click to copy)")
                .attachToEachPart(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, x + " " + y + " " + z + " " + yaw + " " + pitch))
                .create());

    }
}
