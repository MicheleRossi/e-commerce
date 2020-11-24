package com.ecommerce.clusterservice;

import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {
    @Id
    private Long _id;
    private Set<OrderItem> items = new HashSet<>();
    private String timestamp;
    private String status;

    public Long getId_order() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(_id, order._id) &&
                Objects.equals(items, order.items) &&
                Objects.equals(timestamp, order.timestamp) &&
                Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, items, timestamp, status);
    }

    @Override
    public String toString() {
        return "id=" + _id +
                ", orderItems=" + items +
                ", timestamp=" + timestamp +
                ", status=" + status;
    }
}
