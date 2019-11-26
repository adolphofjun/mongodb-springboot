package com.example.demo.config.mongodb;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

@Configuration
public class MongoConf {

    @Autowired private MongoDbFactory mongoDbFactory;
    @Autowired private GridFSBucket gridFSBucket;

    @Bean
    public GridFSBucket getGridFSBucket(){
        MongoDatabase database = mongoDbFactory.getDb();
        return GridFSBuckets.create(database);
    }

}
