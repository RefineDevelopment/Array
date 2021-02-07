package me.drizzy.practice.profile.meta;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitLoadout;
import me.drizzy.practice.profile.ProfileState;
import lombok.Getter;
import lombok.Setter;

@Setter
public class ProfileKitEditor {

    @Getter
    private boolean active;
    private boolean rename;
    @Getter
    private ProfileState previousState;
    @Getter
    private Kit selectedKit;
    @Getter
    private KitLoadout selectedKitLoadout;

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

}
