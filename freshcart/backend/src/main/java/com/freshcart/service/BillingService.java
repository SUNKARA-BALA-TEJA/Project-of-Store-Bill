package com.freshcart.service;

import com.freshcart.dto.BillDto;
import com.freshcart.model.*;
import com.freshcart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BillingService {
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    public BillingService(BillRepository billRepository, BillItemRepository billItemRepository,
                         ProductRepository productRepository, InventoryRepository inventoryRepository,
                         StockMovementRepository stockMovementRepository, UserRepository userRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BillDto.BillResponse createBill(BillDto.CreateBillRequest request, String username) {
        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
            throw new RuntimeException("Customer name is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("At least one item is required");
        }

        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        Bill bill = Bill.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName(request.getCustomerName())
                .user(user)
                .status("COMPLETED")
                .charges(request.getCharges() == null ? BigDecimal.ZERO : request.getCharges())
                .discount(request.getDiscount() == null ? BigDecimal.ZERO : request.getDiscount())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (BillDto.BillItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Inventory inventory = inventoryRepository.findByProductWithLock(product)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (itemReq.getQuantity() == null || itemReq.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Invalid quantity");
            }
            if (inventory.getCurrentStock().compareTo(itemReq.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            BigDecimal unitPrice = product.getCurrentSellingPrice();
            BigDecimal lineSubtotal = unitPrice.multiply(itemReq.getQuantity());
            subtotal = subtotal.add(lineSubtotal);

            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .product(product)
                    .productNameSnapshot(product.getName())
                    .unitSnapshot(product.getUnit())
                    .quantity(itemReq.getQuantity())
                    .actualUnitPriceCharged(unitPrice)
                    .lineSubtotal(lineSubtotal)
                    .build();
            bill.getItems().add(billItem);

            // Deduct stock
            inventory.setCurrentStock(inventory.getCurrentStock().subtract(itemReq.getQuantity()));
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .movementType("SALE")
                    .quantityChange(itemReq.getQuantity().negate())
                    .operatorUsername(username)
                    .relatedInvoiceNumber(bill.getInvoiceNumber())
                    .reason("Sale")
                    .createdAt(LocalDateTime.now())
                    .build();
            stockMovementRepository.save(movement);
        }

        bill.setSubtotal(subtotal);
        bill.setFinalTotal(subtotal.add(bill.getCharges()).subtract(bill.getDiscount()));
        Bill savedBill = billRepository.save(bill);

        List<BillDto.BillItemResponse> itemResponses = savedBill.getItems().stream().map(bi -> 
            BillDto.BillItemResponse.builder()
                    .productId(bi.getProduct() != null ? bi.getProduct().getId() : null)
                    .productName(bi.getProductNameSnapshot())
                    .unit(bi.getUnitSnapshot())
                    .quantity(bi.getQuantity())
                    .unitPrice(bi.getActualUnitPriceCharged())
                    .subtotal(bi.getLineSubtotal())
                    .imagePath(bi.getProduct() != null ? bi.getProduct().getImagePath() : null)
                    .build()
        ).collect(Collectors.toList());

        return BillDto.BillResponse.builder()
                .invoiceNumber(savedBill.getInvoiceNumber())
                .customerName(savedBill.getCustomerName())
                .createdAt(savedBill.getCreatedAt())
                .subtotal(savedBill.getSubtotal())
                .charges(savedBill.getCharges())
                .discount(savedBill.getDiscount())
                .finalTotal(savedBill.getFinalTotal())
                .items(itemResponses)
                .build();
    }

    public List<BillDto.BillSummaryResponse> getAllBills() {
        return billRepository.findAllByOrderByCreatedAtDesc().stream().map(bill -> 
            BillDto.BillSummaryResponse.builder()
                    .invoiceNumber(bill.getInvoiceNumber())
                    .createdAt(bill.getCreatedAt())
                    .customerName(bill.getCustomerName())
                    .itemCount(bill.getItems() != null ? bill.getItems().size() : 0)
                    .finalTotal(bill.getFinalTotal())
                    .status(bill.getStatus())
                    .build()
        ).collect(Collectors.toList());
    }

    public BillDto.BillResponse getBill(String invoiceNumber) {
        Bill bill = billRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        List<BillDto.BillItemResponse> itemResponses = bill.getItems().stream().map(bi -> 
            BillDto.BillItemResponse.builder()
                    .productId(bi.getProduct() != null ? bi.getProduct().getId() : null)
                    .productName(bi.getProductNameSnapshot())
                    .unit(bi.getUnitSnapshot())
                    .quantity(bi.getQuantity())
                    .unitPrice(bi.getActualUnitPriceCharged())
                    .subtotal(bi.getLineSubtotal())
                    .imagePath(bi.getProduct() != null ? bi.getProduct().getImagePath() : null)
                    .build()
        ).collect(Collectors.toList());

        return BillDto.BillResponse.builder()
                .invoiceNumber(bill.getInvoiceNumber())
                .customerName(bill.getCustomerName())
                .createdAt(bill.getCreatedAt())
                .subtotal(bill.getSubtotal())
                .charges(bill.getCharges())
                .discount(bill.getDiscount())
                .finalTotal(bill.getFinalTotal())
                .items(itemResponses)
                .build();
    }
}
