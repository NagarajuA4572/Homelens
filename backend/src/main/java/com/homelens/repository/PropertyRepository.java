package com.homelens.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.homelens.model.Property;
import com.homelens.model.PropertyStatus;
import com.homelens.model.PropertyType;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);

    List<Property> findByAgentId(Long agentId);

    List<Property> findByCityIgnoreCase(String city);

    Page<Property> findByBhkType(String bhkType, Pageable pageable);

    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);

    Page<Property> findByWaterAvailability(Boolean waterAvailability, Pageable pageable);

    Page<Property> findByFamilyAllowed(Boolean familyAllowed, Pageable pageable);

    Page<Property> findBySingleAllowed(Boolean singleAllowed, Pageable pageable);

    Page<Property> findByMonthlyRentBetween(BigDecimal minRent, BigDecimal maxRent, Pageable pageable);
}
