package com.ecommerce.clusterservice;

import org.springframework.data.annotation.Id;

import java.util.Objects;

class Item {
    private @Id
    Long _id;
    private String name;
    private Double price;
    private String availability;
    private Object _class;

    Item() {}

    Item(String name, Double price, String availability){
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    public Long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }

    private Object get_class() {
        return _class;
    }

    private void set_class(Object _class) {
        this._class = _class;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item item = (Item) o;
        return Objects.equals(this._id, item._id)
                && Objects.equals(this.name, item.name)
                && Objects.equals(this.price, item.price)
                && this.availability.equals(item.availability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._id, this.name, price, this.availability);    }

    @Override
    public String toString() {
        return "Item{" + "id=" + this._id + ", name='" + this.name + '\'' + ", price=" + this.price + ", availability=" + this.availability + '}';    }
}
