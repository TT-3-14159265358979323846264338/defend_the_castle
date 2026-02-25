package defaultdata;

public enum Type implements StatusText{
	NORMAL(0, "一般"),
	FACILITY(1, "設備"),
	BOSS(2, "ボス");
	
	private final int id;
	private final String text;
	
	Type(int id, String status) {
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