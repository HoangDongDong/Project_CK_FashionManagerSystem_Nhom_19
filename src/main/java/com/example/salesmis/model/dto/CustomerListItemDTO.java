package com.example.salesmis.model.dto;

import java.util.Objects;

public class CustomerListItemDTO {
    private final Integer customerId;
    private final String displayName;

    public CustomerListItemDTO(Integer customerId, String displayName) {
        this.customerId = customerId;
        this.displayName = displayName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPlaceholder() {
        return customerId == null;
    }

    public static CustomerListItemDTO placeholder() {
        return new CustomerListItemDTO(null, "-- Khach Van Lai (Khong luu thong tin) --");
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerListItemDTO)) return false;
        CustomerListItemDTO that = (CustomerListItemDTO) o;
        return Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
