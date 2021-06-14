package xyz.refinedev.practice.api.events.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.events.BaseEvent;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/24/2021
 * Project: Array
 */

@Getter
@Setter
@AllArgsConstructor
public class ProfileGlobalEloCalculateEvent extends BaseEvent {

    private final Profile profile;
}
