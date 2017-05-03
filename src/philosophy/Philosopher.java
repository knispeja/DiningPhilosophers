package philosophy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.KeeperException;

import zookeeper.ZClient;
import zookeeper.ZServer;

/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {

	// Command line flags
	private static final String LEFT_PHILOSOPHER_ID = "-l";
	private static final String RIGHT_PHILOSOPHER_ID = "-r";
	private static final String THIS_PHILOSOPHER_ID = "-i";
	private static final String ZOOKEEPER = "-z";
	private static final String HAS_LEFT_FORK = "-hasleftfork";
	private static final String HAS_RIGHT_FORK = "-hasrightfork";
	private static final String HAS_CUP = "-hascup";
	private static final String NO_GUI = "-nogui";
	private static final String HELP = "-help";

	// Queue size, should be [# of possible requests]*[# of neighbors] = 2*2 = 4
	private static final int QUEUE_SIZE = 6;

	// Constants dictating the philosopher's behavior
	private static final long DELAY_BETWEEN_TURNS_MS = 200; // Millisecond delay
															// between turns
	private static final int TURNS_HUNGRY_UNTIL_DEATH = 100; // Turns the
																// philosopher
																// can be hungry
																// without dying
	private static final int TURNS_TAKEN_TO_EAT = 4; // Turns the philosopher
														// takes to eat
	private static final int TURNS_TAKEN_TO_SLEEP = 10;
	private static final float HUNGRY_PROBABILITY = 0.21f; // Probability the
															// philosopher will
															// become hungry on
															// a given turn

	// Publicly available variables and flags, TODO move main into its own class
	// to avoid these
	public static boolean hungerFlag = false;
	public static boolean thirstFlag = false;
	public static boolean satisfactionFlag = false;
	public static boolean randomMode = false;
	public static boolean sleepFlag = false;
	public static boolean wakenFlag = false;
	public static boolean playLeftFlag = false;
	public static boolean playRightFlag = false;
	public static boolean stopPlayFlag = false;
	
	public static Fork leftHand;
	public static Fork rightHand;
	public static HungerState hungerState;
	public static ThirstState thirstState;
	public static PlayState playState;
	
	// philosophers asks to their right, and pass cups back to left.
	public static boolean hasCup, hasAskedRight, beAskedByLeft;
	

	public static void main(String[] args) throws InterruptedException, KeeperException {
		
		// Initialize these variables using args if available
		leftHand = new Fork();
		rightHand = new Fork();

		int idLeft = -1;
		int idRight = -1;
		int thisID= -1;
		String zIP = "";
		
		// initialize cup status
		hasCup = false;
		hasAskedRight = false;
		beAskedByLeft = false;

		boolean noGUI = false;

		// Parse command line arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].toLowerCase();
			if (arg.equals(LEFT_PHILOSOPHER_ID)) {
				idLeft = Integer.parseInt(args[++i]);
			} else if (arg.equals(RIGHT_PHILOSOPHER_ID)) {
				idRight = Integer.parseInt(args[++i]);
			} else if (arg.equals(THIS_PHILOSOPHER_ID)) {
				thisID = Integer.parseInt(args[++i]);
			} else if (arg.equals(ZOOKEEPER)) {
				zIP = args[++i];
			} else if (arg.equals(HAS_LEFT_FORK)) {
				leftHand.exists = true;
			} else if (arg.equals(HAS_RIGHT_FORK)) {
				rightHand.exists = true;
			} else if (arg.equals(NO_GUI)) {
				noGUI = true;
			} else if (arg.equals(HAS_CUP)) {
				hasCup = true;
			} else if (arg.equals(HELP)) {
				System.out.println("Valid command line arguments (given in any order): ");
				System.out.println("\t-l [left_philosopher_id]");
				System.out.println("\t-r [right_philosopher_id]");
				System.out.println("\t-i [this_philosopher_id]");
				System.out.println("\t-hasLeftFork: philosopher starts with the left fork");
				System.out.println("\t-hasRightFork: philosopher starts with the right fork");
				System.out.println("\t-noGUI: the GUI will not open");
				return;
			} else {
				System.err.println("Unrecognized flag: " + arg);
			}
		}

		// Input validation
		if (idLeft == -1 || idRight == -1 || thisID == -1 || zIP.equals("")) {
			System.err.println("Missing Parameters. Please provide: -z [zookeeper ip] -i [this_id] -l [left_id] and -r [right_id].");
			return;
		}

		// Initialize some important values
		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(QUEUE_SIZE);
		hungerState = HungerState.THINKING;
		thirstState = ThirstState.THINKING;
		playState = PlayState.INACTIVE;

		// Open the GUI
		PhilosopherGui gui = new PhilosopherGui(noGUI);

		// Create new instances of Client and Server
		ZClient client = new ZClient(idLeft, idRight, thisID, zIP);
		ZServer server = new ZServer(requests, zIP, idLeft, idRight, thisID);

		// Create and run Client and Server threads
		//new Thread(client).start();
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

				if (sleepFlag) {
					if(thirstState != ThirstState.SLEEPING) {
						thirstState = ThirstState.SLEEPING;
						gui.updateThirstState();
					}
					sleepingTurns = 0;
				}
				
				if(thirstState.equals(ThirstState.DRINKING) && hungerState.equals(HungerState.EATING)){
					System.err.println("Drinking and Eating at the same time");
				}
				
				if (thirstState == ThirstState.SLEEPING) {
					// increment sleep timer
					sleepingTurns++;
					if (sleepingTurns > TURNS_TAKEN_TO_SLEEP || wakenFlag){
						wakenFlag = false;
						sleepFlag = false;
						thirstState = ThirstState.THINKING;
						if(beAskedByLeft){
							beAskedByLeft = false;
							client.sendMessageToNeighbor(Request.YES_CUP, true);
							hasCup = false;
						}
						gui.updateThirstState();
					}

				} else if(playState.equals(PlayState.PLAY_RIGHT) || playState.equals(PlayState.PLAY_LEFT)){
					if(!requests.isEmpty()){
						Request request = requests.poll();
						if(request.getMessage().equals(Request.STOP_PLAY)){
							if(playState.equals(PlayState.PLAY_RIGHT)){
								playState = PlayState.INACTIVE;
								gui.updatePlayState();
								stopPlayFlag = false;
							} else if (playState.equals(PlayState.PLAY_LEFT)){
								playState = PlayState.INACTIVE;
								gui.updatePlayState();
								stopPlayFlag = false;
							} else {
								System.err.println("Inconsistent print state");
							}
						} else {
							requests.put(request);
						}
					}
					
					if(stopPlayFlag){
						if(playState.equals(PlayState.PLAY_RIGHT)){
							playState = PlayState.INACTIVE;
							gui.updatePlayState();
							stopPlayFlag = false;
							client.sendMessageToNeighbor(Request.STOP_PLAY, false);
						} else if (playState.equals(PlayState.PLAY_LEFT)){
							playState = PlayState.INACTIVE;
							gui.updatePlayState();
							stopPlayFlag = false;
							client.sendMessageToNeighbor(Request.STOP_PLAY, true);
						} else {
							System.err.println("Inconsistent print state");
						}
					}
				}else {
					// add everything
					// if not sleep, this part handles cup handling

					// if not sleep, philosopher can DrinkThinking or thirsty, or
					// drinking

					// if sleep: ZZZ~~~
					
					//THIRST STATES ###########################################################################
					if (thirstState == ThirstState.THINKING){
						if ((randomMode && Math.random() < 0.02) || thirstFlag){
							thirstFlag = false;
							thirstState = ThirstState.THIRSTY;
							System.out.println("Philosopher has become thirsty");
							gui.updateThirstState();
							// send out message to RIGHT, asking for cups
							if (!hasCup && !hasAskedRight){
								client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_CUP, false);
								hasAskedRight = true;
							}
						}
					}else if (thirstState == ThirstState.THIRSTY){
						if (!leftHand.exists && ! rightHand.exists){
							if (hasCup && hungerState.equals(HungerState.THINKING) && playState.equals(PlayState.INACTIVE)) {
								thirstState = ThirstState.DRINKING;
								System.out.println("Philosopher has started drinking");
								gui.updateThirstState();
							}
						}
						
					}else if (thirstState == ThirstState.DRINKING){
						drinkingTurns++;
						if(drinkingTurns > drinkingTurnThreshold || satisfactionFlag){
							satisfactionFlag = false;
							thirstState = ThirstState.SLEEPING;
							System.out.println("Philosopher has fallen asleep");
							sleepingTurns = 0;
							gui.updateThirstState();
						}
						
					}else{
						System.err.println("drinking state messed up");
						System.exit(1);
					}
					
					

					// Hunger States ############################################################################################################

					if (hungerState.equals(HungerState.THINKING)) {
						if (!thirstState.equals(ThirstState.DRINKING) && playState.equals(PlayState.INACTIVE) 
								&& ((randomMode && Math.random() < HUNGRY_PROBABILITY) || hungerFlag)) {
							hungerState = HungerState.HUNGRY;
							hungryTurns = 0;
							hungerFlag = false;
							gui.updateHungerState();
						}
					}

					if (hungerState.equals(HungerState.EATING)) {
						if (eatingTurns > Philosopher.TURNS_TAKEN_TO_EAT || satisfactionFlag) {
							System.out.println("Finished Eating");
							eatingTurns = 0;
							leftHand.clean = false;
							rightHand.clean = false;
							hungerState = HungerState.THINKING;
							satisfactionFlag = false;
							gui.updateHungerState();
						} else {
							eatingTurns++;
						}
					} else {
						
						
						
						//Add play state stuff
						
						if(hungerState.equals(HungerState.THINKING) && thirstState.equals(ThirstState.THINKING) && playState.equals(PlayState.INACTIVE)){
							if(playLeftFlag && !rightHand.exists){
								playState = PlayState.WANT_PLAY_LEFT;
								client.sendMessageToNeighbor(Request.CAN_WE_PLAY, true);
								playLeftFlag = false;
								gui.updatePlayState();
							} else if (playRightFlag && !leftHand.exists){
								playState = PlayState.WANT_PLAY_RIGHT;
								client.sendMessageToNeighbor(Request.CAN_WE_PLAY, false);
								playRightFlag = false;
								gui.updatePlayState();
							}
						}
						
						// Handle requests
						Request request;
						while ((request = requests.poll()) != null) {
							
							//Left side #############################################################################################################
							if (request.getId() == idLeft) {
								if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_FORK)) {
									if (leftHand.clean) {
										//System.out.println("Ignoring for now: ");
										requests.put(request);
										break;
									} else {
										//System.out.println("Giving my left neighbor the fork, because: ");
										client.sendMessageToNeighbor(Request.YES_FORK, true);
										leftHand.exists = false;
										gui.updateForks();
									}
								} else if (request.getMessage().equals(Request.YES_FORK)) {
									leftHand.exists = true;
									leftHand.askedFor = false;
									leftHand.clean = true;
								} else if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_CUP)){
									if(thirstState.equals(ThirstState.THINKING)){
										if(hasCup){
											client.sendMessageToNeighbor(Request.YES_CUP, true);
											hasCup = false;
										} else if(!hasAskedRight){
											client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_CUP, false);
											hasAskedRight = true;
											beAskedByLeft = true;
										} // else: hasaskedright = true, so do nothing. 
										
										//Drinking or thirsty
									} else {
										//System.out.println("Ignoring cup request for now: ");
										requests.put(request);
										break;
									}
								}else if (request.getMessage().equals(Request.CAN_WE_PLAY)){
									if(playState.equals(PlayState.WANT_PLAY_LEFT)){
										playState = PlayState.PLAY_LEFT;
										gui.updatePlayState();

									} else if (playState.equals(PlayState.WANT_PLAY_RIGHT)){
										//Ignoring for now
										requests.put(request);
									} else if (playState.equals(PlayState.INACTIVE)){
										if(!rightHand.exists && thirstState.equals(ThirstState.THINKING) && hungerState.equals(HungerState.THINKING)){
											client.sendMessageToNeighbor(Request.CAN_WE_PLAY, true);
											playState = PlayState.PLAY_LEFT;
											gui.updatePlayState();
										} else {
											//Not Ready to Play, Ignoring for now. 
											requests.put(request);
										}
									}
								}else if (request.getMessage().equals(Request.STOP_PLAY)){
									if(playState.equals(PlayState.PLAY_LEFT)){
										playState = PlayState.INACTIVE;
										gui.updatePlayState();
									}
								}else if (request.getMessage().equals(Request.YES_CUP)){
									System.err.println("Recieved cup request from left");
								} else {
									System.err.println("Reieved unexpected response");
								}
								
								//System.out.println("Message found in queue from left: '" + request.getMessage() + "'");
							
								
								//Right side #############################################################################################################
							} else if (request.getId() == idRight) {
								if (request.getMessage().equals(Request.CAN_I_HAVE_YOUR_FORK)) {
									if (rightHand.clean) {
										//System.out.println("Ignoring for now: ");
										requests.put(request);
										break;
									} else {
										//System.out.println("Giving my right neighbor the fork, because: ");
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
									if(thirstState.equals(ThirstState.THIRSTY)){
										hasCup = true;
									} else if(thirstState.equals(ThirstState.THINKING)){
										if(beAskedByLeft){
											client.sendMessageToNeighbor(Request.YES_CUP, true);
											hasCup = false;
										} else {
											System.err.println("Why do I have this cup? I've been drinking too much...");
										}
									} else {
										System.err.println("Recieved duplicate cup");
									}
								}else if (request.getMessage().equals(Request.CAN_WE_PLAY)){
									if(playState.equals(PlayState.WANT_PLAY_RIGHT)){
										playState = PlayState.PLAY_RIGHT;
										gui.updatePlayState();
									} else if (playState.equals(PlayState.WANT_PLAY_LEFT)){
										//Ignoring for now
										requests.put(request);
									} else if (playState.equals(PlayState.INACTIVE)){
										if(!leftHand.exists && thirstState.equals(ThirstState.THINKING) && hungerState.equals(HungerState.THINKING)){
											client.sendMessageToNeighbor(Request.CAN_WE_PLAY, false);
											playState = PlayState.PLAY_RIGHT;
											gui.updatePlayState();
										} else {
											//Not Ready to Play, Ignoring for now. 
											requests.put(request);
										}
									}
								}else if (request.getMessage().equals(Request.STOP_PLAY)){
									if(playState.equals(PlayState.PLAY_RIGHT)){
										playState = PlayState.INACTIVE;
										gui.updatePlayState();
									} 
								
								
								} else {
									System.err.println("Reieved unexpected response");
								}
								//System.out.println("Message found in queue from right: '" + request.getMessage() + "'");
							} else {
								System.err.println("Request received from invalid source: " + request.getId());
							}
							gui.update();
						}
					}

					if (hungerState.equals(HungerState.HUNGRY)) {

						if (hungryTurns++ == Philosopher.TURNS_HUNGRY_UNTIL_DEATH) {
							System.out.println("This philosopher has died of starvation.");
						}

						if (!leftHand.exists && !leftHand.askedFor) {
							//System.out.println("Asking my left neighbor for his fork...");
							client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_FORK, true);
							leftHand.askedFor = true;
						}
						if (!rightHand.exists && !rightHand.askedFor) {
							//System.out.println("Asking my right neighbor for his fork...");
							client.sendMessageToNeighbor(Request.CAN_I_HAVE_YOUR_FORK, false);
							rightHand.askedFor = true;
						}

						if (leftHand.exists && rightHand.exists) {
							System.out.println("Beginning to eat!");
							hungerState = HungerState.EATING;
							gui.updateHungerState();
						}
					}
				}
			}

			
		}
	}
}
