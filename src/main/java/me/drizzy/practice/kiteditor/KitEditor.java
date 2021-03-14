package me.drizzy.practice.kiteditor;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitLoadout;
import me.drizzy.practice.profile.ProfileState;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KitEditor {

    private boolean active;
    private boolean rename;
    private ProfileState previousState;
    private Kit selectedKit;
    private KitLoadout selectedKitLoadout;

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

}
