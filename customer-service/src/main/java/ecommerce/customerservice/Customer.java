package ecommerce.customerservice;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Customer")
class Customer {
    private @Id @GeneratedValue Long id;
    private String name;
    private String lastName;
    private String address;
    private Subscription subscription;

    Customer() {}

    Customer(String name, String lastName , String address, Subscription subscription){
        this.name = name;
        this.lastName = lastName;
        this.address =address;
        this.subscription = subscription;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Customer))
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(this.id, customer.id)
                && Objects.equals(this.name, customer.name)
                && Objects.equals(this.lastName, customer.lastName)
                && Objects.equals(this.address, customer.address)
                && this.subscription == customer.subscription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, address, subscription);
    }

    @Override
    public String toString() {
        return "{" + "\"id\"=" + id + ", \"name\"=" + this.name + ", \"lastName\"="  + lastName + ", \"address\"=" + address + ", \"subscription\"=" + subscription + "}";    }
}
