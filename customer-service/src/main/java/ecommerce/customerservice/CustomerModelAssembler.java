package ecommerce.customerservice;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class CustomerModelAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {
    @Override
    public EntityModel<Customer> toModel(Customer customer) {
        EntityModel<Customer> customerModel = EntityModel.of(customer, linkTo(methodOn(CustomerController.class)
                        .one(customer.getId()))
                        .withSelfRel(), linkTo(methodOn(CustomerController.class)
                        .all())
                        .withRel("customers"));
        if(customer.getSubscription() == Subscription.FREE){
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.PLUS.name())).withRel("subscription"));
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.PRO.name())).withRel("subscription"));
        }
        if(customer.getSubscription() == Subscription.PLUS){
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.FREE.name())).withRel("subscription"));
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.PRO.name())).withRel("subscription"));
        }
        if(customer.getSubscription() == Subscription.PRO){
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.FREE.name())).withRel("subscription"));
            customerModel.add(linkTo(methodOn(CustomerController.class).subscription(customer.getId(), Subscription.PLUS.name())).withRel("subscription"));
        }
        customerModel.add(linkTo(methodOn(CustomerController.class).delete(customer.getId())).withRel("delete"));
        return customerModel;
    }
}
