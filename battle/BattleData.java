package battle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
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
	private ScheduledExecutorService atackScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> atackFuture;
	private long beforeAtackTime;
	private ScheduledExecutorService motionScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> motionFuture;
	private long beforeMotionTime;
	private ScheduledExecutorService healScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> healFuture;
	private long beforeHealTime;
	
	protected void initialize() {
		leftActionImage = rightActionImage.stream().map(i -> EditImage.mirrorImage(i)).toList();
		nowHP = defaultUnitStatus.get(1);
	}
	
	protected void schedulerEnd() {
		atackScheduler.shutdown();
		motionScheduler.shutdown();
		healScheduler.shutdown();
		generatedBuff.stream().forEach(i -> i.schedulerEnd());
		individualSchedulerEnd();
	}
	
	protected void individualSchedulerEnd() {
		//詳細は@Overrideで記載
	}
	
	protected void futureStop() {
		if(atackFuture != null && !atackFuture.isCancelled() && !canAtack) {
			atackFuture.cancel(true);
			long atackTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait).thenRun(() -> atackTimer(atackTime));
		}
		if(motionFuture != null && !motionFuture.isCancelled()) {
			motionFuture.cancel(true);
			long motionTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait).thenRun(() -> motionTimer(motionTime));
		}
		if(healFuture != null && !healFuture.isCancelled()) {
			healFuture.cancel(true);
			long healTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait).thenRun(() -> healTimer(healTime));
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
		int delay = getAtackSpeed();
		if(delay <= 0) {
			return;
		}
		long initialDelay;
		if(stopTime == 0) {
			initialDelay = delay;
		}else {
			initialDelay = (stopTime - beforeAtackTime < delay)? delay - (stopTime - beforeAtackTime): 0;
			beforeAtackTime += System.currentTimeMillis() - stopTime;
		}
		atackFuture = atackScheduler.schedule(() -> {
			if(delay != getAtackSpeed()) {
				CompletableFuture.runAsync(() -> atackFuture.cancel(true)).thenRun(() -> atackTimer(0));
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
		motionFuture = motionScheduler.scheduleAtFixedRate(() -> {
			beforeMotionTime = System.currentTimeMillis();
			if(rightActionImage.size() - 1 <= motionNumber) {
				motionNumber = 0;
				bulletList = targetList.stream().map(i -> new Bullet(Battle, this, i, bulletImage, hitImage)).toList();
				CompletableFuture.allOf(atackProcess()).thenRun(this::timerRestart).thenRun(() -> atackTimer(0));
				motionFuture.cancel(true);
				return;
			}
			motionNumber++;
		}, initialDelay, delay, TimeUnit.MICROSECONDS);
	}
	
	private CompletableFuture<?>[] atackProcess(){
		return bulletList.stream().map(Bullet -> CompletableFuture.supplyAsync(Bullet::waitCompletion).thenAccept(this::result)).toArray(CompletableFuture[]::new);
	}
	
	protected synchronized void timerWait() {
		try {
			wait();
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
		return (int) (awakeningCorrection() * baseDamage * (100 - cutRatio) / 100);
	}
	
	protected int moraleCorrection() {
		//詳細は@Overrideで記載
		return 0;
	}
	
	protected double awakeningCorrection() {
		//BattleUnitのみ@Overrideで記載
		return 1;
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
			if(nowHP <= 0 && canActivate) {
				defeat(atackUnit);
				atackUnit.activateBuff(Buff.KILL, this);
				atackUnit.kill();
				return;
			}
		}
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
		healFuture = healScheduler.scheduleAtFixedRate(() -> {
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
		if(buff.	size() == 0) {
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
	protected List<BattleEnemy> getBlock(){
		return block;
	}
	
	protected void addBlock(BattleEnemy BattleEnemy) {
		synchronized(blockLock) {
			block.add(BattleEnemy);
		}
	}
	
	protected void removeBlock(BattleEnemy BattleEnemy) {
		BattleEnemy.releaseBlock();
		enemyData.stream().forEach(i -> i.getBlock().remove(BattleEnemy));
	}
	
	protected void clearBlock() {
		block.stream().forEach(i -> i.releaseBlock());
		block.clear();
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
		return statusControl(Buff.ATACK);
	}
	
	public int getRange() {
		return statusControl(Buff.RANGE);
	}
	
	private int getAtackSpeed() {
		return statusControl(Buff.ATACK_SPEED);
	}
	
	public int getAtackNumber() {
		return statusControl(Buff.ATACK_NUMBER);
	}
	
	public List<Integer> getWeapon(){
		return IntStream.range(0, defaultWeaponStatus.size()).mapToObj(i -> statusControl(i)).toList();
	}
	
	protected int getMaxHP() {
		return statusControl(Buff.HP);
	}
	
	public int getNowHP() {
		return nowHP;
	}
	
	private int getDefense() {
		return statusControl(Buff.DEFENCE);
	}
	
	private int getRecover() {
		return statusControl(Buff.HEAL);
	}
	
	protected int getMoveSpeedOrBlock() {
		return statusControl(Buff.MOVE_SPEED_OR_BLOCK);
	}
	
	protected int getCost() {
		return statusControl(Buff.COST);
	}
	
	public List<Integer> getUnit(){
		return IntStream.range(10, defaultUnitStatus.size() + 10).mapToObj(i -> statusControl(i)).toList();
	}
	
	private int getCut(double number) {
		return statusControl(number);
	}
	
	public List<Integer> getCut(){
		return IntStream.range(100, defaultCutStatus.size() + 100).mapToObj(i -> statusControl(i)).toList();
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
}