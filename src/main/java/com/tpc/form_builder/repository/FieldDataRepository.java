package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.FieldData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FieldDataRepository extends JpaRepository<FieldData, UUID> {
}
