package defendthecastle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.DefaultEnemy;
import defaultdata.DefaultStage;
import defaultdata.EditImage;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;
import savedata.SaveGameProgress;
import screendisplay.DisplayStatus;

//ステージ選択画面
public class MenuSelectStage extends JPanel{
	JLabel stageLabel = new JLabel();
	JLabel informationLabel = new JLabel();
	JButton returnButton = new JButton();
	JButton normalModeButton = new JButton();
	JButton hardModeButton = new JButton();
	JScrollPane stageScroll = new JScrollPane();
	JScrollPane enemyScroll = new JScrollPane();
	JScrollPane meritScroll = new JScrollPane();
	ProgressData ProgressData = new ProgressData();
	StageData[] StageData = IntStream.range(0, DefaultStage.STAGE_DATA_MAP.size()).mapToObj(i -> DefaultStage.STAGE_DATA_MAP.get(i)).toArray(StageData[]::new);
	List<BufferedImage> stageImage = Stream.of(StageData).map(i -> EditImage.stageImage(i, 5)).toList();
	SelectPanel SelectPanel = new SelectPanel(stageImage, ProgressData.getSelectStage(), StageData);
	MeritPanel MeritPanel = new MeritPanel(SelectPanel, ProgressData.getMeritStatus(), StageData);
	EnemyPanel EnemyPanel = new EnemyPanel(SelectPanel, StageData);
	
	protected MenuSelectStage(MainFrame MainFrame) {
		setBackground(new Color(240, 170, 80));
		add(stageLabel);
		add(informationLabel);
		addReturnButton(MainFrame);
		addNormalModeButton(MainFrame);
		addHardModeButton(MainFrame);
		addStageScroll();
		addMeritScroll();
		addEnemyScroll();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabel(stageLabel, "ステージ選択", 10, 10, 200, 30);
		setLabel(informationLabel, "ステージ情報", 170, 10, 200, 30);
		setButton(returnButton, "戻る", 10, 460, 150, 60);
		setButton(normalModeButton, "normal", 580, 460, 155, 60);
		setButton(hardModeButton, "hard", 745, 460, 155, 60);
		setScroll(stageScroll, 10, 40, 150, 410);
		setScroll(meritScroll, 170, 275, 400, 245);
		setScroll(enemyScroll, 580, 40, 320, 410);
		g.drawImage(stageImage.get(SelectPanel.getSelelct()), 170, 40, this);
		requestFocus();
	}
	
	private void addReturnButton(MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(e->{
			MainFrame.mainMenuDraw();
		});
	}
	
	private void addNormalModeButton(MainFrame MainFrame) {
		add(normalModeButton);
		normalModeButton.addActionListener(e->{
			ProgressData.save(SelectPanel.getSelelct());
			//難易度コードは後で追記
			MainFrame.battleDraw(StageData[SelectPanel.getSelelct()], ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), 0);
		});
	}
	
	private void addHardModeButton(MainFrame MainFrame) {
		add(hardModeButton);
		hardModeButton.addActionListener(e->{
			ProgressData.save(SelectPanel.getSelelct());
			//難易度コードは後で追記
			MainFrame.battleDraw(StageData[SelectPanel.getSelelct()],ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), 1);
		});
	}
	
	private void addStageScroll() {
		stageScroll.getViewport().setView(SelectPanel);
    	add(stageScroll);
	}
	
	private void addMeritScroll() {
		meritScroll.getViewport().setView(MeritPanel);
    	add(meritScroll);
	}
	
	private void addEnemyScroll() {
		enemyScroll.getViewport().setView(EnemyPanel);
    	add(enemyScroll);
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
	}
}

//クリアデータ取込み
class ProgressData{
	SaveGameProgress SaveGameProgress = new SaveGameProgress();
	
	protected ProgressData() {
		SaveGameProgress.load();
	}
	
	protected void save(int select) {
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), SaveGameProgress.getMedal(), select);
	}
	
	protected List<List<Boolean>> getMeritStatus(){
		return SaveGameProgress.getMeritStatus();
	}
	
	protected int getSelectStage() {
		return SaveGameProgress.getSelectStage();
	}
}

//ステージ切り替え
class SelectPanel extends JPanel implements MouseListener{
	JLabel[] nameLabel = IntStream.range(0, DefaultStage.STAGE_DATA_MAP.size()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
	List<BufferedImage> stageImage;
	List<String> stageNameList;
	int select = 0;
	
	protected SelectPanel(List<BufferedImage> stageImage, int select, StageData[] StageData) {
		Stream.of(nameLabel).forEach(i -> addLabel(i));
		this.stageImage = stageImage.stream().map(i -> EditImage.scalingImage(i, 3.5)).toList();
		stageNameList = Stream.of(StageData).map(i -> i.getName()).toList();
		this.select = select;
		addMouseListener(this);
		setPreferredSize(new Dimension(100, 85 * stageImage.size()));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, nameLabel.length).forEach(i -> setLabel(i));
		IntStream.range(0, stageImage.size()).forEach(i -> drawField(i, g));
	}
	
