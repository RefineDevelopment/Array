package me.array.ArrayPractice.event.impl.tournament.command;

import me.array.ArrayPractice.event.impl.tournament.Tournament;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = "tournament host", permission = "tournament.host")
public class TournamentHostCommand {

	public void execute(Player player, @CPL("ladder") String ladder, @CPL("team size") Integer size) {
		if(Tournament.CURRENT_TOURNAMENT != null){
			player.sendMessage(ChatColor.RED + "The Tournament has already started");
			return;
		}
		Tournament.CURRENT_TOURNAMENT = new Tournament();
		if (size == 1 || size == 2) {
			if (size == 1) Tournament.CURRENT_TOURNAMENT.setTeamCount(1);
			else Tournament.CURRENT_TOURNAMENT.setTeamCount(2);
		} else {
			player.sendMessage(ChatColor.RED + "Please choose 1 or 2");
			Tournament.CURRENT_TOURNAMENT.cancel();
			Tournament.CURRENT_TOURNAMENT = null;
			return;
		}
		if (Kit.getByName(ladder) != null) {
			Tournament.CURRENT_TOURNAMENT.setLadder(Kit.getByName(ladder));
		} else {
			player.sendMessage(ChatColor.RED + "Please choose a valid kit");
			Tournament.CURRENT_TOURNAMENT.cancel();
			Tournament.CURRENT_TOURNAMENT = null;
			return;
		}
		String type = Tournament.CURRENT_TOURNAMENT.getTeamCount() + "vs" + Tournament.CURRENT_TOURNAMENT.getTeamCount();
		Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(Tournament) " + CC.WHITE + "A " + ChatColor.GOLD + ChatColor.BOLD + type +  Tournament.CURRENT_TOURNAMENT.getLadder().getName() + ChatColor.WHITE + " tournament is being hosted by " + CC.translate(CC.AQUA + Array.get().getChat().getPlayerPrefix(player) + player.getName()));
		broadcastMessage(CC.AQUA + CC.BOLD + "(Tournament) " + ChatColor.GREEN + "(Click to accept)");

		Tournament.RUNNABLE = new BukkitRunnable(){
			private int countdown = 60;
			@Override
			public void run() {
				countdown--;
				if(countdown % 10 == 0 || countdown <= 10){
					if (countdown > 0) {
						Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(Tournament) "  + CC.WHITE + "The Tournament is starting in " + ChatColor.GOLD + countdown + ChatColor.WHITE + " seconds. ");
						broadcastMessage(CC.AQUA + CC.BOLD + "(Tournament) " + ChatColor.GREEN + "(Click to accept)");
					}
				}
				if(countdown <= 0){
					Tournament.RUNNABLE = null;
					cancel();
					if(Tournament.CURRENT_TOURNAMENT.getParticipatingCount() < 2){
						Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(Tournament) " + CC.RED + "The Tournament has been cancelled .");
						Tournament.CURRENT_TOURNAMENT.cancel();
						Tournament.CURRENT_TOURNAMENT = null;
					}else {
						Tournament.CURRENT_TOURNAMENT.tournamentstart();;
					}
				}
			}
		};
		Tournament.RUNNABLE.runTaskTimer(Array.get() , 20 , 20);
	}
	private static void broadcastMessage(String message){
		BaseComponent[] component = TextComponent.fromLegacyText(message);
		for(BaseComponent baseComponent : component){
			baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/tournament join"));
			baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the Tournament")));
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			player.spigot().sendMessage(component);
		}
	}
}


