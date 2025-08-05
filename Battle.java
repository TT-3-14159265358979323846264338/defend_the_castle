package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
	JButton returnButton = new JButton();
	BufferedImage stageImage;
	List<BufferedImage> placementImage = new DefaultStage().getPlacementImage(4);
	List<List<List<Double>>> placementList;
	BattleUnit[] unitMainData;//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	BattleUnit[] unitLeftData;//左武器用
	BattleFacility[] facilityData;
	BattleEnemy[] enemyData;
	Point mouse;
	int select;
	boolean canSelect;
	public final static int SIZE = 28;
	int time;
	boolean canStop;
	boolean canRangeDraw;
	int cost = 50;
	
	public Battle(MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(new Color(240, 170, 80));
		install(StageData);
		addCostLabel();
		addRangeDrawButton();
		addMeritButton(StageData, clearMerit);
		addPauseButton(MainFrame);
		addReturnButton(MainFrame, StageData, clearMerit, difficultyCode);
		mainTimer();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		rangeDrawButton.setBounds(0, 0, 95, 40);
		setCostLabel();
		setButton(rangeDrawButton, "射程表示", 1010, 465, 95, 40);
		setButton(meritButton, "戦功表示", 1110, 465, 95, 40);
		setButton(pauseButton, "一時停止", 1010, 515, 95, 40);
		setButton(returnButton, "降参/再戦", 1110, 515, 95, 40);
		drawField(g);
		drawEnemy(g);
		drawBackground(g);
		drawUnit(g);
		drawBullet(g);
		drawSelectUnit(g);
		requestFocus();
	}
	
	private void install(StageData StageData) {
		stageImage = StageData.getImage(2);
		placementList = StageData.getPlacementPoint();
		SaveComposition SaveComposition = new SaveComposition();
		SaveComposition.load();
		List<List<Integer>> composition = SaveComposition.getAllCompositionList().get(SaveComposition.getSelectNumber());
		unitMainData = IntStream.range(0, composition.size()).mapToObj(i -> new BattleUnit(this, composition.get(i), initialX(i), initialY(i))).toArray(BattleUnit[]::new);
		unitLeftData = IntStream.range(0, composition.size()).mapToObj(i -> new BattleUnit(this, composition.get(i))).toArray(BattleUnit[]::new);;
		facilityData = IntStream.range(0, StageData.getFacility().size()).mapToObj(i -> new BattleFacility(this, StageData, i)).toArray(BattleFacility[]::new);
		enemyData = IntStream.range(0, StageData.getEnemy().size()).mapToObj(i -> new BattleEnemy(this, StageData, i)).toArray(BattleEnemy[]::new);
		Stream.of(unitMainData).forEach(i -> i.install(unitMainData, facilityData, enemyData));
		Stream.of(unitLeftData).forEach(i -> i.install(unitMainData, facilityData, enemyData));
		Stream.of(facilityData).forEach(i -> i.install(unitMainData, facilityData, enemyData));
		Stream.of(enemyData).forEach(i -> i.install(unitMainData, facilityData, enemyData));
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
			canStop = true;
			new PauseDialog(this, StageData, clearMerit);
		});
	}
	
	private void addPauseButton(MainFrame MainFrame) {
		add(pauseButton);
		pauseButton.addActionListener(e->{
			canStop = true;
			new PauseDialog(this);
		});
	}
	
	private void addReturnButton(MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		add(returnButton);
		returnButton.addActionListener(e->{
			canStop = true;
			new PauseDialog(this, MainFrame, StageData, clearMerit, difficultyCode);
		});
	}
	
	private void setCostLabel() {
		costLabel.setText("コスト: " + cost);
		costLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		costLabel.setBounds(1010, 15, 200, 30);
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 12));
		button.setBounds(x, y, width, height);
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
		IntStream.range(0, facilityData.length).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), (int) facilityData[i].getPositionX(), (int) facilityData[i].getPositionY(), facilityData[i].getRange());
			}
			g.drawImage(facilityData[i].getActivate()? facilityData[i].getActionImage(): facilityData[i].getBreakImage(), (int) facilityData[i].getPositionX(), (int) facilityData[i].getPositionY(), this);
			drawHP(g, facilityData[i]);
		});
	}
	
	private void drawEnemy(Graphics g) {
		IntStream.range(0, enemyData.length).filter(i -> enemyData[i].getActivate()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> {
			if(canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), (int) enemyData[i].getPositionX(), (int) enemyData[i].getPositionY(), enemyData[i].getRange());
			}
			g.drawImage(enemyData[i].getActionImage(), (int) enemyData[i].getPositionX(), (int) enemyData[i].getPositionY(), this);
			drawHP(g, enemyData[i]);
		});
	}
	
	private void drawBackground(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			switch(unitMainData[i].getType()) {
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
	
	private void drawUnit(Graphics g) {
		IntStream.range(0, 8).forEach(i -> {
			if(unitMainData[i].getActivate() && canRangeDraw) {
				rangeDraw(g, new Color(255, 0, 0, 20), (int) unitMainData[i].getPositionX(), (int) unitMainData[i].getPositionY(), unitMainData[i].getRange());
				rangeDraw(g, new Color(0, 0, 255, 20), (int) unitLeftData[i].getPositionX(), (int) unitLeftData[i].getPositionY(), unitLeftData[i].getRange());
			}
			int x = (int) unitMainData[i].getPositionX();
			int y = (int) unitMainData[i].getPositionY();
			g.drawImage(unitMainData[i].getActionImage(), x, y, this);
			g.drawImage(unitMainData[i].getCoreImage(), x, y, this);
			g.drawImage(unitLeftData[i].getActionImage(), x, y, this);
			if(unitMainData[i].getActivate()) {
				drawHP(g, unitMainData[i]);
			}
		});
	}
	
	private void drawBullet(Graphics g) {
		drawBullet(g, unitMainData);
		drawBullet(g, unitLeftData);
		drawBullet(g, facilityData);
		drawBullet(g, enemyData);
	}
	
	private void drawBullet(Graphics g, BattleData[] data) {
		Stream.of(data).filter(i -> i.getAtackMotion()).forEach(i -> i.getBulletList().stream().forEach(j -> g.drawImage(j.getImage(), (int) j.getPsitionX(), (int) j.getPsitionY(), this)));
	}
	
	private void drawSelectUnit(Graphics g) {
		if(canSelect) {
			int x = mouse.x - 45;
			int y = mouse.y - 45;
			rangeDraw(g, new Color(255, 0, 0, 20), x, y, unitMainData[select].getRange());
			rangeDraw(g, new Color(0, 0, 255, 20), x, y, unitLeftData[select].getRange());
			g.drawImage(unitMainData[select].getDefaultImage(), x, y, this);
			g.drawImage(unitMainData[select].getDefaultCoreImage(), x, y, this);
			g.drawImage(unitLeftData[select].getDefaultImage(), x, y, this);
		}
	}
	
	private void rangeDraw(Graphics g, Color color, int x, int y, int range) {
		int correction = 45;
		g.setColor(color);
		g.fillOval((int) (x + correction - range),
				(int) (y + correction - range),
				range * 2,
				range * 2);
	}
	
	private void drawHP(Graphics g, BattleData data) {
		int x = (int) data.getPositionX() + 30;
		int y = (int) data.getPositionY() + 60;
		int height = 5;
		g.setColor(Color.BLACK);
		g.fillRect(x, y, SIZE, height);
		g.setColor(new Color(150, 200, 100));
		g.fillRect(x, y, SIZE * data.getNowHP() / data.getMaxHP(), height);
		g.setColor(Color.WHITE);
		g.drawRect(x, y, SIZE, height);
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
		int number = clickPointCheck(e, unitMainData);
		if(0 <= number) {
			unitStatus(number);
			return;
		}
		number = clickPointCheck(e, facilityData);
		if(0 <= number && facilityData[number].getActivate()) {
			facilityStatus(number);
			return;
		}
		number = clickPointCheck(e, enemyData);
		if(0 <= number && enemyData[number].getActivate()) {
			enemyStatus(number);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		mouse = e.getPoint();
		IntStream.range(0, unitMainData.length).filter(i -> !unitMainData[i].getActivate()).forEach(i -> {
			int x = initialX(i) + 30;
			int y = initialY(i) + 30;
			if(ValueRange.of(x, x + SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE).isValidIntValue(e.getY())) {
				if(unitMainData[i].getCost() <= cost) {
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
			switch(unitMainData[select].getType()) {
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
			int x = (int) data[i].getPositionX() + 30;
			int y = (int) data[i].getPositionY() + 30;
			if(ValueRange.of(x, x + SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE).isValidIntValue(e.getY())) {
				return i;
			}
		}
		return -1;
	}
	
	private void unitStatus(int number) {
		canStop = true;
		new DisplayStatus().unit(unitMainData[number], unitLeftData[number]);
		timerRestart();
	}
	
	private void facilityStatus(int number) {
		canStop = true;
		new DisplayStatus().facility(facilityData[number]);
		timerRestart();
	}
	
	private void enemyStatus(int number) {
		canStop = true;
		new DisplayStatus().enemy(enemyData[number]);
		timerRestart();
	}
	
	private void placeUnit(int placementCode) {
		Function<Double, Integer> correctPosition = (position) -> {
			return position.intValue() - SIZE;
		};
		Predicate<List<Double>> positionCheck = (point) -> {
			return Stream.of(unitMainData).noneMatch(i -> (int) i.getPositionX() == correctPosition.apply(point.get(0))
					&& (int) i.getPositionY() == correctPosition.apply(point.get(1)));
		};
		placementList.get(placementCode).stream().filter(i -> positionCheck.test(i)).forEach(i -> {
			if(ValueRange.of(i.get(0).intValue(), i.get(0).intValue() + SIZE).isValidIntValue(mouse.x)
					&& ValueRange.of(i.get(1).intValue(), i.get(1).intValue() + SIZE).isValidIntValue(mouse.y)) {
				consumeCost(unitMainData[select].getCost());
				unitMainData[select].activate(correctPosition.apply(i.get(0)), correctPosition.apply(i.get(1)));
				unitLeftData[select].activate(correctPosition.apply(i.get(0)), correctPosition.apply(i.get(1)));
			}
		});
	}
	
	private void consumeCost(int consumeValue) {
		cost -= consumeValue;
	}
	
	protected void addCost(int addValue) {
		cost += addValue;
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