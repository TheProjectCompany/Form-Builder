package org.bnbdevelopers.form_builder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bnbdevelopers.form_builder.models.Dropdown;
import org.bnbdevelopers.form_builder.models.DropdownElement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dropdown")
@RequiredArgsConstructor
@Log4j2
public class DropdownController {

    @PostMapping()
    public void createDropdown(@RequestBody Dropdown dropdown) {
        log.info("Received request to create dropdown: {}", dropdown);
    }

    @GetMapping("/{id}")
    public void getDropdownById(@PathVariable("id") String id) {
        log.info("Received request to get dropdown by id: {}", id);
    }

    @GetMapping("")
    public void createDropdown() {
        log.info("Received request to get all dropdowns");
    }

    @PostMapping("/{id}/element")
    public void addDropdownElement(@PathVariable("id") String id, @RequestBody DropdownElement dropdownElement) {
        log.info("Received request to add dropdown element: {}", dropdownElement);
    }

    @PutMapping("/{id}/element/{element-id}")
    public void updateDropdownElement(@PathVariable("id") String dropdownId, @PathVariable("element-id") String elementId, @RequestBody DropdownElement dropdownElement) {
        log.info("Received request to update dropdown element: {}", dropdownElement);
    }

    @DeleteMapping("/{id}/element/{element-id}")
    public void deleteDropdownElement(@PathVariable("id") String dropdownId, @PathVariable("element-id") String elementId) {
        log.info("Received request to delete dropdown element: {}", dropdownId);
    }

    @DeleteMapping("/{id}/elements")
    public void deleteDropdownElements(@PathVariable("id") String dropdownId) {
        log.info("Received request to delete dropdown elements: {}", dropdownId);
    }

    @PutMapping("/{id}")
    public void updateDropdown(@PathVariable("id") String dropdownId, @RequestBody Dropdown dropdown) {
        log.info("Received request to update dropdown: {}", dropdown);
    }

    @DeleteMapping("/{id}")
    public void deleteDropdownById(@PathVariable("id") String id) {
        log.info("Received request to delete dropdown by id: {}", id);
    }
}
