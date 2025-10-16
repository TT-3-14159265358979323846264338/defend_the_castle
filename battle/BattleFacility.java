package battle;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultStage;
import defaultdata.DefaultUnit;
import defaultdata.facility.FacilityData;
import defaultdata.stage.StageData;

//設備のバトル情報
public class BattleFacility extends BattleData{
	private BufferedImage breakImage;
	
	protected BattleFacility(Battle Battle, StageData StageData, int number) {
		this.Battle = Battle;
		FacilityData FacilityData = DefaultStage.FACILITY_DATA_MAP.get(StageData.getFacility().get(number));
		name = FacilityData.getName();
		explanation = FacilityData.getExplanation();
		rightActionImage = StageData.getFacilityDirection().get(number)? FacilityData.getActionFrontImage(4): FacilityData.getActionSideImage(4);
		bulletImage = FacilityData.getBulletImage(4);
		hitImage = FacilityData.getHitImage(4);
		generatedBuffInformation = FacilityData.getBuff();
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
		schedulerStart();
		atackTimer(0);
		healTimer(0);
	}
	
	protected void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		if(Objects.isNull(AtackPattern)) {
			return;
		}
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		if(element.stream().anyMatch(i -> i == DefaultUnit.SUPPORT)){
			AtackPattern.install(this, allyData);
		}else {
			AtackPattern.install(this, this.enemyData);
		}
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData)).toList();
		activateBuff(Buff.BIGINNING, null);
	}
	
	protected BufferedImage getBreakImage() {
		return breakImage;
	}
	
	@Override
	protected void individualSchedulerEnd() {
		//特になし
	}
	
	@Override
	protected void individualFutureStop() {
		//特になし
	}
	
	@Override
	protected int moraleCorrection() {
		return 0;
	}
	
	@Override
	protected void defeat(BattleData target) {
		canActivate = false;
		clearBlock();
		GameData.lowMorale(battle.GameData.UNIT, 30);
		activateBuff(Buff.DEFEAT, target);
	}
}