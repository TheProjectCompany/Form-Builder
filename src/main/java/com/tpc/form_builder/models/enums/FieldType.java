package com.tpc.form_builder.models.enums;

import lombok.Getter;

@Getter
public enum FieldType {
    TEXT("Text", "Single line text input"),
    PARAGRAPH("Paragraph", "Multi-line text input"),
    NUMBER("Number", "Numeric input"),
    DECIMAL("Decimal", "Decimal number input"),
    PERCENT("Percent", "Percental input (%)"),
    DROPDOWN("Dropdown", "Select from a list of options"),
    CHECKBOX("Checkbox", "Select multiple options"),
    RADIO("Radio", "Select a single option"),
    DATE("Date", "Select a date"),
    DATE_TIME("Date & Time", "Select date and time"),
    EMAIL("Email", "Email address input"),
    PHONE("Phone", "Phone number input"),
    FILE_UPLOAD("File Upload", "Upload files"),
    IMAGE_UPLOAD("Image Upload", "Upload images"),
//    SIGNATURE,
//    RICH_TEXT,
//    SLIDER,
//    MATRIX,
//    SECTION_BREAK,
//    PAGE_BREAK,
//    CAPTCHA,
//    GEO_LOCATION,
//    BARCODE,
//    QR_CODE,
//    COLOR_PICKER,
//    TIME,
//    WEBSITE,
//    ADDRESS,
//    PAYMENT,
//    RATING,
//    CUSTOM,
//    REPEATER,
//    HIDDEN,
//    VIDEO,
    ;

    private final String displayName;
    private final String description;

    FieldType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
