package org.tpc.form_builder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.service.FormFieldService;
import org.tpc.form_builder.service.dto.FormFieldDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/form-field")
@Log4j2
@RequiredArgsConstructor
public class FormFieldController {
    private final FormFieldService formFieldService;

    @GetMapping("/{id}")
    public ResponseEntity<FormField> getFormFieldById(@PathVariable String id) {
        FormField formField = formFieldService.getFormFieldById(id);
        return new ResponseEntity<>(formField, HttpStatus.OK);
    }

    @GetMapping("/section/{section-id}")
    public ResponseEntity<List<FormField>> getSectionById(@PathVariable("section-id") String sectionId) {
        List<FormField> sectionFields = formFieldService.getFormFieldsBySectionId(sectionId);
        return new ResponseEntity<>(sectionFields, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FormFieldDto> createFormField(@RequestBody @Valid FormFieldDto formFieldDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formFieldService.createFormField(formFieldDto));
    }
}
