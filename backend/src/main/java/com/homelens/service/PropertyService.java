package com.homelens.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.homelens.dto.CreatePropertyRequest;
import com.homelens.dto.PageResponse;
import com.homelens.dto.PropertyResponse;
import com.homelens.dto.PropertySearchCriteria;
import com.homelens.dto.UpdatePropertyRequest;
import com.homelens.model.Property;
import com.homelens.model.PropertyImage;
import com.homelens.model.PropertyStatus;
import com.homelens.model.Role;
import com.homelens.model.User;
import com.homelens.repository.PropertyImageRepository;
import com.homelens.repository.PropertyRepository;
import com.homelens.repository.PropertySpecification;
import com.homelens.repository.UserRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public PropertyService(PropertyRepository propertyRepository,
                           PropertyImageRepository propertyImageRepository,
                           UserRepository userRepository,
                           FileStorageService fileStorageService) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> searchProperties(PropertySearchCriteria criteria, Pageable pageable) {
        Specification<Property> spec = PropertySpecification.buildSpecification(criteria);
        Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);
        Page<PropertyResponse> responsePage = propertyPage.map(PropertyResponse::fromEntity);
        return PageResponse.of(responsePage);
    }

    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + id));
        return PropertyResponse.fromEntity(property);
    }

    @Transactional
    public PropertyResponse createProperty(CreatePropertyRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        BigDecimal rent = request.getMonthlyRent() != null ? request.getMonthlyRent() : BigDecimal.ZERO;
        BigDecimal electricity = request.getElectricityCharges() != null ? request.getElectricityCharges() : BigDecimal.ZERO;
        BigDecimal water = request.getWaterCharges() != null ? request.getWaterCharges() : BigDecimal.ZERO;

        Property property = Property.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .price(rent)
                .monthlyRent(rent)
                .electricityCharges(electricity)
                .waterCharges(water)
                .bhkType(request.getBhkType() != null ? request.getBhkType().trim() : "2 BHK")
                .propertyType(request.getPropertyType())
                .listingType(request.getListingType())
                .status(request.getStatus() != null ? request.getStatus() : PropertyStatus.AVAILABLE)
                .bedrooms(request.getBedrooms() != null ? request.getBedrooms() : 2)
                .bathrooms(request.getBathrooms() != null ? request.getBathrooms() : 2)
                .areaSqft(request.getAreaSqft() != null ? request.getAreaSqft() : BigDecimal.ZERO)
                .familyAllowed(request.getFamilyAllowed() != null ? request.getFamilyAllowed() : true)
                .singleAllowed(request.getSingleAllowed() != null ? request.getSingleAllowed() : true)
                .waterAvailability(request.getWaterAvailability() != null ? request.getWaterAvailability() : true)
                .distanceHospitalKm(request.getDistanceHospitalKm())
                .distanceCollegeKm(request.getDistanceCollegeKm())
                .distanceSchoolKm(request.getDistanceSchoolKm())
                .distanceMetroKm(request.getDistanceMetroKm())
                .distanceBusStopKm(request.getDistanceBusStopKm())
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .postalCode(request.getPostalCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .owner(owner)
                .build();

        if (request.getInitialImages() != null && !request.getInitialImages().isEmpty()) {
            int order = 0;
            for (String imgUrl : request.getInitialImages()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    PropertyImage image = PropertyImage.builder()
                            .imageUrl(imgUrl.trim())
                            .isPrimary(order == 0)
                            .displayOrder(order++)
                            .build();
                    property.addImage(image);
                }
            }
        }

        Property saved = propertyRepository.save(property);
        return PropertyResponse.fromEntity(saved);
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, UpdatePropertyRequest request, String userEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + id));

        verifyOwnershipOrAdmin(property, userEmail);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            property.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            property.setDescription(request.getDescription());
        }
        if (request.getMonthlyRent() != null) {
            property.setMonthlyRent(request.getMonthlyRent());
            property.setPrice(request.getMonthlyRent());
        }
        if (request.getElectricityCharges() != null) {
            property.setElectricityCharges(request.getElectricityCharges());
        }
        if (request.getWaterCharges() != null) {
            property.setWaterCharges(request.getWaterCharges());
        }
        if (request.getBhkType() != null && !request.getBhkType().trim().isEmpty()) {
            property.setBhkType(request.getBhkType().trim());
        }
        if (request.getPropertyType() != null) {
            property.setPropertyType(request.getPropertyType());
        }
        if (request.getListingType() != null) {
            property.setListingType(request.getListingType());
        }
        if (request.getStatus() != null) {
            property.setStatus(request.getStatus());
        }
        if (request.getBedrooms() != null) {
            property.setBedrooms(request.getBedrooms());
        }
        if (request.getBathrooms() != null) {
            property.setBathrooms(request.getBathrooms());
        }
        if (request.getAreaSqft() != null) {
            property.setAreaSqft(request.getAreaSqft());
        }
        if (request.getFamilyAllowed() != null) {
            property.setFamilyAllowed(request.getFamilyAllowed());
        }
        if (request.getSingleAllowed() != null) {
            property.setSingleAllowed(request.getSingleAllowed());
        }
        if (request.getWaterAvailability() != null) {
            property.setWaterAvailability(request.getWaterAvailability());
        }
        if (request.getDistanceHospitalKm() != null) {
            property.setDistanceHospitalKm(request.getDistanceHospitalKm());
        }
        if (request.getDistanceCollegeKm() != null) {
            property.setDistanceCollegeKm(request.getDistanceCollegeKm());
        }
        if (request.getDistanceSchoolKm() != null) {
            property.setDistanceSchoolKm(request.getDistanceSchoolKm());
        }
        if (request.getDistanceMetroKm() != null) {
            property.setDistanceMetroKm(request.getDistanceMetroKm());
        }
        if (request.getDistanceBusStopKm() != null) {
            property.setDistanceBusStopKm(request.getDistanceBusStopKm());
        }
        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            property.setAddress(request.getAddress().trim());
        }
        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            property.setCity(request.getCity().trim());
        }
        if (request.getState() != null && !request.getState().trim().isEmpty()) {
            property.setState(request.getState().trim());
        }
        if (request.getPostalCode() != null) {
            property.setPostalCode(request.getPostalCode());
        }
        if (request.getLatitude() != null) {
            property.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            property.setLongitude(request.getLongitude());
        }

        Property updated = propertyRepository.save(property);
        return PropertyResponse.fromEntity(updated);
    }

    @Transactional
    public PropertyResponse updatePropertyStatus(Long id, PropertyStatus status, String userEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + id));

        verifyOwnershipOrAdmin(property, userEmail);
        property.setStatus(status);

        Property saved = propertyRepository.save(property);
        return PropertyResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteProperty(Long id, String userEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + id));

        verifyOwnershipOrAdmin(property, userEmail);

        // Delete local images from disk
        if (property.getImages() != null) {
            for (PropertyImage image : property.getImages()) {
                fileStorageService.deletePropertyImage(image.getImageUrl());
            }
        }

        propertyRepository.delete(property);
    }

    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> getPropertiesByOwner(String ownerEmail, Pageable pageable) {
        User owner = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        Specification<Property> spec = (root, query, cb) -> cb.equal(root.get("agent").get("id"), owner.getId());
        Page<Property> page = propertyRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(PropertyResponse::fromEntity));
    }

    @Transactional
    public PropertyResponse uploadPropertyImage(Long propertyId, MultipartFile file, Boolean isPrimary, String userEmail) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        verifyOwnershipOrAdmin(property, userEmail);

        String imageUrl = fileStorageService.storePropertyImage(file);
        int nextOrder = property.getImages() != null ? property.getImages().size() : 0;
        boolean primary = (isPrimary != null && isPrimary) || nextOrder == 0;

        if (primary && property.getImages() != null) {
            for (PropertyImage img : property.getImages()) {
                img.setIsPrimary(false);
            }
        }

        PropertyImage image = PropertyImage.builder()
                .property(property)
                .imageUrl(imageUrl)
                .isPrimary(primary)
                .displayOrder(nextOrder)
                .build();
        property.addImage(image);
        propertyImageRepository.save(image);

        Property saved = propertyRepository.saveAndFlush(property);
        return PropertyResponse.fromEntity(saved);
    }

    @Transactional
    public PropertyResponse deletePropertyImage(Long propertyId, Long imageId, String userEmail) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        verifyOwnershipOrAdmin(property, userEmail);

        PropertyImage targetImage = null;
        if (property.getImages() != null) {
            for (PropertyImage img : property.getImages()) {
                if (img.getId().equals(imageId)) {
                    targetImage = img;
                    break;
                }
            }
        }

        if (targetImage == null) {
            throw new IllegalArgumentException("Image with id " + imageId + " does not belong to property " + propertyId);
        }

        property.removeImage(targetImage);
        fileStorageService.deletePropertyImage(targetImage.getImageUrl());
        propertyImageRepository.delete(targetImage);

        Property saved = propertyRepository.saveAndFlush(property);
        return PropertyResponse.fromEntity(saved);
    }

    private void verifyOwnershipOrAdmin(Property property, String userEmail) {
        User user = userRepository.findByEmail(userEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (property.getAgent() == null || !property.getAgent().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to manage this property.");
        }
    }
}
