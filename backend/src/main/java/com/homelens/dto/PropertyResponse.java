package com.homelens.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.homelens.model.ListingType;
import com.homelens.model.Property;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;

public class PropertyResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal monthlyRent;
    private BigDecimal electricityCharges;
    private BigDecimal waterCharges;
    private BigDecimal totalMonthlyCharges;
    private String bhkType;
    private PropertyType propertyType;
    private ListingType listingType;
    private PropertyStatus status;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal areaSqft;
    private Boolean familyAllowed;
    private Boolean singleAllowed;
    private Boolean waterAvailability;
    private Double distanceHospitalKm;
    private Double distanceCollegeKm;
    private Double distanceSchoolKm;
    private Double distanceMetroKm;
    private Double distanceBusStopKm;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private AgentSummaryResponse owner;
    private List<PropertyImageResponse> images = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public PropertyResponse() {
    }

    public static PropertyResponse fromEntity(Property property) {
        if (property == null) {
            return null;
        }

        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setDescription(property.getDescription());
        response.setPrice(property.getPrice());
        response.setMonthlyRent(property.getMonthlyRent());
        response.setElectricityCharges(property.getElectricityCharges());
        response.setWaterCharges(property.getWaterCharges());
        response.setTotalMonthlyCharges(property.getTotalMonthlyCharges());
        response.setBhkType(property.getBhkType());
        response.setPropertyType(property.getPropertyType());
        response.setListingType(property.getListingType());
        response.setStatus(property.getStatus());
        response.setBedrooms(property.getBedrooms());
        response.setBathrooms(property.getBathrooms());
        response.setAreaSqft(property.getAreaSqft());
        response.setFamilyAllowed(property.getFamilyAllowed());
        response.setSingleAllowed(property.getSingleAllowed());
        response.setWaterAvailability(property.getWaterAvailability());
        response.setDistanceHospitalKm(property.getDistanceHospitalKm());
        response.setDistanceCollegeKm(property.getDistanceCollegeKm());
        response.setDistanceSchoolKm(property.getDistanceSchoolKm());
        response.setDistanceMetroKm(property.getDistanceMetroKm());
        response.setDistanceBusStopKm(property.getDistanceBusStopKm());
        response.setAddress(property.getAddress());
        response.setCity(property.getCity());
        response.setState(property.getState());
        response.setPostalCode(property.getPostalCode());
        response.setLatitude(property.getLatitude());
        response.setLongitude(property.getLongitude());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        if (property.getAgent() != null) {
            response.setOwner(new AgentSummaryResponse(
                    property.getAgent().getId(),
                    property.getAgent().getFullName(),
                    property.getAgent().getEmail(),
                    property.getAgent().getPhone()
            ));
        }

        if (property.getImages() != null && !property.getImages().isEmpty()) {
            List<PropertyImageResponse> imageResponses = property.getImages().stream()
                    .map(img -> new PropertyImageResponse(
                            img.getId(),
                            img.getImageUrl(),
                            img.getIsPrimary(),
                            img.getDisplayOrder()
                    ))
                    .toList();
            response.setImages(imageResponses);
        }

        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getElectricityCharges() {
        return electricityCharges;
    }

    public void setElectricityCharges(BigDecimal electricityCharges) {
        this.electricityCharges = electricityCharges;
    }

    public BigDecimal getWaterCharges() {
        return waterCharges;
    }

    public void setWaterCharges(BigDecimal waterCharges) {
        this.waterCharges = waterCharges;
    }

    public BigDecimal getTotalMonthlyCharges() {
        return totalMonthlyCharges;
    }

    public void setTotalMonthlyCharges(BigDecimal totalMonthlyCharges) {
        this.totalMonthlyCharges = totalMonthlyCharges;
    }

    public String getBhkType() {
        return bhkType;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public BigDecimal getAreaSqft() {
        return areaSqft;
    }

    public void setAreaSqft(BigDecimal areaSqft) {
        this.areaSqft = areaSqft;
    }

    public Boolean getFamilyAllowed() {
        return familyAllowed;
    }

    public void setFamilyAllowed(Boolean familyAllowed) {
        this.familyAllowed = familyAllowed;
    }

    public Boolean getSingleAllowed() {
        return singleAllowed;
    }

    public void setSingleAllowed(Boolean singleAllowed) {
        this.singleAllowed = singleAllowed;
    }

    public Boolean getWaterAvailability() {
        return waterAvailability;
    }

    public void setWaterAvailability(Boolean waterAvailability) {
        this.waterAvailability = waterAvailability;
    }

    public Double getDistanceHospitalKm() {
        return distanceHospitalKm;
    }

    public void setDistanceHospitalKm(Double distanceHospitalKm) {
        this.distanceHospitalKm = distanceHospitalKm;
    }

    public Double getDistanceCollegeKm() {
        return distanceCollegeKm;
    }

    public void setDistanceCollegeKm(Double distanceCollegeKm) {
        this.distanceCollegeKm = distanceCollegeKm;
    }

    public Double getDistanceSchoolKm() {
        return distanceSchoolKm;
    }

    public void setDistanceSchoolKm(Double distanceSchoolKm) {
        this.distanceSchoolKm = distanceSchoolKm;
    }

    public Double getDistanceMetroKm() {
        return distanceMetroKm;
    }

    public void setDistanceMetroKm(Double distanceMetroKm) {
        this.distanceMetroKm = distanceMetroKm;
    }

    public Double getDistanceBusStopKm() {
        return distanceBusStopKm;
    }

    public void setDistanceBusStopKm(Double distanceBusStopKm) {
        this.distanceBusStopKm = distanceBusStopKm;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public AgentSummaryResponse getOwner() {
        return owner;
    }

    public void setOwner(AgentSummaryResponse owner) {
        this.owner = owner;
    }

    public List<PropertyImageResponse> getImages() {
        return images;
    }

    public void setImages(List<PropertyImageResponse> images) {
        this.images = images;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
