package xyz.refinedev.practice.profile.killeffect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Effect;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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

@Getter @Setter
@RequiredArgsConstructor
public class KillEffect {

    private final List<KillEffectSound> killEffectSounds = new ArrayList<>();
    private final List<String> description = Arrays.asList(" &fThis is the default", " &fdescription for kill", " &feffects, you can change", " &fthem in killeffects.yml");

    private final UUID uniqueId;
    private final String name;

    private String displayName;
    private String permission;
    private Effect effect;
    private ItemStack itemStack;
    private int data, priority;

    private boolean enabled;
    private boolean defaultEffect;

    private boolean animateDeath;
    private boolean permissionEnabled;
    private boolean dropsClear;
    private boolean lightning;
}
