package com.ecommerce.clusterservice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, Long> {
}
