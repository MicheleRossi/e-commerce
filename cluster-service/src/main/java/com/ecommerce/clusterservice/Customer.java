package com.ecommerce.clusterservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document
public class Customer {
    @Id
    private Long _id;
    private String name;
    private String lastName;
    private String subscription;
    private Set<Order> orders = new HashSet<>();

    public Customer(){}

    public Customer(String name, String lastName, String subscription, HashSet<Order> orders){
        this.name = name;
        this.lastName = lastName;
        this.subscription = subscription;
        this.orders = orders;
    }

    public Long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSubscription() {
        return subscription;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Customer))
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(this._id, customer._id)
                && Objects.equals(this.name, customer.name)
                && Objects.equals(this.lastName, customer.lastName)
                && this.subscription.equals(customer.subscription)
                && this.orders.equals(customer.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name, lastName, subscription, orders);
    }

    @Override
    public String toString() {
        return "{" + "\"_id\"=" + _id + ", \"name\"=" + this.name + ", \"lastName\"="  + lastName + ", \"subscription\"=" + subscription + ", \"orders:\"" + orders + "}"; }
}
