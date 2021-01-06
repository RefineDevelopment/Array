package me.array.ArrayPractice.util.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.TaskUtil;
import me.array.ArrayPractice.util.external.CC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@Getter
public class HologramUtils {

    private Location holoLoc;
    private Hologram hologram;

    public void HologramUtils(Location location) {
        this.holoLoc = location;
        TaskUtil.runSync(() -> HologramUtils.this.hologram = HologramsAPI.createHologram(Practice.get(), holoLoc));
        switchLadderHolo();

    }

    public void HologramLadder(String Ladder, Location Location) {
        this.holoLoc = Location;
        TaskUtil.runAsync(() -> HologramUtils.this.hologram = HologramsAPI.createHologram(Practice.get(), holoLoc));

        ladderHolo(Ladder);
    }

    public void HologramDefault(Location Location) {
        this.holoLoc = Location;
        TaskUtil.runAsync(() -> HologramUtils.this.hologram = HologramsAPI.createHologram(Practice.get(), holoLoc));

       createDefaultLadderHolo();
    }

    public void createDefaultLadderHolo() {
        TaskUtil.runAsync(() -> {
            if (!this.holoLoc.getChunk().isLoaded())
                holoLoc.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&b&lNoDebuff &7Leaderboards"));
            hologram.appendTextLine(CC.translate("&7"));
            for (int i = 1; i <= 10; i++) {
                hologram.appendTextLine(CC.translate("%practice_lb_NoDebuff_" + i + "%"));
            }
        });
    }

    public void switchLadderHolo() {
        TaskUtil.runSync(() -> {

            String ladder = getNextLadder();

            hologram.clearLines();

            hologram.appendTextLine(CC.translate("&b&l" + ladder + " &7Leaderboards"));
            hologram.appendTextLine(CC.translate("&7"));
            for (int i = 1; i <= 10; i++) {
                hologram.appendTextLine(CC.translate("%practice_lb_" + ladder + "_" + i + "%"));
            }
        });
    }

    public void ladderHolo(String Ladder) {
        TaskUtil.runSync(() -> {

            hologram.clearLines();

            hologram.appendTextLine(CC.translate("&b&l" + Ladder + " &7Leaderboards"));
            hologram.appendTextLine(CC.translate("&7"));
            for (int i = 1; i <= 10; i++) {
                hologram.appendTextLine(CC.translate("%practice_lb_" + Ladder + "_" + i + "%"));
            }
        });
    }

    public String getNextLadder() {
        String currentLadder = ChatColor.stripColor(((TextLine) hologram.getLine(0)).getText()).split(" ")[0];
        switch (currentLadder) {
            case "NoDebuff":
                return "Debuff";
            case "Debuff":
                return "Gapple";
            case "Gapple":
                return "Combo";
            case "Combo":
                return "BuildUHC";
            case "BuildUHC":
                return "BoxFight";
            case "BoxFight":
                return "Classic";
            case "Classic":
                return "AxePvP";
            case "AxePvP":
                return "Vanilla";
            case "Vanilla":
                return "Soup";
            case "Soup":
                return "SoupRefill";
            case "SoupRefill":
                return "NoDebuff";
            default:
                return "";
        }
    }
}
