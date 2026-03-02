package defaultdata;

public enum Move implements DefaultEnum<String>{
	GROUND(0, "地上"),
	FLIGHT(1, "飛行"),
	ON_WATER(2, "水上"),
	NO_MOVE(3, "移動不可");
	
	private final int id;
	private final String label;
	
	Move(int id, String label) {
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