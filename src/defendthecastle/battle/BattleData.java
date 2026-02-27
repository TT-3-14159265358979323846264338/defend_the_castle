package defendthecastle.battle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import commonclass.EditImage;
import defaultdata.Element;
import defaultdata.atackpattern.AtackPatternData;

//全キャラクターの共通システム
public abstract class BattleData{
	//基礎データ
	protected GameData gameData;
	protected GameTimer gameTimer;
	protected List<BattleData> allyData;
	protected List<BattleData> enemyData;
	protected final int IMAGE_RATIO = 4;
	
	//攻撃関連
	protected AtackPatternData atackPatternData;
	protected boolean existsRight = true;
	protected boolean canAtack;
	private int motionNumber = 0;
	protected List<BufferedImage> rightActionImage;
	protected List<BufferedImage> leftActionImage;
	protected BufferedImage bulletImage;
	protected List<BufferedImage> hitImage;
	private List<Bullet> bulletList = Arrays.asList();
	private List<BattleData> targetList = new ArrayList<>();
	private int hitedCount;
	protected final int NONE_DELAY = 0;
	
	//バフ関連
	protected List<List<Double>> generatedBuffInformation;
	protected List<Buff> generatedBuff = new ArrayList<>();
	protected List<Buff> receivedBuff = new ArrayList<>();
	
	//ステータス関連
	protected String name;
	protected String explanation;
	protected boolean canActivate;
	protected int nowHP;
	protected double positionX;
	protected double positionY;
	protected List<Element> element;
	protected List<Integer> defaultWeaponStatus;
	protected List<Integer> defaultUnitStatus;
	protected List<Integer> defaultCutStatus;
	protected List<BattleEnemy> block = new ArrayList<>();
	private final int GUARANTEE_ATACK = 10;
	private final int GUARANTEE_RANGE = 10;
	private final int GUARANTEE_ATACK_SPEED = 100;
	private final int GUARANTEE_MAX_HP = 100;
	private final int MOTION_DELAY = 20;
	private final int HEAL_DELAY = 5000;
	
	//システム関連
	private Object buffLock = new Object();
	private Object blockLock = new Object();
	private Object HPLock = new Object();
	protected ScheduledExecutorService scheduler;
	private ScheduleTimerOperation atackTimer;
	private TimerOperation motionTimer;
	private TimerOperation healTimer;
	
	void initialize(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
		leftActionImage = rightActionImage.stream().map(i -> EditImage.mirrorImage(i)).toList();
		nowHP = defaultUnitStatus.get(1);
		atackTimer = createScheduleTimerOperation();
		motionTimer = createTimerOperation();
		healTimer = createTimerOperation();
	}
	
	ScheduleTimerOperation createScheduleTimerOperation() {
		return new ScheduleTimerOperation(gameTimer, scheduler);
	}
	
	TimerOperation createTimerOperation() {
		return new TimerOperation(gameTimer, scheduler);
	}
	
	void timerPause() {
		if(!canAtack) {
			atackTimer.timerPause(this::atackTimer);
		}
		motionTimer.timerPause(this::motionTimer);
		healTimer.timerPause(this::healTimer);
		bulletList.stream().forEach(Bullet::timerPause);
		generatedBuff.stream().forEach(Buff::timerPause);
		individualTimerPause();
	}
	
	protected abstract void individualTimerPause();
	
	void timerEnd() {
		atackTimer.timerStop();
		motionTimer.timerStop();
		bulletList.stream().forEach(Bullet::timerEnd);
		generatedBuff.stream().forEach(Buff::timerEnd);
		individualTimerEnd();
	}
	
	protected abstract void individualTimerEnd();
	
	//画像管理
	BufferedImage getActionImage(){
		return existsRight? rightActionImage.get(motionNumber): leftActionImage.get(motionNumber);
	}
	
	public BufferedImage getDefaultImage() {
		return rightActionImage.get(0);
	}
	
