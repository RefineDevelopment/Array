package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.TournamentManager;
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

	public void execute(Player player, @CPL("team size") Integer size) {
		if(TournamentManager.CURRENT_TOURNAMENT != null){
			player.sendMessage(ChatColor.RED + "The TournamentManager has already started");
			return;
		}
		TournamentManager.CURRENT_TOURNAMENT = new TournamentManager();
		if (size == 1 || size == 2) {
			if (size == 1) TournamentManager.CURRENT_TOURNAMENT.setTeamCount(1);
			else TournamentManager.CURRENT_TOURNAMENT.setTeamCount(2);
		} else {
			player.sendMessage(ChatColor.RED + "Please choose 1 or 2");
			TournamentManager.CURRENT_TOURNAMENT.cancel();
			TournamentManager.CURRENT_TOURNAMENT = null;
			return;
		}
		if (Kit.getByName("NoDebuff") != null) {
			TournamentManager.CURRENT_TOURNAMENT.setLadder(Kit.getByName("NoDebuff"));
		} else {
			player.sendMessage(ChatColor.RED + "The Kit 'NoDebuff' does not exist, Please contact an Admin!");
			TournamentManager.CURRENT_TOURNAMENT.cancel();
			TournamentManager.CURRENT_TOURNAMENT = null;
			return;
		}
		String type = TournamentManager.CURRENT_TOURNAMENT.getTeamCount() + "vs" + TournamentManager.CURRENT_TOURNAMENT.getTeamCount();
		Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(TournamentManager) " + CC.WHITE + "A " + ChatColor.GOLD + ChatColor.BOLD + type +  TournamentManager.CURRENT_TOURNAMENT.getLadder().getName() + ChatColor.WHITE + " tournament is being hosted by " + CC.translate(CC.AQUA + Array.get().getChat().getPlayerPrefix(player) + player.getName()));
		broadcastMessage(CC.AQUA + CC.BOLD + "(TournamentManager) " + ChatColor.GREEN + "(Click to accept)");

		TournamentManager.RUNNABLE = new BukkitRunnable(){
			private int countdown = 60;
			@Override
			public void run() {
				countdown--;
				if(countdown % 10 == 0 || countdown <= 10){
					if (countdown > 0) {
						Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(TournamentManager) "  + CC.WHITE + "The TournamentManager is starting in " + ChatColor.GOLD + countdown + ChatColor.WHITE + " seconds. ");
						broadcastMessage(CC.AQUA + CC.BOLD + "(TournamentManager) " + ChatColor.GREEN + "(Click to accept)");
					}
				}
				if(countdown <= 0){
					TournamentManager.RUNNABLE = null;
					cancel();
					if(TournamentManager.CURRENT_TOURNAMENT.getParticipatingCount() < 2){
						Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "(TournamentManager) " + CC.RED + "The TournamentManager has been cancelled .");
						TournamentManager.CURRENT_TOURNAMENT.cancel();
						TournamentManager.CURRENT_TOURNAMENT = null;
					}else {
						TournamentManager.CURRENT_TOURNAMENT.tournamentstart();
					}
				}
			}
		};
		TournamentManager.RUNNABLE.runTaskTimer(Array.get() , 20 , 20);
	}
	private static void broadcastMessage(String message){
		BaseComponent[] component = TextComponent.fromLegacyText(message);
		for(BaseComponent baseComponent : component){
			baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/tournament join"));
			baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the TournamentManager")));
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			player.spigot().sendMessage(component);
		}
	}
}


