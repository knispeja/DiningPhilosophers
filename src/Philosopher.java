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
	private static boolean hungry;
	
	private static boolean haveLeftFork, haveRightFork, leftForkClean, rightForkClean;
	private static String state;
	
	
	public static void main(String[] args) {

		BlockingQueue<String> requests = new ArrayBlockingQueue<String>(NEIGHBORS);
		
		leftForkClean = false;
		rightForkClean = false;
		
		state = "thinking";
		
		hungry = false;
		
		// initialize haveLeftFork and haveRightFork with args
		
		
		
		
		
		//create new instances of Client and Server
		Runnable client = new Client();
		Runnable server = new Server(PORT, requests);

		//Create threads to run Client and Server as Threads
		Thread t1 = new Thread(client);
		Thread t2 = new Thread(server);

		//start the threads
		t1.start();
		t2.start();
		
		long time = System.currentTimeMillis();
		long deathThreshold = 100;
		int eatingThreshold =5;
		int eatingTurns ;
		
		while(true) {
			// ensure while loop runs every 1ms
			if (System.currentTimeMillis() - time >10){
				
				// check state
				if (state.equals("thinking")){
					
				}else if (state.equals("hungry")){
					
					
				}else if (state.equals("eating")){
					if (eatingTurns > eatingThreshold){
						state = "thinking";
						eatingTurns = 0;
					}else{
						eatingTurns++;
					}
				}else{
					System.err.println("something with the state is wrong");
				}
				
				if (!state.equals("eating")){
					// Handle requests
					String request;
					while((request = requests.poll()) != null) {
						// Answer request
					}
				}
				
						
				
//				
//				// Decide if hungry
//				// randomize random state
//				if (hungry){
//					if (System.currentTimeMillis() - hungryStartTime > deathThreshold){
//						System.err.println(" Philosopher Died !!! Really Bad");
//						System.exit(1);
//					}
//				}else if (Math.random() < 0.1){
//					hungry = true;
//					hungryStartTime = System.currentTimeMillis();
//				}
				
				
				
				time = System.currentTimeMillis();
				
			}
		}
	}
}
