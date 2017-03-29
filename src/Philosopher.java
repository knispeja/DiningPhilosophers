import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {
	
	private static final String CAN_I_HAVE_YOUR_FORK = "Can I have your fork, please?";
	private static final String YES = "Fine, take it.";
	
	private static final int PORT = 8080;
	private static final int NEIGHBORS = 2;
	
	private static final long DELAY_BETWEEN_TURNS_MS = 10;
	
	private static final int TURNS_HUNGRY_UNTIL_DEATH = 100;
	private static final int TURNS_TAKEN_TO_EAT = 5;
	
	private static final float HUNGRY_PROBABILITY = 0.01f;
	
	private static enum State {
		THINKING,
		EATING,
		HUNGRY
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(NEIGHBORS);
		
		State state = State.THINKING;
		
		// TODO: initialize these variables using args or the GUI
		Fork leftHand = new Fork();
		Fork rightHand = new Fork();
		leftHand.exists = true;
		rightHand.exists = true;
		String ipLeft = "137.112.223.192";
		String ipRight = "137.112.226.203";
		
		// Create new instances of Client and Server
		Client client = new Client(ipLeft, PORT, ipRight, PORT);
		Server server = new Server(PORT, requests);

		// Create threads to run Client and Server as Threads
		Thread t1 = new Thread(client);
		Thread t2 = new Thread(server);

		// Start the threads
		t1.start();
		t2.start();
		
		long time = System.currentTimeMillis();
		int hungryTurns = 0;
		int eatingTurns = 0;
		
		while(true) {
			if (System.currentTimeMillis() - time > DELAY_BETWEEN_TURNS_MS){
				
				time = System.currentTimeMillis();
				
				if (state.equals(State.THINKING)) {
					if (Math.random() < HUNGRY_PROBABILITY){
						state = State.HUNGRY;
						hungryTurns = 0;
					}
				}

				if (state.equals(State.HUNGRY)) {
					
					if (hungryTurns++ == Philosopher.TURNS_HUNGRY_UNTIL_DEATH) {
						System.out.println("R.I.P. I'm a ghost");
					}
					
					if (!leftHand.exists && !leftHand.askedFor) {
						System.out.println("Asking my left neighbor for his fork...");
						client.sendMessageToNeighbor(Philosopher.CAN_I_HAVE_YOUR_FORK, true);
						leftHand.askedFor = true;
					}
					if (!rightHand.exists && !rightHand.askedFor) {
						System.out.println("Asking my right neighbor for his fork...");
						client.sendMessageToNeighbor(Philosopher.CAN_I_HAVE_YOUR_FORK, false);
						rightHand.askedFor = true;
					}
					
					if (leftHand.exists && rightHand.exists) {
						System.out.println("Beginning to eat!");
						state = State.EATING;
					}
				}
				
				if (state.equals(State.EATING)) {
					if (eatingTurns > Philosopher.TURNS_TAKEN_TO_EAT){
						System.out.println("Done eating. That was delicious!");
						eatingTurns = 0;
						leftHand.clean = false;
						rightHand.clean = false;;
						state = State.THINKING;
					}else{
						eatingTurns++;
					}
				} else {
					// Handle requests
					Request request;
					while((request = requests.poll()) != null) {
						if(request.ip.equals(ipLeft)) {
							System.out.println("Message received from left: " + request.message);
							if(request.message.equals(Philosopher.CAN_I_HAVE_YOUR_FORK)) {
								if(leftHand.clean) {
									requests.put(request);
								} else {
									System.out.println("Giving my left neighbor the fork...");
									client.sendMessageToNeighbor(Philosopher.YES, true);
									leftHand.exists = false;
								}
							} else if(request.message.equals(Philosopher.YES)) {
								leftHand.exists = true;
								leftHand.askedFor = false;
								leftHand.clean = true;
							}
						} else if (request.ip.equals(ipRight)) {
							System.out.println("Message received from right: " + request.message);
							if(request.message.equals(Philosopher.CAN_I_HAVE_YOUR_FORK)) {
								if(rightHand.clean) {
									requests.put(request);
								} else {
									System.out.println("Giving my right neighbor the fork...");
									client.sendMessageToNeighbor(Philosopher.YES, false);
									rightHand.exists = false;
								}
							} else if(request.message.equals(Philosopher.YES)) {
								rightHand.exists = true;
								rightHand.askedFor = false;
								rightHand.clean = true;;
							}
						} else {
							System.err.println("Request received from invalid source: " + request.ip);
						}
					}
				}
			}
		}
	}
}
