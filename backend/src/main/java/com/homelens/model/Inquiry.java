package com.homelens.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.NEW;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Inquiry() {
    }

    public Inquiry(Long id, Property property, User user, String name, String email, String phone,
                   String message, InquiryStatus status) {
        this.id = id;
        this.property = property;
        this.user = user;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.status = status != null ? status : InquiryStatus.NEW;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = InquiryStatus.NEW;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Property property;
        private User user;
        private String name;
        private String email;
        private String phone;
        private String message;
        private InquiryStatus status = InquiryStatus.NEW;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder property(Property property) {
            this.property = property;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(InquiryStatus status) {
            this.status = status;
            return this;
        }

        public Inquiry build() {
            return new Inquiry(id, property, user, name, email, phone, message, status);
        }
    }
}
