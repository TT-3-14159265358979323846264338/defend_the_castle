package defaultdata;

public enum Handle implements DefaultEnum<String>{
	ONE(0, "片手"),
	BOTH(1, "両手");
	
	private final int id;
	private final String label;
	
	Handle(int id, String label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}
}