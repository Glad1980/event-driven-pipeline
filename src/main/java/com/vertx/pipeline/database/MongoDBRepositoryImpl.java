/**
 * 
 */
package com.vertx.pipeline.database;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aalrbee
 *
 */
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoDBRepositoryImpl<T> implements MongoDBRepository<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBRepositoryImpl.class);
	
    private static final String DATABASE_NAME = "testDb";
    private static final String COLLECTION_NAME = "exampleCollection";
    
    private final MongoClient client;
    
    public MongoDBRepositoryImpl() {
        this.client = MongoClients.create("mongodb://localhost:27017");
    }
    
    @Override
    public MongoDatabase getDatabase() {
        return client.getDatabase(DATABASE_NAME);
    }
    
    @Override
    public MongoCollection<Document> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME);
    }
    
    @Override
    public void create(T entity) {
    	LOGGER.trace("Create Entity : {} " , entity);
        Document document = convertEntityToDocument(entity);
        getCollection().insertOne(document);
    }
    
    @Override
    public T read(Object id, T type) {
    	LOGGER.trace("Read Entity of type  : {} " , type.getClass().getSimpleName());
        Document document = getCollection().find(Filters.eq("_id", id)).first();
        return convertDocumentToEntity(document, type);
    }
    
    @Override
    public void update(Object id, T entity) {
    	LOGGER.trace("Update Entity : {} " , entity);
        Document updatedDocument = convertEntityToDocument(entity);
        getCollection().replaceOne(Filters.eq("_id", id), updatedDocument);
    }
    
    @Override
    public void delete(Object id) {
    	LOGGER.trace("Delete Entity id : {} " , id);
        getCollection().deleteOne(Filters.eq("_id", id));
    }
    
    // Helper methods to convert between entity and document representations
    private Document convertEntityToDocument(T entity) {
        // Convert your entity to a Document object
        // Implement this method based on your entity structure
    	
        return ((MongoEntity)entity).toDocument();
    }
    
    private T convertDocumentToEntity(Document document, T type) {
        // Convert the Document object to your entity
        // Implement this method based on your entity structure
    	 if (document != null) {
//    	        T entity = createEmptyEntity(type);
    	        ((MongoEntity) type).fromDocument(document);
    	        return type;
    	    }
    	    return null;
    }
    
 // Helper method to create an empty instance of the entity
//    @SuppressWarnings("unchecked")
//    private T createEmptyEntity(T type) {
//        try {
//            return (T) type.newInstance();
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException("Failed to create an instance of the entity.", e);
//        }
//    }
}
