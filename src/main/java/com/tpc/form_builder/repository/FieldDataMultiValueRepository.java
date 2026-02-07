package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.FieldDataMultiValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FieldDataMultiValueRepository extends JpaRepository<FieldDataMultiValue, UUID> {
}
