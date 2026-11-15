package com.homelens.dto;

import com.homelens.model.InquiryStatus;
import jakarta.validation.constraints.NotNull;

public class InquiryStatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private InquiryStatus status;

    public InquiryStatusUpdateRequest() {
    }

    public InquiryStatusUpdateRequest(InquiryStatus status) {
        this.status = status;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }
}
