package defendthecastle.battle;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
	private ScheduledFuture<?> achievementFuture;
	private long beforeAchievementTime;
	private ScheduledFuture<?> relocationFuture;
	private long beforeRelocationTime;
	
	//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	BattleUnit(Battle Battle, OneUnitData OneUnitData, int positionX, int positionY, ScheduledExecutorService scheduler) {
		this.Battle = Battle;
		composition = OneUnitData.getUnitDataList();
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
		AtackPatternData = new AtackPattern().getAtackPattern(StatusCalculation.getRightAtackPattern());
		defaultWeaponStatus = StatusCalculation.getRightWeaponStatus().stream().collect(Collectors.toList());
		defaultUnitStatus = StatusCalculation.getUnitStatus().stream().collect(Collectors.toList());
		defaultCutStatus = StatusCalculation.getCutStatus().stream().collect(Collectors.toList());
		canActivate = false;
		super.initialize(scheduler);
	}
	
	//左武器用
	BattleUnit(Battle Battle, OneUnitData OneUnitData, ScheduledExecutorService scheduler) {
		this.Battle = Battle;
		composition = OneUnitData.getUnitDataList();
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
		AtackPatternData = new AtackPattern().getAtackPattern(StatusCalculation.getLeftAtackPattern());
		defaultWeaponStatus = StatusCalculation.getLeftWeaponStatus();
		defaultUnitStatus = StatusCalculation.getUnitStatus();
		defaultCutStatus = StatusCalculation.getCutStatus();
		super.initialize(scheduler);
	}
	
	void install(GameData GameData, BattleUnit otherWeapon, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		this.otherWeapon = otherWeapon;
		allyData = Stream.concat(Stream.of(unitMainData), Stream.of(facilityData)).toList();
		this.enemyData = Stream.of(enemyData).toList();
		existsOtherBuffRange = (defaultWeaponStatus.get((int) Buff.RANGE) <= otherWeapon.defaultWeaponStatus.get((int) Buff.RANGE))? true: false;
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData, scheduler)).toList();
		canPossessSkill = generatedBuff.stream().anyMatch(i -> i.canPossessSkill());
		if(Objects.isNull(AtackPatternData)) {
			return;
		}
		if(element.stream().anyMatch(i -> i == Element.SUPPORT)){
			AtackPatternData.install(this, allyData);
		}else {
			AtackPatternData.install(this, this.enemyData);
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
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = (stopTime - beforeAchievementTime < TIMER_INTERVAL)? TIMER_INTERVAL - (stopTime - beforeAchievementTime): NONE_DELAY;
			beforeAchievementTime += System.currentTimeMillis() - stopTime;
		}
		achievementFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeAchievementTime = System.currentTimeMillis();
			if(!canActivate) {
				achievementFuture.cancel(true);
				return;
			}
			setAchievement(PLACEMENT_ACHIEVEMENT);
		}, initialDelay, TIMER_INTERVAL, TimeUnit.MILLISECONDS);
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
		GameData.moraleBoost(defendthecastle.battle.GameData.UNIT, SOTIE_MORALE);
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
			GameData.moraleBoost(defendthecastle.battle.GameData.UNIT, SOTIE_MORALE);
		}
		positionX = x;
		positionY = y;
		atackTimer(NONE_DELAY);
		healTimer(NONE_DELAY);
		activateBuff(Buff.BIGINNING, null);
		achievementTimer(NONE_DELAY);
	}
	
	@Override
	protected void individualFutureStop() {
		if(achievementFuture != null && !achievementFuture.isCancelled()) {
			achievementFuture.cancel(true);
			long achievementTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> achievementTimer(achievementTime));
		}
		if(relocationFuture != null && !relocationFuture.isCancelled()) {
			relocationFuture.cancel(true);
			long relocationTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> relocation(relocationTime));
		}
	}
	
	@Override
	protected int moraleCorrection() {
		return (GameData.getMoraleDifference() <= 0)? Math.abs(GameData.getMoraleDifference()): 0;
	}
	
	@Override
	protected void defeat(BattleData target) {
		int price = 60;
		defeatNumber++;
		GameData.lowMorale(defendthecastle.battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocation(NONE_DELAY);
		reset(target);
	}
	
	void retreat() {
		int price = (5 + 10 * (getMaxHP() - nowHP) / getMaxHP());
		GameData.lowMorale(defendthecastle.battle.GameData.UNIT, price);
		relocationTime = price * 1000;
		relocation(NONE_DELAY);
		reset(null);
	}
	
	void relocation(long stopTime) {
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = (stopTime - beforeRelocationTime < TIMER_INTERVAL)? TIMER_INTERVAL - (stopTime - beforeRelocationTime): NONE_DELAY;
			beforeRelocationTime += System.currentTimeMillis() - stopTime;
		}
		relocationFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeRelocationTime = System.currentTimeMillis();
			relocationCount += TIMER_INTERVAL;
			if(relocationTime <= relocationCount) {
				relocationCount = 0;
				canLocate = true;
				relocationFuture.cancel(true);
			}
		}, initialDelay, TIMER_INTERVAL, TimeUnit.MILLISECONDS);
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