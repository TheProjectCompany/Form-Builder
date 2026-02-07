package com.tpc.form_builder.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forms")
public class FormController {

    @PostMapping("")
    public String createForm() {
        return "Form created";
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
    public String getForm(@PathVariable String formId) {
        return "Form " + formId + " details";
    }


}
