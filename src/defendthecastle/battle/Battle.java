package defendthecastle.battle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import commonclass.CommonJPanel;
import defaultdata.Distance;
import defaultdata.Stage;
import defendthecastle.MainFrame;
import defendthecastle.battle.battledialog.PauseDialog;
import defendthecastle.screendisplay.DisplayStatus;
import savedata.OneUnitData;
import savedata.SaveComposition;
import savedata.SaveSelect;

//バトル画面制御
public class Battle extends CommonJPanel implements MouseListener, MouseMotionListener{
	public static final int SIZE = 28;
	
	//ゲームデータ
	private final MainFrame mainFrame;
	private final Stage stage;
	private final double difficultyCorrection;
	private final GameTimer gameTimer;
	private final GameData gameData;
	private final StageImage stageImage;
	private final AwakeUnit awakeUnit;
	private final BattleUnit[] unitMainData;//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	private final BattleUnit[] unitLeftData;//左武器用
	private final BattleFacility[] facilityData;
	private final BattleEnemy[] enemyData;
	
	//表示パーツ
	private final JLabel costLabel = new JLabel();
	private final JLabel[] awakeLabel;
	private final JButton rangeDrawButton = new JButton();
	private final JButton autoAwakeningButton = new JButton();
	private final JButton stageReturnButton = new JButton();
	private final JButton statusButton = new UnitButton();
	private final JButton retreatButton = new UnitButton();
	private final JButton awakeningButton = new UnitButton();
	private final JButton unitReturnButton = new UnitButton();
	private final Color rangeRed = new Color(255, 0, 0, 20);
	private final Color rangeBlue = new Color(0, 0, 255, 20);
	private final Color placeRed = new Color(255, 220, 220);
	private final Color placeBlue = new Color(220, 220, 255);
	private final Color recastGray = new Color(128, 128, 128, 125);
	private final Color recastWhite = new Color(255, 255, 255, 125);
	private final Font costFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font menuFont = new Font("ＭＳ ゴシック", Font.BOLD, 12);
	private final Font unitFont = new Font("ＭＳ ゴシック", Font.BOLD, 10);
	private final Font skillFont = new Font("Ravie", Font.BOLD, 30);
	
	//操作関連
	private Point mouse;
	private int select;
	private boolean canSelect;
	private boolean canRangeDraw;
	
	//メイン画面制御
	public Battle(MainFrame mainFrame, ScheduledExecutorService scheduler, Stage stage, double difficultyCorrection) {
		this.mainFrame = mainFrame;
		this.stage = stage;
		this.difficultyCorrection = difficultyCorrection;
		addMouseListener(this);
		addMouseMotionListener(this);
		gameTimer = createGameTimer(scheduler);
		gameData = createGameData();
		List<OneUnitData> unitList = createOneCompositionData();
		unitMainData = createBattleData(unitList, number -> createMainBattleUnit(unitList, number, scheduler), BattleUnit[]::new);
		unitLeftData = createBattleData(unitList, number -> createLeftBattleUnit(unitList, number, scheduler), BattleUnit[]::new);
		facilityData = createBattleData(stage.getStageData().getFacility(), number -> createBattleFacility(number, scheduler), BattleFacility[]::new);
		enemyData = createBattleData(stage.getStageData().getEnemy(), number -> createBattleEnemy(number, scheduler), BattleEnemy[]::new);
		install();
		stageImage = createStageImage();
		awakeUnit = createAwakeUnit(scheduler);
		setCostLabel();
		awakeLabel = IntStream.range(0, unitMainData.length).mapToObj(_ -> new AwakeLabel()).toArray(JLabel[]::new);
		setButton(rangeDrawButton, rangeText(), 1010, 465, 95, 40, menuFont, this::rangeDrawButtonAction);
		setButton(autoAwakeningButton, awakeText(), 1115, 465, 95, 40, menuFont, this::autoAwakeningButtonAction);
		setButton(stageReturnButton, "一時停止", 1010, 515, 200, 40, menuFont, this::stageReturnButtonAction);
		setUnitButton(statusButton, "能力", this::statusButtonAction);
		setUnitButton(retreatButton, "撤退", this::retreatButtonAction);
		setUnitButton(awakeningButton, "覚醒", this::awakeningButtonAction);
		setUnitButton(unitReturnButton, "戻る", this::unitReturnButtonAction);
		movie(scheduler, brown());
		gameTimer.timerStart(gameData, awakeUnit, stageImage, unitMainData, unitLeftData, facilityData, enemyData);
	}
	
