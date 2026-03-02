package defaultdata;

public enum EnemyUnit implements DefaultEnum<String>{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	MOVE(4, "移動速度"),
	COST(5, "撃破コスト");
	
	private final int id;
	private final String label;
	
	EnemyUnit(int id, String label) {
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