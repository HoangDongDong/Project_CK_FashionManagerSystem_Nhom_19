package com.example.salesmis.model.dto;

/**
 * Mot dong trong combo chon ma giam gia (hien thi day du de nhan biet, khong can nho ma).
 */
public class VoucherListItemDTO {
    private final Integer voucherId;
    private final String code;
    private final String displayText;

    public VoucherListItemDTO(Integer voucherId, String code, String displayText) {
        this.voucherId = voucherId;
        this.code = code;
        this.displayText = displayText;
    }

    public static VoucherListItemDTO placeholder() {
        return new VoucherListItemDTO(null, null, "-- Chon ma giam gia --");
    }

    public boolean isPlaceholder() {
        return voucherId == null;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
