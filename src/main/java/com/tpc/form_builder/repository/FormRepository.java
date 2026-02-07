package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {
}
