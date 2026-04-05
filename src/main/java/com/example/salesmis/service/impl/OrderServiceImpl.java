package com.example.salesmis.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.CreateInvoiceRequest;
import com.example.salesmis.model.dto.DiscountResult;
import com.example.salesmis.model.dto.InvoiceSummaryDTO;
import com.example.salesmis.model.dto.InvoiceItemRequest;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.Customer;
import com.example.salesmis.model.entity.OrderDetail;
import com.example.salesmis.model.entity.OrderEntity;
import com.example.salesmis.model.entity.Product;
import com.example.salesmis.model.entity.Staff;
import com.example.salesmis.model.entity.Voucher;
import com.example.salesmis.repository.InvoiceRepository;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.service.OrderService;
import com.example.salesmis.service.VoucherService;
import com.example.salesmis.service.exception.InsufficientStockException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final VoucherService voucherService;
    private final EntityManagerProvider entityManagerProvider;

    public OrderServiceImpl(
            ProductRepository productRepository,
            InvoiceRepository invoiceRepository,
            VoucherService voucherService,
            EntityManagerProvider entityManagerProvider) {
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.voucherService = voucherService;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public OrderEntity placeOrder(CreateInvoiceRequest request) {
        validateRequest(request);

        EntityManager entityManager = entityManagerProvider.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Use-case include: always check inventory before saving invoice.
            for (InvoiceItemRequest item : request.getItems()) {
                if (!productRepository.hasEnoughStock(entityManager, item.getProductId(), item.getQuantity())) {
                    throw new InsufficientStockException("Khong du ton kho cho san pham ID: " + item.getProductId());
                }
            }

            OrderEntity order = buildOrderEntity(entityManager, request);
            invoiceRepository.save(entityManager, order);

            // Deduct inventory after invoice is persisted.
            for (InvoiceItemRequest item : request.getItems()) {
                productRepository.decreaseStock(entityManager, item.getProductId(), item.getQuantity());
            }

            transaction.commit();
            return order;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<ProductInventoryDTO> searchProducts(String keyword) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            return productRepository.searchProductsWithStock(entityManager, keyword);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<InvoiceSummaryDTO> getRecentInvoices(int limit) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            return invoiceRepository.findRecentInvoices(entityManager, limit)
                    .stream()
                    .map(this::toInvoiceSummary)
                    .toList();
        } finally {
            entityManager.close();
        }
    }

    private void validateRequest(CreateInvoiceRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Hoa don phai co it nhat 1 san pham");
        }
        for (InvoiceItemRequest item : request.getItems()) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Thong tin san pham trong hoa don khong hop le");
            }
        }
    }

    private OrderEntity buildOrderEntity(EntityManager entityManager, CreateInvoiceRequest request) {
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setNote(request.getNote());

        if (request.getStaffId() != null) {
            Staff staff = entityManager.getReference(Staff.class, request.getStaffId());
            order.setStaff(staff);
        }
        if (request.getCustomerId() != null) {
            Customer customer = entityManager.getReference(Customer.class, request.getCustomerId());
            order.setCustomer(customer);
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (InvoiceItemRequest item : request.getItems()) {
            Product product = productRepository.findById(entityManager, item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay san pham ID: " + item.getProductId()));

            BigDecimal unitPrice = Objects.requireNonNullElse(item.getUnitPrice(), product.getBasePrice());
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(unitPrice);
            detail.setSubtotal(subtotal);
            order.getOrderDetails().add(detail);

            totalAmount = totalAmount.add(subtotal);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalTotal = totalAmount;
        if (request.getVoucherId() != null) {
            DiscountResult applied = voucherService.validateAndCalculateForOrder(request.getVoucherId(), totalAmount);
            discountAmount = applied.getDiscountAmount();
            finalTotal = applied.getNewTotal();
            
            Voucher voucher = entityManager.find(Voucher.class, request.getVoucherId());
            if (voucher.getUsageLimit() != null) {
                if (voucher.getUsageLimit() <= 0) {
                    throw new IllegalArgumentException("Mã Voucher đã hết lượt sử dụng!");
                }
                voucher.setUsageLimit(voucher.getUsageLimit() - 1);
            }
            order.setVoucher(voucher);
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalTotal(finalTotal);
        return order;
    }

    private InvoiceSummaryDTO toInvoiceSummary(OrderEntity order) {
        InvoiceSummaryDTO dto = new InvoiceSummaryDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setFinalTotal(order.getFinalTotal());
        return dto;
    }
}

