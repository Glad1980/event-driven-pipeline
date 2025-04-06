/**
 * 
 */
package com.vertx.pipeline.database;

/**
 * @author aalrbee
 *
 */
public class TestMongo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoDBRepositoryImpl<MongoEntity> mi = new MongoDBRepositoryImpl<>();
		User user = new User("12", "Abed", 43);
		mi.create(user);
		
		
		User readUser = (User) mi.read("11", new User());
		System.out.println(readUser);
	}

}
