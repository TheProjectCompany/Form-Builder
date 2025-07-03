package org.tpc.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.service.FormFieldService;

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
}
