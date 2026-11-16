package com.homelens.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import com.homelens.dto.InquiryRequest;
import com.homelens.dto.InquiryResponse;
import com.homelens.dto.PageResponse;
import com.homelens.model.Inquiry;
import com.homelens.model.InquiryStatus;
import com.homelens.model.Property;
import com.homelens.model.Role;
import com.homelens.model.User;
import com.homelens.repository.InquiryRepository;
import com.homelens.repository.PropertyRepository;
import com.homelens.repository.UserRepository;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public InquiryService(InquiryRepository inquiryRepository,
                          PropertyRepository propertyRepository,
                          UserRepository userRepository) {
        this.inquiryRepository = inquiryRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InquiryResponse createInquiry(Long propertyId, InquiryRequest request, String userEmailOrNull) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        User user = null;
        if (userEmailOrNull != null && !userEmailOrNull.trim().isEmpty()) {
            user = userRepository.findByEmail(userEmailOrNull.toLowerCase().trim()).orElse(null);
        }

        Inquiry inquiry = Inquiry.builder()
                .property(property)
                .user(user)
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .phone(request.getPhone())
                .message(request.getMessage().trim())
                .status(InquiryStatus.NEW)
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        return InquiryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<InquiryResponse> getInquiriesForOwner(String ownerEmail, Pageable pageable) {
        User owner = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        Specification<Inquiry> spec = (root, query, cb) -> {
            if (owner.getRole() == Role.ROLE_ADMIN) {
                return cb.conjunction();
            }
            return cb.equal(root.get("property").get("agent").get("id"), owner.getId());
        };

        Page<Inquiry> inquiryPage = inquiryRepository.findAll(spec, pageable);
        return PageResponse.of(inquiryPage.map(InquiryResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<InquiryResponse> getInquiriesForSeeker(String userEmail, Pageable pageable) {
        User seeker = userRepository.findByEmail(userEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Specification<Inquiry> spec = (root, query, cb) -> {
            Predicate byUser = cb.equal(root.get("user").get("id"), seeker.getId());
            Predicate byEmail = cb.equal(cb.lower(root.get("email")), seeker.getEmail().toLowerCase().trim());
            return cb.or(byUser, byEmail);
        };

        Page<Inquiry> inquiryPage = inquiryRepository.findAll(spec, pageable);
        return PageResponse.of(inquiryPage.map(InquiryResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> getInquiriesByProperty(Long propertyId, String ownerEmail) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        User owner = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        if (owner.getRole() != Role.ROLE_ADMIN) {
            if (property.getAgent() == null || !property.getAgent().getId().equals(owner.getId())) {
                throw new AccessDeniedException("You do not have permission to view inquiries for this property.");
            }
        }

        List<Inquiry> inquiries = inquiryRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
        return inquiries.stream().map(InquiryResponse::fromEntity).toList();
    }

    @Transactional
    public InquiryResponse updateInquiryStatus(Long inquiryId, InquiryStatus status, String ownerEmail) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + inquiryId));

        User user = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        Property property = inquiry.getProperty();
        if (user.getRole() != Role.ROLE_ADMIN) {
            if (property == null || property.getAgent() == null || !property.getAgent().getId().equals(user.getId())) {
                throw new AccessDeniedException("You do not have permission to update this inquiry.");
            }
        }

        inquiry.setStatus(status);
        Inquiry updated = inquiryRepository.save(inquiry);
        return InquiryResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteInquiry(Long inquiryId, String ownerEmail) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + inquiryId));

        User user = userRepository.findByEmail(ownerEmail.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + ownerEmail));

        Property property = inquiry.getProperty();
        if (user.getRole() != Role.ROLE_ADMIN) {
            if (property == null || property.getAgent() == null || !property.getAgent().getId().equals(user.getId())) {
                throw new AccessDeniedException("You do not have permission to delete this inquiry.");
            }
        }

        inquiryRepository.delete(inquiry);
    }
}
