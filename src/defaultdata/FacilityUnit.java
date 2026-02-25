package defaultdata;

public enum FacilityUnit implements StatusText{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	BLOCK(4, "足止め数");
	
	private final int id;
	private final String text;
	
	FacilityUnit(int id, String status) {
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