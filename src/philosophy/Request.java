package philosophy;

public class Request {
	
	public static final String CAN_I_HAVE_YOUR_FORK = "May I have your fork, please?";
	public static final String YES_FORK = "Fine, take it, you glutton.";
	public static final String CAN_I_HAVE_YOUR_CUP = "May I have the cup, please?";
	public static final String YES_CUP = "Fine, take it, you drunkard.";
	public static final String CAN_WE_PLAY = "Can we play a game together?";
	public static final String STOP_PLAY = "I'm out.";
	
	private String message;
	private int id;
	
	public Request(String message, int ip) {
		this.message = message;
		this.id = ip;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public int getId() {
		return this.id;
	}
}
