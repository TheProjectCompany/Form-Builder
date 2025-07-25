package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tpc.form_builder.models.Dropdown;
import org.tpc.form_builder.models.DropdownElement;
import org.tpc.form_builder.models.repository.DropdownRepository;
import org.tpc.form_builder.service.DropdownService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DropDownServiceImpl implements DropdownService {

    private final DropdownRepository dropdownRepository;

    @Override
    public Dropdown createCustomDropdown(Dropdown dropdown) {
        for (DropdownElement dropdownElement : dropdown.getDropdownElements())
            dropdownElement.setId(UUID.randomUUID().toString());
        dropdownRepository.save(dropdown);
        return null;
    }

    @Override
    public Dropdown getDropdownById(String id) {
        return null;
    }

    @Override
    public Dropdown getAllClientDropdowns() {
        return null;
    }

    @Override
    public Dropdown addDropdownElement(DropdownElement dropdownElement) {
        return null;
    }

    @Override
    public Dropdown updateDropdownElement(DropdownElement dropdownElement) {
        return null;
    }

    @Override
    public Dropdown deleteDropdownElement(DropdownElement dropdownElement) {
        return null;
    }

    @Override
    public Dropdown deleteAllDropdownElements(String id) {
        return null;
    }

    @Override
    public Dropdown updateDropdown(Dropdown dropdown) {
        return null;
    }

    @Override
    public void deleteDropdownById(Dropdown dropdown) {
    }
}
