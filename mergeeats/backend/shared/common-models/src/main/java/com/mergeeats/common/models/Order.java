package com.mergeeats.common.models;

import com.mergeeats.common.enums.OrderStatus;
import com.mergeeats.common.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public class Order {
    
    @Id
    private String orderId;
    
    @NotBlank(message = "User ID is required")
    @Indexed
    private String userId;
    
    @NotBlank(message = "Restaurant ID is required")
    @Indexed
    private String restaurantId;
    
    @NotNull(message = "Order items are required")
    private List<OrderItem> items;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;
    
    @NotNull(message = "Order status is required")
    @Indexed
    private OrderStatus status;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
    
    private Address deliveryAddress;
    
    private String specialInstructions;
    
    @Indexed
    private LocalDateTime orderTime;
    
    private LocalDateTime estimatedDeliveryTime;
    
    private LocalDateTime actualDeliveryTime;
    
    // Group ordering fields
    private String groupOrderId;
    
    private boolean isGroupOrder = false;
    
    private String groupOrderCreatorId;
    
    // Order merging fields
    private String mergedOrderId;
    
    private boolean isMerged = false;
    
    private List<String> mergedWithOrderIds;
    
    // Delivery tracking
    private String deliveryPartnerId;
    
    private String deliveryTrackingId;
    
    // Payment details
    private String paymentId;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal serviceFee;
    
    private BigDecimal taxAmount;
    
    private BigDecimal discountAmount;
    
    // Timestamps
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Order() {
        this.orderTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }
    
    public Order(String userId, String restaurantId, List<OrderItem> items, BigDecimal totalAmount) {
        this();
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
    
    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }
    
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }
    
    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }
    
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }
    
    public String getGroupOrderId() {
        return groupOrderId;
    }
    
    public void setGroupOrderId(String groupOrderId) {
        this.groupOrderId = groupOrderId;
    }
    
    public boolean isGroupOrder() {
        return isGroupOrder;
    }
    
    public void setGroupOrder(boolean groupOrder) {
        isGroupOrder = groupOrder;
    }
    
    public String getGroupOrderCreatorId() {
        return groupOrderCreatorId;
    }
    
    public void setGroupOrderCreatorId(String groupOrderCreatorId) {
        this.groupOrderCreatorId = groupOrderCreatorId;
    }
    
    public String getMergedOrderId() {
        return mergedOrderId;
    }
    
    public void setMergedOrderId(String mergedOrderId) {
        this.mergedOrderId = mergedOrderId;
    }
    
    public boolean isMerged() {
        return isMerged;
    }
    
    public void setMerged(boolean merged) {
        isMerged = merged;
    }
    
    public List<String> getMergedWithOrderIds() {
        return mergedWithOrderIds;
    }
    
    public void setMergedWithOrderIds(List<String> mergedWithOrderIds) {
        this.mergedWithOrderIds = mergedWithOrderIds;
    }
    
    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }
    
    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }
    
    public String getDeliveryTrackingId() {
        return deliveryTrackingId;
    }
    
    public void setDeliveryTrackingId(String deliveryTrackingId) {
        this.deliveryTrackingId = deliveryTrackingId;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public BigDecimal getServiceFee() {
        return serviceFee;
    }
    
    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}