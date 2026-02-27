package defendthecastle.battle;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import commonclass.EditImage;
import defaultdata.Core;
import defaultdata.AtackPattern;
import defaultdata.Distance;
import defaultdata.Element;
import defaultdata.Weapon;
import defendthecastle.screendisplay.StatusCalculation;
import savedata.OneUnitData;

//ユニットのバトル情報
public class BattleUnit extends BattleData{
	//基礎データ
	private List<Integer> composition;
	private BattleUnit otherWeapon;
	private BufferedImage rightCoreImage;
	private BufferedImage leftCoreImage;
	private BufferedImage skillImage;
	private Point initialPosition = new Point();
	private Distance type;
	private boolean canPossessSkill;
	private boolean existsOtherBuffRange;
	
	//ユニット制御
	private final int AWAKEING_CONDETION = 300;
	private final int PLACEMENT_ACHIEVEMENT = 1;
	private final int KILL_ACHIEVEMENT = 60;
	private final int TIMER_INTERVAL = 100;
	private final double AWAKENING_RATIO = 1.15;
	private final int MAX_AWAKENING = 5;
	private final int SOTIE_MORALE = 5;
	private int achievement;
	private boolean canLocate = true;
	private int relocationCount;
	private int relocationTime;
	private int awakeningNumber;
	private int defeatNumber;
	
	//システム関連
	private Object achievementLock = new Object();
	private TimerOperation achievementTimer;
	private TimerOperation relocationTimer;
	
