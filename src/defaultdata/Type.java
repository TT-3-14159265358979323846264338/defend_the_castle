package defaultdata;

public enum Type implements DefaultEnum<String>{
	NORMAL(0, "一般"),
	FACILITY(1, "設備"),
	BOSS(2, "ボス");
	
	private final int id;
	private final String label;
	
	Type(int id, String label) {
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