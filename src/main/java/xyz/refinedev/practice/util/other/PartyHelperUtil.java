package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 12/27/2021
 * Project: Array
 */

@UtilityClass
public class PartyHelperUtil {

    public String getRandomClass() {
        List<String> classes = Arrays.asList(
                "Diamond",
                "Bard",
                "Archer",
                "Rogue"
        );
        Collections.shuffle(classes);
        return classes.get(0);
    }
}
