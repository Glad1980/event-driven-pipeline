/**
 * 
 */
package com.vertx.pipeline.database;

/**
 * @author aalrbee
 *
 */
import org.bson.Document;

public class User implements MongoEntity {
    private String id;
    private String name;
    private int age;
    
   public User() {
   }
    
    public User(String id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	@Override
    public Document toDocument() {
        Document document = new Document();
        document.append("_id", id)
                .append("name", name)
                .append("age", age);
        return document;
    }
    
    @Override
    public void fromDocument(Document document) {
        id = document.getString("_id");
        name = document.getString("name");
        age = document.getInteger("age");
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}
