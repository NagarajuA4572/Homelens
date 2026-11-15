package com.homelens.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    @JsonIgnore
    private Property property;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    private Boolean isPrimary = false;

    private Integer displayOrder = 0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public PropertyImage() {
    }

    public PropertyImage(Long id, Property property, String imageUrl, Boolean isPrimary, Integer displayOrder) {
        this.id = id;
        this.property = property;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.isPrimary == null) {
            this.isPrimary = false;
        }
        if (this.displayOrder == null) {
            this.displayOrder = 0;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
        private String imageUrl;
        private Boolean isPrimary = false;
        private Integer displayOrder = 0;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder property(Property property) {
            this.property = property;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder isPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public PropertyImage build() {
            return new PropertyImage(id, property, imageUrl, isPrimary, displayOrder);
        }
    }
}
