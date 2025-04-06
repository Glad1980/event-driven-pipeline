/**
 * 
 */
package com.vertx.pipeline.database;

/**
 * @author aalrbee
 *
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface MongoDBRepository<T> {
    MongoDatabase getDatabase();
    
    MongoCollection<Document> getCollection();
    
    void create(T entity);
    
    T read(Object id, T type);
    
    void update(Object id, T entity);
    
    void delete(Object id);
}
