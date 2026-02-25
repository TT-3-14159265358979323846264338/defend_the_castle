package defendthecastle.selectstage;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;

import commonclass.CommonJPanel;
import defaultdata.Stage;

class GameCondition extends CommonJPanel{
	private final JLabel clearCommentLabel = new JLabel();
	private final JLabel clearConditionLabel = new JLabel();
	private final JLabel gameOverCommentLabel = new JLabel();
	private final JLabel gameOverConditionLabel = new JLabel();
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final List<List<String>> condition;
	
	GameCondition(ProgressData progressData) {
		condition = conditionData(progressData);
		setLabel(clearCommentLabel, "勝利条件: ", 5, 5, 80, 40, font);
		setLabel(clearConditionLabel, "", 80, 5, 240, 40, font);
		setLabel(gameOverCommentLabel, "敗北条件: ", 5, 60, 100, 40, font);
		setLabel(gameOverConditionLabel, "", 80, 60, 240, 40, font);
		stillness(defaultWhite());
	}
	
	List<List<String>> conditionData(ProgressData progressData){
		return progressData.getActivateStage().stream().map(i -> condition(i)).toList();
	}
	
	List<String> condition(Stage stage){
		List<String> stageCondition = new ArrayList<>();
		stageCondition.add(conditionComment(stage.getStageData().getClearCondition()));
		stageCondition.add(conditionComment(stage.getStageData().getGameOverCondition()));
		return stageCondition;
	}
	
	String conditionComment(String comment) {
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
	
	void changeSelect(int select) {
		clearConditionLabel.setText(condition.get(select).get(0));
		gameOverConditionLabel.setText(condition.get(select).get(1));
	}
}
