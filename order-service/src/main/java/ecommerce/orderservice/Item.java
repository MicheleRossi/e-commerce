package ecommerce.orderservice;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "Item")
class Item {
    @Id
    @GeneratedValue
    private Long idOrderItem;
    private Integer quantity;
    private Long id;
    private Float price;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_order")
    @JsonBackReference
    private Order order;

    Item() {}

    Item(Long id, Integer quantity){
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getIdOrderItem() {
        return idOrderItem;
    }

    public Float getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdOrderItem(Long idOrderItem) {
        this.idOrderItem = idOrderItem;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item item = (Item) o;
        return Objects.equals(this.id, item.id)
                && Objects.equals(this.quantity, item.quantity)
                && Objects.equals(this.price, item.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.quantity);    }

    @Override
    public String toString() {
        return "id:" + this.id + ", quantity:" + this.quantity + ", price:" + price;    }
}
