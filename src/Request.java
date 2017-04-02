
public class Request {
	
	public static final String CAN_I_HAVE_YOUR_FORK = "Can I have your fork, please?";
	public static final String YES_FORK = "Fine, take it.";
	public static final String AN_I_HAVE_YOUR_CUP = "Can I have your cup, please?";
	public static final String YES_CUP = "Fine, take it. drink it";
	
	private String message;
	private String ip;
	
	public Request(String message, String ip) {
		this.message = message;
		this.ip = ip;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String getIp() {
		return this.ip;
	}
}
