package philosophy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.KeeperException;

import zookeeper.ZKBool;

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
	private static final String IS_LEADER = "-leader";
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
	
	
	public static Fork leftFork;
	public static Fork rightFork;
	
	public static HungerState hungerState;
	public static ThirstState thirstState;
	public static PlayState playState;
	public static SleepState sleepState;
	
	// philosophers asks to their right, and pass cups back to left.
	public static boolean hasCup, hasAskedRight, beAskedByLeft;
	

	public static void main(String[] args) throws InterruptedException, KeeperException {
		
		// Initialize these variables using args if available
		leftFork = new Fork();
		rightFork = new Fork();

		int idLeft = -1;
		int idRight = -1;
		int thisID= -1;
		String zIP = "";
		
		// initialize cup status
		hasCup = false;
		hasAskedRight = false;
		beAskedByLeft = false;

		boolean noGUI = false;

		boolean leader = false;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
		
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
			} else if (arg.equals(IS_LEADER)) {
				leader = true;
			} else if (arg.equals(HELP)) {
				System.out.println("Valid command line arguments (given in any order): ");
				System.out.println("\t-l [left_philosopher_id]");
				System.out.println("\t-r [right_philosopher_id]");
				System.out.println("\t-i [this_philosopher_id]");
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

		String lStr = Integer.toString(idLeft);
		String tStr = Integer.toString(thisID);
		String rStr = Integer.toString(idRight);
		
		//Create ZK Nodes
		ZKBool zkLeftFork = new ZKBool(zIP, "F"+lStr+"-"+tStr, true);
		ZKBool zkRightFork = new ZKBool(zIP, "F"+tStr+"-"+rStr, true);
		
		ZKBool zkPlayOutLeft = new ZKBool(zIP, "P"+tStr+"-"+lStr, false);
		ZKBool zkPlayInLeft = new ZKBool(zIP, "P"+lStr+"-"+tStr, false);		
		ZKBool zkPlayOutRight = new ZKBool(zIP, "P"+tStr+"-"+rStr, false);
		ZKBool zkPlayInRight = new ZKBool(zIP, "P"+rStr+"-"+tStr, false);	
		ZKBool zkTurn = new ZKBool(zIP, "T"+tStr, false);	
		if(leader){
			zkTurn.set(true);		
		} 
		ZKBool zkLeftTurn = new ZKBool(zIP, "T"+lStr, false);
		
		//ZKBool zkMyCup = new ZKBool(zIP, "C"+tStr);
		ZKBool zkGlobalCup = new ZKBool(zIP, "C-GLOBAL", true);
		
		// Initialize some important values
		hungerState = HungerState.THINKING;
		thirstState = ThirstState.THINKING;
		playState = PlayState.INACTIVE;
		sleepState = SleepState.THINKING;

		// Open the GUI
		PhilosopherGui gui = new PhilosopherGui(noGUI);


		// Loop for the duration of the program...
		int hungryTurns = 0;
		int eatingTurns = 0;
		int drinkingTurns = 0;
		int drinkingTurnThreshold = 4;
	
		
		while(true){
			Thread.sleep(DELAY_BETWEEN_TURNS_MS);
			
			if(zkTurn.get()){
				
				//Sleeping
				if(amNeutral()){
					if(sleepFlag){
						sleepState = SleepState.SLEEPING;
						sleepFlag = false;
					}
				}
				
				if(sleepState.equals(SleepState.SLEEPING)){
					if(wakenFlag){
						sleepState = SleepState.THINKING;
						wakenFlag = false;
					}
				}
				//Playing
				if(amNeutral()){
					if(playState.equals(PlayState.INACTIVE)){
						if(playLeftFlag){
							playLeftFlag = false;
							zkPlayOutLeft.set(true);
							playState = PlayState.WANT_PLAY_LEFT;
						}
						else if(playRightFlag){
							playRightFlag = false;
							zkPlayOutRight.set(true);
							playState = PlayState.WANT_PLAY_RIGHT;
						}
					}
					
					if(zkPlayInLeft.get()){
						if(playState.equals(PlayState.WANT_PLAY_LEFT) || playState.equals(PlayState.INACTIVE)){
							playState = PlayState.PLAY_LEFT;
							zkPlayOutLeft.set(true);
						}
					}
					if(zkPlayInRight.get()){
						if(playState.equals(PlayState.WANT_PLAY_RIGHT) || playState.equals(PlayState.INACTIVE)){
							playState = PlayState.PLAY_RIGHT;
							zkPlayOutRight.set(true);
						}
					}
					
				} 
				//Not Neutral
				if(playState.equals(PlayState.PLAY_LEFT) && !zkPlayInLeft.get()){
					zkPlayOutLeft.set(false);
					playState = PlayState.INACTIVE;
				} 
				if(playState.equals(PlayState.PLAY_RIGHT) && !zkPlayInRight.get()){
					zkPlayOutRight.set(false);
					playState = PlayState.INACTIVE;
				}
				
				
				if(playState.equals(PlayState.PLAY_LEFT) || playState.equals(PlayState.PLAY_RIGHT)){
					if(leftFork.exists){
						leftFork.exists = false;
						zkLeftFork.set(true);
					}
					
					if(rightFork.exists){
						rightFork.exists = false;
						zkRightFork.set(true);
					}
					if(hasCup){
						hasCup = false;
						zkGlobalCup.set(true);
					}
					
					
					if(stopPlayFlag){
						stopPlayFlag = false;
						if(playState.equals(PlayState.PLAY_LEFT)){
							zkPlayOutLeft.set(false);
						}
						if(playState.equals(PlayState.PLAY_RIGHT)){
							zkPlayOutRight.set(false);
						}
						playState = PlayState.INACTIVE;
						
					}
				}
				
				
				//Drinking
				if(amNeutral()){
					if(thirstState.equals(ThirstState.THINKING)){
						if(thirstFlag){
							thirstFlag = false;
							thirstState = ThirstState.THIRSTY;
						}
					}
					
					if(thirstState.equals(ThirstState.THIRSTY)){
						if(zkGlobalCup.get()){
							zkGlobalCup.set(false);
							if(hasCup){
								System.err.println("cup error");
							}
							hasCup = true;
						}
						
						if(hasCup){
							thirstState = ThirstState.DRINKING;
						}
					}
					
				}
				if(thirstState.equals(ThirstState.DRINKING)){
					drinkingTurns++;
					if(drinkingTurns > drinkingTurnThreshold){
						drinkingTurns = 0;
						thirstState = ThirstState.THINKING;
						if(hasCup)
							zkGlobalCup.set(true);
						else
							System.err.println("Cup error");
						hasCup = false;
						
						sleepState = SleepState.SLEEPING;
						if(leftFork.exists){
							zkLeftFork.set(true);
						}
						if(rightFork.exists){
							zkRightFork.set(true);
						}
						leftFork.exists = false;
						rightFork.exists = false;
					}
				}
				
				//Eating
				if(amNeutral()){
					if(hungerState.equals(HungerState.THINKING)){
						if(hungerFlag){
							hungerFlag = false;
							hungerState = HungerState.HUNGRY;
							hungryTurns = 0;
						}
					}
					
					if(hungerState.equals(HungerState.HUNGRY)){
						hungryTurns++;
						if(hungryTurns > TURNS_HUNGRY_UNTIL_DEATH){
							//System.out.println("RIP. Death.");
						}
						if(zkLeftFork.get() && zkRightFork.get()){
							zkLeftFork.set(false);
							zkRightFork.set(false);
							leftFork.exists = true;
							rightFork.exists = true;
							hungerState = HungerState.EATING;
						}
					}
				}
				
				if(hungerState.equals(HungerState.EATING)){
					eatingTurns++;
					if(eatingTurns > TURNS_TAKEN_TO_EAT || satisfactionFlag){
						eatingTurns = 0;
						if(satisfactionFlag)
							satisfactionFlag = false;
						hungerState = hungerState.THINKING;
						
						if(leftFork.exists)
							zkLeftFork.set(true);
						else
							System.err.println("Fork State Error");
						if(rightFork.exists)
							zkRightFork.set(true);
						else
							System.err.println("Fork State Error");
						leftFork.exists = false;
						rightFork.exists = false;
					}
					
				}
				//Turn Logic
				zkTurn.set(false);
				zkLeftTurn.set(true);
				
				//GUI update
				gui.update();
			}
			
		}
		
	
	}
	
	static boolean amNeutral(){
		int exCount = 0;
		if(Philosopher.hungerState.equals(HungerState.EATING))
			exCount++;
		if(Philosopher.sleepState.equals(SleepState.SLEEPING))
			exCount++;
		if(Philosopher.thirstState.equals(ThirstState.DRINKING))
			exCount++;
		if(Philosopher.playState.equals(PlayState.PLAY_LEFT) || Philosopher.playState.equals(PlayState.PLAY_RIGHT))
			exCount++;
		if(exCount == 0)
			return true;
		
		if(exCount == 1)
			return false;
		
		if(exCount > 1)
			System.err.println("Invalid State");
		
		return false;
	}
}
