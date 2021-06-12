package xyz.refinedev.practice.kit.kiteditor;

import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.profile.ProfileState;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KitEditor {

    private boolean active;
    private boolean rename;
    private ProfileState previousState;
    private Kit selectedKit;
    private KitInventory selectedKitInventory;

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

}