	GameTimer createGameTimer(ScheduledExecutorService scheduler) {
		return new GameTimer(mainFrame, stage, difficultyCorrection, scheduler);
	}
	
	GameData createGameData() {
		return new GameData(this, stage.getStageData());
	}
	
	List<OneUnitData> createOneCompositionData() {
		SaveComposition saveComposition = createSaveComposition();
		saveComposition.load();
		SaveSelect saveSelect = createSaveSelect();
		saveSelect.load();
		return saveComposition.getOneCompositionData(saveSelect.getCompositionSelectNumber()).getOneUnitDataList();
	}
	
	SaveComposition createSaveComposition() {
		return new SaveComposition();
	}
	
	SaveSelect createSaveSelect() {
		return new SaveSelect();
	}
	
	<T>T[] createBattleData(List<?> list, IntFunction<T> method, IntFunction<T[]> instance){
		return IntStream.range(0, list.size()).mapToObj(method).toArray(instance);
	}
	
	BattleUnit createMainBattleUnit(List<OneUnitData> unitList, int number, ScheduledExecutorService scheduler) {
		return new BattleUnit(gameTimer, unitList.get(number), initialX(number), initialY(number), scheduler);
	}
	
	BattleUnit createLeftBattleUnit(List<OneUnitData> unitList, int number, ScheduledExecutorService scheduler) {
		return new BattleUnit(gameTimer, unitList.get(number), scheduler);
	}
	
	BattleFacility createBattleFacility(int number, ScheduledExecutorService scheduler) {
		return new BattleFacility(gameTimer, stage.getStageData(), number, scheduler);
	}
	
	BattleEnemy createBattleEnemy(int number, ScheduledExecutorService scheduler) {
		return new BattleEnemy(gameTimer, stage.getStageData(), number, difficultyCorrection, scheduler);
	}
	
	void install() {
		IntStream.range(0, unitMainData.length).forEach(i -> unitMainData[i].install(gameData, unitLeftData[i], unitMainData, facilityData, enemyData));
		IntStream.range(0, unitLeftData.length).forEach(i -> unitLeftData[i].install(gameData, unitMainData[i], unitMainData, facilityData, enemyData));
		Stream.of(facilityData).forEach(i -> i.install(gameData, unitMainData, facilityData, enemyData));
		Stream.of(enemyData).forEach(i -> i.install(gameData, unitMainData, facilityData, enemyData));
	}
	
	StageImage createStageImage(){
		return new StageImage(stage.getStageData(), enemyData, gameTimer);
	}
	
	AwakeUnit createAwakeUnit(ScheduledExecutorService scheduler){
		return new AwakeUnit(this, gameData, unitMainData, unitLeftData, scheduler);
	}
	
	String rangeText() {
		return canRangeDraw? "射程表示": "射程なし";
	}
	
	String awakeText() {
		return awakeUnit.canAutoAwake()? "自動覚醒": "手動覚醒";
	}
	
