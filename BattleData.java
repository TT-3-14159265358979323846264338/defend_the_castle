package battle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultdata.EditImage;
import defaultdata.atackpattern.AtackPattern;

//全キャラクターの共通システム
public class BattleData{
	Battle Battle;
	GameData GameData;
	List<BattleData> allyData;
	List<BattleData> enemyData;
	List<BufferedImage> rightActionImage;
	List<BufferedImage> leftActionImage;
	BufferedImage bulletImage;
	List<BufferedImage> hitImage;
	List<Bullet> bulletList = Arrays.asList();
	boolean existsRight = true;
	int motionNumber = 0;
	boolean canAtack;
	String name;
	double positionX;
	double positionY;
	List<Integer> element;
	AtackPattern AtackPattern;
	List<Integer> defaultWeaponStatus;
	List<Integer> defaultUnitStatus;
	List<Integer> defaultCutStatus;
	List<Integer> collectionWeaponStatus;
	List<Integer> collectionUnitStatus;
	List<Integer> collectionCutStatus;
	List<Double> ratioWeaponStatus;
	List<Double> ratioUnitStatus;
	List<Double> ratioCutStatus;
	int nowHP;
	boolean canActivate;
	List<BattleEnemy> block = new ArrayList<>();
	
	protected void initialize() {
		leftActionImage = rightActionImage.stream().map(i -> EditImage.mirrorImage(i)).toList();
		collectionWeaponStatus = defaultWeaponStatus.stream().map(i -> 0).collect(Collectors.toList());
		collectionUnitStatus = defaultUnitStatus.stream().map(i -> 0).collect(Collectors.toList());
		collectionCutStatus = defaultCutStatus.stream().map(i -> 0).collect(Collectors.toList());
		ratioWeaponStatus = defaultWeaponStatus.stream().map(i -> 1.0).collect(Collectors.toList());
		ratioUnitStatus = defaultUnitStatus.stream().map(i -> 1.0).collect(Collectors.toList());
		ratioCutStatus = defaultCutStatus.stream().map(i -> 1.0).collect(Collectors.toList());
		nowHP = unitCalculate(1);
	}
	
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
		if(getAtack() == 0) {
			buff(target);
			return;
		}
		if(element.stream().anyMatch(i -> i == 11)) {
			heal(target);
			return;
		}
		damage(target);
	}
	
	private void buff(BattleData target) {
		
		
		
		
		
		
	}
	
	private void heal(BattleData target) {
		int healValue = (getAtack() * (100 + getCut(11)) / 100) * (100 + moraleRatio()) / 100 + target.nowHP;
		target.nowHP = (target.getMaxHP() < healValue)? target.getMaxHP(): healValue;
	}
	
	private void damage(BattleData target) {
		if(getAtack() == 0 && target.getDefense() == 0) {
			return;
		}
		double baseDamage = (Math.pow(getAtack(), 2) / (getAtack() + target.getDefense())) * (100 + moraleRatio()) / 100;
		double cutRatio = element.stream().mapToInt(i -> target.getCut(i)).sum() / element.size();
		if(100 <= cutRatio) {
			cutRatio = 100;
		}
		target.nowHP -= (int) (baseDamage * (100 - cutRatio) / 100);
		if(target.nowHP <= 0 && canActivate) {
			target.defeat();
		}
	}
	
	protected int moraleRatio() {
		//詳細は@Overrideで記載
		return 0;
	}
	
	protected void defeat() {
		//詳細は@Overrideで記載
	}
	
	protected void healTimer() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(!canActivate) {
				scheduler.shutdown();
				return;
			}
			int healValue = nowHP + getRecover();
			nowHP = (getMaxHP() < healValue)? getMaxHP(): healValue;
		}, 0, 5, TimeUnit.SECONDS);
	}
	
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
		return weaponCalculate(0);
	}
	
	public int getRange() {
		return weaponCalculate(1);
	}
	
	private int getAtackSpeed() {
		return weaponCalculate(2);
	}
	
	public int getAtackNumber() {
		return weaponCalculate(3);
	}
	
	protected int getMaxHP() {
		return unitCalculate(0);
	}
	
	public int getNowHP() {
		return nowHP;
	}
	
	private int getDefense() {
		return unitCalculate(2);
	}
	
	private int getRecover() {
		return unitCalculate(3);
	}
	
	protected int getMoveSpeedOrBlock() {
		return unitCalculate(4);
	}
	
	protected int getCost() {
		return unitCalculate(5);
	}
	
	public List<Integer> getWeapon(){
		return IntStream.range(0, defaultWeaponStatus.size()).mapToObj(i -> weaponCalculate(i)).toList();
	}
	
	private int weaponCalculate(int number) {
		return calculate(defaultWeaponStatus.get(number), collectionWeaponStatus.get(number), ratioWeaponStatus.get(number));
	}
	
	public List<Integer> getUnit(){
		return IntStream.range(0, defaultUnitStatus.size()).mapToObj(i -> unitCalculate(i)).toList();
	}
	
	private int unitCalculate(int number) {
		return calculate(defaultUnitStatus.get(number), collectionUnitStatus.get(number), ratioUnitStatus.get(number));
	}
	
	public List<Integer> getCut(){
		return IntStream.range(0, defaultCutStatus.size()).mapToObj(i -> getCut(i)).toList();
	}
	
	private int getCut(int number) {
		return calculate(defaultCutStatus.get(number), collectionCutStatus.get(number), ratioCutStatus.get(number));
	}
	
	private int calculate(int fixedValue, int flexValue, double ratio) {
		return (int) ((fixedValue + flexValue) * ratio);
	}
	
	protected List<BattleEnemy> getBlock(){
		return block;
	}
	
	protected void addBlock(BattleEnemy BattleEnemy) {
		block.add(BattleEnemy);
	}
	
	protected void removeBlock(BattleEnemy BattleEnemy) {
		BattleEnemy.releaseBlock();
		enemyData.stream().forEach(i -> i.getBlock().remove(BattleEnemy));
	}
	
	protected void clearBlock() {
		block.stream().forEach(i -> i.releaseBlock());
		block.clear();
	}
}