package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cancel", permission = "array.staff")
public class BracketsCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getBracketsManager().getActiveBrackets() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Brackets events.");
			return;
		}

		Profile.getProfiles().values().stream().filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
		Profile.getProfiles().values().stream().filter(Profile::isInQueue).forEach(Profile::refreshHotbar);
		Array.getInstance().getBracketsManager().getActiveBrackets().end();
	}

}
