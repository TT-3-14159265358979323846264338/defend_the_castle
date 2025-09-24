package battle;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import screendisplay.DisplayStatus;
import screendisplay.StatusCalculation;

//ユニットのバトル情報
public class BattleUnit extends BattleData{
	//基礎データ
	private BattleUnit otherWeapon;
	private BufferedImage rightCoreImage;
	private BufferedImage leftCoreImage;
	private BufferedImage skillImage;
	private Point initialPosition = new Point();
	private int type;
	private boolean canPossessSkill;
	private boolean existsOtherBuffRange;
	
	//ユニット制御
	private final int AWAKEING_CONDETION = 300;
	private final int PLACEMENT_ACHIEVEMENT = 1;
	private final int KILL_ACHIEVEMENT = 60;
	private final int TIMER_INTERVAL = 100;
	private int achievement;
	private boolean canLocate = true;
	private int relocationCount;
	private int relocationTime;
	private int awakeningNumber;
	private int defeatNumber;
	
	//システム関連
	private Object achievementLock = new Object();
	private ScheduledExecutorService achievementScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> achievementFuture;
	private ScheduledExecutorService relocationScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> relocationFuture;
	
	//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	protected BattleUnit(Battle Battle, List<Integer> composition, int positionX, int positionY) {
		this.Battle = Battle;
		StatusCalculation StatusCalculation = new StatusCalculation(composition);
		name = new DisplayStatus().getUnitName(composition);
		try {
			rightActionImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.RIGHT_WEAPON)).getRightActionImage(4);
			bulletImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.RIGHT_WEAPON)).getBulletImage(4);
			hitImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.RIGHT_WEAPON)).getHitImage(4);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		rightCoreImage = DefaultUnit.CORE_DATA_MAP.get(composition.get(DefaultUnit.CORE)).getActionImage(4);
		leftCoreImage = EditImage.mirrorImage(rightCoreImage);
		skillImage = DefaultUnit.CORE_DATA_MAP.get(composition.get(DefaultUnit.CORE)).getSkillImage(4);
		generatedBuffInformation = StatusCalculation.getRightBuffList();
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
			rightActionImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.LEFT_WEAPON)).getLeftActionImage(4);
			bulletImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.LEFT_WEAPON)).getBulletImage(4);
			hitImage = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.LEFT_WEAPON)).getHitImage(4);
		}catch(Exception e) {
			rightActionImage = Arrays.asList(getBlankImage());
		}
		generatedBuffInformation = StatusCalculation.getLeftBuffList();
		element = StatusCalculation.getLeftElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(StatusCalculation.getLeftAtackPattern());
		defaultWeaponStatus = StatusCalculation.getLeftWeaponStatus();
		defaultUnitStatus = StatusCalculation.getUnitStatus();
		defaultCutStatus = StatusCalculation.getCutStatus();
		super.initialize();
	}
	
	protected void install(GameData GameData, BattleUnit otherWeapon, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		this.otherWeapon = otherWeapon;
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		existsOtherBuffRange = (defaultWeaponStatus.get((int) Buff.RANGE) <= otherWeapon.defaultWeaponStatus.get((int) Buff.RANGE))? true: false;
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData)).toList();
		canPossessSkill = generatedBuff.stream().anyMatch(i -> i.canPossessSkill());
		if(Objects.isNull(AtackPattern)) {
			return;
		}
		if(element.stream().anyMatch(i -> i == DefaultUnit.SUPPORT)){
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
	
	protected BufferedImage getSkillImage() {
		return skillImage;
	}
	
	public int getType() {
		return type;
	}
	
	private void achievementTimer() {
		achievementFuture = achievementScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(!canActivate) {
				achievementFuture.cancel(true);
				return;
			}
			setAchievement(PLACEMENT_ACHIEVEMENT);
		}, 0, TIMER_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	private void setAchievement(int value) {
		synchronized(achievementLock) {
			achievement += value;
		}
	}
	
	protected boolean canAwake() {
		if(10 <= awakeningNumber) {
			return false;
		}
		return AWAKEING_CONDETION * (awakeningNumber + 1) <= achievement;
	}
	
	protected int getAwakeningNumber() {
		return awakeningNumber;
	}
	
	protected void awakening() {
		GameData.moraleBoost(battle.GameData.UNIT, 5);
		awakeningNumber++;
	}
	
	protected boolean canLocate() {
		return canLocate;
	}
	
	protected double locationRatio() {
		return (double) relocationCount / relocationTime;
	}
	
	public int getDefeatNumber() {
		return defeatNumber;
	}
	
	protected boolean canPossessSkill() {
		return canPossessSkill;
	}
	
	protected boolean canRecast() {
		return generatedBuff.stream().anyMatch(i -> i.canRecast());
	}
	
	protected Point getInitialPosition() {
		return initialPosition;
	}
	
	protected double recastRatio() {
		for(Buff i: generatedBuff) {
			if(i.canPossessSkill()) {
				return i.recastRatio();
			}
		}
		return 0;
	}
	
	protected void activate(int x, int y) {
		canActivate = true;
		canLocate = false;
		GameData.moraleBoost(battle.GameData.UNIT, 5);
		positionX = x;
		positionY = y;
		atackTimer();
		healTimer();
		activateBuff(Buff.BIGINNING, null);
		achievementTimer();
	}
	
	@Override
	protected void individualSchedulerEnd() {
		achievementScheduler.shutdown();
		relocationScheduler.shutdown();
	}
	
	@Override
	protected int moraleCorrection() {
		return (GameData.getMoraleDifference() <= 0)? Math.abs(GameData.getMoraleDifference()): 0;
	}
	
	@Override
	protected double awakeningCorrection() {
		if(awakeningNumber == 0) {
			return 1;
		}
		return Math.pow(1.1, awakeningNumber);
	}
	
	@Override
	protected void defeat(BattleData target) {
		int price = 60;
		defeatNumber++;
		GameData.lowMorale(battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocation();
		reset(target);
	}
	
	protected void retreat() {
		int price = (5 + 20 * (getMaxHP() - nowHP) / getMaxHP());
		GameData.lowMorale(battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocation();
		reset(null);
	}
	
	private void relocation() {
		relocationFuture = relocationScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			relocationCount += TIMER_INTERVAL;
			if(relocationTime <= relocationCount) {
				relocationCount = 0;
				canLocate = true;
				relocationFuture.cancel(true);
			}
		}, 0, TIMER_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	private void reset(BattleData target) {
		nowHP = defaultUnitStatus.get(0);
		clearBlock();
		individualReset(target);
		otherWeapon.individualReset(target);
	}
	
	private void individualReset(BattleData target) {
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
}