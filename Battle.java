package battle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.DefaultStage;
import defaultdata.stage.StageData;
import defendthecastle.MainFrame;
import savedata.SaveComposition;
import screendisplay.DisplayStatus;

//バトル画面制御
public class Battle extends JPanel implements MouseListener, MouseMotionListener{
	public static final int SIZE = 28;
	private JLabel costLabel = new JLabel();
	private JButton rangeDrawButton = new JButton();
	private JButton meritButton = new JButton();
	private JButton pauseButton = new JButton();
	private JButton stageReturnButton = new JButton();
	private JButton statusButton = new UnitButton();
	private JButton retreatButton = new UnitButton();
	private JButton awakeningButton = new UnitButton();
	private JButton unitReturnButton = new UnitButton();
	private BufferedImage stageImage;
	private List<BufferedImage> placementImage = new DefaultStage().getPlacementImage(4);
	private List<List<List<Double>>> placementList;
	private BattleUnit[] UnitMainData;//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	private BattleUnit[] UnitLeftData;//左武器用
	private BattleFacility[] FacilityData;
	private BattleEnemy[] EnemyData;
	private GameData GameData;
	private Point mouse;
	private Point menuPoint;
	private int select;
	private boolean canSelect;
	private int time;
	private boolean canStop;
	private boolean canRangeDraw;
	private boolean canAwake;
	private int awakeUnit;
	private ScheduledExecutorService mainScheduler = Executors.newSingleThreadScheduledExecutor();
	
	public Battle(MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(new Color(240, 170, 80));
		install(StageData);
		addCostLabel();
		addRangeDrawButton();
		addMeritButton(StageData, clearMerit);
		addPauseButton(MainFrame);
		addStageReturnButton(MainFrame, StageData, clearMerit, difficultyCode);
		mainTimer();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		rangeDrawButton.setBounds(0, 0, 95, 40);
		setCostLabel();
		setMenuButton(rangeDrawButton, "射程表示", 1010, 465);
		setMenuButton(meritButton, "戦功表示", 1110, 465);
		setMenuButton(pauseButton, "一時停止", 1010, 515);
		setMenuButton(stageReturnButton, "降参/再戦", 1110, 515);
		if(statusButton.isValid()) {
			setUnitButton(statusButton, "能力", menuPoint.x + 15, menuPoint.y - 10);
			setUnitButton(retreatButton, "撤退", menuPoint.x - 45, menuPoint.y + 30);
			setUnitButton(awakeningButton, "覚醒", menuPoint.x + 75, menuPoint.y + 30);
			setUnitButton(unitReturnButton, "戻る", menuPoint.x + 15, menuPoint.y + 70);
		}
		drawField(g);
		drawEnemy(g);
		drawBackground(g);
		drawSkill(g);
		drawUnit(g);
		if(canAwake) {
			drawAwake(g);
		}
		drawBullet(g);
		drawSelectUnit(g);
		drawMorale(g);
		requestFocus();
	}
	
	private void install(StageData StageData) {
		GameData = new GameData(StageData);
		stageImage = StageData.getImage(2);
		placementList = StageData.getPlacementPoint();
		SaveComposition SaveComposition = new SaveComposition();
		SaveComposition.load();
		List<List<Integer>> composition = SaveComposition.getAllCompositionList().get(SaveComposition.getSelectNumber());
		UnitMainData = IntStream.range(0, composition.size()).mapToObj(i -> new BattleUnit(this, composition.get(i), initialX(i), initialY(i))).toArray(BattleUnit[]::new);
		UnitLeftData = IntStream.range(0, composition.size()).mapToObj(i -> new BattleUnit(this, composition.get(i))).toArray(BattleUnit[]::new);;
		FacilityData = IntStream.range(0, StageData.getFacility().size()).mapToObj(i -> new BattleFacility(this, StageData, i)).toArray(BattleFacility[]::new);
		EnemyData = IntStream.range(0, StageData.getEnemy().size()).mapToObj(i -> new BattleEnemy(this, StageData, i)).toArray(BattleEnemy[]::new);
		IntStream.range(0, UnitMainData.length).forEach(i -> UnitMainData[i].install(GameData, UnitLeftData[i], UnitMainData, FacilityData, EnemyData));
		IntStream.range(0, UnitLeftData.length).forEach(i -> UnitLeftData[i].install(GameData, UnitMainData[i], UnitMainData, FacilityData, EnemyData));
		Stream.of(FacilityData).forEach(i -> i.install(GameData, UnitMainData, FacilityData, EnemyData));
		Stream.of(EnemyData).forEach(i -> i.install(GameData, UnitMainData, FacilityData, EnemyData));
	}
	
