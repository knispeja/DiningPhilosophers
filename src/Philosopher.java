import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {

	private static final int PORT = 8080;
	private static final int NEIGHBORS = 2;
	
	public static void main(String[] args) {

		BlockingQueue<String> requests = new ArrayBlockingQueue<String>(NEIGHBORS);
		
		//create new instances of Client and Server
		Runnable client = new Client();
		Runnable server = new Server(PORT, requests);

		//Create threads to run Client and Server as Threads
		Thread t1 = new Thread(client);
		Thread t2 = new Thread(server);

		//start the threads
		t1.start();
		t2.start();
		
		while(true) {
			// Handle requests
			String request;
			while((request = requests.poll()) != null) {
				// Answer request
			}
			
			// Decide if hungry
		}
	}
}
