package com.tpc.form_builder.service.impl;

import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.repository.FormRepository;
import com.tpc.form_builder.service.FormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    @Override
    public Form createForm(Form form) {
        return formRepository.save(form);
    }

    @Override
    public Form getFormById(UUID id) {
        return formRepository.findById(id).orElseThrow(() -> new RuntimeException("Form not found with id: " + id));
    }

    @Override
    public Form updateForm(UUID id, Form form) {
        return null;
    }
}
