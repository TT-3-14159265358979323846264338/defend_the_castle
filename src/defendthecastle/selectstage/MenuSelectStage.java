package defendthecastle.selectstage;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import commonclass.CommonJPanel;
import defendthecastle.MainFrame;
import defendthecastle.battle.BattleEnemy;

//ステージ選択画面
public class MenuSelectStage extends CommonJPanel{
	private final MainFrame mainFrame;
	private final ProgressData progressData;
	private final StageImage stageImage;
	private final SelectPanel selectPanel;
	private final MeritPanel meritPanel;
	private final EnemyPanel enemyPanel;
	private final GameCondition gameCondition;
	private final JLabel stageLabel = new JLabel();
	private final JLabel informationLabel = new JLabel();
	private final JButton returnButton = new JButton();
	private final JButton normalModeButton = new JButton();
	private final JButton hardModeButton = new JButton();
	private final JScrollPane stageScroll = new JScrollPane();
	private final JScrollPane enemyScroll = new JScrollPane();
	private final JScrollPane meritScroll = new JScrollPane();
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	public MenuSelectStage(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		progressData = createProgressData();
		stageImage = createStageImage();
		meritPanel = createMeritPanel();
		enemyPanel = createEnemyPanel();
		gameCondition = createGameCondition();
		selectPanel = createSelectPanel();
		setLabel(stageLabel, "ステージ選択", 10, 10, 200, 30, font);
		setLabel(informationLabel, "ステージ情報", 170, 10, 200, 30, font);
		setButton(returnButton, "戻る", 10, 460, 150, 60, font, this::returnButtonAction);
		setButton(normalModeButton, "normal", 580, 460, 155, 60, font, this::normalModeButtonAction);
		setButton(hardModeButton, "hard", 745, 460, 155, 60, font, this::hardModeButtonAction);
		setScroll(stageScroll, 10, 40, 150, 410, selectPanel);
		setScroll(meritScroll, 170, 275, 400, 245, meritPanel);
		setScroll(enemyScroll, 580, 40, 320, 295, enemyPanel);
		setGameCondition();
		stillness(brown());
	}
	
	ProgressData createProgressData() {
		return new ProgressData();
	}
	
	StageImage createStageImage() {
		return new StageImage(progressData);
	}
	
	MeritPanel createMeritPanel() {
		return new MeritPanel(progressData);
	}
	
	EnemyPanel createEnemyPanel() {
		return new EnemyPanel(progressData);
	}
	
	GameCondition createGameCondition() {
		return new GameCondition(progressData);
	}
	
	SelectPanel createSelectPanel() {
		return new SelectPanel(progressData, stageImage, meritPanel, gameCondition, enemyPanel, this);
	}
	
	void returnButtonAction(ActionEvent e) {
		mainFrame.mainMenuDraw();
	}
	
	void normalModeButtonAction(ActionEvent e) {
		battleStart(BattleEnemy.NORMAL_MODE);
	}
	
	void hardModeButtonAction(ActionEvent e) {
		battleStart(BattleEnemy.HARD_MODE);
	}
	
	private void battleStart(double mode) {
		progressData.save(selectPanel.getSelelct());
		mainFrame.battleDraw(progressData.getActivateStage().get(selectPanel.getSelelct()), mode);
	}
	
	private void setGameCondition() {
		gameCondition.setBounds(580, 345, 320, 105);
		add(gameCondition);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(stageImage.getDetailImage(selectPanel.getSelelct()), 170, 40, this);
	}
	
	@Override
	protected void repaintPanel() {
		super.repaintPanel();
	}
}