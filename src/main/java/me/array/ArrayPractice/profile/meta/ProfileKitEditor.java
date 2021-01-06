package me.array.ArrayPractice.profile.meta;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.ProfileState;
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
