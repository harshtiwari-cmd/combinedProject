package com.digi.common.domain.model;

public enum NotificationType {
    EMAIL("EMAIL"),
    SMS("SMS"),
    PUSH("PUSH"),
    WEBHOOK("WEBHOOK"),
    IN_APP("IN_APP");
    
    private final String value;
    
    NotificationType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static NotificationType fromString(String text) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant found for: " + text);
    }
}
