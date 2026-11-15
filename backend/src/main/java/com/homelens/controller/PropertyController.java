package com.homelens.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.homelens.dto.CreatePropertyRequest;
import com.homelens.dto.PageResponse;
import com.homelens.dto.PropertyResponse;
import com.homelens.dto.PropertySearchCriteria;
import com.homelens.dto.PropertyStatusUpdateRequest;
import com.homelens.dto.UpdatePropertyRequest;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;
import com.homelens.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Endpoints for property browsing, search, and owner management")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    @Operation(summary = "Search and filter rental properties with pagination")
    public ResponseEntity<PageResponse<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String bhkType,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Boolean familyAllowed,
            @RequestParam(required = false) Boolean singleAllowed,
            @RequestParam(required = false) Boolean waterAvailability,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Default to AVAILABLE for public search when status is not specified
        PropertyStatus filterStatus = status != null ? status : PropertyStatus.AVAILABLE;

        PropertySearchCriteria criteria = new PropertySearchCriteria(
                query, city, bhkType, minRent, maxRent, propertyType,
                familyAllowed, singleAllowed, waterAvailability, filterStatus
        );

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<PropertyResponse> results = propertyService.searchProperties(criteria, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full property details including charges breakdown and nearby distances")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        PropertyResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Register a new property listing (Owner / Agent only)")
    public ResponseEntity<PropertyResponse> createProperty(
            @Valid @RequestBody CreatePropertyRequest request,
            Authentication authentication
    ) {
        PropertyResponse response = propertyService.createProperty(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Update property details and rental charges")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePropertyRequest request,
            Authentication authentication
    ) {
        PropertyResponse response = propertyService.updateProperty(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Change property availability status (Available / Rented)")
    public ResponseEntity<PropertyResponse> updatePropertyStatus(
            @PathVariable Long id,
            @Valid @RequestBody PropertyStatusUpdateRequest request,
            Authentication authentication
    ) {
        PropertyResponse response = propertyService.updatePropertyStatus(id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Delete property listing and local media")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, Authentication authentication) {
        propertyService.deleteProperty(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get properties registered by the currently authenticated owner")
    public ResponseEntity<PageResponse<PropertyResponse>> getMyProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<PropertyResponse> myProperties = propertyService.getPropertiesByOwner(authentication.getName(), pageable);
        return ResponseEntity.ok(myProperties);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Upload and attach a photograph to property")
    public ResponseEntity<PropertyResponse> uploadPropertyImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false, defaultValue = "false") Boolean isPrimary,
            Authentication authentication
    ) {
        PropertyResponse response = propertyService.uploadPropertyImage(id, file, isPrimary, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Remove photograph from property")
    public ResponseEntity<PropertyResponse> deletePropertyImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            Authentication authentication
    ) {
        PropertyResponse response = propertyService.deletePropertyImage(id, imageId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
