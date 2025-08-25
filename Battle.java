package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.DefaultStage;
import defaultdata.stage.StageData;
import defendthecastle.MainFrame;
import savedata.SaveComposition;
import screendisplay.DisplayStatus;

//バトル画面制御
public class Battle extends JPanel implements MouseListener, MouseMotionListener{
	JLabel costLabel = new JLabel();
	JButton rangeDrawButton = new JButton();
	JButton meritButton = new JButton();
	JButton pauseButton = new JButton();
	JButton stageReturnButton = new JButton();
	JButton statusButton = new UnitButton();
	JButton retreatButton = new UnitButton();
	JButton awakeningButton = new UnitButton();
	JButton unitReturnButton = new UnitButton();
	BufferedImage stageImage;
	List<BufferedImage> placementImage = new DefaultStage().getPlacementImage(4);
	List<List<List<Double>>> placementList;
	BattleUnit[] UnitMainData;//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	BattleUnit[] UnitLeftData;//左武器用
	BattleFacility[] FacilityData;
	BattleEnemy[] EnemyData;
	GameData GameData;
	Point mouse;
	Point menuPoint;
	int select;
	boolean canSelect;
	public final static int SIZE = 28;
	int time;
	boolean canStop;
	boolean canRangeDraw;
	
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
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
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
			g.drawImage(FacilityData[i].getActivate()? FacilityData[i].getActionImage(): FacilityData[i].getBreakImage(), FacilityData[i].getPositionX(), FacilityData[i].getPositionY(), this);
			if(FacilityData[i].getActivate()) {
				drawHP(g, FacilityData[i]);
			}
		});
	}
	
	private void drawEnemy(Graphics g) {
		IntStream.range(0, EnemyData.length).filter(i -> EnemyData[i].getActivate()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> {
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
		Stream.of(UnitMainData).filter(i -> i.getActivate() && i.possessSkill()).forEach(i -> skill(g, i));
	}
	
	private void skill(Graphics g, BattleUnit BattleUnit) {
		if(BattleUnit.getRecast()) {
			g.drawImage(BattleUnit.getSkillImage(), BattleUnit.initialPosition().x, BattleUnit.initialPosition().y, this);
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.GRAY);
		g2.fillOval(BattleUnit.initialPosition().x, BattleUnit.initialPosition().y, 90, 90);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fill(new Arc2D.Double(BattleUnit.initialPosition().x, BattleUnit.initialPosition().y, 90, 90, 90, 360 * BattleUnit.recastRatio(), Arc2D.PIE));
	}
	
	private void drawUnit(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			if(UnitMainData[i].getActivate() && canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), UnitMainData[i].getPositionX(), UnitMainData[i].getPositionY(), UnitMainData[i].getRange());
				rangeDraw(g, new Color(0, 0, 255, 20), UnitLeftData[i].getPositionX(), UnitLeftData[i].getPositionY(), UnitLeftData[i].getRange());
			}
			int x = UnitMainData[i].getPositionX();
			int y = UnitMainData[i].getPositionY();
			g.drawImage(UnitMainData[i].getActionImage(), x, y, this);
			g.drawImage(UnitMainData[i].getCoreImage(), x, y, this);
			g.drawImage(UnitLeftData[i].getActionImage(), x, y, this);
			if(UnitMainData[i].getActivate()) {
				drawHP(g, UnitMainData[i]);
			}
		});
	}
	
	private void drawBullet(Graphics g) {
		drawBullet(g, UnitMainData);
		drawBullet(g, UnitLeftData);
		drawBullet(g, FacilityData);
		drawBullet(g, EnemyData);
	}
	
	private void drawBullet(Graphics g, BattleData[] BattleData) {
		Stream.of(BattleData).filter(i -> i.getAtackMotion()).forEach(i -> i.getBulletList().stream().forEach(j -> g.drawImage(j.getImage(), j.getPsitionX(), j.getPsitionY(), this)));
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
			if(UnitMainData[number].getActivate()) {
				unitMenu(number);
				return;
			}
			unitStatus(number);
			return;
		}
		if(activateSkill(e)) {
			return;
		}
		number = clickPointCheck(e, FacilityData);
		if(0 <= number && FacilityData[number].getActivate()) {
			facilityStatus(number);
			return;
		}
		number = clickPointCheck(e, EnemyData);
		if(0 <= number && EnemyData[number].getActivate()) {
			enemyStatus(number);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		removeMenu();
		mouse = e.getPoint();
		IntStream.range(0, UnitMainData.length).filter(i -> !UnitMainData[i].getActivate()).forEach(i -> {
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
	
	private boolean activateSkill(MouseEvent e) {
		for(int i = 0; i < UnitMainData.length; i++) {
			if(!UnitMainData[i].getActivate() || !UnitMainData[i].possessSkill() || !UnitMainData[i].getRecast()) {
				continue;
			}
			int x = UnitMainData[i].initialPosition().x;
			int y = UnitMainData[i].initialPosition().y;
			if(ValueRange.of(x, x + 90).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + 90).isValidIntValue(e.getY())) {
				UnitMainData[i].activateBuff(Buff.SKILL);
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
		addUnitReturnButton(number);
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
			UnitMainData[number].defeat();
			UnitLeftData[number].defeat();
			removeMenu();
		});
	}
	
	private void addAwakeningButton(int number) {
		add(awakeningButton);
		awakeningButton.addActionListener(e->{
			
			
			
			
			removeMenu();
		});
	}
	
	private void addUnitReturnButton(int number) {
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

//ユニット用JButtonの編集
class UnitButton extends JButton{
	protected UnitButton() {
		setPreferredSize(new Dimension(60, 30));
		setContentAreaFilled(false);
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		if(getModel().isArmed()) {
			g2.setPaint(new GradientPaint(25.0f, 5.0f, Color.LIGHT_GRAY, 30.0f, 20.0f, Color.GRAY));
		}else {
			g2.setPaint(new GradientPaint(25.0f, 5.0f, Color.YELLOW, 30.0f, 20.0f, Color.ORANGE));
		}
		g2.fillOval(0, 0, getSize().width, getSize().height);
		super.paintComponent(g2);
	}
	
	protected void paintBorder(Graphics g) {
		g.drawOval(0, 0, getSize().width, getSize().height);
	}
}

//一時停止中の画面
class PauseDialog extends JDialog implements WindowListener{
	Battle Battle;
	
	protected PauseDialog(Battle Battle, StageData StageData, List<Boolean> clearMerit) {
		setDialog(Battle);
		setTitle("戦功");
		setSize(435, 255);
		setLocationRelativeTo(null);
		add(new MeritPanel(this, StageData, clearMerit));
		setVisible(true);
	}
	
	protected PauseDialog(Battle Battle) {
		setDialog(Battle);
		setTitle("一時停止");
		setSize(285, 140);
		setLocationRelativeTo(null);
		add(new PausePanel(this));
		setVisible(true);
	}
	
	protected PauseDialog(Battle Battle, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		setDialog(Battle);
		setTitle("降参/再戦");
		setSize(415, 140);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, MainFrame, StageData, clearMerit, difficultyCode));
		setVisible(true);
	}
	
	private void setDialog(Battle Battle) {
		this.Battle = Battle;
		addWindowListener(this);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
	}
	
	protected void disposeDialog() {
		dispose();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
		Battle.timerRestart();
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}

//戦功表示
class MeritPanel extends JPanel{
	JButton restartButton = new JButton();
	JScrollPane meritScroll = new JScrollPane();
	
	protected MeritPanel(PauseDialog PauseDialog, StageData StageData, List<Boolean> clearMerit) {
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(390, 20 * clearMerit.size()));
		IntStream.range(0, clearMerit.size()).forEach(i -> {
			JLabel label = new JLabel();
			label.setText(StageData.getMerit().get(i));
			if(clearMerit.get(i)) {
				label.setForeground(Color.LIGHT_GRAY);
			}else {
				label.setForeground(Color.BLACK);
			}
			panel.add(label);
		});
		meritScroll.getViewport().setView(panel);
    	add(meritScroll);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		restartButton.setBounds(150, 170, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		meritScroll.setBounds(10, 10, 400, 150);
		meritScroll.setPreferredSize(meritScroll.getSize());
	}
}

//一時停止表示
class PausePanel extends JPanel{
	JLabel comment = new JLabel();
	JButton restartButton = new JButton();
	
	protected PausePanel(PauseDialog PauseDialog) {
		add(comment);
		comment.setText("ゲームを一時停止しています");
		comment.setHorizontalAlignment(JLabel.CENTER);
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		comment.setBounds(10, 10, 250, 40);
		comment.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		restartButton.setBounds(75, 50, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
}

//戻る・再戦
class ReturnPanel extends JPanel{
	JLabel comment = new JLabel();
	JButton restartButton = new JButton();
	JButton returnButton = new JButton();
	JButton retryButton = new JButton();
	
	protected ReturnPanel(PauseDialog PauseDialog, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		add(comment);
		comment.setText("ゲーム操作を選択してください");
		comment.setHorizontalAlignment(JLabel.CENTER);
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
		add(returnButton);
		returnButton.addActionListener(e->{
			PauseDialog.disposeDialog();
			MainFrame.selectStageDraw();
		});
		returnButton.setText("降参");
		add(retryButton);
		retryButton.addActionListener(e->{
			PauseDialog.disposeDialog();
			MainFrame.battleDraw(StageData, clearMerit, difficultyCode);
		});
		retryButton.setText("再戦");
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		comment.setBounds(10, 10, 380, 40);
		comment.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		restartButton.setBounds(10, 50, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		returnButton.setBounds(140, 50, 120, 40);
		returnButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		retryButton.setBounds(270, 50, 120, 40);
		retryButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
}