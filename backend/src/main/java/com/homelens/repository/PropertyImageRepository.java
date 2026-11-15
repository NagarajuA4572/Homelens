package com.homelens.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homelens.model.PropertyImage;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

    List<PropertyImage> findByPropertyIdOrderByDisplayOrderAsc(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}
