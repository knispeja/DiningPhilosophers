package philosophy;

public enum SleepState {
	THINKING("My thirst is quenched."), 
	SLEEPING("I am asleep");

	private final String fieldString;

	private SleepState(String value) {
		this.fieldString = value;
	}

	@Override
	public String toString() {
		return this.fieldString;
	}

}
