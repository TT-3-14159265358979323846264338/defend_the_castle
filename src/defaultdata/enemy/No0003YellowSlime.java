package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.Move;
import defaultdata.Type;

public class No0003YellowSlime extends EnemyData{
	@Override
	public String getName() {
		return "イエロースライム";
	}

	@Override
	public String getExplanation() {
		return "ブルースライムよりも少し速度の速い敵。";
	}
	
	@Override
	public String getImageName() {
		return "image/enemy/No0003 yellow slime/yellow slime.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/enemy/No0003 yellow slime/yellow slime 0.png",
				"image/enemy/No0003 yellow slime/yellow slime 1.png",
				"image/enemy/No0003 yellow slime/yellow slime 2.png",
				"image/enemy/No0003 yellow slime/yellow slime 3.png",
				"image/enemy/No0003 yellow slime/yellow slime 4.png",
				"image/enemy/No0003 yellow slime/yellow slime 5.png");
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
		return Arrays.asList(50, 30, 750, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(500, 500, 10, 0, 125, 1);
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