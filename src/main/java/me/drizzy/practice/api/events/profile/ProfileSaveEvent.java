package me.drizzy.practice.api.events.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.events.BaseEvent;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/24/2021
 * Project: Array
 */

@Getter
@Setter
@AllArgsConstructor
public class ProfileSaveEvent extends BaseEvent {

    private final Profile profile;
}
