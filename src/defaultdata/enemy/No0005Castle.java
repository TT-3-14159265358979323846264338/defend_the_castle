package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.Move;
import defaultdata.Type;

public class No0005Castle extends EnemyData{
	@Override
	public String getName() {
		return "敵本丸";
	}

	@Override
	public String getExplanation() {
		return "敵の防衛拠点。高い耐久力を有し、周囲の敵に射撃を行う。";
	}
	
	@Override
	public String getImageName() {
		return "image/facility/castle.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/facility/castle.png");
	}

	@Override
	public String getBulletImageName() {
		return "image/weapon/No0001 bow/bow bullet.png";
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList("image/weapon/No0001 bow/bow hit 1.png",
				"image/weapon/No0001 bow/bow hit 2.png",
				"image/weapon/No0001 bow/bow hit 3.png");
	}

	@Override
	public Move getMove() {
		return Move.NO_MOVE;
	}

	@Override
	public Type getType() {
		return Type.BOSS;
	}

	@Override
	public List<Element> getElement() {
		return Arrays.asList(Element.PIERCE);
	}

	@Override
	public int getAtackPattern() {
		return AtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(50, 250, 500, 3);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(5000, 5000, 5, 0, 0, 20);
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