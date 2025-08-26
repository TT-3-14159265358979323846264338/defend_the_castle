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
	private JLabel comment = new JLabel();
	private JButton restartButton = new JButton();
	private JButton returnButton = new JButton();
	private JButton retryButton = new JButton();
	
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