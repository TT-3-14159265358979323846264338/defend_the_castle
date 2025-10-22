package battle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defaultdata.atackpattern.AtackPattern;

//全キャラクターの共通システム
public class BattleData{
	//基礎データ
	protected Battle Battle;
	protected GameData GameData;
	protected List<BattleData> allyData;
	protected List<BattleData> enemyData;
	
	//攻撃関連
	protected AtackPattern AtackPattern;
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
	protected List<Integer> element;
	protected List<Integer> defaultWeaponStatus;
	protected List<Integer> defaultUnitStatus;
	protected List<Integer> defaultCutStatus;
	protected List<BattleEnemy> block = new ArrayList<>();
	
	//システム関連
	private Object buffLock = new Object();
	private Object blockLock = new Object();
	private Object HPLock = new Object();
	protected ScheduledExecutorService scheduler;
	private ScheduledFuture<?> atackFuture;
	private long beforeAtackTime;
	private ScheduledFuture<?> motionFuture;
	private long beforeMotionTime;
	private ScheduledFuture<?> healFuture;
	private long beforeHealTime;
	
	protected void initialize(ScheduledExecutorService scheduler) {
		leftActionImage = rightActionImage.stream().map(i -> EditImage.mirrorImage(i)).toList();
		nowHP = defaultUnitStatus.get(1);
		this.scheduler = scheduler;
	}
	
