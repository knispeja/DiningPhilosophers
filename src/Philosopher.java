import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {

	// Command line flags
	private static final String LEFT_PHILOSOPHER_IP = "-l";
	private static final String RIGHT_PHILOSOPHER_IP = "-r";
	private static final String HAS_LEFT_FORK = "-hasleftfork";
	private static final String HAS_RIGHT_FORK = "-hasrightfork";
	private static final String NO_GUI = "-nogui";
	private static final String HELP = "-help";

	// Connectivity
	private static final int PORT = 8080;

	// Queue size, should be [# of possible requests]*[# of neighbors] = 2*2 = 4
	private static final int QUEUE_SIZE = 6;

	// Constants dictating the philosopher's behavior
	private static final long DELAY_BETWEEN_TURNS_MS = 100; // Millisecond delay
															// between turns
	private static final int TURNS_HUNGRY_UNTIL_DEATH = 100; // Turns the
																// philosopher
																// can be hungry
																// without dying
	private static final int TURNS_TAKEN_TO_EAT = 4; // Turns the philosopher
														// takes to eat
	private static final float HUNGRY_PROBABILITY = 0.21f; // Probability the
															// philosopher will
															// become hungry on
															// a given turn

	// Publicly available variables and flags, TODO move main into its own class
	// to avoid these
	public static boolean hungerFlag = false;
	public static boolean satisfactionFlag = false;

	public static Fork leftHand;
	public static Fork rightHand;
	public static String state;
	public static String drinkingState;
	
	// philosophers asks to their right, and pass cups back to left.
	public static boolean hasCup, hasAskedRight, beAskedByLeft;
	

	public static void main(String[] args) throws InterruptedException {

		// Initialize these variables using args if available
		leftHand = new Fork();
		rightHand = new Fork();

		String ipLeft = "";
		String ipRight = "";
		
		// initialize cup status
		hasCup = false;
		hasAskedRight = false;
		beAskedByLeft = false;

		boolean noGUI = false;

		// Parse command line arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].toLowerCase();
			if (arg.equals(LEFT_PHILOSOPHER_IP)) {
				ipLeft = args[++i];
			} else if (arg.equals(RIGHT_PHILOSOPHER_IP)) {
				ipRight = args[++i];
			} else if (arg.equals(HAS_LEFT_FORK)) {
				leftHand.exists = true;
			} else if (arg.equals(HAS_RIGHT_FORK)) {
				rightHand.exists = true;
			} else if (arg.equals(NO_GUI)) {
				noGUI = true;
			} else if (arg.equals(HELP)) {
				System.out.println("Valid command line arguments (given in any order): ");
				System.out.println("\t-l [left_philosopher_ip]");
				System.out.println("\t-r [right_philosopher_ip]");
				System.out.println("\t-hasLeftFork: philosopher starts with the left fork");
				System.out.println("\t-hasRightFork: philosopher starts with the right fork");
				System.out.println("\t-noGUI: the GUI will not open");
				return;
			} else {
				System.err.println("Unrecognized flag: " + arg);
			}
		}

		// Input validation
		if (ipLeft.isEmpty() || ipRight.isEmpty()) {
			System.err.println("Please provide IPs for both the left and right neighbors via -l [ip] and -r [ip].");
			return;
		}

		// Initialize some important values
		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(QUEUE_SIZE);
		state = State.THINKING;
		drinkingState = DrinkingState.THINKING;

		// Open the GUI
		PhilosopherGui gui = new PhilosopherGui(noGUI);

		// Create new instances of Client and Server
		Client client = new Client(ipLeft, PORT, ipRight, PORT);
		Server server = new Server(PORT, requests);

		// Create and run Client and Server threads
		new Thread(client).start();
		new Thread(server).start();

		// Loop for the duration of the program...
		long time = System.currentTimeMillis();
		int hungryTurns = 0;
		int eatingTurns = 0;
		int drinkingTurns = 0;
		int sleepingTurns = 0;
		int drinkingTurnThreshold = 4;
		while (true) {

			if (System.currentTimeMillis() - time > DELAY_BETWEEN_TURNS_MS) {

				time = System.currentTimeMillis();

				if(drinkingState.equals(DrinkingState.DRINKING) && state.equals(State.EATING)){
					System.err.println("Drinking and Eating at the same time");
				}
				
				if (drinkingState == DrinkingState.SLEEPING) {
					// increment sleep timer
					sleepingTurns ++;
					if (sleepingTurns >10){
						drinkingState = DrinkingState.THINKING;
						if(beAskedByLeft){
							beAskedByLeft = false;
							client.sendMessageToNeighbor(Request.YES_CUP, true);
						}
						gui.updateGUI();
					}

				} else {
					// add everything
					// if not sleep, this part handles cup handling

					// if not sleep, philosopher can DrinkThinking or thirsty, or
					// drinking

					// if sleep: ZZZ~~~
					
					
					if (drinkingState == DrinkingState.THINKING){
						if (Math.random() < 0.02){
							drinkingState = DrinkingState.THIRSTY;
							gui.updateGUI();
							// send out message to RIGHT, asking for cups
							if (!hasCup && !hasAskedRight){
								client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_CUP, false);
								hasAskedRight = true;
							}
						}
					}else if (drinkingState == DrinkingState.THIRSTY){
						if (!leftHand.exists && ! rightHand.exists){
							if (hasCup && state.equals(State.THINKING)) {
								drinkingState = DrinkingState.DRINKING;
								gui.updateGUI();
							}
						}
						
					}else if (drinkingState == DrinkingState.DRINKING){
						drinkingTurns ++;
						if(drinkingTurns > drinkingTurnThreshold){
							drinkingState = DrinkingState.SLEEPING;
							sleepingTurns = 0;
						}
						
					}else{
						System.err.println("drinking state messed up");
						System.exit(1);
					}
					
					

					// beyond this point, is our lab1-version code

					if (state.equals(State.THINKING)) {
						if (!drinkingState.equals(DrinkingState.DRINKING) 
								&& (Math.random() < HUNGRY_PROBABILITY || hungerFlag)) {
							state = State.HUNGRY;
							gui.updateGUI();
							hungryTurns = 0;
							hungerFlag = false;
							gui.updateGUI();
						}
					}

					if (state.equals(State.EATING)) {
						if (eatingTurns > Philosopher.TURNS_TAKEN_TO_EAT || satisfactionFlag) {
							System.out.println("Done eating. That was delicious!");
							eatingTurns = 0;
							leftHand.clean = false;
							rightHand.clean = false;
							state = State.THINKING;
							satisfactionFlag = false;
							gui.updateGUI();
						} else {
							eatingTurns++;
						}
					} else {
						// Handle requests
						Request request;
						while ((request = requests.poll()) != null) {
							
							if (request.getIp().equals(ipLeft)) {
								if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_FORK)) {
									if (leftHand.clean) {
										System.out.println("Ignoring for now: ");
										requests.put(request);
										break;
									} else {
										System.out.println("Giving my left neighbor the fork, because: ");
										client.sendMessageToNeighbor(Request.YES_FORK, true);
										leftHand.exists = false;
									}
								} else if (request.getMessage().equals(Request.YES_FORK)) {
									leftHand.exists = true;
									leftHand.askedFor = false;
									leftHand.clean = true;
								} else if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_CUP)){
									if(drinkingState.equals(DrinkingState.THINKING)){
										if(hasCup){
											client.sendMessageToNeighbor(Request.YES_CUP, true);
										} else if(!hasAskedRight){
											client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_CUP, false);
											hasAskedRight = true;
											beAskedByLeft = true;
										} // else: hasaskedright = true, so do nothing. 
										
										//Drinking or thirsty
									} else {
										System.out.println("Ignoring cup request for now: ");
										requests.put(request);
										break;
									}
								} else if (request.getMessage().equals(Request.YES_CUP)){
									System.err.println("Recieved cup request from left");
								} else {
									System.err.println("Reieved unexpected response");
								}
								
								System.out.println("Message found in queue from left: '" + request.getMessage() + "'");
							
							} else if (request.getIp().equals(ipRight)) {
								if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_FORK)) {
									if (rightHand.clean) {
										System.out.println("Ignoring for now: ");
										requests.put(request);
										break;
									} else {
										System.out.println("Giving my right neighbor the fork, because: ");
										client.sendMessageToNeighbor(Request.YES_FORK, false);
										rightHand.exists = false;
									}
								} else if (request.getMessage().equals(Request.YES_FORK)) {
									rightHand.exists = true;
									rightHand.askedFor = false;
									rightHand.clean = true;
								} else if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_CUP)){
									System.err.println("Recieved cup confirmation from right");
								} else if (request.getMessage().equals(Request.YES_CUP)){
									hasAskedRight = false;
									if(drinkingState.equals(DrinkingState.THIRSTY)){
										hasCup = true;
									} else if(drinkingState.equals(DrinkingState.THINKING)){
										if(beAskedByLeft){
											client.sendMessageToNeighbor(Request.YES_CUP, true);
										} else {
											System.err.println("Why do I have this cup? I've been drinking too much...");
										}
									} else {
										System.err.println("Recieved duplicate cup");
									}
								} else {
									System.err.println("Reieved unexpected response");
								}
								System.out.println("Message found in queue from right: '" + request.getMessage() + "'");
							} else {
								System.err.println("Request received from invalid source: " + request.getIp());
							}
							gui.updateGUI();
						}
					}

					if (state.equals(State.HUNGRY)) {

						if (hungryTurns++ == Philosopher.TURNS_HUNGRY_UNTIL_DEATH) {
							System.out.println("R.I.P. I'm a ghost");
						}

						if (!leftHand.exists && !leftHand.askedFor) {
							System.out.println("Asking my left neighbor for his fork...");
							client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_FORK, true);
							leftHand.askedFor = true;
						}
						if (!rightHand.exists && !rightHand.askedFor) {
							System.out.println("Asking my right neighbor for his fork...");
							client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_FORK, false);
							rightHand.askedFor = true;
						}

						if (leftHand.exists && rightHand.exists) {
							System.out.println("Beginning to eat!");
							state = State.EATING;
							gui.updateGUI();
						}
					}
				}
			}

			

		}
	}
}
