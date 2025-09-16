package battle;

import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.stage.StageData;
import defendthecastle.MainFrame;

//戻る・再戦パネル
class ReturnPanel extends JPanel{
	private JLabel commentLabel = new JLabel();
	private JButton restartButton = new JButton();
	private JButton returnButton = new JButton();
	private JButton retryButton = new JButton();
	
	protected ReturnPanel(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, double difficultyCorrection) {
		addCommentLabel();
		addRestartButton(PauseDialog);
		addReturnButton(PauseDialog, Battle, MainFrame);
		addRetryButton(PauseDialog, Battle, MainFrame, StageData, clearMerit, difficultyCorrection);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		commentLabel.setBounds(10, 10, 380, 40);
		commentLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		restartButton.setBounds(10, 50, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		returnButton.setBounds(140, 50, 120, 40);
		returnButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		retryButton.setBounds(270, 50, 120, 40);
		retryButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void addCommentLabel() {
		add(commentLabel);
		commentLabel.setText("ゲーム操作を選択してください");
		commentLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void addRestartButton(PauseDialog PauseDialog) {
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
	}
	
	private void addReturnButton(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(e->{
			Battle.gameEnd();
			PauseDialog.disposeDialog();
			MainFrame.selectStageDraw();
		});
		returnButton.setText("降参");
	}
	
	private void addRetryButton(PauseDialog PauseDialog, Battle Battle, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, double difficultyCorrection) {
		add(retryButton);
		retryButton.addActionListener(e->{
			Battle.gameEnd();
			PauseDialog.disposeDialog();
			MainFrame.battleDraw(StageData, clearMerit, difficultyCorrection);
		});
		retryButton.setText("再戦");
	}
}