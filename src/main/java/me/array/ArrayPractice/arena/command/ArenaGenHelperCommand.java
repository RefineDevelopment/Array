package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena genhelper", permission = "practice.staff")
public class ArenaGenHelperCommand {

	public void execute(Player player) {
		Block origin = player.getLocation().getBlock();
		Block up = origin.getRelative(BlockFace.UP);

		origin.setType(Material.SPONGE);
		up.setType(Material.SIGN_POST);

		if (up.getState() instanceof Sign) {
			Sign sign = (Sign) up.getState();
			sign.setLine(0, ((int) player.getLocation().getPitch()) + "");
			sign.setLine(1, ((int) player.getLocation().getYaw()) + "");
			sign.update();

			player.sendMessage(CC.GREEN + "Generator helper placed.");
		}
	}

}
