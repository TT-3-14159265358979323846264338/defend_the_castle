package defendthecastle.battle;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.Atack;
import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.facility.FacilityData;
import defaultdata.stage.StageData;

//設備のバトル情報
public class BattleFacility extends BattleData{
	private BufferedImage breakImage;
	private final int DEFEAT_MORALE = 30;
	
	BattleFacility(GameTimer gameTimer, StageData stageData, int number, ScheduledExecutorService scheduler) {
		this.gameTimer = gameTimer;
		FacilityData FacilityData = stageData.getFacility().get(number).getFacilityData();
		name = FacilityData.getName();
		explanation = FacilityData.getExplanation();
		rightActionImage = stageData.getFacilityDirection().get(number)? FacilityData.getActionFrontImage(IMAGE_RATIO): FacilityData.getActionSideImage(IMAGE_RATIO);
		bulletImage = FacilityData.getBulletImage(IMAGE_RATIO);
		hitImage = FacilityData.getHitImage(IMAGE_RATIO);
		generatedBuffInformation = FacilityData.getBuff();
		breakImage = FacilityData.getBreakImage(IMAGE_RATIO);
		positionX = stageData.getFacilityPoint().get(number).x;
		positionY = stageData.getFacilityPoint().get(number).y;
		element = FacilityData.getElement().stream().toList();
		atackPatternData = new AtackPattern().getAtackPattern(FacilityData.getAtackPattern());
		if(FacilityData.getWeaponStatus() == null || FacilityData.getWeaponStatus().isEmpty()) {
			defaultWeaponStatus = IntStream.range(0, Atack.values().length).mapToObj(_ -> 0).toList();
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
	
	void install(GameData gameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.gameData = gameData;
		if(Objects.isNull(atackPatternData)) {
			return;
		}
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		if(element.stream().anyMatch(i -> i == Element.SUPPORT)){
			atackPatternData.install(this, allyData);
		}else {
			atackPatternData.install(this, this.enemyData);
		}
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, gameTimer, gameData, scheduler)).toList();
		activateBuff(Buff.BIGINNING, null);
	}
	
	BufferedImage getBreakImage() {
		return breakImage;
	}
	
	@Override
	protected void individualTimerPause() {
		//特になし
	}
	
	@Override
	protected void individualTimerEnd() {
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
		gameData.lowMorale(GameData.UNIT, DEFEAT_MORALE);
		activateBuff(Buff.DEFEAT, target);
	}
	
	@Override
	protected void kill() {
		//特に処理なし
	}
}