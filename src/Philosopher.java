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
		public boolean exists;
		public boolean askedFor;
		
		public Fork(){
			this.clean = false;
			this.exists = false;
			this.askedFor = false;
		}
		
		public void cleanMyself(){
			clean = true;
		}
		
		public void getDirty(){
			clean = false;
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {

		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(NEIGHBORS);
		
		state = "thinking";
		
		hungry = false;
		
		// initialize haveLeftFork and haveRightFork with args
		Fork leftHand = new Fork();
		Fork rightHand = new Fork();
		leftHand.exists = true;
		rightHand.exists = true;
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
				if (state.equals("hungry")){
					if (!leftHand.exists && !leftHand.askedFor) {
						client.sendMessageToNeighbor(Philosopher.CAN_I_HAVE_YOUR_FORK, true);
						leftHand.askedFor = true;
					}
					if (!rightHand.exists && !rightHand.askedFor) {
						client.sendMessageToNeighbor(Philosopher.CAN_I_HAVE_YOUR_FORK, false);
						rightHand.askedFor = true;
					}
					
					if (leftHand.exists && rightHand.exists) {
						state = "eating";
					}
				}
				
				if (state.equals("eating")){
					if (eatingTurns > eatingThreshold){
						eatingTurns = 0;
						leftHand.getDirty();
						rightHand.getDirty();
						state = "thinking";
					}else{
						eatingTurns++;
					}
				} else {
					// Handle requests
					Request request;
					while((request = requests.poll()) != null) {
						if(request.ip.equals(ipLeft)) {
							if(request.message.equals(Philosopher.CAN_I_HAVE_YOUR_FORK)) {
								if(leftHand.clean) {
									requests.put(request);
								} else {
									client.sendMessageToNeighbor(Philosopher.YES, true);
									leftHand.exists = false;
								}
							} else if(request.message.equals(Philosopher.YES)) {
								leftHand.exists = true;
								leftHand.askedFor = false;
								leftHand.cleanMyself();
							}
						} else if (request.ip.equals(ipRight)) {
							if(request.message.equals(Philosopher.CAN_I_HAVE_YOUR_FORK)) {
								if(rightHand.clean) {
									requests.put(request);
								} else {
									client.sendMessageToNeighbor(Philosopher.YES, false);
									rightHand.exists = false;
								}
							} else if(request.message.equals(Philosopher.YES)) {
								rightHand.exists = true;
								rightHand.askedFor = false;
								rightHand.cleanMyself();
							}
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
