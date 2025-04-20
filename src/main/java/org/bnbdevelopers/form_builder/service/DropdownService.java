package org.bnbdevelopers.form_builder.service;

import org.bnbdevelopers.form_builder.models.Dropdown;
import org.bnbdevelopers.form_builder.models.DropdownElement;

public interface DropdownService {
    Dropdown createCustomDropdown(Dropdown dropdown);
    Dropdown getDropdownById(String id);
    Dropdown getAllClientDropdowns();
    Dropdown addDropdownElement(DropdownElement dropdownElement);
    Dropdown updateDropdownElement(DropdownElement dropdownElement);
    Dropdown deleteDropdownElement(DropdownElement dropdownElement);
    Dropdown deleteAllDropdownElements(String id);
    Dropdown updateDropdown(Dropdown dropdown);
    void deleteDropdownById(Dropdown dropdown);
}
