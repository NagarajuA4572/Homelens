package com.homelens.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.homelens.model.Inquiry;
import com.homelens.model.InquiryStatus;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long>, JpaSpecificationExecutor<Inquiry> {

    List<Inquiry> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Inquiry> findByStatus(InquiryStatus status, Pageable pageable);
}
