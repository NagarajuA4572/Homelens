package com.homelens.dto;

public class PropertyImageResponse {

    private Long id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;

    public PropertyImageResponse() {
    }

    public PropertyImageResponse(Long id, String imageUrl, Boolean isPrimary, Integer displayOrder) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
