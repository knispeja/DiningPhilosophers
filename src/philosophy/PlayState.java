package philosophy;

public enum PlayState {
	INACTIVE("I'm not playing"),
	WANT_PLAY_LEFT("I want to play with the left side"),
	WANT_PLAY_RIGHT("I want to play with the right side"),
	PLAY_LEFT("Playing with the left side"),
	PLAY_RIGHT("Playing with the right side");
	
	
	private final String fieldString;
	private PlayState(String value) {
		this.fieldString = value;
	}
	
	@Override
	public String toString() {
		return this.fieldString;
	}
}
