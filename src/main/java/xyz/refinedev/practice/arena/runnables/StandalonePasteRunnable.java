package xyz.refinedev.practice.arena.runnables;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.CustomLocation;

/**
 * @since 11/25/2017
 * @author Zonix
 */
@Getter
public class StandalonePasteRunnable extends DuplicateArenaRunnable {

    private final Array plugin;
    private final StandaloneArena copiedArena;

    private int times;
    private int amount;
    private int arenaId = 0;

    public StandalonePasteRunnable(Array plugin, StandaloneArena copiedArena, int copyAmount) {
        super(plugin, copiedArena, 1000, 1000, 500, 500);

        this.plugin = plugin;
        this.times = copyAmount;
        this.copiedArena = copiedArena;
    }

    @Override
    public void run() {
        amount = times;

        while (--times > 0) {
            arenaId++;
            super.run();
        }

        this.message();
    }

    @Override
    public void onComplete() {
        double minX = this.copiedArena.getMin().getX() + this.getOffsetX();
        double minZ = this.copiedArena.getMin().getZ() + this.getOffsetZ();
        double maxX = this.copiedArena.getMax().getX() + this.getOffsetX();
        double maxZ = this.copiedArena.getMax().getZ() + this.getOffsetZ();

        double aX = this.copiedArena.getSpawn1().getX() + this.getOffsetX();
        double aZ = this.copiedArena.getSpawn1().getZ() + this.getOffsetZ();
        double bX = this.copiedArena.getSpawn2().getX() + this.getOffsetX();
        double bZ = this.copiedArena.getSpawn2().getZ() + this.getOffsetZ();

        CustomLocation min = new CustomLocation(this.copiedArena.getSpawn1().getWorld(), minX, this.copiedArena.getMin().getY(), minZ, this.copiedArena.getMin().getYaw(), this.copiedArena.getMin().getPitch());
        CustomLocation max = new CustomLocation(this.copiedArena.getSpawn1().getWorld(), maxX, this.copiedArena.getMax().getY(), maxZ, this.copiedArena.getMax().getYaw(), this.copiedArena.getMax().getPitch());
        CustomLocation a = new CustomLocation(this.copiedArena.getSpawn1().getWorld(), aX, this.copiedArena.getSpawn1().getY(), aZ, this.copiedArena.getSpawn1().getYaw(), this.copiedArena.getSpawn1().getPitch());
        CustomLocation b = new CustomLocation(this.copiedArena.getSpawn1().getWorld(), bX, this.copiedArena.getSpawn2().getY(), bZ, this.copiedArena.getSpawn2().getYaw(), this.copiedArena.getSpawn2().getPitch());

        StandaloneArena duplicate = new StandaloneArena(plugin, this.copiedArena.getName() + "#" + arenaId);
        duplicate.setDuplicate(true);
        duplicate.setSpawn1(a.toBukkitLocation());
        duplicate.setSpawn2(b.toBukkitLocation());
        duplicate.setMax(max.toBukkitLocation());
        duplicate.setMin(min.toBukkitLocation());
        duplicate.setDisplayName(this.copiedArena.getDisplayName());

        this.copiedArena.getDuplicates().add(duplicate);
        plugin.getArenaManager().getArenas().add(duplicate);
    }

    public void message() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("array.arena.admin") || player.isOp()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas."));
            }
        }
        plugin.logger("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas.");
        plugin.getArenaManager().setPasting(false);
        plugin.getArenaManager().getArenas().forEach(plugin.getArenaManager()::save);
    }
}