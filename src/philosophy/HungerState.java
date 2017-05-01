package philosophy;

public enum HungerState {
	
	THINKING("I'm full!"),
	HUNGRY("My tummy's rumbling..."),
	EATING("OM NOM NOM NOM");
	
	private final String fieldString;
	private HungerState(String value) {
		this.fieldString = value;
	}
	
	@Override
	public String toString() {
		return this.fieldString;
	}
}