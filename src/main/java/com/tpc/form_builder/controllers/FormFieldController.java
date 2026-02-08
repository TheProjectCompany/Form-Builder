package com.tpc.form_builder.controllers;

import com.tpc.form_builder.dto.FieldMetadataUpdateDto;
import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/form")
@RequiredArgsConstructor
public class FormFieldController {

    private final FieldService fieldService;

    @PostMapping("/field")
    public ResponseEntity<Field> createFormField(@RequestParam UUID formId,
                                  @RequestBody Field field) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fieldService.createField(formId, field));
    }

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<Field> getFormField(@PathVariable UUID fieldId) {
        return ResponseEntity.status(HttpStatus.OK).body(fieldService.getFieldById(fieldId));
    }

    @GetMapping("/{formId}/fields")
    public ResponseEntity<List<Field>> getFormFieldsByFormId(@PathVariable UUID formId) {
        return ResponseEntity.status(HttpStatus.OK).body(fieldService.getFieldsByFormId(formId));
    }

    @PutMapping("/form/{fieldId}")
    public ResponseEntity<Field> updateFormField(@PathVariable UUID fieldId,
                                  @RequestBody FieldMetadataUpdateDto fieldMetadataUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(fieldService.updateFieldMetadata(fieldId, fieldMetadataUpdateDto));
    }

    @DeleteMapping("/field/{fieldId}")
    public ResponseEntity<String> deleteFormField(@PathVariable UUID fieldId) {
        // Implement delete logic in service layer and call it here
        return ResponseEntity.status(HttpStatus.OK).body("Field " + fieldId + " deleted");
    }
}
