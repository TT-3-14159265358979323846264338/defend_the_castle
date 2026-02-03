package battle.battledialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import battle.GameData;
import defaultdata.DefaultStage;
import defaultdata.stage.StageData;
import savedata.OneStageMeritData;
import savedata.SaveGameProgress;

class ClearMerit extends JPanel{
	private int stageNumber;
	private List<Boolean> thisClearList;
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private JLabel[] meritLabel;
	private Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private JLabel[] rewardLabel;
	private Font rewardFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private JLabel[] completeLabel;
	private Font completeFont = new Font("ＭＳ ゴシック", Font.BOLD, 40);
	private JLabel[] clearLabel;
	private Font clearFont = new Font("Arail", Font.BOLD, 30);
	
	ClearMerit(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		thisClearList = StageData.canClearMerit(UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection);
		beforeSet(StageData);
		updateClearData(StageData);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(clearComment(i))).toArray(JLabel[]::new);
		afterSet();
	}
	
	ClearMerit(StageData StageData) {
		beforeSet(StageData);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		afterSet();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i));
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(1));
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 500, 70 * i));
	}
	
	void beforeSet(StageData StageData) {
		stageNumber = DefaultStage.STAGE_DATA.indexOf(StageData);
		SaveGameProgress.load();
		meritLabel = IntStream.range(0, StageData.getMerit().size()).mapToObj(i -> new JLabel(meritComment(i, StageData))).toArray(JLabel[]::new);
		rewardLabel = StageData.getReward().stream().map(i -> new JLabel(i)).toArray(JLabel[]::new);
		completeLabel =  IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(completeComment(i))).toArray(JLabel[]::new);
	}
	
	void afterSet() {
		IntStream.range(0, meritLabel.length).forEach(i -> addLabel(i));
		setPreferredSize(new Dimension(200, 70 * meritLabel.length));
	}
	
	void updateClearData(StageData StageData) {
		List<Boolean> newClearList = new ArrayList<>();
		for(int i = 0; i < thisClearList.size(); i++){
			if(thisClearList.get(i) && !hasCleared(i)){
				getMeritData().setMeritClear(i, true);
				newClearList.add(true);
				continue;
			}
			newClearList.add(false);
		}
		if(getMeritData().getMeritClearList().stream().allMatch(i -> i)) {
			SaveGameProgress.setStage(stageNumber, true);
		}
		SaveGameProgress.save();
		StageData.giveClearReward(newClearList);
	}
	
	String meritComment(int number, StageData StageData) {
		String comment = StageData.getMerit().get(number);
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		for(int i = 0; i < comment.length(); i++) {
			if(comment.substring(i, i + 1).equals("(") || 280 < getFontMetrics(meritFont).stringWidth(comment.substring(lastPosition, i))) {
				wrapPosition.add(i);
				lastPosition = i;
			}
		}
		if(wrapPosition.isEmpty()) {
			return comment;
		}
		StringBuilder wrapComment = new StringBuilder(comment);
		wrapPosition.stream().sorted(Comparator.reverseOrder()).forEach(i -> wrapComment.insert(i, "<br>"));
		return wrapComment.insert(0, "<html>").toString();
	}
	
	String completeComment(int number) {
		return hasCleared(number)? "済": "";
	}
	
	OneStageMeritData getMeritData() {
		return SaveGameProgress.getMeritData(stageNumber);
	}
	
	boolean hasCleared(int number) {
		return getMeritData().getMeritClear(number);
	}
	
	String clearComment(int number) {
		return thisClearList.get(number)? "clear": "";
	}
	
	void addLabel(int number) {
		addMeritLabel(number);
		addRewardLabel(number);
		addCompleteLabel(number);
		addClearLabel(number);
	}
	
	void addMeritLabel(int number) {
		add(meritLabel[number]);
		meritLabel[number].setFont(meritFont);
	}
	
	void addRewardLabel(int number) {
		add(rewardLabel[number]);
		rewardLabel[number].setFont(rewardFont);
		rewardLabel[number].setHorizontalAlignment(JLabel.CENTER);
	}
	
	void addCompleteLabel(int number) {
		add(completeLabel[number]);
		completeLabel[number].setHorizontalAlignment(JLabel.CENTER);
		completeLabel[number].setForeground(Color.GRAY);
		completeLabel[number].setFont(completeFont);
	}
	
	void addClearLabel(int number) {
		add(clearLabel[number]);
		clearLabel[number].setHorizontalAlignment(JLabel.CENTER);
		clearLabel[number].setForeground(Color.RED);
		clearLabel[number].setFont(clearFont);
	}
	
	void setLabel(int number) {
		meritLabel[number].setBounds(5, number * 70, 400, 70);
		rewardLabel[number].setBounds(290, number * 70, 100, 70);
		completeLabel[number].setBounds(290, number * 70, 100, 70);
		clearLabel[number].setBounds(400, number * 70, 100, 70);
	}
}
