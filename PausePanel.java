package battle;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

//一時停止表示パネル
class PausePanel extends JPanel{
	private JLabel comment = new JLabel();
	private JButton restartButton = new JButton();
	
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