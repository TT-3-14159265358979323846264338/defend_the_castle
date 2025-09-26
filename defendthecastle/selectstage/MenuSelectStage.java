package defendthecastle.selectstage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import battle.BattleEnemy;
import defaultdata.DefaultStage;
import defaultdata.EditImage;
import defendthecastle.MainFrame;

//ステージ選択画面
public class MenuSelectStage extends JPanel{
	private JLabel stageLabel = new JLabel();
	private JLabel informationLabel = new JLabel();
	private JLabel clearConditionLabel = new JLabel();
	private JLabel gameOverLabel = new JLabel();
	private JButton returnButton = new JButton();
	private JButton normalModeButton = new JButton();
	private JButton hardModeButton = new JButton();
	private JScrollPane stageScroll = new JScrollPane();
	private JScrollPane enemyScroll = new JScrollPane();
	private JScrollPane meritScroll = new JScrollPane();
	private ProgressData ProgressData = new ProgressData();
	private List<BufferedImage> stageImage = DefaultStage.STAGE_DATA.stream().map(i -> EditImage.stageImage(i, 5)).toList();
	private SelectPanel SelectPanel = new SelectPanel(stageImage, ProgressData.getClearStatus(), ProgressData.getSelectStage());
	private MeritPanel MeritPanel = new MeritPanel(SelectPanel, ProgressData.getMeritStatus());
	private EnemyPanel EnemyPanel = new EnemyPanel(SelectPanel);
	
	public MenuSelectStage(MainFrame MainFrame) {
		setBackground(new Color(240, 170, 80));
		add(stageLabel);
		add(informationLabel);
		add(clearConditionLabel);
		add(gameOverLabel);
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
		setLabel(clearConditionLabel, conditionComment("勝利条件: " + DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()).getClearCondition()), 580, 350, 320, 40);
		setLabel(gameOverLabel, conditionComment("敗北条件: " + DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()).getGameOverCondition()), 580, 400, 320, 40);
		setButton(returnButton, "戻る", 10, 460, 150, 60);
		setButton(normalModeButton, "normal", 580, 460, 155, 60);
		setButton(hardModeButton, "hard", 745, 460, 155, 60);
		setScroll(stageScroll, 10, 40, 150, 410);
		setScroll(meritScroll, 170, 275, 400, 245);
		setScroll(enemyScroll, 580, 40, 320, 295);
		g.drawImage(stageImage.get(SelectPanel.getSelelct()), 170, 40, this);
		g.setColor(stageScroll.getBackground());
		g.fillRect(580, 345, 320, 100);
		requestFocus();
	}
	
	private String conditionComment(String comment) {
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		Font font = new Font("ＭＳ ゴシック", Font.BOLD, 20);
		for(int i = 0; i < comment.length(); i++) {
			if(320 < getFontMetrics(font).stringWidth(comment.substring(lastPosition, i))) {
				wrapPosition.add(i - 1);
				lastPosition = i - 1;
			}
		}
		if(wrapPosition.isEmpty()) {
			return comment;
		}
		StringBuilder wrapComment = new StringBuilder(comment);
		wrapPosition.stream().sorted(Comparator.reverseOrder()).forEach(i -> wrapComment.insert(i, "<br>"));
		return wrapComment.insert(0, "<html>").toString();
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
			MainFrame.battleDraw(DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()), ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), BattleEnemy.NORMAL_MODE);
		});
	}
	
	private void addHardModeButton(MainFrame MainFrame) {
		add(hardModeButton);
		hardModeButton.addActionListener(e->{
			ProgressData.save(SelectPanel.getSelelct());
			MainFrame.battleDraw(DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()), ProgressData.getMeritStatus().get(SelectPanel.getSelelct()), BattleEnemy.HARD_MODE);
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