
public enum ThirstState {
	THINKING("My thirst is quenched."),
	THIRSTY("I'm parched..."), 
	DRINKING("gudong gudong"),
	SLEEPING("~~ZZ~~ZZ~~***");
	
	private final String fieldString;
	private ThirstState(String value) {
		this.fieldString = value;
	}
	
	@Override
	public String toString() {
		return this.fieldString;
	}
}
