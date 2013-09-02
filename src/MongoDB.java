

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDB {

	
	public static void main(String[] args) throws IOException {
		try {
			MongoClient mongo = new MongoClient( "localhost" , 27017 );

			DB db = mongo.getDB("new_test");
			DBCollection table = db.getCollection("tcs");							
				
			String str = readFile("files/tcs.json", StandardCharsets.UTF_8);
			
			DBObject dbObject = (DBObject) JSON.parse(str);
			table.insert(dbObject);				
			
		} catch (UnknownHostException e) {
			System.err.println(e);
		}		
		
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

}
