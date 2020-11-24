package ecommerce.articleservice;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ItemModelAssembler implements RepresentationModelAssembler<Item, EntityModel<Item>> {
    @Override
    public EntityModel<Item> toModel(Item item) {
        EntityModel<Item> orderModel = EntityModel.of(item, linkTo(methodOn(ItemController.class)
                        .one(item.getId()))
                        .withSelfRel(), linkTo(methodOn(ItemController.class)
                        .all())
                        .withRel("items"));

        orderModel.add(linkTo(methodOn(ItemController.class).avaiability(item.getId())).withRel("availability"));

        return orderModel;
    }
}
