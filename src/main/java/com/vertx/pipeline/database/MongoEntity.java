/**
 * 
 */
package com.vertx.pipeline.database;

/**
 * @author aalrbee
 *
 */
import org.bson.Document;

public interface MongoEntity {
    Document toDocument();
    
    void fromDocument(Document document);
}
