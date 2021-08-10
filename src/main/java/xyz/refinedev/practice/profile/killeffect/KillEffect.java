package xyz.refinedev.practice.profile.killeffect;

import lombok.Data;
import org.bukkit.Effect;

import java.util.ArrayList;
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

    private String displayName;
    private String permission;

    private Effect effect;
    private int data;

    private boolean animateDeath;
    private boolean permissionEnabled;
    private boolean dropsClear;
    private boolean lightning;

    private final List<KillEffectSound> killEffectSounds = new ArrayList<>();
    private final List<String> description = new ArrayList<>();
}
