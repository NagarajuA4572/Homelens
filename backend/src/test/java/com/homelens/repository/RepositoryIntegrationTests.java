package com.homelens.repository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.homelens.model.Inquiry;
import com.homelens.model.InquiryStatus;
import com.homelens.model.ListingType;
import com.homelens.model.Property;
import com.homelens.model.PropertyImage;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;
import com.homelens.model.Role;
import com.homelens.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RepositoryIntegrationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Test
    void testUserAndPropertyPersistenceWithImagesAndInquiry() {
        // 1. Create and save Property Owner user
        User owner = User.builder()
                .email("owner.test@homelens.com")
                .password("$2a$10$nq4Kb66AbHS6k7B58KAsEuhJKQwWPYRVOe6UqrWPjN8qMigWlx.M2")
                .fullName("Test Owner")
                .phone("+1-555-010-9999")
                .role(Role.ROLE_OWNER)
                .build();
        User savedOwner = userRepository.save(owner);
        Assertions.assertNotNull(savedOwner.getId());
        Assertions.assertEquals(Role.ROLE_OWNER, savedOwner.getRole());

        // 2. Create and save Gated Community Property with Indian rental specifications
        Property property = Property.builder()
                .title("Skyline Heights Gated Residency")
                .description("Spacious 3 BHK luxury flat in premium gated community.")
                .price(new BigDecimal("65000.00"))
                .monthlyRent(new BigDecimal("65000.00"))
                .bhkType("3 BHK")
                .propertyType(PropertyType.GATED_COMMUNITY)
                .listingType(ListingType.FOR_RENT)
                .status(PropertyStatus.AVAILABLE)
                .bedrooms(3)
                .bathrooms(3)
                .areaSqft(new BigDecimal("1850.00"))
                .familyAllowed(true)
                .singleAllowed(true)
                .electricityCharges(new BigDecimal("3000.00"))
                .waterCharges(new BigDecimal("1000.00"))
                .waterAvailability(true)
                .distanceHospitalKm(1.5)
                .distanceCollegeKm(3.0)
                .distanceSchoolKm(1.0)
                .distanceMetroKm(0.8)
                .distanceBusStopKm(0.3)
                .address("100 Gated Enclave Road")
                .city("Hyderabad")
                .state("Telangana")
                .postalCode("500081")
                .latitude(17.4435)
                .longitude(78.3772)
                .owner(savedOwner)
                .build();

        PropertyImage primaryImage = PropertyImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00")
                .isPrimary(true)
                .displayOrder(0)
                .build();
        property.addImage(primaryImage);

        Property savedProperty = propertyRepository.save(property);
        Assertions.assertNotNull(savedProperty.getId());
        Assertions.assertEquals("3 BHK", savedProperty.getBhkType());
        Assertions.assertEquals(PropertyType.GATED_COMMUNITY, savedProperty.getPropertyType());
        Assertions.assertEquals(new BigDecimal("69000.00"), savedProperty.getTotalMonthlyCharges());
        Assertions.assertTrue(savedProperty.getWaterAvailability());
        Assertions.assertTrue(savedProperty.getFamilyAllowed());
        Assertions.assertTrue(savedProperty.getSingleAllowed());
        Assertions.assertEquals(0.8, savedProperty.getDistanceMetroKm());

        // 3. Query Property by city, BHK, and rental range
        List<Property> hyderabadProperties = propertyRepository.findByCityIgnoreCase("hyderabad");
        Assertions.assertFalse(hyderabadProperties.isEmpty());

        Page<Property> bhkMatches = propertyRepository.findByBhkType("3 BHK", PageRequest.of(0, 10));
        Assertions.assertEquals(1, bhkMatches.getTotalElements());

        Page<Property> rentMatches = propertyRepository.findByMonthlyRentBetween(
                new BigDecimal("50000.00"),
                new BigDecimal("70000.00"),
                PageRequest.of(0, 10)
        );
        Assertions.assertEquals(1, rentMatches.getTotalElements());

        Page<Property> waterMatches = propertyRepository.findByWaterAvailability(true, PageRequest.of(0, 10));
        Assertions.assertEquals(1, waterMatches.getTotalElements());

        // 4. Create and save Buyer & Inquiry
        User seeker = User.builder()
                .email("seeker.test@homelens.com")
                .password("$2a$10$nq4Kb66AbHS6k7B58KAsEuhJKQwWPYRVOe6UqrWPjN8qMigWlx.M2")
                .fullName("Test Seeker")
                .phone("+1-555-011-8888")
                .role(Role.ROLE_BUYER)
                .build();
        User savedSeeker = userRepository.save(seeker);

        Inquiry inquiry = Inquiry.builder()
                .property(savedProperty)
                .user(savedSeeker)
                .name("Test Seeker")
                .email("seeker.test@homelens.com")
                .phone("+1-555-011-8888")
                .message("Is this 3 BHK flat available for family occupancy starting next month?")
                .status(InquiryStatus.NEW)
                .build();
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        Assertions.assertNotNull(savedInquiry.getId());

        // 5. Query inquiries by property
        List<Inquiry> propertyInquiries = inquiryRepository.findByPropertyIdOrderByCreatedAtDesc(savedProperty.getId());
        Assertions.assertEquals(1, propertyInquiries.size());
        Assertions.assertEquals("Test Seeker", propertyInquiries.get(0).getName());
    }
}
