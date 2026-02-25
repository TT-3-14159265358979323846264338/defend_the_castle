package defaultdata;

public enum Handle implements StatusText{
	ONE(0, "片手"),
	BOTH(1, "両手");
	
	private final int id;
	private final String text;
	
	Handle(int id, String status) {
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