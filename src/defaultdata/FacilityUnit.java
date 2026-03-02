package defaultdata;

public enum FacilityUnit implements DefaultEnum<String>{
	MAXHP(0, "最大HP"),
	HP(1, "HP"),
	DEFENSE(2, "防御"),
	HEAL(3, "回復"),
	BLOCK(4, "足止め数");
	
	private final int id;
	private final String label;
	
	FacilityUnit(int id, String label) {
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