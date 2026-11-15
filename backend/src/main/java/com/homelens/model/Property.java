package com.homelens.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyRent = BigDecimal.ZERO;

    @Column(length = 50)
    private String bhkType = "2 BHK";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType = ListingType.FOR_RENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    private Integer bedrooms = 0;

    private Integer bathrooms = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal areaSqft = BigDecimal.ZERO;

    // Occupancy Requirements
    private Boolean familyAllowed = true;
    private Boolean singleAllowed = true;

    // Additional Monthly Charges (distinguished from base rent)
    @Column(precision = 10, scale = 2)
    private BigDecimal electricityCharges = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal waterCharges = BigDecimal.ZERO;

    // Water Availability
    private Boolean waterAvailability = true;

    // Nearby Facilities & Distances (in kilometres)
    private Double distanceHospitalKm;
    private Double distanceCollegeKm;
    private Double distanceSchoolKm;
    private Double distanceMetroKm;
    private Double distanceBusStopKm;

    // Location
    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(length = 20)
    private String postalCode;

    private Double latitude;

    private Double longitude;

    // Relationship: Agent / Property Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<PropertyImage> images = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Property() {
    }

    public Property(Long id, String title, String description, BigDecimal price, BigDecimal monthlyRent,
                    String bhkType, PropertyType propertyType, ListingType listingType, PropertyStatus status,
                    Integer bedrooms, Integer bathrooms, BigDecimal areaSqft, Boolean familyAllowed,
                    Boolean singleAllowed, BigDecimal electricityCharges, BigDecimal waterCharges,
                    Boolean waterAvailability, Double distanceHospitalKm, Double distanceCollegeKm,
                    Double distanceSchoolKm, Double distanceMetroKm, Double distanceBusStopKm,
                    String address, String city, String state, String postalCode,
                    Double latitude, Double longitude, User agent) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.monthlyRent = monthlyRent != null ? monthlyRent : (price != null ? price : BigDecimal.ZERO);
        this.bhkType = bhkType != null ? bhkType : "2 BHK";
        this.propertyType = propertyType;
        this.listingType = listingType != null ? listingType : ListingType.FOR_RENT;
        this.status = status != null ? status : PropertyStatus.AVAILABLE;
        this.bedrooms = bedrooms != null ? bedrooms : 0;
        this.bathrooms = bathrooms != null ? bathrooms : 0;
        this.areaSqft = areaSqft != null ? areaSqft : BigDecimal.ZERO;
        this.familyAllowed = familyAllowed != null ? familyAllowed : true;
        this.singleAllowed = singleAllowed != null ? singleAllowed : true;
        this.electricityCharges = electricityCharges != null ? electricityCharges : BigDecimal.ZERO;
        this.waterCharges = waterCharges != null ? waterCharges : BigDecimal.ZERO;
        this.waterAvailability = waterAvailability != null ? waterAvailability : true;
        this.distanceHospitalKm = distanceHospitalKm;
        this.distanceCollegeKm = distanceCollegeKm;
        this.distanceSchoolKm = distanceSchoolKm;
        this.distanceMetroKm = distanceMetroKm;
        this.distanceBusStopKm = distanceBusStopKm;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agent = agent;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) {
            this.status = PropertyStatus.AVAILABLE;
        }
        if (this.listingType == null) {
            this.listingType = ListingType.FOR_RENT;
        }
        if (this.monthlyRent == null) {
            this.monthlyRent = this.price != null ? this.price : BigDecimal.ZERO;
        }
        if (this.price == null) {
            this.price = this.monthlyRent;
        }
        if (this.bhkType == null) {
            this.bhkType = "2 BHK";
        }
        if (this.familyAllowed == null) {
            this.familyAllowed = true;
        }
        if (this.singleAllowed == null) {
            this.singleAllowed = true;
        }
        if (this.electricityCharges == null) {
            this.electricityCharges = BigDecimal.ZERO;
        }
        if (this.waterCharges == null) {
            this.waterCharges = BigDecimal.ZERO;
        }
        if (this.waterAvailability == null) {
            this.waterAvailability = true;
        }
        if (this.bedrooms == null) {
            this.bedrooms = 0;
        }
        if (this.bathrooms == null) {
            this.bathrooms = 0;
        }
        if (this.areaSqft == null) {
            this.areaSqft = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addImage(PropertyImage image) {
        images.add(image);
        image.setProperty(this);
    }

    public void removeImage(PropertyImage image) {
        images.remove(image);
        image.setProperty(null);
    }

    // Owner alias for Agent relationship
    public User getOwner() {
        return this.agent;
    }

    public void setOwner(User owner) {
        this.agent = owner;
    }

    // Total monthly charges calculation
    public BigDecimal getTotalMonthlyCharges() {
        BigDecimal rent = monthlyRent != null ? monthlyRent : BigDecimal.ZERO;
        BigDecimal electricity = electricityCharges != null ? electricityCharges : BigDecimal.ZERO;
        BigDecimal water = waterCharges != null ? waterCharges : BigDecimal.ZERO;
        return rent.add(electricity).add(water);
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

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public List<PropertyImage> getImages() {
        return images;
    }

    public void setImages(List<PropertyImage> images) {
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private BigDecimal price;
        private BigDecimal monthlyRent;
        private String bhkType = "2 BHK";
        private PropertyType propertyType;
        private ListingType listingType = ListingType.FOR_RENT;
        private PropertyStatus status = PropertyStatus.AVAILABLE;
        private Integer bedrooms = 0;
        private Integer bathrooms = 0;
        private BigDecimal areaSqft = BigDecimal.ZERO;
        private Boolean familyAllowed = true;
        private Boolean singleAllowed = true;
        private BigDecimal electricityCharges = BigDecimal.ZERO;
        private BigDecimal waterCharges = BigDecimal.ZERO;
        private Boolean waterAvailability = true;
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
        private User agent;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder monthlyRent(BigDecimal monthlyRent) {
            this.monthlyRent = monthlyRent;
            return this;
        }

        public Builder bhkType(String bhkType) {
            this.bhkType = bhkType;
            return this;
        }

        public Builder propertyType(PropertyType propertyType) {
            this.propertyType = propertyType;
            return this;
        }

        public Builder listingType(ListingType listingType) {
            this.listingType = listingType;
            return this;
        }

        public Builder status(PropertyStatus status) {
            this.status = status;
            return this;
        }

        public Builder bedrooms(Integer bedrooms) {
            this.bedrooms = bedrooms;
            return this;
        }

        public Builder bathrooms(Integer bathrooms) {
            this.bathrooms = bathrooms;
            return this;
        }

        public Builder areaSqft(BigDecimal areaSqft) {
            this.areaSqft = areaSqft;
            return this;
        }

        public Builder familyAllowed(Boolean familyAllowed) {
            this.familyAllowed = familyAllowed;
            return this;
        }

        public Builder singleAllowed(Boolean singleAllowed) {
            this.singleAllowed = singleAllowed;
            return this;
        }

        public Builder electricityCharges(BigDecimal electricityCharges) {
            this.electricityCharges = electricityCharges;
            return this;
        }

        public Builder waterCharges(BigDecimal waterCharges) {
            this.waterCharges = waterCharges;
            return this;
        }

        public Builder waterAvailability(Boolean waterAvailability) {
            this.waterAvailability = waterAvailability;
            return this;
        }

        public Builder distanceHospitalKm(Double distanceHospitalKm) {
            this.distanceHospitalKm = distanceHospitalKm;
            return this;
        }

        public Builder distanceCollegeKm(Double distanceCollegeKm) {
            this.distanceCollegeKm = distanceCollegeKm;
            return this;
        }

        public Builder distanceSchoolKm(Double distanceSchoolKm) {
            this.distanceSchoolKm = distanceSchoolKm;
            return this;
        }

        public Builder distanceMetroKm(Double distanceMetroKm) {
            this.distanceMetroKm = distanceMetroKm;
            return this;
        }

        public Builder distanceBusStopKm(Double distanceBusStopKm) {
            this.distanceBusStopKm = distanceBusStopKm;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder agent(User agent) {
            this.agent = agent;
            return this;
        }

        public Builder owner(User owner) {
            this.agent = owner;
            return this;
        }

        public Property build() {
            if (this.monthlyRent == null) {
                this.monthlyRent = this.price != null ? this.price : BigDecimal.ZERO;
            }
            if (this.price == null) {
                this.price = this.monthlyRent;
            }
            return new Property(id, title, description, price, monthlyRent, bhkType, propertyType,
                    listingType, status, bedrooms, bathrooms, areaSqft, familyAllowed, singleAllowed,
                    electricityCharges, waterCharges, waterAvailability, distanceHospitalKm,
                    distanceCollegeKm, distanceSchoolKm, distanceMetroKm, distanceBusStopKm,
                    address, city, state, postalCode, latitude, longitude, agent);
        }
    }
}
