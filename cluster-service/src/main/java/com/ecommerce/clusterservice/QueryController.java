package com.ecommerce.clusterservice;


import com.google.gson.Gson;
import com.mongodb.*;

import com.mongodb.client.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
import org.bson.BSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import javax.print.Doc;
import java.net.UnknownHostException;
import java.util.*;

@RestController
public class QueryController {
    @Value("spring.data.mongodb.host")
    String host;
    @Value("spring.data.mongodb.port")
    String port;
    @Value("spring.data.mongodb.db")
    String db;

    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    MongoClient mongoClient;

    QueryController(MongoClient mongoClient, ItemRepository itemRepository, CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.mongoClient = mongoClient;
    }

    @GetMapping("/query/find({queryJson})")
    List<Customer> query(@PathVariable String queryJson) throws Exception {
        MongoDatabase database = mongoClient.getDatabase("db");//mongoClient.getDB("db");
        MongoCollection coll = database.getCollection("customer");
        Document queryDoc = Document.parse(queryJson);
        MongoCursor iterator = coll.find(queryDoc).iterator();

        List<Customer> customerList = new ArrayList<>();
        while(iterator.hasNext()){
            Document doc = (Document) iterator.next();
            Customer customer = new Gson().fromJson(doc.toJson(), Customer.class);
            customerList.add(customer);
        }

        return customerList;
    }






















    //Document lookup = coll.aggregate();
    /*@GetMapping("/query/find({queryJson})")
    List<Customer> query(@PathVariable String queryJson) throws Exception {
        MongoDatabase database = mongoClient.getDatabase("db");//mongoClient.getDB("db");
        MongoCollection coll = database.getCollection("customer");
        Document queryDoc = Document.parse(queryJson);

        MongoCursor iterator = coll.find(queryDoc).iterator();

        List<Customer> customerList = new ArrayList<>();
        while(iterator.hasNext()){
            Document doc = (Document) iterator.next();
            Customer customer = new Gson().fromJson(doc.toJson(), Customer.class);

            customer.getOrders().forEach(order ->
                    order.getItems().forEach(orderItem ->
                            orderItem.setName(itemRepository.findById(orderItem.get_id()).get().getName())));
            customerList.add(customer);
        }
        return customerList;
    }*/

    /*@GetMapping("/query/find({queryJson})")
    List<Customer> query(@PathVariable String queryJson) throws Exception {
        MongoDatabase database = mongoClient.getDatabase("db");//mongoClient.getDB("db");
        MongoCollection coll = database.getCollection("customer");
        Document queryDoc = Document.parse(queryJson);


        AggregateIterable<Document> documents = coll.aggregate(Arrays.asList(Aggregates.lookup("item", "orders.items._id", "_id", "orders.items")));
        MongoCursor iterator1 = documents.iterator();

        StringBuilder querysb = new StringBuilder();
        Document mainDoc = new Document();

        List<Document> list = new ArrayList<>();



        while(iterator1.hasNext()) {
            Document doc1 = (Document) iterator1.next();
            querysb.append(doc1.toJson());
            list.add(doc1);


                *//*coll.aggregate(Arrays.asList(

                // Java equivalent of the $match stage
                Aggregates.match(Filters.regex("name", "John")),

                // Java equivalent of the $group stage
                Aggregates.group("$name", Accumulators.first("category", "$category"))

        ));*//*


        }



        System.out.println(querysb.toString());
        coll.insertMany(list);
        Document result = Document.parse(querysb.toString());
        System.out.println(result);
        MongoCursor iterator = coll.find(queryDoc1).iterator();

        List<Customer> customerList = new ArrayList<>();
        while(iterator.hasNext()){
            Document doc = (Document) iterator.next();
            System.out.println(doc.toJson());
            Customer customer = new Gson().fromJson(doc.toJson(), Customer.class);

            *//*customer.getOrders().forEach(order ->
                    order.getItems().forEach(orderItem ->
                            orderItem.setName(itemRepository.findById(orderItem.get_id()).get().getName())));*//*
            customerList.add(customer);
        }
        return customerList;
    }*/

    /*@GetMapping("/query/find({queryJson})")
    List<Customer> query(@PathVariable String queryJson) throws Exception {
        MongoDatabase database = mongoClient.getDatabase("db");//mongoClient.getDB("db");
        MongoCollection coll = database.getCollection("customer");
        Document queryDoc = Document.parse(queryJson);
        AggregateIterable<Document> documents = coll.aggregate(Arrays.asList(Aggregates.lookup("item", "orders.items._id", "_id", "orders.items")));
        List<Document> documentList = new ArrayList<>();
        for(Document document : documents){
            documentList.add(document);
        }
        coll.

        MongoCursor iterator = coll.find(queryDoc).iterator();

        List<Customer> customerList = new ArrayList<>();
        while(iterator.hasNext()){

            Document doc = (Document) iterator.next();
            Customer customer = new Gson().fromJson(doc.toJson(), Customer.class);
            customerList.add(customer);
        }
        return customerList;
    }*/

    /*@GetMapping("/query/find({queryJson})")
    List<Customer> query(@PathVariable String queryJson) throws Exception {
        MongoDatabase database = mongoClient.getDatabase("db");//mongoClient.getDB("db");
        MongoCollection collectionCustomer = database.getCollection("customer");
        MongoCollection collectionJoin = database.getCollection("join");

        AggregateIterable<Document> documents = collectionCustomer.aggregate(Arrays.asList(Aggregates.lookup("item", "orders.items._id", "_id", "orders.items")));

        List<Document> documentList = new ArrayList<>();
        for(Document document : documents)
            documentList.add(document);

        collectionJoin.deleteMany(new Document());
        collectionJoin.insertMany(documentList);

        Document queryDoc = Document.parse(queryJson);
        MongoCursor iterator = collectionJoin.find(queryDoc).iterator();

        List<Customer> customerList = new ArrayList<>();
        while(iterator.hasNext()){
            Document doc = (Document) iterator.next();
            System.out.println(doc.toJson());
            Customer customer = new Gson().fromJson(doc.toJson(), Customer.class);
            customerList.add(customer);
        }

        return customerList;
    }*/

}
