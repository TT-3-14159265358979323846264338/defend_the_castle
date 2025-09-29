package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.stage.StageData;

//戦功表示パネル
class MeritPanel extends JPanel{
	private JButton restartButton = new JButton();
	private JScrollPane meritScroll = new JScrollPane();
	
	protected MeritPanel(PauseDialog PauseDialog, StageData StageData, List<Boolean> clearMerit) {
		addRestartButton(PauseDialog);
		addMeritScroll(StageData, clearMerit);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		restartButton.setBounds(150, 170, 120, 40);
		restartButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		meritScroll.setBounds(10, 10, 400, 150);
		meritScroll.setPreferredSize(meritScroll.getSize());
	}
	
	private void addRestartButton(PauseDialog PauseDialog) {
		add(restartButton);
		restartButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		restartButton.setText("再開");
	}
	
	private void addMeritScroll(StageData StageData, List<Boolean> clearMerit) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(390, 20 * clearMerit.size()));
		IntStream.range(0, clearMerit.size()).forEach(i -> {
			JLabel label = new JLabel();
			label.setText(StageData.getMerit().get(i));
			if(clearMerit.get(i)) {
				label.setForeground(Color.LIGHT_GRAY);
			}else {
				label.setForeground(Color.BLACK);
			}
			panel.add(label);
		});
		meritScroll.getViewport().setView(panel);
		add(meritScroll);
	}
}
