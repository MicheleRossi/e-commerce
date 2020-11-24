package ecommerce.orderservice;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
    @Override
    public EntityModel<Order> toModel(Order order) {
        //crea link per se stesso
        EntityModel<Order> orderModel = EntityModel.of(order, linkTo(methodOn(OrderController.class)
                        .one(order.getId()))
                        .withSelfRel(), linkTo(methodOn(OrderController.class)
                        .all())
                        .withRel("orders"));
        //aggiunge link per le altre operazioni
        if(order.getStatus() == Status.IN_PROGRESS){
            orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }
        return orderModel;
    }
}
