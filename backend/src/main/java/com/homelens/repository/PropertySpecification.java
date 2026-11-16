package com.homelens.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.homelens.dto.PropertySearchCriteria;
import com.homelens.model.Property;
import jakarta.persistence.criteria.Predicate;

public class PropertySpecification {

    public static Specification<Property> buildSpecification(PropertySearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return cb.conjunction();
            }

            // Keyword query matching title, description, address, or city
            if (criteria.getQuery() != null && !criteria.getQuery().trim().isEmpty()) {
                String searchPattern = "%" + criteria.getQuery().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                Predicate addressMatch = cb.like(cb.lower(root.get("address")), searchPattern);
                Predicate cityMatch = cb.like(cb.lower(root.get("city")), searchPattern);
                predicates.add(cb.or(titleMatch, descMatch, addressMatch, cityMatch));
            }

            // Specific City filter
            if (criteria.getCity() != null && !criteria.getCity().trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), criteria.getCity().trim().toLowerCase()));
            }

            // BHK Configuration (1 RK, 1 BHK, 2 BHK, 3 BHK, 4 BHK, 4+ BHK)
            if (criteria.getBhkType() != null && !criteria.getBhkType().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("bhkType"), criteria.getBhkType().trim()));
            }

            // Property Type (GATED_COMMUNITY or INDIVIDUAL_HOUSE)
            if (criteria.getPropertyType() != null) {
                predicates.add(cb.equal(root.get("propertyType"), criteria.getPropertyType()));
            }

            // Minimum Monthly Rent
            if (criteria.getMinRent() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("monthlyRent"), criteria.getMinRent()));
            }

            // Maximum Monthly Rent
            if (criteria.getMaxRent() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("monthlyRent"), criteria.getMaxRent()));
            }

            // Occupancy: Family allowed (filter only when user explicitly requests family-friendly homes)
            if (Boolean.TRUE.equals(criteria.getFamilyAllowed())) {
                predicates.add(cb.isTrue(root.get("familyAllowed")));
            }

            // Occupancy: Single / Bachelor allowed (filter only when user explicitly requests single-friendly homes)
            if (Boolean.TRUE.equals(criteria.getSingleAllowed())) {
                predicates.add(cb.isTrue(root.get("singleAllowed")));
            }

            // Water Availability (filter only when user explicitly requests guaranteed water)
            if (Boolean.TRUE.equals(criteria.getWaterAvailability())) {
                predicates.add(cb.isTrue(root.get("waterAvailability")));
            }

            // Availability Status (AVAILABLE or RENTED)
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
