package defaultdata;

public enum Atack implements DefaultEnum<String>{
	ATACK(0, "攻撃"),
	RANGE(1, "射程"),
	SPEED(2, "攻撃速度"),
	TARGET(3, "攻撃対象");
	
	private final int id;
	private final String text;
	
	Atack(int id, String label) {
		this.id = id;
		this.text = label;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return text;
	}
}