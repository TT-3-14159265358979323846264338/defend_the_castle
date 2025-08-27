package battle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import defaultdata.EditImage;
import defaultdata.atackpattern.AtackPattern;

//全キャラクターの共通システム
public class BattleData{
	protected Battle Battle;
	protected GameData GameData;
	protected List<BattleData> allyData;
	protected List<BattleData> enemyData;
	
	protected AtackPattern AtackPattern;
	protected boolean existsRight = true;
	private boolean canAtack;
	private int motionNumber = 0;
	protected List<BufferedImage> rightActionImage;
	protected List<BufferedImage> leftActionImage;
	protected BufferedImage bulletImage;
	protected List<BufferedImage> hitImage;
	private List<Bullet> bulletList = Arrays.asList();
	
	protected List<List<Double>> generatedBuffInformation;
	protected List<Buff> generatedBuff = new ArrayList<>();
	protected List<Buff> receivedBuff = new ArrayList<>();
	
	protected String name;
	protected boolean canActivate;
	protected int nowHP;
	protected double positionX;
	protected double positionY;
	protected List<Integer> element;
	protected List<Integer> defaultWeaponStatus;
	protected List<Integer> defaultUnitStatus;
	protected List<Integer> defaultCutStatus;
	protected List<BattleEnemy> block = new ArrayList<>();
	
	private Object buffLock = new Object();
	private Object blockLock = new Object();
	private Object HPLock = new Object();
	
	protected void initialize() {
		leftActionImage = rightActionImage.stream().map(i -> EditImage.mirrorImage(i)).toList();
		nowHP = defaultUnitStatus.get(0);
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
	protected void atackTimer() {
		int nowSpeed = getAtackSpeed();
		if(nowSpeed <= 0) {
			return;
		}
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			if(nowSpeed != getAtackSpeed()) {
				atackTimer();
				scheduler.shutdown();
				return;
			}
			List<BattleData> targetList = targetCheck();
			if(targetList.isEmpty()) {
				scheduler.shutdown();
				return;
			}
			modeChange(targetList);
			motionTimer(targetList);
			timerWait();
		}, 0, getAtackSpeed(), TimeUnit.MILLISECONDS);
	}
	
	private List<BattleData> targetCheck() {
		List<BattleData> targetList = AtackPattern.getTarget();
		do {
			Battle.timerWait();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!canActivate) {
				return Arrays.asList();
			}
			targetList = AtackPattern.getTarget();
		}while(targetList.isEmpty());
		return targetList;
	}
	
	private void modeChange(List<BattleData> targetList) {
		canAtack = true;
		existsRight = (targetList.get(0).getPositionX() <= positionX)? true: false;
	}
	
	private void motionTimer(List<BattleData> targetList) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(rightActionImage.size() - 1 <= motionNumber) {
				motionNumber = 0;
				bulletList = targetList.stream().map(i -> new Bullet(Battle, this, i, bulletImage, hitImage)).toList();
				CompletableFuture.allOf(atackProcess()).join();
				timerRestart();
				scheduler.shutdown();
				return;
			}
			motionNumber++;
		}, 0, 1000 * getAtackSpeed() / 50, TimeUnit.MICROSECONDS);
	}
	
	private CompletableFuture<?>[] atackProcess(){
		return bulletList.stream().map(Bullet -> CompletableFuture.supplyAsync(Bullet::waitCompletion).thenAccept(this::result)).toArray(CompletableFuture[]::new);
	}
	
	protected synchronized void timerWait() {
		if(canAtack) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void timerRestart() {
		notifyAll();
		bulletList = Arrays.asList();
		canAtack = false;
	}
	
	private void result(BattleData target) {
		activateBuff(Buff.HIT, target);
		target.activateBuff(Buff.DAMAGE, this);
		if(element.stream().anyMatch(i -> i == 11)) {
			heal(target);
			return;
		}
		damage(target);
	}
	
	private void heal(BattleData target) {
		target.HPIncrease(healValue());
	}
	
	private int healValue() {
		return (getAtack() * (100 + getCut(Buff.SUPPORT)) / 100) * (100 + moraleRatio()) / 100;
	}
	
	private void damage(BattleData target) {
		if(getAtack() == 0 && target.getDefense() == 0) {
			return;
		}
		target.HPDecrease(damageValue(target), this);
	}
	
	private int damageValue(BattleData target) {
		double baseDamage = (Math.pow(getAtack(), 2) / (getAtack() + target.getDefense())) * (100 + moraleRatio()) / 100;
		double cutRatio = element.stream().mapToInt(i -> target.getCut(i + 100)).sum() / element.size();
		if(100 <= cutRatio) {
			cutRatio = 100;
		}
		return (int) (baseDamage * (100 - cutRatio) / 100);
	}
	
	protected int moraleRatio() {
		//詳細は@Overrideで記載
		return 0;
	}
	
	protected void healTimer() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(!canActivate) {
				scheduler.shutdown();
				return;
			}
			HPIncrease(getRecover());
		}, 0, 5, TimeUnit.SECONDS);
	}
	
	//バフ管理
	protected void activateBuff(double timingCode, BattleData target){
		generatedBuff.stream().filter(i -> i.getBuffTiming() == timingCode).forEach(i -> i.buffStart(target));
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
	
	protected void HPIncrease(int increase) {
		synchronized(HPLock) {
			nowHP += increase;
			if(getMaxHP() < nowHP) {
				nowHP = getMaxHP();
			}
		}
	}
	
	private void HPDecrease(int decrease, BattleData target) {
		synchronized(HPLock) {
			nowHP -= decrease;
			if(nowHP <= 0 && canActivate) {
				defeat(target);
				return;
			}
		}
	}
	
	protected void defeat(BattleData target) {
		//詳細は@Overrideで記載
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