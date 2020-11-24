package ecommerce.articleservice;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.minidev.json.JSONObject;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemModelAssembler assembler;

    private Producer producer;
    private static final String ITEM = "ITEM";


    ItemController(ItemRepository itemRepository, ItemModelAssembler assembler, Producer producer){
        this.itemRepository = itemRepository;
        this.assembler = assembler;
        this.producer = producer;
    }

    @ApiOperation(
            value = "Cerca tutti gli Item",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Item.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item trovati"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @GetMapping("/items")
    CollectionModel<EntityModel<Item>> all() {

        List<EntityModel<Item>> items = itemRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(items, linkTo(methodOn(ItemController.class)
                        .all())
                        .withSelfRel());
    }

    @ApiOperation(
            value = "Cerca un Item",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Item.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item trovato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @GetMapping("/items/{id}")
    EntityModel<Item> one(@ApiParam("ID dell' Item") @PathVariable Long id){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        return assembler.toModel(item);
    }

    @ApiOperation(
            value = "Crea un nuovo Item",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Item.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item aggiunto"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @PostMapping("/items")
    ResponseEntity<EntityModel<Item>> newItem(@ApiParam("Item in JSON") @RequestBody Item item){
        item.setAvailability(Availability.AVAILABLE);
        Item newItem = itemRepository.save(item);
        sendMessage(newItem);

        return ResponseEntity
                        .created(linkTo(methodOn(ItemController.class)
                        .one(newItem.getId()))
                        .toUri())
                        .body(assembler.toModel(newItem));
    }

    @ApiOperation(
            value = "Cambia la disponibilità dell' Item",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Item.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item modificato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @PutMapping("/items/{id}/avaiability")
    ResponseEntity<?> avaiability (@ApiParam("ID dell' Item") @PathVariable Long id){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        if(item.getAvailability() == Availability.AVAILABLE)
            item.setAvailability(Availability.UNAVAILABLE);
        else item.setAvailability(Availability.AVAILABLE);
        sendMessage(item);
        return ResponseEntity.ok(assembler.toModel(itemRepository.save(item)));
    }



    private void sendMessage(Item item){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", item.getId());
        jsonObject.put("name", item.getName());
        jsonObject.put("availability", item.getAvailability());
        jsonObject.put("price", item.getPrice());

        this.producer.sendMessage(ITEM, jsonObject.toString());
    }
}
