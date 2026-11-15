package com.homelens.dto;

import java.math.BigDecimal;

import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;

public class PropertySearchCriteria {

    private String query;
    private String city;
    private String bhkType;
    private BigDecimal minRent;
    private BigDecimal maxRent;
    private PropertyType propertyType;
    private Boolean familyAllowed;
    private Boolean singleAllowed;
    private Boolean waterAvailability;
    private PropertyStatus status;

    public PropertySearchCriteria() {
    }

    public PropertySearchCriteria(String query, String city, String bhkType, BigDecimal minRent,
                                  BigDecimal maxRent, PropertyType propertyType, Boolean familyAllowed,
                                  Boolean singleAllowed, Boolean waterAvailability, PropertyStatus status) {
        this.query = query;
        this.city = city;
        this.bhkType = bhkType;
        this.minRent = minRent;
        this.maxRent = maxRent;
        this.propertyType = propertyType;
        this.familyAllowed = familyAllowed;
        this.singleAllowed = singleAllowed;
        this.waterAvailability = waterAvailability;
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBhkType() {
        return bhkType;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
    }

    public BigDecimal getMinRent() {
        return minRent;
    }

    public void setMinRent(BigDecimal minRent) {
        this.minRent = minRent;
    }

    public BigDecimal getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(BigDecimal maxRent) {
        this.maxRent = maxRent;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
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

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
}
