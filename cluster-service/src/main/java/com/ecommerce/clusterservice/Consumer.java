package com.ecommerce.clusterservice;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class Consumer {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;


    @KafkaListener(topics = "CUSTOMER", groupId = "group_id")
    public void consumeMessageCustomer(String message) throws JSONException{
        JSONObject json = new JSONObject(message);
        Long id = json.getLong("id");
        Customer customer;
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if(customerOptional.isPresent())
            customer = customerOptional.get();
        else {
            customer = new Customer();
            customer.setId(json.getLong("id"));
        }
        customer.setName(json.getString("name"));
        customer.setLastName(json.getString("lastName"));
        customer.setSubscription(json.getString("subscription"));

        customerRepository.save(customer);
    }


    @KafkaListener(topics = "ORDER", groupId = "group_id")
    public void consumeMessageOrder(String message) throws JSONException{
        JSONObject jOrder = getJSON(message);
        JSONArray jItems = jOrder.getJSONArray("items"); //Cioè un orderItems

        Set<OrderItem> orderItems = new HashSet<>();
        for(int i = 0; i<jItems.length(); i++) {
            JSONObject jItem = jItems.getJSONObject(i);
            //Item item = itemRepository.findById(jItem.getLong("id")).get();

            OrderItem orderItem = new OrderItem();
            orderItem.set_id(jItem.getLong("id"));
            orderItem.setPrice(jItem.getDouble("price"));
            orderItem.setQuantity(jItem.getInt("quantity"));

            orderItems.add(orderItem);
        }

        Customer customer = new Customer();
        if(customerRepository.findById(jOrder.getLong("id_customer")).isPresent())
            customer = customerRepository.findById(jOrder.getLong("id_customer")).get();
        else{
            System.err.println("CUSTOMER NON PRESENTE NELLA LISTA EVENTI");
        }

        Order order = new Order();
        order.set_id(jOrder.getLong("id"));
        order.setItems(orderItems);
        order.setStatus(jOrder.getString("status"));
        order.setTimestamp(jOrder.getString("timestamp"));
        Set<Order> orders = new HashSet<>(customer.getOrders());
        orders.add(order);
        customer.setOrders(orders);
        customerRepository.save(customer);
    }


    @KafkaListener(topics = "ITEM", groupId = "group_id")
    public void consumeMessageItem(String message) throws JSONException{

        JSONObject json = getJSON(message);
        Item item = new Item();
        item.set_id(json.getLong("id"));
        item.setName(json.getString("name"));
        item.setPrice(json.getDouble("price"));
        item.setAvailability(json.getString("availability"));
        itemRepository.save(item);
    }


    private JSONObject getJSON(String message) throws JSONException {
        return new JSONObject(message);
    }
}
