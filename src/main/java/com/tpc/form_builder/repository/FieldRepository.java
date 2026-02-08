package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FieldRepository extends JpaRepository<Field, UUID> {
    List<Field> findAllByFormId(UUID formId);
}