	List<Bullet> getBulletList(){
		return bulletList;
	}
	
	boolean canAtack() {
		return canAtack;
	}
	
	public boolean canActivate() {
		return canActivate;
	}
	
	//攻撃・回復処理
	void atackTimer(long stopTime) {
		if(defaultWeaponStatus.get((int) Buff.ATACK) <= 0) {
			return;
		}
		atackTimer.timerStrat(stopTime, getAtackSpeed(), this::atackTimerProcess);
	}
	
	void atackTimerProcess() {
		if(atackTimer.getDelay() != getAtackSpeed()) {
			CompletableFuture.runAsync(atackTimer::timerStop, scheduler).thenRun(() -> atackTimer(NONE_DELAY));
			return;
		}
		targetList = targetCheck();
		if(targetList.isEmpty()) {
			return;
		}
		atackTimer.updateBeforeTime();
		modeChange();
		motionTimer(NONE_DELAY);
	}
	
	List<BattleData> targetCheck() {
		List<BattleData> targetList = atackPatternData.getTarget();
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//タイマー停止時に割り込みが発生する。
				//停止させるため空のListを返却する。
				return Arrays.asList();
			}
			if(!canActivate) {
				atackTimer.timerStop();
				return Arrays.asList();
			}
			targetList = atackPatternData.getTarget();
		}while(targetList.isEmpty());
		return targetList;
	}
	
	void modeChange() {
		canAtack = true;
		existsRight = (targetList.get(0).getPositionX() <= positionX)? true: false;
	}
	
	void motionTimer(long stopTime) {
		motionTimer.timerStrat(stopTime, getAtackSpeed() * MOTION_DELAY, this::motionTimerProcess);
	}
	
	void motionTimerProcess() {
		motionTimer.updateBeforeTime();
		if(rightActionImage.size() - 1 <= motionNumber) {
			motionNumber = 0;
			bulletList = targetList.stream().map(i -> new Bullet(gameTimer, this, i, bulletImage, hitImage, scheduler)).toList();
			CompletableFuture.runAsync(this::atackProcess, scheduler);
			motionTimer.timerStop();
			return;
		}
		motionNumber++;
	}
	
	void atackProcess(){
		bulletList.get(0).waitCompletion();
		targetList.stream().forEach(this::result);
		timerRestart();
		atackTimer(NONE_DELAY);
	}
	
	synchronized void atackWait() {
		try {
			if(canAtack) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	synchronized void timerRestart() {
		bulletList = Arrays.asList();
		canAtack = false;
		notifyAll();
	}
	
	void result(BattleData target) {
		activateBuff(Buff.HIT, target);
		target.activateBuff(Buff.DAMAGE, this);
		if(element.stream().anyMatch(i -> i == Element.SUPPORT)) {
			heal(target);
			return;
		}
		damage(target);
	}
	
	void heal(BattleData target) {
		target.HPIncrease(healValue());
	}
	
	int healValue() {
		double baseHeal = ((double) getAtack() * (100 + getCut(Buff.SUPPORT)) / 100) * (100 + moraleCorrection()) / 100;
		return (int) baseHeal;
	}
	
	void damage(BattleData target) {
		if(getAtack() == 0 && target.getDefense() == 0) {
			return;
		}
		target.HPDecrease(damageValue(target), this);
	}
	
	int damageValue(BattleData target) {
		double baseDamage = (Math.pow(getAtack(), 2) / (getAtack() + target.getDefense())) * (100 + moraleCorrection()) / 100;
		double cutRatio = element.stream().mapToInt(i -> target.getCut(i.getId() + 100)).sum() / element.size();
		if(100 <= cutRatio) {
			cutRatio = 100;
		}
		return (int) (baseDamage * (100 - cutRatio) / 100);
	}
	
	protected abstract int moraleCorrection();
	
	void HPIncrease(int increase) {
		synchronized(HPLock) {
			if(nowHP <= 0) {
				return;
			}
			nowHP += increase;
			if(getMaxHP() < nowHP) {
				nowHP = getMaxHP();
			}
		}
	}
	
	void HPDecrease(int decrease, BattleData atackUnit) {
		synchronized(HPLock) {
			nowHP -= decrease;
			hitedCount++;
			if(nowHP <= 0 && canActivate) {
				defeat(atackUnit);
				atackUnit.activateBuff(Buff.KILL, this);
				atackUnit.kill();
				return;
			}
		}
	}
	
	public int getHitedCount() {
		return hitedCount;
	}
	
	protected abstract void defeat(BattleData target);
	
	protected abstract void kill();
	
	void healTimer(long stopTime) {
		healTimer.timerStrat(stopTime, HEAL_DELAY, this::healTimerProcess);
	}
	
	void healTimerProcess() {
		healTimer.updateBeforeTime();
		if(!canActivate) {
			healTimer.timerStop();
			return;
		}
		HPIncrease(getRecover());
	}
	
	//バフ管理
	void activateBuff(double timingCode, BattleData target){
		generatedBuff.stream().filter(i -> i.getBuffTiming() == timingCode).forEach(i -> i.buffStart(target));
	}
	
	void activateSkillBuff() {
		List<Buff> buff = generatedBuff.stream().filter(i -> i.getBuffTiming() == Buff.SKILL).filter(this::canPossessCost).toList();
		if(buff.size() == 0) {
			return;
		}
		buff.forEach(i -> i.buffStart(null));
		gameData.consumeCost(skillCost());
	}
	
	int skillCost() {
		return generatedBuff.stream().mapToInt(i -> i.getCost()).max().getAsInt();
	}
	
	boolean canPossessCost(Buff Buff) {
		return Buff.getCost() <= gameData.getCost();
	}
	
	void receiveBuff(Buff Buff) {
		synchronized(buffLock) {
			receivedBuff.add(Buff);
		}
	}
	
	void removeBuff(Buff Buff) {
		synchronized(buffLock) {
			receivedBuff.remove(Buff);
		}
	}
	
	protected double getAdditionalBuff(double statusCode) {
		//BattleUnitのみ@Overrideで記載
		return totalAdditionalBuff(0, statusCode, this);
	}
	
	double totalAdditionalBuff(double buff, double statusCode, BattleData BattleData) {
		for(Buff i: BattleData.receivedBuff){
			if(i.getBuffStatusCode() == statusCode) {
				buff = i.additionalEffect(BattleData, buff);
			}
		}
		return buff;
	}
	
	protected double getRatioBuff(double statusCode) {
		//BattleUnitのみ@Overrideで記載
		return totalRatioBuff(1, statusCode, this);
	}
	
	double totalRatioBuff(double buff, double statusCode, BattleData BattleData) {
		for(Buff i: BattleData.receivedBuff){
			if(i.getBuffStatusCode() == statusCode) {
				buff = i.ratioEffect(BattleData, buff);
			}
		}
		return buff;
	}
	
	protected int buffRange() {
		//BattleUnitのみ@Overrideで記載
		return getRange();
	}
	
	void HPBuff(double buffValue) {
		HPIncrease((int) buffValue);
	}
	
	//ブロック管理
	void addBlock(BattleEnemy BattleEnemy) {
		synchronized(blockLock) {
			block.add(BattleEnemy);
		}
	}
	
	void removeBlock(BattleEnemy BattleEnemy) {
		synchronized(blockLock) {
			block.remove(BattleEnemy);
		}
	}
	
	void releaseBlock(BattleEnemy BattleEnemy) {
		enemyData.stream().forEach(i -> i.removeBlock(BattleEnemy));
		BattleEnemy.releaseBlock();
	}
	
	void clearBlock() {
		synchronized(blockLock) {
			block.stream().forEach(i -> i.releaseBlock());
			block.clear();
		}
	}
	
	//ステータス計算
	public String getName() {
		return name;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public int getPositionX() {
		return (int) positionX;
	}
	
	public int getPositionY() {
		return (int) positionY;
	}
	
	public List<Element> getElement(){
		return element;
	}
	
	public AtackPatternData getAtackPattern() {
		return atackPatternData;
	}
	
	int getAtack() {
		if(defaultWeaponStatus.get((int) Buff.ATACK) == 0) {
			return 0;
		}
		int atack = (int) (statusControl(Buff.ATACK) * awakeningCorrection());
		if(atack <= GUARANTEE_ATACK) {
			return GUARANTEE_ATACK;
		}
		return atack;
	}
	
	public int getRange() {
		int range = (int) (statusControl(Buff.RANGE) * awakeningCorrection());
		if(range <= GUARANTEE_RANGE) {
			return GUARANTEE_RANGE;
		}
		return range;
	}
	
	int getAtackSpeed() {
		int atackSpeed = statusControl(Buff.ATACK_SPEED);
		if(atackSpeed <= GUARANTEE_ATACK_SPEED) {
			return GUARANTEE_ATACK_SPEED;
		}
		return atackSpeed;
	}
	
	public int getAtackNumber() {
		return statusControl(Buff.ATACK_NUMBER);
	}
	
	public List<Integer> getWeapon(){
		List<Integer> status = new ArrayList<>();
		status.add(getAtack());
		status.add(getRange());
		status.add(getAtackSpeed());
		status.add(getAtackNumber());
		return status;
	}
	
	public int getMaxHP() {
		int HP = statusControl(Buff.HP);
		if(HP <= GUARANTEE_MAX_HP) {
			return GUARANTEE_MAX_HP;
		}
		return HP;
	}
	
	public int getNowHP() {
		return nowHP;
	}
	
	int getDefense() {
		int defence = (int) (statusControl(Buff.DEFENCE) * awakeningCorrection());
		if(defence <= 0) {
			return 0;
		}
		return defence;
	}
	
	int getRecover() {
		int recover = statusControl(Buff.HEAL);
		if(recover <= 0) {
			return 0;
		}
		return recover;
	}
	
	int getMoveSpeedOrBlock() {
		return statusControl(Buff.MOVE_SPEED_OR_BLOCK);
	}
	
	int getCost() {
		int cost = statusControl(Buff.COST);
		if(cost <= 0) {
			return 0;
		}
		return cost;
	}
	
	public List<Integer> getUnit(){
		List<Integer> status = new ArrayList<>();
		status.add(getMaxHP());
		status.add(getNowHP());
		status.add(getDefense());
		status.add(getRecover());
		status.add(getMoveSpeedOrBlock());
		status.add(getCost());
		return status;
	}
	
	int getCut(double number) {
		int cut = statusControl(number);
		if(cut <= 0) {
			return 0;
		}
		return cut;
	}
	
	public List<Integer> getCut(){
		return IntStream.range(100, defaultCutStatus.size() + 100).mapToObj(i -> getCut(i)).toList();
	}
	
	int statusControl(double number) {
		if(number < 10) {
			return calculate(defaultWeaponStatus.get((int) number), getAdditionalBuff(number), getRatioBuff(number));
		}
		if(number < 100) {
			return calculate(defaultUnitStatus.get((int) number - 10), getAdditionalBuff(number), getRatioBuff(number));
		}
		if(number < 1000) {
			return calculate(defaultCutStatus.get((int) number - 100), getAdditionalBuff(number), getRatioBuff(number));
		}
		return 0;
	}
	
	int calculate(int initialValue, double additionalValue, double ratio) {
		return (int) ((initialValue + additionalValue) * ratio);
	}
	
	protected double awakeningCorrection() {
		//BattleUnitのみ@Overrideで記載
		return 1;
	}
}