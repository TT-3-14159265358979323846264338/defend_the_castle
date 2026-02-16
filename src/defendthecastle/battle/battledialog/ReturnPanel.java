package defendthecastle.battle.battledialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.stage.StageData;
import defendthecastle.MainFrame;
import defendthecastle.battle.Battle;

//戻る・再戦パネル
class ReturnPanel extends JPanel{
	private GameCondition GameCondition;
	private JScrollPane enemyScroll = new JScrollPane();
	private JScrollPane meritScroll = new JScrollPane();
	private JButton restartButton = new JButton();
	private JButton returnButton = new JButton();
	private JButton retryButton = new JButton();
	private Font buttonFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	ReturnPanel(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame, StageData StageData, double difficultyCorrection) {
		setBackground(new Color(240, 170, 80));
		addGameCondition(StageData, difficultyCorrection);
		addEnemyScroll(StageData);
		addMeritScroll(StageData);
		addRestartButton(PauseDialog);
		addReturnButton(PauseDialog, Battle, MainFrame);
		addRetryButton(PauseDialog, Battle, MainFrame, StageData, difficultyCorrection);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		GameCondition.setBounds(50, 10, 430, 150);
		enemyScroll.setBounds(50, 170, 430, 200);
		enemyScroll.setPreferredSize(enemyScroll.getSize());
		meritScroll.setBounds(50, 380, 430, 140);
		meritScroll.setPreferredSize(meritScroll.getSize());
		restartButton.setBounds(75, 530, 120, 40);
		returnButton.setBounds(205, 530, 120, 40);
		retryButton.setBounds(335, 530, 120, 40);
	}
	
	void addGameCondition(StageData StageData, double difficultyCorrection) {
		GameCondition = new GameCondition(StageData, difficultyCorrection);
		add(GameCondition);
	}
	
	void addEnemyScroll(StageData StageData) {
		enemyScroll.getViewport().setView(new AllEnemy(StageData));
		add(enemyScroll);
	}
	
	void addMeritScroll(StageData StageData) {
		meritScroll.getViewport().setView(new ClearMerit(StageData));
		add(meritScroll);
	}
	
	void addRestartButton(PauseDialog PauseDialog) {
		add(restartButton);
		restartButton.addActionListener(_ ->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
		restartButton.setFont(buttonFont);
	}
	
	void addReturnButton(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(_ ->{
			PauseDialog.disposeDialog();
			MainFrame.selectStageDraw();
		});
		returnButton.setText("退却");
		returnButton.setFont(buttonFont);
	}
	
	void addRetryButton(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame, StageData StageData, double difficultyCorrection) {
		add(retryButton);
		retryButton.addActionListener(_ ->{
			PauseDialog.disposeDialog();
			MainFrame.battleDraw(StageData, difficultyCorrection);
		});
		retryButton.setText("再挑戦");
		retryButton.setFont(buttonFont);
	}
}