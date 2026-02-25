package defaultdata;

public enum Element implements StatusText{
	SLASH(0, "斬撃"),
	PIERCE(1, "刺突"),
	STRIKE(2, "殴打"),
	IMPACT(3, "衝撃"),
	FLAME(4, "炎"),
	WATER(5, "水"),
	WIND(6, "風"),
	SOIL(7, "土"),
	THUNDER(8, "雷"),
	HOLY(9, "聖"),
	DARK(10, "闇"),
	SUPPORT(11, "支援");
	
	private final int id;
	private final String text;
	
	Element(int id, String status) {
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