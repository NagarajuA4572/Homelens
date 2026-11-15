package com.homelens.dto;

import java.time.Instant;

import com.homelens.model.Inquiry;
import com.homelens.model.InquiryStatus;

public class InquiryResponse {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String message;
    private InquiryStatus status;
    private Instant createdAt;

    public InquiryResponse() {
    }

    public static InquiryResponse fromEntity(Inquiry inquiry) {
        if (inquiry == null) {
            return null;
        }

        InquiryResponse response = new InquiryResponse();
        response.setId(inquiry.getId());
        if (inquiry.getProperty() != null) {
            response.setPropertyId(inquiry.getProperty().getId());
            response.setPropertyTitle(inquiry.getProperty().getTitle());
        }
        if (inquiry.getUser() != null) {
            response.setUserId(inquiry.getUser().getId());
        }
        response.setName(inquiry.getName());
        response.setEmail(inquiry.getEmail());
        response.setPhone(inquiry.getPhone());
        response.setMessage(inquiry.getMessage());
        response.setStatus(inquiry.getStatus());
        response.setCreatedAt(inquiry.getCreatedAt());

        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
