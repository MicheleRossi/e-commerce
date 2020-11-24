package ecommerce.customerservice;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.minidev.json.JSONObject;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerModelAssembler assembler;

    private static final String CUSTOMER = "CUSTOMER";
    private Producer producer;

    CustomerController(CustomerRepository customerRepository, CustomerModelAssembler assembler, Producer producer){
        this.customerRepository = customerRepository;
        this.assembler = assembler;
        this.producer = producer;
    }

    @ApiOperation(
            value = "Cerca tutti i Customer",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer trovati"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @GetMapping("/customers")
    CollectionModel<EntityModel<Customer>> all() {

        List<EntityModel<Customer>> customers = customerRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(customers,
                linkTo(methodOn(CustomerController.class).all()).withSelfRel());
    }

    @ApiOperation(
            value = "Ricerca un Customer",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer trovato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @GetMapping("/customers/{id}")
    EntityModel<Customer> one(@ApiParam("ID del Customer") @PathVariable Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return assembler.toModel(customer);
    }

    @ApiOperation(
            value = "Crea un nuovo Customer",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer aggiunto"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @PostMapping("/customers")
    @Transactional
    ResponseEntity<EntityModel<Customer>> newCustomer(@ApiParam("Customer in JSON") @RequestBody Customer customer){
        Customer newCustomer = customerRepository.save(customer);
        //kafka
        sendMessage(CUSTOMER, newCustomer);
        return ResponseEntity
                        .created(linkTo(methodOn(CustomerController.class)
                        .one(newCustomer.getId()))
                        .toUri())
                        .body(assembler.toModel(newCustomer));
    }





    @ApiOperation(
            value = "Modifica un Customer",
            notes = "Restituisce i dati dei Customer in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer modificato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @Transactional
    @PutMapping("/customers/{id}")
    ResponseEntity<?> replaceCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
        Customer customer;
        if(customerRepository.findById(id).isPresent()) {
            customer = customerRepository.findById(id).get();
            customer.setName(newCustomer.getName());
            customer.setLastName(newCustomer.getLastName());
            customer.setAddress(newCustomer.getAddress());
            customer.setSubscription(newCustomer.getSubscription());
            customerRepository.save(customer);
            sendMessage(CUSTOMER, customer);
            return ResponseEntity
                            .created(linkTo(methodOn(CustomerController.class)
                            .one(newCustomer.getId()))
                            .toUri())
                            .body(assembler.toModel(customer));
        }

        else return notAllowed("The customer with id=" + id + " doesn't exist");
    }






    @ApiOperation(
            value = "Modifica la sottoscrizione di unCustomer",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer modificato"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @Transactional
    @PutMapping("/customers/{id}/subscription/{subscription}")
    ResponseEntity<?> subscription(@ApiParam("ID del customer") @PathVariable Long id, @ApiParam("Valore sottoscrizione") @PathVariable(value = "subscription") String subscription){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        if(customer.getSubscription() == Subscription.valueOf(subscription))
            return notAllowed("The customer " + id + " already has a " + subscription + " subscription");

        customer.setSubscription(Subscription.valueOf(subscription));
        //kafka
        sendMessage(CUSTOMER, customer);
        return ResponseEntity.ok(assembler.toModel(customerRepository.save(customer)));
    }

    /*@PutMapping("/customers/{id}/free")
    ResponseEntity<?> subscriptionFree(@PathVariable Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setSubscription(Subscription.FREE);

        return ResponseEntity.ok(assembler.toModel(customerRepository.save(customer)));
    }*/

    @ApiOperation(
            value = "Cancella un Customer e i relativi ordini",
            notes = "Restituisce i dati degli ordini in formato JSON",
            response = Customer.class,
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Non restituisce contenuto"),
            @ApiResponse(code = 404, message = "Errore")
    })
    @Transactional
    @DeleteMapping("/customers/{id}/delete")
    ResponseEntity<?> delete(@PathVariable Long id){
        //String linkOrders = "http://localhost:9090/orders/delete?customer=" + id;
        //kafka
        //sendMessage(CUSTOMER, customerRepository.findById(id).get());
        customerRepository.deleteById(id);
        //new RestTemplateBuilder().build().delete(linkOrders);
        return ResponseEntity.noContent().build();
    }



    private void sendMessage(String topic, Customer customer){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", customer.getId());
        jsonObject.put("name", customer.getName());
        jsonObject.put("lastName", customer.getLastName());
        jsonObject.put("subscription", customer.getSubscription());


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



    //TROVA TUTTI GLI ORDINI CHE HA EFFETTUATO
    //TROVA TUTTI GLI ARTICOLI CHE HA ORDINATO

//    @GetMapping("/customers/{id}")
//    EntityModel<Customer> one(@PathVariable String name){
//        Customer customerName = customerRepository.findCustomerByName(name)
//                .orElseThrow(() -> new NameNotFoundException(name));
//
//        return assembler.toModel(customerName);
//    }


}
