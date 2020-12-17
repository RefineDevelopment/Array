package me.array.ArrayPractice.profile.meta;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.ProfileState;

public class ProfileKitEditor
{
    private boolean active;
    private boolean rename;
    private ProfileState previousState;
    private Kit selectedKit;
    private KitLoadout selectedKitLoadout;
    
    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void setRename(final boolean rename) {
        this.rename = rename;
    }
    
    public void setPreviousState(final ProfileState previousState) {
        this.previousState = previousState;
    }
    
    public void setSelectedKit(final Kit selectedKit) {
        this.selectedKit = selectedKit;
    }
    
    public void setSelectedKitLoadout(final KitLoadout selectedKitLoadout) {
        this.selectedKitLoadout = selectedKitLoadout;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public ProfileState getPreviousState() {
        return this.previousState;
    }
    
    public Kit getSelectedKit() {
        return this.selectedKit;
    }
    
    public KitLoadout getSelectedKitLoadout() {
        return this.selectedKitLoadout;
    }
}
