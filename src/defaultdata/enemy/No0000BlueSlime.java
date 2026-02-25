package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.Move;
import defaultdata.Type;

public class No0000BlueSlime extends EnemyData{
	@Override
	public String getName() {
		return "ブルースライム";
	}

	@Override
	public String getExplanation() {
		return "最も弱い敵。特出すべき能力はない。";
	}
	
	@Override
	public String getImageName() {
		return "image/enemy/No0000 blue slime/blue slime.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/enemy/No0000 blue slime/blue slime 0.png",
				"image/enemy/No0000 blue slime/blue slime 1.png",
				"image/enemy/No0000 blue slime/blue slime 2.png",
				"image/enemy/No0000 blue slime/blue slime 3.png",
				"image/enemy/No0000 blue slime/blue slime 4.png",
				"image/enemy/No0000 blue slime/blue slime 5.png");
	}

	@Override
	public String getBulletImageName() {
		return null;
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList();
	}

	@Override
	public Move getMove() {
		return Move.GROUND;
	}

	@Override
	public Type getType() {
		return Type.NORMAL;
	}

	@Override
	public List<Element> getElement() {
		return Arrays.asList(Element.STRIKE);
	}

	@Override
	public int getAtackPattern() {
		return AtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(50, 30, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(500, 500, 10, 0, 100, 1);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}