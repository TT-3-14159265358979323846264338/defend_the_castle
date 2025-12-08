package battle.battledialog;

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import battle.BattleEnemy;
import defaultdata.stage.StageData;

class GameCondition extends JPanel{
	private JLabel difficultyCommentLabel = new JLabel();
	private JLabel difficultyConditionLabel = new JLabel();
	private JLabel clearCommentLabel = new JLabel();
	private JLabel clearConditionLabel = new JLabel();
	private JLabel gameOverCommentLabel = new JLabel();
	private JLabel gameOverConditionLabel = new JLabel();
	private Font font = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	
	GameCondition(StageData StageData, double difficultyCorrection) {
		addDifficultyCommentLabel();
		addDifficultyConditionLabel(difficultyCorrection);
		addClearCommentLabel();
		addClearConditionLabel(StageData);
		addGameOverCommentLabel();
		addGameOverConditionLabel(StageData);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		difficultyCommentLabel.setBounds(5, 5, 80, 40);
		difficultyConditionLabel.setBounds(80, 5, 350, 40);
		clearCommentLabel.setBounds(5, 50, 80, 40);
		clearConditionLabel.setBounds(80, 50, 350, 40);
		gameOverCommentLabel.setBounds(5, 105, 100, 40);
		gameOverConditionLabel.setBounds(80, 105, 350, 40);
	}
	
	void addDifficultyCommentLabel() {
		add(difficultyCommentLabel);
		difficultyCommentLabel.setText("難易度: ");
		difficultyCommentLabel.setFont(font);
	}
	
	void addDifficultyConditionLabel(double difficultyCorrection) {
		add(difficultyConditionLabel);
		difficultyConditionLabel.setText((difficultyCorrection == BattleEnemy.NORMAL_MODE)? "normal": "hard");
		difficultyConditionLabel.setFont(font);
	}
	
	void addClearCommentLabel() {
		add(clearCommentLabel);
		clearCommentLabel.setText("勝利条件: ");
		clearCommentLabel.setFont(font);
	}
	
	void addClearConditionLabel(StageData StageData) {
		add(clearConditionLabel);
		clearConditionLabel.setText(conditionComment(StageData.getClearCondition()));
		clearConditionLabel.setFont(font);
	}
	
	void addGameOverCommentLabel() {
		add(gameOverCommentLabel);
		gameOverCommentLabel.setText("敗北条件: ");
		gameOverCommentLabel.setFont(font);
	}
	
	void addGameOverConditionLabel(StageData StageData) {
		add(gameOverConditionLabel);
		gameOverConditionLabel.setText(conditionComment(StageData.getGameOverCondition()));
		gameOverConditionLabel.setFont(font);
	}
	
	String conditionComment(String comment) {
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		for(int i = 0; i < comment.length(); i++) {
			if(350 < getFontMetrics(font).stringWidth(comment.substring(lastPosition, i))) {
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
