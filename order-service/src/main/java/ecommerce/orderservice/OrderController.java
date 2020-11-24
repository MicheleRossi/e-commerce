package ecommerce.orderservice;

import io.swagger.annotations.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(value = "ecommerce", tags = "Controller operazioni che riguardano gli ordini")
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderModelAssembler assembler;

    private static final String ORDER = "ORDER";
    private Producer producer;

    @Value( "${portCustomerService}" )
    private String portCustomerService;
    @Value( "${portItemService}" )
    private String portItemService;
    @Value( "${host}" )
    private String host;




    OrderController(OrderRepository orderRepository, OrderModelAssembler assembler, Producer producer){
        this.orderRepository = orderRepository;
        this.assembler = assembler;
        this.producer = producer;
    }

    @ApiOperation(
            value = "Ricerca  tutti gli ordini",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Ordini trovati"),
                    @ApiResponse(code = 404, message = "Errore")
            })
    @GetMapping("/orders")
    CollectionModel<EntityModel<Order>> all() {

        List<EntityModel<Order>> orders = orderRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @ApiOperation(
            value = "Ricerca un ordine",
            notes = "Restituisce i dati degll'ordine in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordine trovato"),
            @ApiResponse(code = 404, message = "Ordine non trovato")
    })
    @GetMapping("/orders/{id}")
    EntityModel<Order> one(@ApiParam("ID dell'ordine") @PathVariable Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @ApiOperation(
            value = "Inserisce nuovo ordine",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordini trovati"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @PostMapping("/orders")
    ResponseEntity<?> newOrder(@ApiParam("Ordine in JSON") @RequestBody Order order) throws IOException{

        String linkCustomers = "http://" + host + ":" + portCustomerService + "/" + "customers" + "/" + order.getId_customer();

        //CONTROLLO SE ESISTE IL CLIENTE
        if(!(checkExists(linkCustomers)))
            return notAllowed("The customer " + order.getId_customer() + " doesn't exist");

        //CONTROLLO SE ESISTONO GLI ARTICOLI
        for(Item item : order.getItems()) {
            String linkItem = "http://" + host + ":" + portItemService + "/" + "items" + "/" + item.getId();
            if(!(checkExists(linkItem)))
                return notAllowed("The item " + item.getId() + " doesn't exist");
            else
                item.setPrice(Objects.requireNonNull(new RestTemplateBuilder().build().getForObject(linkItem, Item.class)).getPrice());
        }

        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = orderRepository.save(order);
        sendMessage(ORDER, newOrder);

        //JSON ORDER
        return ResponseEntity
                        .created(linkTo(methodOn(OrderController.class)
                        .one(newOrder.getId()))
                        .toUri())
                        .body(assembler.toModel(newOrder));
    }

    @ApiOperation(
            value = "Set status dell'ordine COMPLETATO",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordine modificato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @PutMapping("/orders/{id}/complete")
    ResponseEntity<?> complete(@ApiParam("ID dell'ordine") @PathVariable Long id){
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if(order.getStatus() == Status.IN_PROGRESS){
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(order));
        }
        return notAllowed("You can't complete an order that is in the " + order.getStatus() + " status");
    }

    @ApiOperation(
            value = "Set status dell'ordine CANCELLATO",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordine modificato"),
            @ApiResponse(code = 404, message = "Errore")
    })
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Non restituisce contenuto"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @DeleteMapping("/orders/{id}/delete")
    ResponseEntity<?> delete(@PathVariable Long id){
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(
            value = "Cancella gli ordini di un determinato Customer",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Order.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Non restituisce contenuto"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @DeleteMapping("/orders/delete")
    ResponseEntity<?> deleteByCustomer(@ApiParam("ID del customer") @RequestParam (value = "customer") Long customer) {
        all().forEach(orderEntityModel -> {
            if(Objects.requireNonNull(orderEntityModel.getContent()).getId_customer().equals(customer))
                orderRepository.delete(orderEntityModel.getContent());
        });
        return ResponseEntity.noContent().build();
    }













    private void sendMessage(String topic, Order order){
        /*JSONObject jsonObject = new JSONObject();
        JSONObject nestedJsonObject = new JSONObject();
        try {
        *//*
        Adding some random data into the JSON object.
         *//*
            jsonObject.put("index", 1);
            jsonObject.put("message", "The index is now: " + 1);

        *//*
        We're adding a field in the nested JSON object.
         *//*
            nestedJsonObject.put("nestedObjectMessage", "This is a nested JSON object with index: " + index);

        *//*
        Adding the nexted JSON object to the main JSON object.
         *//*
            jsonObject.put("nestedJsonObject", nestedJsonObject);

        } catch (JSONException e) {
            logger.error(e.getMessage());
        }*/



        JSONObject jsonObject = new JSONObject();
        JSONArray nestedItems = new JSONArray();
        for(Item i : order.getItems()){
            JSONObject jItem = new JSONObject();
            jItem.put("id", i.getId());
            jItem.put("quantity", i.getQuantity());
            jItem.put("price", i.getPrice());
            nestedItems.appendElement(jItem);
        }
        jsonObject.put("id", order.getId());
        jsonObject.put("id_customer", order.getId_customer());
        jsonObject.put("items", nestedItems);
        jsonObject.put("timestamp", order.getTimestamp().toString());
        jsonObject.put("status", order.getStatus());

        System.out.println(jsonObject);
        this.producer.sendMessage(topic, jsonObject.toString());
    }


    private ResponseEntity<?> notAllowed(String detail){
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail(detail));
    }

    private boolean checkExists(String link)throws IOException{
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if(conn.getResponseCode() == 200){
            conn.disconnect();
            return true;
        }
        conn.disconnect();
        return false;
    }

    /*@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }*/

    /*@Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            try {
               restTemplate.getForObject("http://localhost:9292/items/1", String.class);
            }
            catch (Exception e){
                System.err.println(e.getMessage());
            }
        };
    }*/



    /*@Bean
    public CommandLineRunner run() throws Exception {
        return args -> {
            Price quote = new RestTemplateBuilder().build().getForObject(
                    "http://localhost:9292/items/1", Price.class);
            assert quote != null;
            log.info(quote.getPrice().toString());
        };
    }*/

    /*URL urlCustomer = new URL("http://localhost:" + portCustomerService + "/customers/" + order.getCustomer());
        HttpURLConnection conn = (HttpURLConnection) urlCustomer.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) // OR conn_Art != 200
            return notAllowed("The customer " + order.getCustomer() + " doesn't exist");
        conn.disconnect();*/

    /*for(Item i : order.getItems()){
            URL urlItem = new URL("http://localhost:" + portItemService + "/items/" + i.getId());
            HttpURLConnection conn2 = (HttpURLConnection) urlItem.openConnection();
            conn2.setRequestMethod("GET");
            conn2.setRequestProperty("Accept", "application/json");
            if (conn2.getResponseCode() != 200)
                return notAllowed("The item " + i.getId() + " doesn't exist)");
            conn2.disconnect();
            //SET PRICE
            i.setPrice(Objects.requireNonNull(new RestTemplateBuilder().build().getForObject(
                    "http://localhost:" + portItemService + "/items/" + i.getId(), Item.class)).getPrice());
        }*/

    /*ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));*/

    /*private HttpURLConnection createConnection(String urlString, String obj)throws IOException {
        URL url = new URL(urlString + obj);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }*/
}
