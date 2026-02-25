package defaultdata;

public enum WeaponUnit implements StatusText{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	BLOCK(4, "足止め数"),
	COST(5, "配置コスト");
	
	private final int id;
	private final String text;
	
	WeaponUnit(int id, String status) {
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