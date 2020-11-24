package com.ecommerce.clusterservice;

import org.springframework.data.annotation.Id;

public class OrderItem {
    @Id
    private Long _id;
    private Integer quantity;
    private Double price;
    private String name;


    public Integer getQuantity() {
        return quantity;
    }

    public Long get_id() {
        return _id;
    }

    public Double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id=" + _id +
                ", quantity=" + quantity +
                ", price=" + price +
                ", name=" + name +
                '}';
    }
}
