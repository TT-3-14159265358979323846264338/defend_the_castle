package defendthecastle.battle.battledialog;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;

import commonclass.CommonJPanel;
import defaultdata.Difficulty;
import defaultdata.stage.StageData;

class GameCondition extends CommonJPanel{
	private final JLabel difficultyCommentLabel = new JLabel();
	private final JLabel difficultyConditionLabel = new JLabel();
	private final JLabel clearCommentLabel = new JLabel();
	private final JLabel clearConditionLabel = new JLabel();
	private final JLabel gameOverCommentLabel = new JLabel();
	private final JLabel gameOverConditionLabel = new JLabel();
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	
	GameCondition(StageData StageData, Difficulty difficulty) {
		setLabel(difficultyCommentLabel, "難易度: ", 5, 5, 80, 40, font);
		setLabel(difficultyConditionLabel, difficulty.getLabel(), 80, 5, 350, 40, font);
		setLabel(clearCommentLabel, "勝利条件: ", 5, 50, 80, 40, font);
		setLabel(clearConditionLabel, conditionComment(StageData.getClearCondition()), 80, 50, 350, 40, font);
		setLabel(gameOverCommentLabel, "敗北条件: ", 5, 105, 100, 40, font);
		setLabel(gameOverConditionLabel, conditionComment(StageData.getGameOverCondition()), 80, 105, 350, 40, font);
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
