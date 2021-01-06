package me.array.ArrayPractice.profile.meta.option;

public class ProfileOptions
{
    private boolean showScoreboard;
    private boolean receiveDuelRequests;
    private boolean allowSpectators;
    private boolean privateMessages;
    private boolean lightning;
    
    public ProfileOptions() {
        this.showScoreboard = true;
        this.receiveDuelRequests = true;
        this.allowSpectators = true;
        this.privateMessages = true;
        this.lightning = true;
    }
    
    public boolean isShowScoreboard() {
        return this.showScoreboard;
    }
    
    public void setShowScoreboard(final boolean showScoreboard) {
        this.showScoreboard = showScoreboard;
    }
    
    public boolean isReceiveDuelRequests() {
        return this.receiveDuelRequests;
    }
    
    public void setReceiveDuelRequests(final boolean receiveDuelRequests) {
        this.receiveDuelRequests = receiveDuelRequests;
    }
    
    public boolean isAllowSpectators() {
        return this.allowSpectators;
    }
    
    public void setAllowSpectators(final boolean allowSpectators) {
        this.allowSpectators = allowSpectators;
    }
    
    public boolean isPrivateMessages() {
        return this.privateMessages;
    }
    
    public void setPrivateMessages(final boolean privateMessages) {
        this.privateMessages = privateMessages;
    }

    public boolean isLightning() {
        return this.lightning;
    }
    public void setLightning(final boolean lightning) {
        this.lightning = lightning;
    }
}
