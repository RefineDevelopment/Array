package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets setspawn", permission = "practice.staff")
public class BracketsSetSpawnCommand {

	public void execute(Player player, @CPL("one/two/spec") String position) {
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be one/two/spec.");
		} else {
			if (position.equals("one")) {
				Practice.get().getBracketsManager().setBracketsSpawn1(player.getLocation());
			} else if (position.equals("two")){
				Practice.get().getBracketsManager().setBracketsSpawn2(player.getLocation());
			} else {
				Practice.get().getBracketsManager().setBracketsSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated brackets's spawn location " + position + ".");

			Practice.get().getBracketsManager().save();
		}
	}

}
