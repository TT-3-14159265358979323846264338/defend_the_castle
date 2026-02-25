package defaultdata;

public enum CoreUnit implements StatusText{
	MAXHP(0, "最大HP倍率"),
	HP(1, "HP倍率"),
	DEFENSE(2, "防御倍率"),
	HEAL(3, "回復倍率"),
	BLOCK(4, "足止め数倍率"),
	COST(5, "配置コスト倍率");
	
	private final int id;
	private final String text;

	CoreUnit(int id, String status) {
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