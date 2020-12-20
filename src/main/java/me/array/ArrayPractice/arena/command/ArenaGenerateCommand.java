package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.arena.generator.ArenaGenerator;
import me.array.ArrayPractice.arena.generator.Schematic;
import me.array.ArrayPractice.arena.impl.StandaloneArena;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import java.io.File;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = "arena generate", permission = "practice.staff", async = true)
public class ArenaGenerateCommand {

	public void execute(CommandSender sender) {
		File schematicsFolder = new File(Array.get().getDataFolder().getPath() + File.separator + "schematics");
		if (!schematicsFolder.exists()) {
			sender.sendMessage(CC.RED + "The schematics folder does not exist.");
		} else {
		 sender.sendMessage("Found Schematics folder, searching for schematics.");
		}

		for (File file : Objects.requireNonNull(schematicsFolder.listFiles()) ) {
			if (!file.exists()) {
				sender.sendMessage("No schematics found!");
				return;
			}

			if (!file.isDirectory() && file.getName().contains(".schematic")) {
				boolean duplicate = file.getName().endsWith("_duplicate.schematic");

				String name = file.getName()
				                  .replace(".schematic", "")
				                  .replace("_duplicate", "");

				Arena parent = Arena.getByName(name);

				if (parent != null) {
					if (!(parent instanceof StandaloneArena)) {
						System.out.println("Skipping " + name + " because it's not duplicate and an arena with that name already exists.");
						continue;
					}
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							new ArenaGenerator(name, Bukkit.getWorlds().get(0), new Schematic(file), duplicate ?
									(parent != null ? ArenaType.DUPLICATE : ArenaType.STANDALONE) : ArenaType.SHARED)
									.generate(file, (StandaloneArena) parent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.runTask(Array.get());
			}
		}

		sender.sendMessage(CC.GREEN + "Generating arenas... See console for details.");
	}

}
