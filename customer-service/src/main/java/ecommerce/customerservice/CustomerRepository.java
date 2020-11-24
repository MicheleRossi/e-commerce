package ecommerce.customerservice;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findCustomerByName(String name);
}