	//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	BattleUnit(GameTimer gameTimer, OneUnitData oneUnitData, int positionX, int positionY, ScheduledExecutorService scheduler) {
		this.gameTimer = gameTimer;
		composition = oneUnitData.getUnitDataList();
		StatusCalculation StatusCalculation = new StatusCalculation(composition);
		try {
			rightActionImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.RIGHT_WEAPON)).getRightActionImage(IMAGE_RATIO);
			bulletImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.RIGHT_WEAPON)).getBulletImage(IMAGE_RATIO);
			hitImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.RIGHT_WEAPON)).getHitImage(IMAGE_RATIO);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		rightCoreImage = Core.getCoreData(composition.get(savedata.OneUnitData.CORE)).getActionImage(IMAGE_RATIO);
		leftCoreImage = EditImage.mirrorImage(rightCoreImage);
		skillImage = Core.getCoreData(composition.get(savedata.OneUnitData.CORE)).getSkillImage(IMAGE_RATIO);
		generatedBuffInformation = StatusCalculation.getRightBuffList();
		this.positionX = positionX;
		this.positionY = positionY;
		initialPosition = new Point(positionX, positionY);
		type = StatusCalculation.getType();
		element = StatusCalculation.getRightElement().stream().toList();
		atackPatternData = new AtackPattern().getAtackPattern(StatusCalculation.getRightAtackPattern());
		defaultWeaponStatus = StatusCalculation.getRightWeaponStatus().stream().collect(Collectors.toList());
		defaultUnitStatus = StatusCalculation.getUnitStatus().stream().collect(Collectors.toList());
		defaultCutStatus = StatusCalculation.getCutStatus().stream().collect(Collectors.toList());
		canActivate = false;
		super.initialize(scheduler);
		achievementTimer = createTimerOperation();
		relocationTimer = createTimerOperation();
	}
	
	//左武器用
	BattleUnit(GameTimer gameTimer, OneUnitData oneUnitData, ScheduledExecutorService scheduler) {
		this.gameTimer = gameTimer;
		composition = oneUnitData.getUnitDataList();
		StatusCalculation StatusCalculation = new StatusCalculation(composition);
		try {
			rightActionImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.LEFT_WEAPON)).getLeftActionImage(IMAGE_RATIO);
			bulletImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.LEFT_WEAPON)).getBulletImage(IMAGE_RATIO);
			hitImage = Weapon.getWeaponData(composition.get(savedata.OneUnitData.LEFT_WEAPON)).getHitImage(IMAGE_RATIO);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		generatedBuffInformation = StatusCalculation.getLeftBuffList();
		element = StatusCalculation.getLeftElement().stream().toList();
		atackPatternData = new AtackPattern().getAtackPattern(StatusCalculation.getLeftAtackPattern());
		defaultWeaponStatus = StatusCalculation.getLeftWeaponStatus();
		defaultUnitStatus = StatusCalculation.getUnitStatus();
		defaultCutStatus = StatusCalculation.getCutStatus();
		super.initialize(scheduler);
		achievementTimer = createTimerOperation();
		relocationTimer = createTimerOperation();
	}
	
	void install(GameData gameData, BattleUnit otherWeapon, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.gameData = gameData;
		this.otherWeapon = otherWeapon;
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		existsOtherBuffRange = (defaultWeaponStatus.get((int) Buff.RANGE) <= otherWeapon.defaultWeaponStatus.get((int) Buff.RANGE))? true: false;
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, gameTimer, gameData, scheduler)).toList();
		canPossessSkill = generatedBuff.stream().anyMatch(i -> i.canPossessSkill());
		if(Objects.isNull(atackPatternData)) {
			return;
		}
		if(element.stream().anyMatch(i -> i == Element.SUPPORT)){
			atackPatternData.install(this, allyData);
		}else {
			atackPatternData.install(this, this.enemyData);
		}
	}
	
	public List<Integer> getComposition(){
		return composition;
	}
	
	BufferedImage getBlankImage() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 0);
		return image;
	}
	
	BufferedImage getCoreImage() {
		return existsRight? rightCoreImage: leftCoreImage;
	}
	
	public BufferedImage getDefaultCoreImage(){
		return rightCoreImage;
	}
	
	BufferedImage getSkillImage() {
		return skillImage;
	}
	
	public Distance getType() {
		return type;
	}
	
	void achievementTimer(long stopTime) {
		achievementTimer.timerStrat(stopTime, TIMER_INTERVAL, this::achievementTimerProcess);
	}
	
	void achievementTimerProcess() {
		achievementTimer.updateBeforeTime();
		if(!canActivate) {
			achievementTimer.timerStop();
			return;
		}
		setAchievement(PLACEMENT_ACHIEVEMENT);
	}
	
	void setAchievement(int value) {
		synchronized(achievementLock) {
			achievement += value;
		}
	}
	
	boolean canAwake() {
		if(MAX_AWAKENING <= awakeningNumber) {
			return false;
		}
		return AWAKEING_CONDETION * (awakeningNumber + 1) <= achievement;
	}
	
	public int getAwakeningNumber() {
		return awakeningNumber;
	}
	
	void awakening() {
		gameData.moraleBoost(GameData.UNIT, SOTIE_MORALE);
		awakeningNumber++;
	}
	
	boolean canLocate() {
		return canLocate;
	}
	
	double locationRatio() {
		return (double) relocationCount / relocationTime;
	}
	
	public int getDefeatNumber() {
		return defeatNumber;
	}
	
	boolean canPossessSkill() {
		return canPossessSkill;
	}
	
	boolean canRecast() {
		return generatedBuff.stream().anyMatch(i -> i.canRecast());
	}
	
	Point getInitialPosition() {
		return initialPosition;
	}
	
	double recastRatio() {
		for(Buff i: generatedBuff) {
			if(i.canPossessSkill()) {
				return i.recastRatio();
			}
		}
		return 0;
	}
	
	void activate(int x, int y) {
		canActivate = true;
		canLocate = false;
		if(defaultUnitStatus.get(5) != 0) {
			gameData.moraleBoost(GameData.UNIT, SOTIE_MORALE);
		}
		positionX = x;
		positionY = y;
		atackTimer(NONE_DELAY);
		healTimer(NONE_DELAY);
		activateBuff(Buff.BIGINNING, null);
		achievementTimer(NONE_DELAY);
	}
	
	@Override
	protected void individualTimerPause() {
		achievementTimer.timerPause(this::achievementTimer);
		relocationTimer.timerPause(this::relocationTimer);
	}

	@Override
	protected void individualTimerEnd() {
		achievementTimer.timerStop();
		relocationTimer.timerStop();
	}
	
	@Override
	protected int moraleCorrection() {
		return (gameData.getMoraleDifference() <= 0)? Math.abs(gameData.getMoraleDifference()): 0;
	}
	
	@Override
	protected void defeat(BattleData target) {
		int price = 60;
		defeatNumber++;
		gameData.lowMorale(defendthecastle.battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocationTimer(NONE_DELAY);
		reset(target);
	}
	
	void retreat() {
		int price = (5 + 10 * (getMaxHP() - nowHP) / getMaxHP());
		gameData.lowMorale(defendthecastle.battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocationTimer(NONE_DELAY);
		reset(null);
	}
	
	void relocationTimer(long stopTime) {
		relocationTimer.timerStrat(stopTime, TIMER_INTERVAL, this::relocationTimerProcess);
	}
	
	void relocationTimerProcess() {
		relocationTimer.updateBeforeTime();
		relocationCount += TIMER_INTERVAL;
		if(relocationTime <= relocationCount) {
			relocationCount = 0;
			canLocate = true;
			relocationTimer.timerStop();
		}
	}
	
	void reset(BattleData target) {
		nowHP = defaultUnitStatus.get(0);
		individualReset(target);
		otherWeapon.individualReset(target);
		clearBlock();
	}
	
	void individualReset(BattleData target) {
		canActivate = false;
		positionX = initialPosition.x;
		positionY = initialPosition.y;
		existsRight = true;
		activateBuff(Buff.DEFEAT, target);
	}
	
	@Override
	protected void kill() {
		setAchievement(KILL_ACHIEVEMENT);
		otherWeapon.setAchievement(KILL_ACHIEVEMENT);
	}
	
	@Override
	protected double getAdditionalBuff(double statusCode){
		double totalMyBuff = totalAdditionalBuff(0, statusCode, this);
		return totalAdditionalBuff(totalMyBuff, statusCode, otherWeapon);
	}
	
	@Override
	protected double getRatioBuff(double statusCode){
		double totalMyBuff = totalRatioBuff(1, statusCode, this);
		return totalRatioBuff(totalMyBuff, statusCode, otherWeapon);
	}
	
	@Override
	protected int buffRange() {
		return existsOtherBuffRange? otherWeapon.getRange(): getRange();
	}
	
	@Override
	protected double awakeningCorrection() {
		return Math.pow(AWAKENING_RATIO, awakeningNumber);
	}
}