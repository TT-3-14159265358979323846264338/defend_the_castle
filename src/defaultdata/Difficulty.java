package defaultdata;

/**
 * ゲームの難易度調整。
 * 敵の攻撃・防御・回復に{@link #id}の倍率が適応される。
 */
public enum Difficulty {
	NORMAL(1.0, "normal"),
	HARD(2.0, "hard");
	
	private final double id;
	private final String label;

	Difficulty(double id, String label) {
		this.id = id;
		this.label = label;
	}

	public double getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}