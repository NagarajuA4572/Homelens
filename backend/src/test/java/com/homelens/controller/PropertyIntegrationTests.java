package com.homelens.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homelens.dto.CreatePropertyRequest;
import com.homelens.dto.InquiryRequest;
import com.homelens.dto.InquiryStatusUpdateRequest;
import com.homelens.dto.PropertyStatusUpdateRequest;
import com.homelens.dto.RegisterRequest;
import com.homelens.dto.UpdatePropertyRequest;
import com.homelens.model.InquiryStatus;
import com.homelens.model.ListingType;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;
import com.homelens.model.Role;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PropertyIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registerAndGetToken(String email, Role role) throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                email,
                "Password123!",
                "Test " + role.name(),
                "+91-9876543210",
                role
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    void testEndToEndPropertyLifecycleAndInquiries() throws Exception {
        // 1. Register Property Owner & obtain JWT
        String ownerToken = registerAndGetToken("property.owner@homelens.com", Role.ROLE_OWNER);

        // 2. Register a new Gated Community 3 BHK property
        CreatePropertyRequest createReq = new CreatePropertyRequest();
        createReq.setTitle("Green Valley Gated Residency");
        createReq.setDescription("Scenic 3 BHK flat in premium community with round-the-clock water supply.");
        createReq.setMonthlyRent(new BigDecimal("55000.00"));
        createReq.setElectricityCharges(new BigDecimal("2500.00"));
        createReq.setWaterCharges(new BigDecimal("800.00"));
        createReq.setBhkType("3 BHK");
        createReq.setPropertyType(PropertyType.GATED_COMMUNITY);
        createReq.setListingType(ListingType.FOR_RENT);
        createReq.setStatus(PropertyStatus.AVAILABLE);
        createReq.setBedrooms(3);
        createReq.setBathrooms(3);
        createReq.setAreaSqft(new BigDecimal("1750.00"));
        createReq.setFamilyAllowed(true);
        createReq.setSingleAllowed(true);
        createReq.setWaterAvailability(true);
        createReq.setDistanceHospitalKm(1.2);
        createReq.setDistanceCollegeKm(2.5);
        createReq.setDistanceSchoolKm(0.8);
        createReq.setDistanceMetroKm(0.5);
        createReq.setDistanceBusStopKm(0.2);
        createReq.setAddress("402 Green Valley Park");
        createReq.setCity("Bengaluru");
        createReq.setState("Karnataka");
        createReq.setPostalCode("560001");

        MvcResult createResult = mockMvc.perform(post("/api/properties")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.bhkType").value("3 BHK"))
                .andExpect(jsonPath("$.propertyType").value("GATED_COMMUNITY"))
                .andExpect(jsonPath("$.monthlyRent").value(55000.00))
                .andExpect(jsonPath("$.totalMonthlyCharges").value(58300.00))
                .andExpect(jsonPath("$.distanceMetroKm").value(0.5))
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long propertyId = createdJson.get("id").asLong();

        // 3. Search and filter properties publicly (without auth)
        mockMvc.perform(get("/api/properties")
                        .param("city", "Bengaluru")
                        .param("bhkType", "3 BHK")
                        .param("waterAvailability", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].city").value("Bengaluru"));

        // 4. Retrieve single property details by ID (public)
        mockMvc.perform(get("/api/properties/" + propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Green Valley Gated Residency"))
                .andExpect(jsonPath("$.owner.email").value("property.owner@homelens.com"))
                .andExpect(jsonPath("$.waterAvailability").value(true));

        // 5. Update property rental charges and distance info
        UpdatePropertyRequest updateReq = new UpdatePropertyRequest();
        updateReq.setMonthlyRent(new BigDecimal("57000.00"));
        updateReq.setElectricityCharges(new BigDecimal("2000.00"));

        mockMvc.perform(put("/api/properties/" + propertyId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRent").value(57000.00))
                .andExpect(jsonPath("$.totalMonthlyCharges").value(59800.00));

        // 6. Toggle property availability status to RENTED
        PropertyStatusUpdateRequest statusReq = new PropertyStatusUpdateRequest(PropertyStatus.RENTED);

        mockMvc.perform(patch("/api/properties/" + propertyId + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RENTED"));

        // 7. Upload a local photograph for the property
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "hallway.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/properties/" + propertyId + "/images")
                        .file(mockFile)
                        .param("isPrimary", "true")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images", hasSize(1)))
                .andExpect(jsonPath("$.images[0].imageUrl", startsWith("/uploads/properties/")))
                .andExpect(jsonPath("$.images[0].isPrimary").value(true));

        // 8. Submit an inquiry as a prospective seeker (public)
        InquiryRequest inquiryReq = new InquiryRequest(
                "Rohit Sharma",
                "rohit.seeker@gmail.com",
                "+91-9988776655",
                "Hi, I am interested in this flat. Can I schedule a visit this weekend?"
        );

        MvcResult inqResult = mockMvc.perform(post("/api/properties/" + propertyId + "/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inquiryReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.propertyId").value(propertyId))
                .andExpect(jsonPath("$.name").value("Rohit Sharma"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andReturn();

        JsonNode inqJson = objectMapper.readTree(inqResult.getResponse().getContentAsString());
        Long inquiryId = inqJson.get("id").asLong();

        // 9. Owner retrieves inquiries
        mockMvc.perform(get("/api/inquiries/my")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].email").value("rohit.seeker@gmail.com"));

        // 10. Owner updates inquiry status to CONTACTED
        InquiryStatusUpdateRequest updateStatusReq = new InquiryStatusUpdateRequest(InquiryStatus.CONTACTED);
        mockMvc.perform(patch("/api/inquiries/" + inquiryId + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTACTED"));

        // 11. Unauthorized / non-owner user cannot update inquiry status (returns 403)
        String otherUserToken = registerAndGetToken("another.owner@homelens.com", Role.ROLE_OWNER);
        mockMvc.perform(patch("/api/inquiries/" + inquiryId + "/status")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusReq)))
                .andExpect(status().isForbidden());

        // 12. Owner views registered properties via /api/properties/my
        mockMvc.perform(get("/api/properties/my")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(propertyId));

        // 13. Seeker views their pending inquiries & status updates via /api/inquiries/sent
        String seekerToken = registerAndGetToken("rohit.seeker@gmail.com", Role.ROLE_BUYER);
        mockMvc.perform(get("/api/inquiries/sent")
                        .header("Authorization", "Bearer " + seekerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].propertyTitle").value("Green Valley Gated Residency"))
                .andExpect(jsonPath("$.content[0].status").value("CONTACTED"));

        // 14. Owner dismisses / deletes inquiry
        mockMvc.perform(delete("/api/inquiries/" + inquiryId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());
    }
}
