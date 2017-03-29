import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {

	public static final String CAN_I_HAVE_YOUR_FORK = "canIhaveyourfork";
	public static final String YES = "yes";
	
	private static final int PORT = 8080;
	private static final int NEIGHBORS = 2;
	private static boolean hungry;
	
	private static boolean haveLeftFork, haveRightFork, leftForkClean, rightForkClean;
	private static String state;
	
	public static class Fork{
		public boolean clean;
		public Fork(){
			clean = false;
		}
		
		public void cleanMyself(){
			clean = true;
		}
		
		public void getDirty(){
			clean = false;
		}
	}
	
	
	public static void main(String[] args) {

		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(NEIGHBORS);
		
		leftForkClean = false;
		rightForkClean = false;
		
		state = "thinking";
		
		hungry = false;
		
		// initialize haveLeftFork and haveRightFork with args
		
		String ipLeft = "127.0.0.1";
		int portLeft = 8080;
		String ipRight = "127.0.0.1";
		int portRight = 8080;
		
		//create new instances of Client and Server
		Client client = new Client(ipLeft, portLeft, ipRight, portRight);
		Server server = new Server(PORT, requests);

		//Create threads to run Client and Server as Threads
		Thread t1 = new Thread(client);
		Thread t2 = new Thread(server);

		//start the threads
		t1.start();
		t2.start();
		
		long time = System.currentTimeMillis();
		long deathThreshold = 100;
		int eatingThreshold =5;
		int eatingTurns = 0;
		
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
					Request request;
					while((request = requests.poll()) != null) {
						if(request.ip.equals(ipLeft)) {
							System.out.println("Request from left: " + request.message);
						} else if (request.ip.equals(ipRight)) {
							System.out.println("Request from right: " + request.message);
						} else {
							System.err.println("Request received from invalid source: " + request.ip);
						}
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
