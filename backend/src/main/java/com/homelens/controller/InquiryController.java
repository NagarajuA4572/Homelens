package com.homelens.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.homelens.dto.InquiryRequest;
import com.homelens.dto.InquiryResponse;
import com.homelens.dto.InquiryStatusUpdateRequest;
import com.homelens.dto.PageResponse;
import com.homelens.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Inquiries", description = "Endpoints for prospective tenant inquiries and property owner lead management")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping("/api/properties/{id}/inquiries")
    @Operation(summary = "Submit a rental inquiry / tour request on a property")
    public ResponseEntity<InquiryResponse> createInquiry(
            @PathVariable Long id,
            @Valid @RequestBody InquiryRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        InquiryResponse response = inquiryService.createInquiry(id, request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/inquiries/my")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get inquiries received on properties owned by the authenticated user")
    public ResponseEntity<PageResponse<InquiryResponse>> getMyInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<InquiryResponse> responses = inquiryService.getInquiriesForOwner(authentication.getName(), pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/properties/{id}/inquiries")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Get all inquiries for a specific property (Owner only)")
    public ResponseEntity<List<InquiryResponse>> getPropertyInquiries(
            @PathVariable Long id,
            Authentication authentication
    ) {
        List<InquiryResponse> responses = inquiryService.getInquiriesByProperty(id, authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/api/inquiries/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Update inquiry processing status (NEW, CONTACTED, RESOLVED, CLOSED)")
    public ResponseEntity<InquiryResponse> updateInquiryStatus(
            @PathVariable Long id,
            @Valid @RequestBody InquiryStatusUpdateRequest request,
            Authentication authentication
    ) {
        InquiryResponse response = inquiryService.updateInquiryStatus(id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/inquiries/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'AGENT', 'ADMIN')")
    @Operation(summary = "Dismiss / delete an inquiry (Owner only)")
    public ResponseEntity<Void> deleteInquiry(
            @PathVariable Long id,
            Authentication authentication
    ) {
        inquiryService.deleteInquiry(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
