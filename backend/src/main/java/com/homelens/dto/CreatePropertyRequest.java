package com.homelens.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.homelens.model.ListingType;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePropertyRequest {

    @NotBlank(message = "Property title is required")
    private String title;

    private String description;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than zero")
    private BigDecimal monthlyRent;

    private BigDecimal electricityCharges = BigDecimal.ZERO;
    private BigDecimal waterCharges = BigDecimal.ZERO;

    @NotBlank(message = "BHK configuration is required (e.g., '1 BHK', '2 BHK', '3 BHK')")
    private String bhkType = "2 BHK";

    @NotNull(message = "Property type is required (GATED_COMMUNITY or INDIVIDUAL_HOUSE)")
    private PropertyType propertyType = PropertyType.GATED_COMMUNITY;

    private ListingType listingType = ListingType.FOR_RENT;
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    private Integer bedrooms = 2;
    private Integer bathrooms = 2;
    private BigDecimal areaSqft = BigDecimal.ZERO;

    private Boolean familyAllowed = true;
    private Boolean singleAllowed = true;
    private Boolean waterAvailability = true;

    private Double distanceHospitalKm;
    private Double distanceCollegeKm;
    private Double distanceSchoolKm;
    private Double distanceMetroKm;
    private Double distanceBusStopKm;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String postalCode;
    private Double latitude;
    private Double longitude;

    private List<String> initialImages = new ArrayList<>();

    public CreatePropertyRequest() {
    }

    // Getters and Setters
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

    public List<String> getInitialImages() {
        return initialImages;
    }

    public void setInitialImages(List<String> initialImages) {
        this.initialImages = initialImages;
    }
}
