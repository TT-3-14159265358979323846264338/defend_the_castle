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
	Battle Battle;
	GameData GameData;
	List<BattleData> allyData;
	List<BattleData> enemyData;
	
	AtackPattern AtackPattern;
	boolean canAtack;
	boolean existsRight = true;
	int motionNumber = 0;
	List<BufferedImage> rightActionImage;
	List<BufferedImage> leftActionImage;
	BufferedImage bulletImage;
	List<BufferedImage> hitImage;
	List<Bullet> bulletList = Arrays.asList();
	
	List<List<Double>> generatedBuffInformation;
	List<Buff> generatedBuff;
	List<Buff> receivedBuff = new ArrayList<>();
	
	String name;
	boolean canActivate;
	int nowHP;
	double positionX;
	double positionY;
	List<Integer> element;
	List<Integer> defaultWeaponStatus;
	List<Integer> defaultUnitStatus;
	List<Integer> defaultCutStatus;
	List<BattleEnemy> block = new ArrayList<>();
	
	Object buffLock = new Object();
	Object blockLock = new Object();
	Object HPLock = new Object();
	
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
	
	protected boolean getAtackMotion() {
		return canAtack;
	}
	
	public boolean getActivate() {
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
		if(element.stream().anyMatch(i -> i == 11)) {
			heal(target);
			return;
		}
		damage(target);
	}
	
	private void heal(BattleData target) {
		target.HPDecrease(- healValue());
	}
	
	private int healValue() {
		return (getAtack() * (100 + getCut(Buff.SUPPORT)) / 100) * (100 + moraleRatio()) / 100;
	}
	
	private void damage(BattleData target) {
		if(getAtack() == 0 && target.getDefense() == 0) {
			return;
		}
		target.HPDecrease(damageValue(target));
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
			HPDecrease(- getRecover());
		}, 0, 5, TimeUnit.SECONDS);
	}
	
	//被バフ管理
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
	
	private double additionalBuff(double statusCode) {
		double buff = 0;
		for(Buff i: receivedBuffList()){
			if(i.buffStatusCode() == statusCode) {
				buff = i.additionalEffect(this, buff);
			}
		}
		return buff;
	}
	
	private double ratioBuff(double statusCode) {
		double buff = 1;
		for(Buff i: receivedBuffList()){
			if(i.buffStatusCode() == statusCode) {
				buff = i.ratioEffect(this, buff);
			}
		}
		return buff;
	}
	
	protected List<Buff> receivedBuffList(){
		//BattleUnitのみ@Overrideで記載
		return receivedBuff;
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
		return statusControl(Buff.POWER);
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
	
	private void HPDecrease(int decrease) {
		synchronized(HPLock) {
			nowHP -= decrease;
			if(nowHP <= 0 && canActivate) {
				defeat();
				return;
			}
			if(getMaxHP() < nowHP) {
				nowHP = getMaxHP();
			}
		}
	}
	
	protected void defeat() {
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
			return calculate(defaultWeaponStatus.get((int) number), additionalBuff(number), ratioBuff(number));
		}
		if(number < 100) {
			return calculate(defaultUnitStatus.get((int) number - 10), additionalBuff(number), ratioBuff(number));
		}
		if(number < 1000) {
			return calculate(defaultCutStatus.get((int) number - 100), additionalBuff(number), ratioBuff(number));
		}
		return 0;
	}
	
	private int calculate(int initialValue, double additionalValue, double ratio) {
		return (int) ((initialValue + additionalValue) * ratio);
	}
}