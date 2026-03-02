package defendthecastle.battle.battledialog;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import commonclass.CommonJPanel;
import defaultdata.Difficulty;
import defaultdata.Stage;
import defendthecastle.MainFrame;
import defendthecastle.battle.GameTimer;

//戻る・再戦パネル
class ReturnPanel extends CommonJPanel{
	private final PauseDialog pauseDialog;
	private final MainFrame mainFrame;
	private final GameTimer gameTimer;
	private final Stage stage;
	private final Difficulty difficulty;
	private final GameCondition gameCondition;
	private final JButton restartButton = new JButton();
	private final JButton returnButton = new JButton();
	private final JButton retryButton = new JButton();
	private final JScrollPane enemyScroll = new JScrollPane();
	private final JScrollPane meritScroll = new JScrollPane();
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	ReturnPanel(PauseDialog pauseDialog, MainFrame mainFrame, Stage stage, Difficulty difficulty, GameTimer gameTimer) {
		this.pauseDialog = pauseDialog;
		this.mainFrame = mainFrame;
		this.gameTimer = gameTimer;
		this.stage = stage;
		this.difficulty = difficulty;
		gameCondition = createGameCondition();
		gameCondition.setBounds(50, 10, 430, 150);
		add(gameCondition);
		setButton(restartButton, "再開", 75, 530, 120, 40, font, this::restartButtonAction);
		setButton(returnButton, "退却", 205, 530, 120, 40, font, this::returnButtonAction);
		setButton(retryButton, "再挑戦", 335, 530, 120, 40, font, this::retryButtonAction);
		setScroll(enemyScroll, 50, 170, 430, 200, createAllEnemy());
		setScroll(meritScroll, 50, 380, 430, 140, createClearMerit());
		stillness(brown());
	}
	
	GameCondition createGameCondition() {
		return new GameCondition(stage.getLabel(), difficulty);
	}
	
	AllEnemy createAllEnemy() {
		return new AllEnemy(stage.getLabel());
	}
	
	PauseClearPanel createClearMerit() {
		return new PauseClearPanel(stage);
	}
	
	void restartButtonAction(ActionEvent e) {
		pauseDialog.disposeDialog();
	}
	
	void returnButtonAction(ActionEvent e) {
		mainFrame.selectStageDraw();
		gameTimer.gameEnd();
		pauseDialog.disposeDialog();
	}
	
	void retryButtonAction(ActionEvent e) {
		mainFrame.battleDraw(stage, difficulty);
		gameTimer.gameEnd();
		pauseDialog.disposeDialog();
	}
}