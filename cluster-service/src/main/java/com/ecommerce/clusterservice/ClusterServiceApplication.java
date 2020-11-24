package com.ecommerce.clusterservice;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class ClusterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClusterServiceApplication.class, args);
	}

}
