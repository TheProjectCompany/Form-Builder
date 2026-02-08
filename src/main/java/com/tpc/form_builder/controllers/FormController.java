package com.tpc.form_builder.controllers;

import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/form")
public class FormController {

    private final FormService formService;

    @PostMapping()
    public ResponseEntity<Form> createForm(@RequestBody Form form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formService.createForm(form));
    }

    @PutMapping
    public String updateForm() {
        return "Form updated";
    }

    @DeleteMapping("/{formId}")
    public String deleteForm(@PathVariable String formId) {
        return "Form " + formId + " deleted";
    }

    @GetMapping("/{formId}")
    public ResponseEntity<Form> getForm(@PathVariable UUID formId) {
        return ResponseEntity.ok(formService.getFormById(formId));
    }


}
