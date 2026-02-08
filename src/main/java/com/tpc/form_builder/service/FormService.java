package com.tpc.form_builder.service;

import com.tpc.form_builder.models.Form;

import java.util.UUID;

public interface FormService {
    Form createForm(Form form);
    Form getFormById(UUID id);
    Form updateForm(UUID id, Form form);
}