	protected void futureStop() {
		if(atackFuture != null && !atackFuture.isCancelled() && !canAtack) {
			atackFuture.cancel(true);
			long atackTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> atackTimer(atackTime));
		}
		if(motionFuture != null && !motionFuture.isCancelled()) {
			motionFuture.cancel(true);
			long motionTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> motionTimer(motionTime));
		}
		if(healFuture != null && !healFuture.isCancelled()) {
			healFuture.cancel(true);
			long healTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> healTimer(healTime));
		}
		bulletList.stream().forEach(i -> i.futureStop());
		generatedBuff.stream().forEach(i -> i.futureStop());
		individualFutureStop();
	}
	
	protected void individualFutureStop() {
		//詳細は@Overrideで記載
	}
	
	//画像管理
	protected BufferedImage getActionImage(){
		return existsRight? rightActionImage.get(motionNumber): leftActionImage.get(motionNumber);
	}
	
	public BufferedImage getDefaultImage() {
		return rightActionImage.get(0);
	}
	
	protected List<Bullet> getBulletList(){
		return bulletList;
	}
	
	protected boolean canAtack() {
		return canAtack;
	}
	
	public boolean canActivate() {
		return canActivate;
	}
	
	//攻撃・回復処理
	protected void atackTimer(long stopTime) {
		if(defaultWeaponStatus.get((int) Buff.ATACK) <= 0) {
			return;
		}
		int delay = getAtackSpeed();
		long initialDelay;
		if(stopTime == 0) {
			initialDelay = delay;
		}else {
			initialDelay = (stopTime - beforeAtackTime < delay)? delay - (stopTime - beforeAtackTime): 0;
			beforeAtackTime += System.currentTimeMillis() - stopTime;
		}
		atackFuture = scheduler.schedule(() -> {
			if(delay != getAtackSpeed()) {
				CompletableFuture.runAsync(() -> atackFuture.cancel(true), scheduler).thenRun(() -> atackTimer(0));
				return;
			}
			targetList = targetCheck();
			if(targetList.isEmpty()) {
				return;
			}
			beforeAtackTime = System.currentTimeMillis();
			modeChange();
			motionTimer(0);
		}, initialDelay, TimeUnit.MILLISECONDS);
	}
	
	private List<BattleData> targetCheck() {
		List<BattleData> targetList = AtackPattern.getTarget();
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//タイマー停止時に割り込みが発生する。
				//停止させるため空のListを返却する。
				return Arrays.asList();
			}
			if(!canActivate) {
				atackFuture.cancel(true);
				return Arrays.asList();
			}
			targetList = AtackPattern.getTarget();
		}while(targetList.isEmpty());
		return targetList;
	}
	
	private void modeChange() {
		canAtack = true;
		existsRight = (targetList.get(0).getPositionX() <= positionX)? true: false;
	}
	
	private void motionTimer(long stopTime) {
		int delay = 1000 * getAtackSpeed() / 50;
		long initialDelay;
		if(stopTime == 0) {
			initialDelay = 0;
		}else {
			initialDelay = (stopTime - beforeMotionTime < delay)? delay - (stopTime - beforeMotionTime): 0;
			beforeMotionTime += System.currentTimeMillis() - stopTime;
		}
		motionFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeMotionTime = System.currentTimeMillis();
			if(rightActionImage.size() - 1 <= motionNumber) {
				motionNumber = 0;
				bulletList = targetList.stream().map(i -> new Bullet(Battle, this, i, bulletImage, hitImage, scheduler)).toList();
				CompletableFuture.runAsync(this::atackProcess, scheduler);
				motionFuture.cancel(true);
				return;
			}
			motionNumber++;
		}, initialDelay, delay, TimeUnit.MICROSECONDS);
	}
	
	private void atackProcess(){
		bulletList.get(0).waitCompletion();
		targetList.stream().forEach(this::result);
		timerRestart();
		atackTimer(0);
	}
	
	protected synchronized void atackWait() {
		try {
			if(canAtack) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void timerRestart() {
		bulletList = Arrays.asList();
		canAtack = false;
		notifyAll();
	}
	
	private void result(BattleData target) {
		activateBuff(Buff.HIT, target);
		target.activateBuff(Buff.DAMAGE, this);
		if(element.stream().anyMatch(i -> i == DefaultUnit.SUPPORT)) {
			heal(target);
			return;
		}
		damage(target);
	}
	
	private void heal(BattleData target) {
		target.HPIncrease(healValue());
	}
	
	private int healValue() {
		double baseHeal = ((double) getAtack() * (100 + getCut(Buff.SUPPORT)) / 100) * (100 + moraleCorrection()) / 100;
		return (int) baseHeal;
	}
	
	private void damage(BattleData target) {
		if(getAtack() == 0 && target.getDefense() == 0) {
			return;
		}
		target.HPDecrease(damageValue(target), this);
	}
	
	private int damageValue(BattleData target) {
		double baseDamage = (Math.pow(getAtack(), 2) / (getAtack() + target.getDefense())) * (100 + moraleCorrection()) / 100;
		double cutRatio = element.stream().mapToInt(i -> target.getCut(i + 100)).sum() / element.size();
		if(100 <= cutRatio) {
			cutRatio = 100;
		}
		return (int) (baseDamage * (100 - cutRatio) / 100);
	}
	
	protected int moraleCorrection() {
		//詳細は@Overrideで記載
		return 0;
	}
	
	protected void HPIncrease(int increase) {
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
	
	private void HPDecrease(int decrease, BattleData atackUnit) {
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
	
	protected void defeat(BattleData target) {
		//詳細は@Overrideで記載
	}
	
	protected void kill() {
		//BattleUnitのみ@Overrideで記載
	}
	
	protected void healTimer(long stopTime) {
		int delay = 5000;
		long initialDelay;
		if(stopTime == 0) {
			initialDelay = 0;
		}else {
			initialDelay = (stopTime - beforeHealTime < delay)? delay - (stopTime - beforeHealTime): 0;
			beforeHealTime += System.currentTimeMillis() - stopTime;
		}
		healFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeHealTime = System.currentTimeMillis();
			if(!canActivate) {
				healFuture.cancel(true);
				return;
			}
			HPIncrease(getRecover());
		}, initialDelay, delay, TimeUnit.MILLISECONDS);
	}
	
	//バフ管理
	protected void activateBuff(double timingCode, BattleData target){
		generatedBuff.stream().filter(i -> i.getBuffTiming() == timingCode).forEach(i -> i.buffStart(target));
	}
	
	protected void activateSkillBuff() {
		List<Buff> buff = generatedBuff.stream().filter(i -> i.getBuffTiming() == Buff.SKILL).filter(this::canPossessCost).toList();
		if(buff.size() == 0) {
			return;
		}
		buff.forEach(i -> i.buffStart(null));
		GameData.consumeCost(skillCost());
	}
	
	protected int skillCost() {
		return generatedBuff.stream().mapToInt(i -> i.getCost()).max().getAsInt();
	}
	
	private boolean canPossessCost(Buff Buff) {
		return Buff.getCost() <= GameData.getCost();
	}
	
	protected void receiveBuff(Buff Buff) {
		synchronized(buffLock) {
			receivedBuff.add(Buff);
		}
	}
	
	protected void removeBuff(Buff Buff) {
		synchronized(buffLock) {
			receivedBuff.remove(Buff);
		}
	}
	
	protected double getAdditionalBuff(double statusCode) {
		//BattleUnitのみ@Overrideで記載
		return totalAdditionalBuff(0, statusCode, this);
	}
	
	protected double totalAdditionalBuff(double buff, double statusCode, BattleData BattleData) {
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
	
	protected double totalRatioBuff(double buff, double statusCode, BattleData BattleData) {
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
	
	protected void HPBuff(double buffValue) {
		HPIncrease((int) buffValue);
	}
	
	//ブロック管理
	protected void addBlock(BattleEnemy BattleEnemy) {
		synchronized(blockLock) {
			block.add(BattleEnemy);
		}
	}
	
	protected void removeBlock(BattleEnemy BattleEnemy) {
		synchronized(blockLock) {
			enemyData.stream().forEach(i -> i.block.remove(BattleEnemy));
			BattleEnemy.releaseBlock();
		}
	}
	
	protected void clearBlock() {
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
	
	public List<Integer> getElement(){
		return element;
	}
	
	public AtackPattern getAtackPattern() {
		return AtackPattern;
	}
	
	private int getAtack() {
		if(defaultWeaponStatus.get((int) Buff.ATACK) == 0) {
			return 0;
		}
		int atack = (int) (statusControl(Buff.ATACK) * awakeningCorrection());
		if(atack <= 10) {
			return 10;
		}
		return atack;
	}
	
	public int getRange() {
		int range = (int) (statusControl(Buff.RANGE) * awakeningCorrection());
		if(range <= 10) {
			return 10;
		}
		return range;
	}
	
	private int getAtackSpeed() {
		int atackSpeed = statusControl(Buff.ATACK_SPEED);
		if(atackSpeed <= 100) {
			return 100;
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
		if(HP <= 100) {
			return 100;
		}
		return HP;
	}
	
	public int getNowHP() {
		return nowHP;
	}
	
	private int getDefense() {
		int defence = (int) (statusControl(Buff.DEFENCE) * awakeningCorrection());
		if(defence <= 0) {
			return 0;
		}
		return defence;
	}
	
	private int getRecover() {
		int recover = statusControl(Buff.HEAL);
		if(recover <= 0) {
			return 0;
		}
		return recover;
	}
	
	protected int getMoveSpeedOrBlock() {
		return statusControl(Buff.MOVE_SPEED_OR_BLOCK);
	}
	
	protected int getCost() {
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
	
	private int getCut(double number) {
		int cut = statusControl(number);
		if(cut <= 0) {
			return 0;
		}
		return cut;
	}
	
	public List<Integer> getCut(){
		return IntStream.range(100, defaultCutStatus.size() + 100).mapToObj(i -> getCut(i)).toList();
	}
	
	private int statusControl(double number) {
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
	
	private int calculate(int initialValue, double additionalValue, double ratio) {
		return (int) ((initialValue + additionalValue) * ratio);
	}
	
	protected double awakeningCorrection() {
		//BattleUnitのみ@Overrideで記載
		return 1;
	}
}