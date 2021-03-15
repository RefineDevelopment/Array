package me.drizzy.practice.kiteditor;

import me.drizzy.practice.kit.Kit;
<<<<<<< Updated upstream:src/main/java/me/drizzy/practice/kiteditor/KitEditor.java
<<<<<<< Updated upstream
import me.drizzy.practice.kit.KitLoadout;
=======
import me.drizzy.practice.kit.KitInventory;
>>>>>>> Stashed changes
=======
import me.drizzy.practice.kit.KitInventory;
>>>>>>> Stashed changes:src/main/java/me/drizzy/practice/profile/meta/ProfileKitEditor.java
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
<<<<<<< Updated upstream:src/main/java/me/drizzy/practice/kiteditor/KitEditor.java
<<<<<<< Updated upstream
    private KitLoadout selectedKitLoadout;
=======
    private KitInventory selectedKitInventory;
>>>>>>> Stashed changes
=======
    private KitInventory selectedKitInventory;
>>>>>>> Stashed changes:src/main/java/me/drizzy/practice/profile/meta/ProfileKitEditor.java

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

}
