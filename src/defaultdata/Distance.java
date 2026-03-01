package defaultdata;

public enum Distance implements DefaultEnum<String>{
	NEAR(0, "近接"),
	FAR(1, "遠隔"),
	ALL(2, "遠近");
	
	private final int id;
	private final String label;
	
	Distance(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}