	private void addLabel(JLabel label) {
		add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.RED);
	}
	
	private void setLabel(int number) {
		nameLabel[number].setText(stageNameList.get(number));
		nameLabel[number].setBounds(0, 25 + 85 * number, 130, 30);
		nameLabel[number].setFont(new Font("Arial", Font.BOLD, 20));
	}
	
	private void drawField(int number, Graphics g) {
		if(select == number) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 85 * number, 135, 85);
		}
		g.drawImage(stageImage.get(number), 10, 10 + 85 * number, this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < stageImage.size(); i++) {
			if(ValueRange.of(10, 125).isValidIntValue(e.getX())
					&& ValueRange.of(10 + 85 * i, -10 + 85 * (i + 1)).isValidIntValue(e.getY())) {
				select = i;
				break;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	protected int getSelelct() {
		return select;
	}
}

//戦功情報
class MeritPanel extends JPanel{
	JLabel[] meritLabel;
	JLabel[] clearLabel;
	SelectPanel SelectPanel;
	List<List<Boolean>> meritStatus;
	List<List<String>> meritInformation;
	Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	Font clearFont = new Font("Arail", Font.BOLD, 30);
	
	protected MeritPanel(SelectPanel SelectPanel, List<List<Boolean>> meritStatus, StageData[] StageData) {
		this.SelectPanel = SelectPanel;
		this.meritStatus = meritStatus;
		meritLabel = IntStream.range(0, Stream.of(StageData).mapToInt(i -> i.getMerit().size()).max().getAsInt()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		meritInformation = Stream.of(StageData).map(i -> i.getMerit().stream().map(j -> wrap(j)).toList()).toList();
		addLabel();
	}
	
	private String wrap(String comment) {
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		for(int i = 0; i < comment.length(); i++) {
			if(comment.substring(i, i + 1).equals("(") || 280 < getFontMetrics(meritFont).stringWidth(comment.substring(lastPosition, i))) {
				wrapPosition.add(i);
				lastPosition = i;
			}
		}
		if(wrapPosition.isEmpty()) {
			return comment;
		}
		StringBuilder wrapComment = new StringBuilder(comment);
		wrapPosition.stream().sorted(Comparator.reverseOrder()).forEach(i -> wrapComment.insert(i, "<br>"));
		return wrapComment.insert(0, "<html>").toString();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setPreferredSize(new Dimension(200, 70 * meritInformation.get(SelectPanel.getSelelct()).size()));
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i));
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 400, 70 * i));
	}
	
	private void addLabel() {
		Stream.of(meritLabel).forEach(i -> {
			add(i);
			i.setFont(meritFont);
		});
		Stream.of(clearLabel).forEach(i -> {
			add(i);
			i.setHorizontalAlignment(JLabel.CENTER);
			i.setForeground(Color.RED);
			i.setFont(clearFont);
		});
	}
	
	private void setLabel(int number) {
		try{
			meritLabel[number].setText(meritInformation.get(SelectPanel.getSelelct()).get(number));
			meritLabel[number].setBounds(5, number * 70, 400, 70);
			clearLabel[number].setText(meritStatus.get(SelectPanel.getSelelct()).get(number)? "clear": "");
			clearLabel[number].setBounds(290, number * 70, 100, 70);
		}catch (Exception e) {
			meritLabel[number].setText("");
			clearLabel[number].setText("");
		}
	}
}

//敵兵情報
class EnemyPanel extends JPanel implements MouseListener{
	SelectPanel SelectPanel;
	List<List<EnemyData>> enemyData;
	List<List<BufferedImage>> enemyImage;
	List<List<Integer>> enemyCount;
	int drawSize = 100;
	int column = 3;
	
	protected EnemyPanel(SelectPanel SelectPanel, StageData[] StageData) {
		BiFunction<Integer, List<List<Integer>>, Integer> count = (enemy, enemyList) -> {
			return (int) enemyList.stream().filter(i -> i.get(0) == enemy).count();
		};
		this.SelectPanel = SelectPanel;
		enemyData = Stream.of(StageData).map(i -> i.getDisplayOrder().stream().map(j -> DefaultEnemy.DATA_MAP.get(j)).toList()).toList();
		enemyImage = enemyData.stream().map(i -> i.stream().map(j -> j.getImage(2)).toList()).toList();
		enemyCount = Stream.of(StageData).map(i -> i.getDisplayOrder().stream().map(j -> count.apply(j, i.getEnemy())).toList()).toList();
		addMouseListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setPreferredSize(new Dimension(100, (enemyImage.get(SelectPanel.getSelelct()).size() / column + 1) * drawSize));
		IntStream.range(0, enemyImage.get(SelectPanel.getSelelct()).size()).forEach(i -> {
			int x = i % column * drawSize;
			int y = i / column * drawSize;
			g.drawImage(enemyImage.get(SelectPanel.getSelelct()).get(i), x, y, this);
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("" + enemyCount.get(SelectPanel.getSelelct()).get(i), 80 + x, 80 + y);
		});
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < enemyImage.get(SelectPanel.getSelelct()).size(); i++) {
			int x = i % column * drawSize + 10;
			int y = i / column * drawSize + 10;
			if(ValueRange.of(x, x + drawSize - 30).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + drawSize - 30).isValidIntValue(e.getY())){
				new DisplayStatus().enemy(enemyData.get(SelectPanel.getSelelct()).get(i));
				break;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
}