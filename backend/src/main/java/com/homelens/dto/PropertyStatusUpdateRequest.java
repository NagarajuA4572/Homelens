package com.homelens.dto;

import com.homelens.model.PropertyStatus;
import jakarta.validation.constraints.NotNull;

public class PropertyStatusUpdateRequest {

    @NotNull(message = "Property status is required")
    private PropertyStatus status;

    public PropertyStatusUpdateRequest() {
    }

    public PropertyStatusUpdateRequest(PropertyStatus status) {
        this.status = status;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
}