	void setCostLabel() {
		setLabel(costLabel, costText(), 1010, 15, 200, 30, costFont);
		costLabel.setBackground(Color.WHITE);
		costLabel.setOpaque(true);
		costLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	void setCostText() {
		SwingUtilities.invokeLater(() -> costLabel.setText(costText()));
	}
	
	String costText() {
		return String.format("コスト: %d", gameData.getCost());
	}
	
	void setAwakeLabel(int number) {
		SwingUtilities.invokeLater(() -> {
			if(awakeLabel[number].getParent() == null) {
				awakeLabel[number].setLocation(unitMainData[number].getPositionX() + 25, unitMainData[number].getPositionY() + 10);
				add(awakeLabel[number]);
			}else {
				remove(awakeLabel[number]);
			}
		});
	}
	
	void rangeDrawButtonAction(ActionEvent e){
		canRangeDraw = canRangeDraw? false: true;
		rangeDrawButton.setText(rangeText());
	}
	
	void autoAwakeningButtonAction(ActionEvent e){
		awakeUnit.changeAutoAwake();
		autoAwakeningButton.setText(awakeText());
	}
	
	void stageReturnButtonAction(ActionEvent e){
		gameTimer.timerStop();
		new PauseDialog(gameTimer, mainFrame, stage, difficultyCorrection);
	}
	
	void setUnitButton(JButton button, String name, ActionListener task) {
		button.setText(name);
		button.setFont(unitFont);
		button.addActionListener(task);
	}
	
	int initialX(int i) {
		return 1015 + i % 2 * 100;
	}
	
	int initialY(int i) {
		return 55 + i / 2 * 100;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawField(g);
		drawEnemy(g);
		drawCompositionBackground(g);
		drawSkill(g);
		drawUnit(g);
		drawAwake(g);
		drawBullet(g);
		drawSelectUnit(g);
		drawMorale(g);
		//ゲーム時間表示(ステージ調整用)
		g.drawString(gameTimer.getSecondsTime() + "s", 980, 30);
	}
	
	void drawField(Graphics g) {
		g.drawImage(stageImage.getStageImage(), 0, 0, this);
		IntStream.range(0, stageImage.getPlacementSize()).forEach(i -> IntStream.range(0, stageImage.getPlacementList(i).size())
				.filter(j -> stageImage.getUsePlacementList(i).get(j))
				.forEach(j -> g.drawImage(stageImage.getPlacementImage(i), stageImage.getPlacementList(i).get(j).get(0).intValue(), stageImage.getPlacementList(i).get(j).get(1).intValue(), this)));
		IntStream.range(0, facilityData.length).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, rangeRed, facilityData[i].getPositionX(), facilityData[i].getPositionY(), facilityData[i].getRange());
			}
			g.drawImage(facilityData[i].canActivate()? facilityData[i].getActionImage(): facilityData[i].getBreakImage(), facilityData[i].getPositionX(), facilityData[i].getPositionY(), this);
			if(facilityData[i].canActivate()) {
				drawHP(g, facilityData[i], Color.BLUE);
			}
		});
	}
	
	void drawEnemy(Graphics g) {
		IntStream.range(0, enemyData.length).filter(i -> enemyData[i].canActivate()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, rangeRed, enemyData[i].getPositionX(), enemyData[i].getPositionY(), enemyData[i].getRange());
			}
			g.drawImage(enemyData[i].getActionImage(), enemyData[i].getPositionX(), enemyData[i].getPositionY(), this);
			drawHP(g, enemyData[i], Color.RED);
		});
	}
	
	void drawCompositionBackground(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			switch(unitMainData[i].getType()) {
			case Distance.NEAR:
				g.setColor(placeRed);
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 100, 100);
				break;
			case Distance.FAR:
				g.setColor(placeBlue);
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 100, 100);
				break;
			case Distance.ALL:
				g.setColor(placeRed);
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 50, 100);
				g.setColor(placeBlue);
				g.fillRect(initialX(i) + 45, initialY(i) - 5, 50, 100);
				break;
			default:
				break;
			}
		});
		g.setColor(Color.BLACK);
		IntStream.range(0, 3).forEach(i -> g.drawLine(1010 + i * 100, 50, 1010 + i * 100, 450));
		IntStream.range(0, 5).forEach(i -> g.drawLine(1010, 50 + i * 100, 1210, 50 + i * 100));
	}
	
	void drawSkill(Graphics g) {
		Stream.of(unitMainData).filter(i -> i.canActivate() && i.canPossessSkill()).forEach(i -> skill(g, i));
	}
	
	void skill(Graphics g, BattleUnit BattleUnit) {
		int x = BattleUnit.getInitialPosition().x;
		int y = BattleUnit.getInitialPosition().y;
		if(BattleUnit.canRecast()) {
			g.drawImage(BattleUnit.getSkillImage(), x, y, this);
			g.setColor(Color.RED);
			g.setFont(skillFont);
			g.drawString("" + BattleUnit.skillCost(), x + 50, y + 80);
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.GRAY);
		g2.fillOval(x, y, 90, 90);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fill(new Arc2D.Double(x, y, 90, 90, 90, 360 * BattleUnit.recastRatio(), Arc2D.PIE));
	}
	
	void drawUnit(Graphics g) {
		IntStream.range(0, unitMainData.length).forEach(i -> {
			if(unitMainData[i].canActivate() && canRangeDraw) {
				rangeDraw(g, rangeRed, unitMainData[i].getPositionX(), unitMainData[i].getPositionY(), unitMainData[i].getRange());
				rangeDraw(g, rangeBlue, unitLeftData[i].getPositionX(), unitLeftData[i].getPositionY(), unitLeftData[i].getRange());
			}
			int x = unitMainData[i].getPositionX();
			int y = unitMainData[i].getPositionY();
			g.drawImage(unitMainData[i].getActionImage(), x, y, this);
			g.drawImage(unitMainData[i].getCoreImage(), x, y, this);
			g.drawImage(unitLeftData[i].getActionImage(), x, y, this);
			if(unitMainData[i].canActivate()) {
				drawHP(g, unitMainData[i], Color.BLUE);
			}else {
				if(!unitMainData[i].canLocate()) {
					drawRelocation(g, unitMainData[i]);
				}
			}
		});
	}
	
	void drawRelocation(Graphics g, BattleUnit BattleUnit) {
		int x = BattleUnit.getPositionX();
		int y = BattleUnit.getPositionY();
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(recastGray);
		g2.fillOval(x, y, 90, 90);
		g2.setColor(recastWhite);
		g2.fill(new Arc2D.Double(x, y, 90, 90, 90, 360 * BattleUnit.locationRatio(), Arc2D.PIE));
	}
	
	void drawAwake(Graphics g) {
		if(awakeUnit.hasAwaked()) {
			int x = awakeUnit.unitPositionX();
			int y = awakeUnit.unitPositionY();
			g.setColor(Color.RED);
			g.fillRect(x + 15, y + 30, 10, 30);
			g.fillPolygon(new int[] {x + 10, x + 20, x + 30}, new int[] {y + 40, y + 20, y + 40}, 3);
			g.fillRect(x + 60, y + 30, 10, 30);
			g.fillPolygon(new int[] {x + 55, x + 65, x + 75}, new int[] {y + 40, y + 20, y + 40}, 3);
		}
	}
	
	void drawBullet(Graphics g) {
		drawBullet(g, unitMainData);
		drawBullet(g, unitLeftData);
		drawBullet(g, facilityData);
		drawBullet(g, enemyData);
	}
	
	void drawBullet(Graphics g, BattleData[] BattleData) {
		Stream.of(BattleData).filter(i -> i.canAtack()).forEach(i -> i.getBulletList().stream().forEach(j -> g.drawImage(j.getImage(), j.getPsitionX(), j.getPsitionY(), this)));
	}
	
	void drawSelectUnit(Graphics g) {
		if(canSelect) {
			int x = mouse.x - 45;
			int y = mouse.y - 45;
			rangeDraw(g, rangeRed, x, y, unitMainData[select].getRange());
			rangeDraw(g, rangeBlue, x, y, unitLeftData[select].getRange());
			g.drawImage(unitMainData[select].getDefaultImage(), x, y, this);
			g.drawImage(unitMainData[select].getDefaultCoreImage(), x, y, this);
			g.drawImage(unitLeftData[select].getDefaultImage(), x, y, this);
		}
	}
	
	void rangeDraw(Graphics g, Color color, int x, int y, int range) {
		int correction = 45;
		g.setColor(color);
		g.fillOval(x + correction - range, y + correction - range, range * 2, range * 2);
	}
	
	void drawHP(Graphics g, BattleData BattleData, Color color) {
		int x = BattleData.getPositionX() + 30;
		int y = BattleData.getPositionY() + 60;
		int height = 5;
		g.setColor(Color.BLACK);
		g.fillRect(x, y, SIZE, height);
		g.setColor(color);
		g.fillRect(x, y, SIZE * BattleData.getNowHP() / BattleData.getMaxHP(), height);
		g.setColor(Color.WHITE);
		g.drawRect(x, y, SIZE, height);
	}
	
	void drawMorale(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(25, 525, 950, 30);
		int moralePosition = 475 + gameData.getMoraleDifference() * 2;
		if(moralePosition <= 50) {
			moralePosition = 50;
		}else if(900 <= moralePosition) {
			moralePosition = 900;
		}
		g.setColor(Color.RED);
		g.fillRect(25, 525, moralePosition, 30);
		g.setColor(Color.BLACK);
		g.fillPolygon(new int[] {490, 500, 510}, new int[] {500, 525, 500}, 3);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(canSelect) {
			mouse = e.getPoint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		actionInitialize();
		if(hasClicked(e, unitMainData, this::unitOperation, targetCheck())) {
			return;
		}
		if(canActivateSkill(e)) {
			return;
		}
		if(hasClicked(e, facilityData, this::facilityStatus, targetCheck(facilityData))) {
			return;
		}
		hasClicked(e, enemyData, this::enemyStatus, targetCheck(enemyData));
	}
	@Override
	public void mousePressed(MouseEvent e) {
		actionInitialize();
		mouse = e.getPoint();
		List<Integer> activeUnit = IntStream.range(0, unitMainData.length).filter(i -> !unitMainData[i].canActivate()).boxed().toList();
		for(int i: activeUnit) {
			int x = initialX(i) + 30;
			int y = initialY(i) + 30;
			if(existsTarget(x, y, SIZE, e)) {
				if(unitMainData[i].getCost() <= gameData.getCost() && unitMainData[i].canLocate()) {
					select = i;
					canSelect = true;
				}
				return;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(canSelect) {
			mouse = e.getPoint();
			switch(unitMainData[select].getType()) {
			case Distance.NEAR:
				placeUnit(Distance.NEAR.getId());
				placeUnit(Distance.ALL.getId());
				break;
			case Distance.FAR:
				placeUnit(Distance.FAR.getId());
				placeUnit(Distance.ALL.getId());
				break;
			case Distance.ALL:
				placeUnit(Distance.NEAR.getId());
				placeUnit(Distance.FAR.getId());
				placeUnit(Distance.ALL.getId());
				break;
			default:
				break;
			}
			canSelect = false;
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	boolean hasClicked(MouseEvent e, BattleData[] battleData, Runnable task, Predicate<Integer> check) {
		int number = clickTarget(e, battleData);
		if(check.test(number)) {
			select = number;
			task.run();
			return true;
		}
		return false;
	}
	
	Predicate<Integer> targetCheck(){
		return number -> 0 <= number;
	}
	
	Predicate<Integer> targetCheck(BattleData[] battleData){
		return number -> targetCheck().test(number) && battleData[number].canActivate();
	}
	
	int clickTarget(MouseEvent e, BattleData[] data) {
		for(int i = 0; i < data.length; i++) {
			int x = data[i].getPositionX() + 30;
			int y = data[i].getPositionY() + 30;
			if(existsTarget(x, y, SIZE, e)) {
				return i;
			}
		}
		return -1;
	}
	
	boolean existsTarget(int x, int y, int range, MouseEvent e) {
		return existsTarget(x, y, range, e.getX(), e.getY());
	}
	
	boolean existsTarget(int x, int y, int range, int valueX, int valueY) {
		return ValueRange.of(x, x + range).isValidIntValue(valueX)
				&& ValueRange.of(y, y + range).isValidIntValue(valueY);
	}
	
	void unitOperation() {
		if(unitMainData[select].canActivate()) {
			addUnitMenu();
			return;
		}
		unitStatus();
	}
	
	void addUnitMenu() {
		deactivateAction();
		gameTimer.timerStop();
		setLocation(unitMainData[select]);
		add(statusButton);
		add(retreatButton);
		if(awakeUnit.canAwake(select)) {
			add(awakeningButton);
		}
		add(unitReturnButton);
	}
	
	void setLocation(BattleUnit selectUnit) {
		int x = selectUnit.getPositionX();
		int y = selectUnit.getPositionY();
		statusButton.setLocation(x + 15, y - 10);
		retreatButton.setLocation(x - 45, y + 30);
		awakeningButton.setLocation(x + 75, y + 30);
		unitReturnButton.setLocation(x + 15, y + 70);
	}
	
	void statusButtonAction(ActionEvent e) {
		buttonAction(this::displayUnit);
	}
	
	void displayUnit() {
		new DisplayStatus().unit(unitMainData[select], unitLeftData[select]);
	}
	
	void retreatButtonAction(ActionEvent e) {
		buttonAction(() -> {
			gameData.addCost((int) Math.ceil(unitMainData[select].getCost() / 2));
			unitMainData[select].retreat();
		});
	}
	
	void awakeningButtonAction(ActionEvent e) {
		buttonAction(() -> awakeUnit.awake(select));
	}
	
	void unitReturnButtonAction(ActionEvent e) {
		buttonAction(null);
	}
	
	void buttonAction(Runnable task) {
		actionInitialize();
		if(task != null) {
			task.run();
		}
		gameTimer.timerRestart();
	}
	
	void actionInitialize() {
		removeMenu();
		activateAction();
	}
	
	void removeMenu() {
		remove(statusButton);
		remove(retreatButton);
		remove(awakeningButton);
		remove(unitReturnButton);
	}
	
	void activateAction() {
		stageReturnButton.setEnabled(true);
		if(getMouseListeners().length == 0) {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
	}
	
	void deactivateAction() {
		stageReturnButton.setEnabled(false);
		removeMouseListener(this);
		removeMouseMotionListener(this);
	}
	
	void unitStatus() {
		displayStatus(this::displayUnit);
	}
	
	void facilityStatus() {
		displayStatus(this::displayFacility);
	}
	
	void displayFacility() {
		new DisplayStatus().facility(facilityData[select]);
	}
	
	void enemyStatus() {
		displayStatus(this::displayEnemy);
	}
	
	void displayEnemy() {
		new DisplayStatus().enemy(enemyData[select]);
	}
	
	void displayStatus(Runnable task) {
		gameTimer.timerStop();
		task.run();
		gameTimer.timerRestart();
	}
	
	boolean canActivateSkill(MouseEvent e) {
		for(BattleUnit i: unitMainData) {
			if(i.canActivate() || !i.canPossessSkill() || !i.canRecast()) {
				continue;
			}
			int x = i.getInitialPosition().x;
			int y = i.getInitialPosition().y;
			if(existsTarget(x, y, 90, e)) {
				i.activateSkillBuff();
				return true;
			}
		}
		return false;
	}
	
	void placeUnit(int placementCode) {
		List<List<Double>> placementList = stageImage.getPlacementList(placementCode);
		List<List<Double>> activeList = IntStream.range(0, placementList.size())
				.filter(i -> stageImage.getUsePlacementList(placementCode).get(i))
				.mapToObj(i -> placementList.get(i))
				.filter(i -> notExistsOverlapping(i))
				.toList();
		for(List<Double> i: activeList) {
			if(existsTarget(i.get(0).intValue(), i.get(1).intValue(), SIZE, mouse.x, mouse.y)) {
				gameData.consumeCost(unitMainData[select].getCost());
				setPlacement(unitMainData, i);
				setPlacement(unitLeftData, i);
				return;
			}
		}
	}
	
	boolean notExistsOverlapping(List<Double> point) {
		return Stream.of(unitMainData).noneMatch(i -> i.getPositionX() == correctPosition(point.get(0))
				&& i.getPositionY() == correctPosition(point.get(1)));
	}
	
	int correctPosition(double position) {
		return (int) (position - SIZE);
	}
	
	void setPlacement(BattleUnit[] unit, List<Double> position) {
		unit[select].activate(correctPosition(position.get(0)), correctPosition(position.get(1)));
	}
}