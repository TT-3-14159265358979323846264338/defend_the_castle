package defaultdata;

public enum EnemyUnit implements StatusText{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	MOVE(4, "移動速度"),
	COST(5, "撃破コスト");
	
	private final int id;
	private final String text;
	
	EnemyUnit(int id, String status) {
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