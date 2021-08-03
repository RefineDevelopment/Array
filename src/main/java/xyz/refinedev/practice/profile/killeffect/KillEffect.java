package xyz.refinedev.practice.profile.killeffect;

import lombok.Data;
import org.bukkit.Effect;

import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/3/2021
 * Project: Array
 */

@Data
public class KillEffect {

    private final UUID uniqueId;
    private final KillEffectType type;
    
    private String displayName;
    private String permission;
    private Effect effect;
    private List<String> description;
    private LightningEffectType lightningEffectType;
}
