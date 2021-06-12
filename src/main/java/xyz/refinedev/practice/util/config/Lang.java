package xyz.refinedev.practice.util.config;

public enum Lang {

    ENGLISH("en"),
    FRENCH("fr"),
    PORTUGUESE("pt"),
    SPANISH("es");
    
    private final String abbreviation;
    
    Lang(final String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public static Lang getByAbbreviation(final String abbreviation) {
        for (final Lang locale : values()) {
            if (locale.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return locale;
            }
        }
        return Lang.ENGLISH;
    }
    
    public static Lang getByName(final String name) {
        for (final Lang locale : values()) {
            if (locale.getAbbreviation().equalsIgnoreCase(name)) {
                return locale;
            }
        }
        return Lang.ENGLISH;
    }
    
    public String getAbbreviation() {
        return this.abbreviation;
    }
}
