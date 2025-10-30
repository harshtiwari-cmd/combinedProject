package com.digi.common.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for field name-value pairs
 * Accepts both "key/value" (from API requests) and "fieldName/fieldValue" (for internal use)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldValueDto {
    @JsonProperty(value = "fieldName", access = JsonProperty.Access.READ_WRITE)
    private String fieldName;
    
    @JsonProperty(value = "fieldValue", access = JsonProperty.Access.READ_WRITE)
    private String fieldValue;
    
    /**
     * Alternative constructor accepting key/value naming
     * @param key the field name
     * @param value the field value
     */
    public FieldValueDto(String key, String value, boolean useKeyValue) {
        this.fieldName = key;
        this.fieldValue = value;
    }
    
    /**
     * Getter for key (alias for fieldName) - for JSON deserialization compatibility
     */
    @JsonProperty("key")
    public String getKey() {
        return fieldName;
    }
    
    /**
     * Setter for key (alias for fieldName) - for JSON deserialization compatibility
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.fieldName = key;
    }
    
    /**
     * Getter for value (alias for fieldValue) - for JSON deserialization compatibility
     */
    @JsonProperty("value")
    public String getValue() {
        return fieldValue;
    }
    
    /**
     * Setter for value (alias for fieldValue) - for JSON deserialization compatibility
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.fieldValue = value;
    }
}