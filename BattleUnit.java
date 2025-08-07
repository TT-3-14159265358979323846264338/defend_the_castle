package battle;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import screendisplay.DisplayStatus;
import screendisplay.StatusCalculation;

//ユニットのバトル情報
public class BattleUnit extends BattleData{
	BufferedImage rightCoreImage;
	BufferedImage leftCoreImage;
	Point initialPosition = new Point();
	int type;
	int awakeningNumber;
	int defeatNumber;
	
	//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	protected BattleUnit(Battle Battle, List<Integer> composition, int positionX, int positionY) {
		this.Battle = Battle;
		StatusCalculation StatusCalculation = new StatusCalculation(composition);
		name = new DisplayStatus().getUnitName(composition);
		try {
			rightActionImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(0)).getRightActionImage(4);
			bulletImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(0)).getBulletImage(4);
			hitImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(0)).getHitImage(4);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		rightCoreImage = DefaultUnit.CORE_DATA_MAP.get(composition.get(1)).getActionImage(4);
		leftCoreImage = EditImage.mirrorImage(rightCoreImage);
		this.positionX = positionX;
		this.positionY = positionY;
		initialPosition = new Point(positionX, positionY);
		type = StatusCalculation.getType();
		element = StatusCalculation.getRightElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(StatusCalculation.getRightAtackPattern());
		defaultWeaponStatus = StatusCalculation.getRightWeaponStatus().stream().collect(Collectors.toList());
		defaultUnitStatus = StatusCalculation.getUnitStatus().stream().collect(Collectors.toList());
		defaultCutStatus = StatusCalculation.getCutStatus().stream().collect(Collectors.toList());
		canActivate = false;
		super.initialize();
	}
	
	//左武器用
	protected BattleUnit(Battle Battle, List<Integer> composition) {
		this.Battle = Battle;
		StatusCalculation StatusCalculation = new StatusCalculation(composition);
		try {
			rightActionImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(2)).getLeftActionImage(4);
			bulletImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(2)).getBulletImage(4);
			hitImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(2)).getHitImage(4);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		element = StatusCalculation.getLeftElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(StatusCalculation.getLeftAtackPattern());
		defaultWeaponStatus = StatusCalculation.getLeftWeaponStatus();
		defaultUnitStatus = StatusCalculation.getUnitStatus();
		defaultCutStatus = StatusCalculation.getCutStatus();
		super.initialize();
	}
	
	protected void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		if(Objects.isNull(AtackPattern)) {
			return;
		}
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		if(element.stream().anyMatch(i -> i == 11)){
			AtackPattern.install(this, allyData);
		}else {
			AtackPattern.install(this, this.enemyData);
		}
	}
	
	private BufferedImage getBlankImage() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 0);
		return image;
	}
	
	protected BufferedImage getCoreImage() {
		return existsRight? rightCoreImage: leftCoreImage;
	}
	
	public BufferedImage getDefaultCoreImage(){
		return rightCoreImage;
	}
	
	public int getType() {
		return type;
	}
	
	protected int getAwakeningNumber() {
		return awakeningNumber;
	}
	
	protected int getDefeatNumber() {
		return defeatNumber;
	}
	
	protected void activate(int x, int y) {
		canActivate = true;
		GameData.moraleBoost(battle.GameData.UNIT, 5);
		positionX = x;
		positionY = y;
		atackTimer();
		healTimer();
	}
	
	protected void awakening() {
		awakeningNumber++;
	}
	
	@Override
	protected int moraleRatio() {
		return (GameData.getMoraleDifference() <= 0)? Math.abs(GameData.getMoraleDifference()): 0;
	}
	
	@Override
	protected void defeat() {
		if(nowHP <= 0) {
			defeatNumber++;
			GameData.lowMorale(battle.GameData.UNIT, 60);
		}else {
			GameData.lowMorale(battle.GameData.UNIT, 5 + 30 * (getMaxHP() - nowHP) / getMaxHP());
		}
		canActivate = false;
		positionX = initialPosition.x;
		positionY = initialPosition.y;
		existsRight = true;
		super.initialize();
	}
}