	private void mainTimer() {
		mainScheduler.scheduleWithFixedDelay(() -> {
			timerWait();
			time += 10;
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	protected synchronized void timerWait() {
		if(canStop) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void gameEnd() {
		mainScheduler.shutdown();
		Stream.of(UnitMainData).forEach(i -> i.schedulerEnd());
		Stream.of(UnitLeftData).forEach(i -> i.schedulerEnd());
		Stream.of(FacilityData).forEach(i -> i.schedulerEnd());
		Stream.of(EnemyData).forEach(i -> {
			i.schedulerEnd();
			i.moveSchedulerEnd();
		});
	}
	
	private void timerStop() {
		canStop = true;
	}
	
	protected synchronized void timerRestart() {
		notifyAll();
		canStop = false;
	}
	
	protected int getMainTime() {
		return time;
	}
	
	private void addCostLabel() {
		add(costLabel);
		costLabel.setBackground(Color.WHITE);
		costLabel.setOpaque(true);
		costLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void addRangeDrawButton() {
		add(rangeDrawButton);
		rangeDrawButton.addActionListener(e->{
			canRangeDraw = (canRangeDraw)? false: true;
		});
	}
	
	private void addMeritButton(StageData StageData, List<Boolean> clearMerit) {
		add(meritButton);
		meritButton.addActionListener(e->{
			timerStop();
			new PauseDialog(this, StageData, clearMerit);
		});
	}
	
	private void addPauseButton(MainFrame MainFrame) {
		add(pauseButton);
		pauseButton.addActionListener(e->{
			timerStop();
			new PauseDialog(this);
		});
	}
	
	private void addStageReturnButton(MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		add(stageReturnButton);
		stageReturnButton.addActionListener(e->{
			timerStop();
			new PauseDialog(this, MainFrame, StageData, clearMerit, difficultyCode);
		});
	}
	
	private void setCostLabel() {
		costLabel.setText("コスト: " + GameData.getCost());
		costLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		costLabel.setBounds(1010, 15, 200, 30);
	}
	
	private void setMenuButton(JButton button, String name, int x, int y) {
		button.setText(name);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 12));
		button.setBounds(x, y, 95, 40);
	}
	
	private void setUnitButton(JButton button, String name, int x, int y) {
		button.setText(name);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 10));
		button.setLocation(x, y);
	}
	
	private int initialX(int i) {
		return 1015 + i % 2 * 100;
	}
	
	private int initialY(int i) {
		return 55 + i / 2 * 100;
	}
	
	private void drawField(Graphics g) {
		g.drawImage(stageImage, 0, 0, this);
		IntStream.range(0, placementList.size()).forEach(i -> placementList.get(i).stream().forEach(j -> g.drawImage(placementImage.get(i), j.get(0).intValue(), j.get(1).intValue(), this)));
		IntStream.range(0, FacilityData.length).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), FacilityData[i].getPositionX(), FacilityData[i].getPositionY(), FacilityData[i].getRange());
			}
			g.drawImage(FacilityData[i].canActivate()? FacilityData[i].getActionImage(): FacilityData[i].getBreakImage(), FacilityData[i].getPositionX(), FacilityData[i].getPositionY(), this);
			if(FacilityData[i].canActivate()) {
				drawHP(g, FacilityData[i]);
			}
		});
	}
	
	private void drawEnemy(Graphics g) {
		IntStream.range(0, EnemyData.length).filter(i -> EnemyData[i].canActivate()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), EnemyData[i].getPositionX(), EnemyData[i].getPositionY(), EnemyData[i].getRange());
			}
			g.drawImage(EnemyData[i].getActionImage(), EnemyData[i].getPositionX(), EnemyData[i].getPositionY(), this);
			drawHP(g, EnemyData[i]);
		});
	}
	
	private void drawBackground(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			switch(UnitMainData[i].getType()) {
			case 0:
				g.setColor(new Color(255, 220, 220));
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 100, 100);
				break;
			case 1:
				g.setColor(new Color(220, 220, 255));
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 100, 100);
				break;
			case 2:
				g.setColor(new Color(255, 220, 220));
				g.fillRect(initialX(i) - 5, initialY(i) - 5, 50, 100);
				g.setColor(new Color(220, 220, 255));
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
	
	private void drawSkill(Graphics g) {
		Stream.of(UnitMainData).filter(i -> i.canActivate() && i.canPossessSkill()).forEach(i -> skill(g, i));
	}
	
	private void skill(Graphics g, BattleUnit BattleUnit) {
		if(BattleUnit.canRecast()) {
			g.drawImage(BattleUnit.getSkillImage(), BattleUnit.getInitialPosition().x, BattleUnit.getInitialPosition().y, this);
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.GRAY);
		g2.fillOval(BattleUnit.getInitialPosition().x, BattleUnit.getInitialPosition().y, 90, 90);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fill(new Arc2D.Double(BattleUnit.getInitialPosition().x, BattleUnit.getInitialPosition().y, 90, 90, 90, 360 * BattleUnit.recastRatio(), Arc2D.PIE));
	}
	
	private void drawUnit(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			if(UnitMainData[i].canActivate() && canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), UnitMainData[i].getPositionX(), UnitMainData[i].getPositionY(), UnitMainData[i].getRange());
				rangeDraw(g, new Color(0, 0, 255, 20), UnitLeftData[i].getPositionX(), UnitLeftData[i].getPositionY(), UnitLeftData[i].getRange());
			}
			int x = UnitMainData[i].getPositionX();
			int y = UnitMainData[i].getPositionY();
			g.drawImage(UnitMainData[i].getActionImage(), x, y, this);
			g.drawImage(UnitMainData[i].getCoreImage(), x, y, this);
			g.drawImage(UnitLeftData[i].getActionImage(), x, y, this);
			if(UnitMainData[i].canActivate()) {
				drawHP(g, UnitMainData[i]);
			}
		});
	}
	
	private void drawAwake(Graphics g) {
		int x = UnitMainData[awakeUnit].getPositionX();
		int y = UnitMainData[awakeUnit].getPositionY();
		g.setColor(Color.RED);
		g.fillRect(x + 15, y + 30, 10, 30);
		g.fillPolygon(new int[] {x + 10, x + 20, x + 30}, new int[] {y + 40, y + 20, y + 40}, 3);
		g.fillRect(x + 60, y + 30, 10, 30);
		g.fillPolygon(new int[] {x + 55, x + 65, x + 75}, new int[] {y + 40, y + 20, y + 40}, 3);
	}
	
	private void drawBullet(Graphics g) {
		drawBullet(g, UnitMainData);
		drawBullet(g, UnitLeftData);
		drawBullet(g, FacilityData);
		drawBullet(g, EnemyData);
	}
	
	private void drawBullet(Graphics g, BattleData[] BattleData) {
		Stream.of(BattleData).filter(i -> i.canAtack()).forEach(i -> i.getBulletList().stream().forEach(j -> g.drawImage(j.getImage(), j.getPsitionX(), j.getPsitionY(), this)));
	}
	
	private void drawSelectUnit(Graphics g) {
		if(canSelect) {
			int x = mouse.x - 45;
			int y = mouse.y - 45;
			rangeDraw(g, new Color(255, 0, 0, 20), x, y, UnitMainData[select].getRange());
			rangeDraw(g, new Color(0, 0, 255, 20), x, y, UnitLeftData[select].getRange());
			g.drawImage(UnitMainData[select].getDefaultImage(), x, y, this);
			g.drawImage(UnitMainData[select].getDefaultCoreImage(), x, y, this);
			g.drawImage(UnitLeftData[select].getDefaultImage(), x, y, this);
		}
	}
	
	private void rangeDraw(Graphics g, Color color, int x, int y, int range) {
		int correction = 45;
		g.setColor(color);
		g.fillOval(x + correction - range, y + correction - range, range * 2, range * 2);
	}
	
	private void drawHP(Graphics g, BattleData BattleData) {
		int x = BattleData.getPositionX() + 30;
		int y = BattleData.getPositionY() + 60;
		int height = 5;
		g.setColor(Color.BLACK);
		g.fillRect(x, y, SIZE, height);
		g.setColor(new Color(150, 200, 100));
		g.fillRect(x, y, SIZE * BattleData.getNowHP() / BattleData.getMaxHP(), height);
		g.setColor(Color.WHITE);
		g.drawRect(x, y, SIZE, height);
	}
	
	private void drawMorale(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(25, 525, 950, 30);
		int moralePosition = 475 + GameData.getMoraleDifference() * 2;
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
		removeMenu();
		int number = clickPointCheck(e, UnitMainData);
		if(0 <= number) {
			if(UnitMainData[number].canActivate()) {
				unitMenu(number);
				return;
			}
			unitStatus(number);
			return;
		}
		if(canActivateSkill(e)) {
			return;
		}
		number = clickPointCheck(e, FacilityData);
		if(0 <= number && FacilityData[number].canActivate()) {
			facilityStatus(number);
			return;
		}
		number = clickPointCheck(e, EnemyData);
		if(0 <= number && EnemyData[number].canActivate()) {
			enemyStatus(number);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		removeMenu();
		mouse = e.getPoint();
		IntStream.range(0, UnitMainData.length).filter(i -> !UnitMainData[i].canActivate()).forEach(i -> {
			int x = initialX(i) + 30;
			int y = initialY(i) + 30;
			if(ValueRange.of(x, x + SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE).isValidIntValue(e.getY())) {
				if(UnitMainData[i].getCost() <= GameData.getCost()) {
					select = i;
					canSelect = true;
				}
			}
		});
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(canSelect) {
			mouse = e.getPoint();
			switch(UnitMainData[select].getType()) {
			case 0:
				placeUnit(0);
				placeUnit(2);
				break;
			case 1:
				placeUnit(1);
				placeUnit(2);
				break;
			case 2:
				placeUnit(0);
				placeUnit(1);
				placeUnit(2);
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
	
	private int clickPointCheck(MouseEvent e, BattleData[] data) {
		for(int i = 0; i < data.length; i++) {
			int x = data[i].getPositionX() + 30;
			int y = data[i].getPositionY() + 30;
			if(ValueRange.of(x, x + SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE).isValidIntValue(e.getY())) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean canActivateSkill(MouseEvent e) {
		for(int i = 0; i < UnitMainData.length; i++) {
			if(!UnitMainData[i].canActivate() || !UnitMainData[i].canPossessSkill() || !UnitMainData[i].canRecast()) {
				continue;
			}
			int x = UnitMainData[i].getInitialPosition().x;
			int y = UnitMainData[i].getInitialPosition().y;
			if(ValueRange.of(x, x + 90).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + 90).isValidIntValue(e.getY())) {
				UnitMainData[i].activateBuff(Buff.SKILL, null);
				return true;
			}
		}
		return false;
	}
	
	private void unitMenu(int number) {
		timerStop();
		menuPoint = new Point(UnitMainData[number].getPositionX(), UnitMainData[number].getPositionY());
		addStatusButton(number);
		addRetreatButton(number);
		addAwakeningButton(number);
		addUnitReturnButton();
	}
	
	private void addStatusButton(int number) {
		add(statusButton);
		statusButton.addActionListener(e->{
			unitStatus(number);
			removeMenu();
		});
	}
	
	private void addRetreatButton(int number) {
		add(retreatButton);
		retreatButton.addActionListener(e->{
			GameData.addCost((int) Math.ceil(UnitMainData[number].getCost() / 2));
			UnitMainData[number].defeat(null);
			UnitLeftData[number].defeat(null);
			removeMenu();
		});
	}
	
	private void addAwakeningButton(int number) {
		if(UnitMainData[number].canAwake()) {
			add(awakeningButton);
			awakeningButton.addActionListener(e->{
				awakeUnit = number;
				canAwake = true;
				mainScheduler.schedule(() -> canAwake = false, 2, TimeUnit.SECONDS);
				UnitMainData[number].awakening();
				UnitLeftData[number].awakening();
				removeMenu();
			});
		}
	}
	
	private void addUnitReturnButton() {
		add(unitReturnButton);
		unitReturnButton.addActionListener(e->{
			removeMenu();
		});
	}
	
	private void removeMenu() {
		Consumer<JButton> removeButton = (button) -> {
			remove(button);
			Stream.of(button.getActionListeners()).forEach(i -> button.removeActionListener(i));
		};
		removeButton.accept(statusButton);
		removeButton.accept(retreatButton);
		removeButton.accept(awakeningButton);
		removeButton.accept(unitReturnButton);
		timerRestart();
	}
	
	private void unitStatus(int number) {
		timerStop();
		new DisplayStatus().unit(UnitMainData[number], UnitLeftData[number]);
		timerRestart();
	}
	
	private void facilityStatus(int number) {
		timerStop();
		new DisplayStatus().facility(FacilityData[number]);
		timerRestart();
	}
	
	private void enemyStatus(int number) {
		timerStop();
		new DisplayStatus().enemy(EnemyData[number]);
		timerRestart();
	}
	
	private void placeUnit(int placementCode) {
		Function<Double, Integer> correctPosition = (position) -> {
			return position.intValue() - SIZE;
		};
		Predicate<List<Double>> positionCheck = (point) -> {
			return Stream.of(UnitMainData).noneMatch(i -> i.getPositionX() == correctPosition.apply(point.get(0))
					&& i.getPositionY() == correctPosition.apply(point.get(1)));
		};
		placementList.get(placementCode).stream().filter(i -> positionCheck.test(i)).forEach(i -> {
			if(ValueRange.of(i.get(0).intValue(), i.get(0).intValue() + SIZE).isValidIntValue(mouse.x)
					&& ValueRange.of(i.get(1).intValue(), i.get(1).intValue() + SIZE).isValidIntValue(mouse.y)) {
				GameData.consumeCost(UnitMainData[select].getCost());
				UnitMainData[select].activate(correctPosition.apply(i.get(0)), correctPosition.apply(i.get(1)));
				UnitLeftData[select].activate(correctPosition.apply(i.get(0)), correctPosition.apply(i.get(1)));
			}
		});
	}
}