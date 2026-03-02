package defaultdata;

public enum WeaponUnit implements DefaultEnum<String>{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	BLOCK(4, "足止め数"),
	COST(5, "配置コスト");
	
	private final int id;
	private final String label;
	
	WeaponUnit(int id, String label) {
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