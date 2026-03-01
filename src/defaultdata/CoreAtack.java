package defaultdata;

public enum CoreAtack implements DefaultEnum<String>{
	ATACK(0, "攻撃倍率"),
	RANGE(1, "射程倍率"),
	SPEED(2, "攻撃速度倍率"),
	TARGET(3, "攻撃対象倍率");
	
	private final int id;
	private final String label;
	
	CoreAtack(int id, String label) {
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