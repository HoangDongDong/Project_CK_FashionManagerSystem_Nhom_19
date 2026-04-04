package com.example.salesmis.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.DiscountResult;
import com.example.salesmis.model.dto.VoucherListItemDTO;
import com.example.salesmis.model.entity.Voucher;
import com.example.salesmis.repository.VoucherRepository;
import com.example.salesmis.service.VoucherService;
import com.example.salesmis.service.exception.InvalidVoucherException;
import com.example.salesmis.service.exception.RequirementNotMetException;

import jakarta.persistence.EntityManager;

public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final EntityManagerProvider entityManagerProvider;

    public VoucherServiceImpl(VoucherRepository voucherRepository, EntityManagerProvider entityManagerProvider) {
        this.voucherRepository = voucherRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public DiscountResult validateAndCalculateDiscount(String voucherCode, BigDecimal currentOrderTotal) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            Voucher voucher = voucherRepository
                    .findActiveByCode(entityManager, voucherCode)
                    .orElseThrow(() -> new InvalidVoucherException("Ma khong hop le hoac het han"));
            assertVoucherWindowAndMinOrder(voucher, currentOrderTotal);
            return buildDiscountResult(voucher, normalizeTotal(currentOrderTotal));
        } finally {
            entityManager.close();
        }
    }

    @Override
    public DiscountResult validateAndCalculateForOrder(Integer voucherId, BigDecimal currentOrderTotal) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            Voucher voucher = voucherRepository
                    .findById(entityManager, voucherId)
                    .orElseThrow(() -> new InvalidVoucherException("Ma khong hop le hoac het han"));
            if (!isActiveStatus(voucher.getStatus())) {
                throw new InvalidVoucherException("Ma khong hop le hoac het han");
            }
            assertVoucherWindowAndMinOrder(voucher, currentOrderTotal);
            return buildDiscountResult(voucher, normalizeTotal(currentOrderTotal));
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<VoucherListItemDTO> listVouchersForSelection() {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            return voucherRepository.findAllWithCodeOrderByCode(entityManager).stream()
                    .map(this::toSelectionItem)
                    .toList();
        } finally {
            entityManager.close();
        }
    }

    private VoucherListItemDTO toSelectionItem(Voucher v) {
        LocalDateTime now = LocalDateTime.now();
        String code = v.getCode() == null ? "" : v.getCode().trim();

        String type = v.getDiscountType() == null ? "" : v.getDiscountType().trim().toUpperCase(Locale.ROOT);
        boolean percent = type.contains("PERCENT") || "%".equals(type) || "PCT".equals(type);
        BigDecimal val = Objects.requireNonNullElse(v.getDiscountValue(), BigDecimal.ZERO);
        String discountPart = percent ? val.stripTrailingZeros().toPlainString() + "%" : val.stripTrailingZeros().toPlainString() + " (tien)";

        StringBuilder sb = new StringBuilder(code);
        sb.append(" | Giam: ").append(discountPart);
        if (v.getMinOrderValue() != null && v.getMinOrderValue().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" | Don toi thieu: ").append(v.getMinOrderValue().stripTrailingZeros().toPlainString());
        }
        if (!isActiveStatus(v.getStatus())) {
            sb.append(" | [TAM DUNG]");
        }
        if (v.getStartDate() != null && now.isBefore(v.getStartDate())) {
            sb.append(" | [CHUA HIEU LUC]");
        }
        if (v.getEndDate() != null && now.isAfter(v.getEndDate())) {
            sb.append(" | [HET HAN]");
        }

        return new VoucherListItemDTO(v.getVoucherId(), code, sb.toString());
    }

    private static BigDecimal normalizeTotal(BigDecimal currentOrderTotal) {
        return currentOrderTotal == null ? BigDecimal.ZERO : currentOrderTotal;
    }

    private static boolean isActiveStatus(String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        return "ACTIVE".equalsIgnoreCase(status.trim());
    }

    /** Kiem tra thoi gian hieu luc va MinOrderValue (nhanh alt trong sequence diagram). */
    private void assertVoucherWindowAndMinOrder(Voucher voucher, BigDecimal currentOrderTotal) {
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new InvalidVoucherException("Ma khong hop le hoac het han");
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new InvalidVoucherException("Ma khong hop le hoac het han");
        }

        BigDecimal total = normalizeTotal(currentOrderTotal);
        BigDecimal min = voucher.getMinOrderValue();
        if (min != null && min.compareTo(BigDecimal.ZERO) > 0 && total.compareTo(min) < 0) {
            throw new RequirementNotMetException("Don hang chua du gia tri toi thieu");
        }
    }

    private DiscountResult buildDiscountResult(Voucher voucher, BigDecimal orderTotal) {
        BigDecimal discount = computeDiscountAmount(voucher, orderTotal);
        BigDecimal newTotal = orderTotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        DiscountResult result = new DiscountResult();
        result.setVoucherId(voucher.getVoucherId());
        result.setDiscountAmount(discount.setScale(2, RoundingMode.HALF_UP));
        result.setNewTotal(newTotal);
        return result;
    }

    /**
     * discount_type: PERCENT / PERCENTAGE / % -> discount_value la phan tram; nguoc lai la tien co dinh.
     */
    private BigDecimal computeDiscountAmount(Voucher voucher, BigDecimal orderTotal) {
        BigDecimal value = Objects.requireNonNullElse(voucher.getDiscountValue(), BigDecimal.ZERO);
        if (value.compareTo(BigDecimal.ZERO) <= 0 || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        String type = voucher.getDiscountType() == null ? "" : voucher.getDiscountType().trim().toUpperCase(Locale.ROOT);
        boolean percent = type.contains("PERCENT") || "%".equals(type) || "PCT".equals(type);

        BigDecimal raw;
        if (percent) {
            raw = orderTotal.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            raw = value;
        }
        return raw.min(orderTotal).max(BigDecimal.ZERO);
    }
}
