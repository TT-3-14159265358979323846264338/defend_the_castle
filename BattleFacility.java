package battle;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultStage;
import defaultdata.facility.FacilityData;
import defaultdata.stage.StageData;

//設備のバトル情報
public class BattleFacility extends BattleData{
	BufferedImage breakImage;
	
	protected BattleFacility(Battle Battle, StageData StageData, int number) {
		this.Battle = Battle;
		FacilityData FacilityData = DefaultStage.FACILITY_DATA_MAP.get(StageData.getFacility().get(number));
		name = FacilityData.getName();
		rightActionImage = StageData.getFacilityDirection().get(number)? FacilityData.getActionFrontImage(4): FacilityData.getActionSideImage(4);
		bulletImage = FacilityData.getBulletImage(4);
		hitImage = FacilityData.getHitImage(4);
		breakImage = FacilityData.getBreakImage(4);
		positionX = StageData.getFacilityPoint().get(number).x;
		positionY = StageData.getFacilityPoint().get(number).y;
		element = FacilityData.getElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(FacilityData.getAtackPattern());
		if(FacilityData.getWeaponStatus() == null || FacilityData.getWeaponStatus().isEmpty()) {
			defaultWeaponStatus = IntStream.range(0, DefaultStage.WEAPON_MAP.size()).mapToObj(i -> 0).toList();
		}else {
			defaultWeaponStatus = FacilityData.getWeaponStatus().stream().toList();
		}
		defaultUnitStatus = FacilityData.getUnitStatus().stream().toList();
		defaultCutStatus = FacilityData.getCutStatus().stream().toList();
		canActivate = true;
		super.initialize();
		atackTimer();
		healTimer();
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
	
	protected BufferedImage getBreakImage() {
		return breakImage;
	}
	
	@Override
	protected int moraleRatio() {
		return 0;
	}
	
	@Override
	protected void defeat() {
		canActivate = false;
		GameData.lowMorale(battle.GameData.UNIT, 30);
	}
}