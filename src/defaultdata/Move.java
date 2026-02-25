package defaultdata;

public enum Move implements StatusText{
	GROUND(0, "地上"),
	FLIGHT(1, "飛行"),
	ON_WATER(2, "水上"),
	NO_MOVE(3, "移動不可");
	
	private final int id;
	private final String text;
	
	Move(int id, String status) {
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