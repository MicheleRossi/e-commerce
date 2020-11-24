package ecommerce.articleservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "Item")
class Item {
    private @Id @GeneratedValue Long id;
    private String name;
    private Float price;
    private Float weight;
    private Availability availability;

    Item() {}

    Item(String name, float price, Float weight, Availability availability){
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public Float getWeight() {
        return weight;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item item = (Item) o;
        return Objects.equals(this.id, item.id)
                && Objects.equals(this.name, item.name)
                && Objects.equals(this.price, item.price)
                && Objects.equals(this.weight, item.weight)
                && this.availability == item.availability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, price, this.weight, this.availability);    }

    @Override
    public String toString() {
        return "Item{" + "id=" + this.id + ", name='" + this.name + '\'' + ", price=" + this.price + ", weight=" + this.weight + ", availability=" + this.availability + '}';    }
}
