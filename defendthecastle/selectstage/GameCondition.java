package defendthecastle.selectstage;

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.DefaultStage;

class GameCondition extends JPanel{
	private SelectPanel SelectPanel;
	private JLabel clearCommentLabel = new JLabel();
	private JLabel clearConditionLabel = new JLabel();
	private JLabel gameOverCommentLabel = new JLabel();
	private JLabel gameOverConditionLabel = new JLabel();
	private Font font = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	
	protected GameCondition(SelectPanel SelectPanel) {
		this.SelectPanel = SelectPanel;
		add(clearCommentLabel);
		clearCommentLabel.setFont(font);
		add(clearConditionLabel);
		clearConditionLabel.setFont(font);
		add(gameOverCommentLabel);
		gameOverCommentLabel.setFont(font);
		add(gameOverConditionLabel);
		gameOverConditionLabel.setFont(font);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		clearCommentLabel.setText("勝利条件: ");
		clearCommentLabel.setBounds(5, 5, 80, 40);
		clearConditionLabel.setText(conditionComment(DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()).getClearCondition()));
		clearConditionLabel.setBounds(80, 5, 240, 40);
		gameOverCommentLabel.setText("敗北条件: ");
		gameOverCommentLabel.setBounds(5, 60, 100, 40);
		gameOverConditionLabel.setText(conditionComment(DefaultStage.STAGE_DATA.get(SelectPanel.getSelelct()).getGameOverCondition()));
		gameOverConditionLabel.setBounds(80, 60, 240, 40);
	}
	
	private String conditionComment(String comment) {
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		for(int i = 0; i < comment.length(); i++) {
			if(240 < getFontMetrics(font).stringWidth(comment.substring(lastPosition, i))) {
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
}
