package com.homelens.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.homelens.model.Inquiry;
import com.homelens.model.InquiryStatus;
import com.homelens.model.Property;

public class InquiryResponse {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String propertyCity;
    private BigDecimal propertyRent;
    private String propertyImageUrl;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

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
        Property property = inquiry.getProperty();
        if (property != null) {
            response.setPropertyId(property.getId());
            response.setPropertyTitle(property.getTitle());
            response.setPropertyCity(property.getCity());
            response.setPropertyRent(property.getMonthlyRent() != null ? property.getMonthlyRent() : property.getPrice());

            if (property.getImages() != null && !property.getImages().isEmpty()) {
                String primaryUrl = property.getImages().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(property.getImages().get(0).getImageUrl());
                response.setPropertyImageUrl(primaryUrl);
            }

            if (property.getAgent() != null) {
                response.setOwnerName(property.getAgent().getFullName());
                // Security concern: Disclose owner contact details (phone, email) ONLY after request is accepted
                if (inquiry.getStatus() == InquiryStatus.ACCEPTED || inquiry.getStatus() == InquiryStatus.RESOLVED) {
                    response.setOwnerPhone(property.getAgent().getPhone());
                    response.setOwnerEmail(property.getAgent().getEmail());
                } else {
                    response.setOwnerPhone(null);
                    response.setOwnerEmail(null);
                }
            }
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

    public String getPropertyCity() {
        return propertyCity;
    }

    public void setPropertyCity(String propertyCity) {
        this.propertyCity = propertyCity;
    }

    public BigDecimal getPropertyRent() {
        return propertyRent;
    }

    public void setPropertyRent(BigDecimal propertyRent) {
        this.propertyRent = propertyRent;
    }

    public String getPropertyImageUrl() {
        return propertyImageUrl;
    }

    public void setPropertyImageUrl(String propertyImageUrl) {
        this.propertyImageUrl = propertyImageUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
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
