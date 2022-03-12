package xyz.refinedev.practice.hook.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 3/9/2022
 * Project: Array
 */

@Data
public class HologramMeta {

    private Hologram hologram;
    private Location location;
    private World world;
    private String name;
    private HologramType type;
}
