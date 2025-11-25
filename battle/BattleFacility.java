package battle;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
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
	private final int DEFEAT_MORALE = 30;
	
	BattleFacility(Battle Battle, StageData StageData, int number, ScheduledExecutorService scheduler) {
		this.Battle = Battle;
		FacilityData FacilityData = DefaultStage.FACILITY_DATA_MAP.get(StageData.getFacility().get(number));
		name = FacilityData.getName();
		explanation = FacilityData.getExplanation();
		rightActionImage = StageData.getFacilityDirection().get(number)? FacilityData.getActionFrontImage(IMAGE_RATIO): FacilityData.getActionSideImage(IMAGE_RATIO);
		bulletImage = FacilityData.getBulletImage(IMAGE_RATIO);
		hitImage = FacilityData.getHitImage(IMAGE_RATIO);
		generatedBuffInformation = FacilityData.getBuff();
		breakImage = FacilityData.getBreakImage(IMAGE_RATIO);
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
		super.initialize(scheduler);
		atackTimer(NONE_DELAY);
		healTimer(NONE_DELAY);
	}
	
	void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
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
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData, scheduler)).toList();
		activateBuff(Buff.BIGINNING, null);
	}
	
	BufferedImage getBreakImage() {
		return breakImage;
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
		GameData.lowMorale(battle.GameData.UNIT, DEFEAT_MORALE);
		activateBuff(Buff.DEFEAT, target);
	}
}