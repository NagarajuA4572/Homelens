package com.homelens.model;

public enum BhkType {
    ONE_RK("1 RK"),
    ONE_BHK("1 BHK"),
    TWO_BHK("2 BHK"),
    THREE_BHK("3 BHK"),
    FOUR_BHK("4 BHK"),
    FOUR_PLUS_BHK("4+ BHK");

    private final String displayName;

    BhkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BhkType fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        for (BhkType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName.trim()) || type.name().equalsIgnoreCase(displayName.trim())) {
                return type;
            }
        }
        return null;
    }
}
