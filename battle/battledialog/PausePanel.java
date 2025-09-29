package battle;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

//一時停止表示パネル
class PausePanel extends JPanel{
	private JLabel commentLabel = new JLabel();
	private JButton restartButton = new JButton();
	
	protected PausePanel(PauseDialog PauseDialog) {
		addCommentLabel();
		addRestartButton(PauseDialog);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		commentLabel.setBounds(10, 10, 250, 40);
		commentLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		restartButton.setBounds(75, 50, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void addCommentLabel() {
		add(commentLabel);
		commentLabel.setText("ゲームを一時停止しています");
		commentLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void addRestartButton(PauseDialog PauseDialog) {
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
	}
}
