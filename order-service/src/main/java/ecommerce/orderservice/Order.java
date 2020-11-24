package ecommerce.orderservice;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOMER_ORDER")
class Order {
    @Id @GeneratedValue
    private Long id;
    private Long id_customer;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    @JsonManagedReference
    private Set<Item> items = new HashSet<>();
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private Status status;

    Order() {}

    Order(Long id_customer, Set<Item> items){
        this.id_customer = id_customer;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getId_customer() {
        return id_customer;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Long id_order) {
        this.id = id_order;
    }

    public void setId_customer(Long customer) {
        this.id_customer = customer;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Order))
            return false;
        Order order = (Order) o;
        return Objects.equals(this.id, order.id)
                && Objects.equals(this.id_customer, order.id_customer)
                && Objects.equals(this.items, order.items)
                && this.status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.id_customer, this.items, this.status);    }

    @Override
    public String toString() {
        return "Order{" + "id=" + this.id + ", id_customer=" + this.id_customer + "items=[" + this.items + "], status=\"" + this.status.name() + "\"";    }
}
