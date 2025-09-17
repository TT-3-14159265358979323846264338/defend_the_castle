package defendthecastle.selectstage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import battle.BattleEnemy;
import defaultdata.DefaultStage;
import defaultdata.EditImage;
import defaultdata.stage.StageData;
import defendthecastle.MainFrame;

//ステージ選択画面
public class MenuSelectStage extends JPanel{
	private JLabel stageLabel = new JLabel();
	private JLabel informationLabel = new JLabel();
	private JButton returnButton = new JButton();
	private JButton normalModeButton = new JButton();
	private JButton hardModeButton = new JButton();
	private JScrollPane stageScroll = new JScrollPane();
	private JScrollPane enemyScroll = new JScrollPane();
	private JScrollPane meritScroll = new JScrollPane();
	private ProgressData ProgressData = new ProgressData();
	private StageData[] StageData = IntStream.range(0, DefaultStage.STAGE_DATA_MAP.size()).mapToObj(i -> DefaultStage.STAGE_DATA_MAP.get(i)).toArray(StageData[]::new);
	private List<BufferedImage> stageImage = Stream.of(StageData).map(i -> EditImage.stageImage(i, 5)).toList();
	private SelectPanel SelectPanel = new SelectPanel(stageImage, ProgressData.getSelectStage(), StageData);
	private MeritPanel MeritPanel = new MeritPanel(SelectPanel, ProgressData.getMeritStatus(), StageData);
	private EnemyPanel EnemyPanel = new EnemyPanel(SelectPanel, StageData);
	
	public MenuSelectStage(MainFrame MainFrame) {
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
			MainFrame.battleDraw(StageData[SelectPanel.getSelelct()], ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), BattleEnemy.NORMAL_MODE);
		});
	}
	
	private void addHardModeButton(MainFrame MainFrame) {
		add(hardModeButton);
		hardModeButton.addActionListener(e->{
			ProgressData.save(SelectPanel.getSelelct());
			MainFrame.battleDraw(StageData[SelectPanel.getSelelct()], ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), BattleEnemy.HARD_MODE);
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