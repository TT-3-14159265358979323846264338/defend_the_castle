package defaultdata;

public enum Distance implements StatusText{
	NEAR(0, "近接"),
	FAR(1, "遠隔"),
	ALL(2, "遠近");
	
	private final int id;
	private final String text;
	
	Distance(int id, String status) {
		this.id = id;
		this.text = status;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}
}