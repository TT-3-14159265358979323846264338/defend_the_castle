package battle;

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

import defaultdata.DefaultStage;
import defaultdata.stage.StageData;
import savedata.SaveGameProgress;

class ClearMerit extends JPanel{
	private StageData StageData;
	private int stageNumber;
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private List<Boolean> clearList;
	private JLabel[] meritLabel;
	private Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private JLabel[] completeLabel;
	private Font completeFont = new Font("ＭＳ ゴシック", Font.BOLD, 30);
	private JLabel[] clearLabel;
	private Font clearFont = new Font("Arail", Font.BOLD, 30);
	
	protected ClearMerit(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		this.StageData = StageData;
		stageNumber = DefaultStage.STAGE_DATA.indexOf(StageData);
		SaveGameProgress.load();
		meritLabel = IntStream.range(0, StageData.getMerit().size()).mapToObj(i -> new JLabel(meritComment(i))).toArray(JLabel[]::new);
		completeLabel =  IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(completeComment(i))).toArray(JLabel[]::new);
		updateSaveData(UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(clearComment(i))).toArray(JLabel[]::new);
		IntStream.range(0, meritLabel.length).forEach(i -> addLabel(i));
		setPreferredSize(new Dimension(200, 70 * meritLabel.length));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i));
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(1));
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 500, 70 * i));
	}
	
	private void updateSaveData(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		int medal = SaveGameProgress.getMedal() + 100;//いずれ報酬について記載
		clearList = StageData.canClearMerit(UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection);
		List<Boolean> clearStatus = SaveGameProgress.getClearStatus();
		clearStatus.set(stageNumber, true);
		List<List<Boolean>> meritStatus = SaveGameProgress.getMeritStatus();
		for(int i = 0; i < clearList.size(); i++){
			if(clearList.get(i)){
				meritStatus.get(stageNumber).set(i, true);
			}
		}
		SaveGameProgress.save(clearStatus, meritStatus, medal, SaveGameProgress.getSelectStage());
	}
	
	private String meritComment(int number) {
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
	
	private String completeComment(int number) {
		return hasCleared(number)? "済": "";
	}
	
	private boolean hasCleared(int number) {
		return SaveGameProgress.getMeritStatus().get(stageNumber).get(number);
	}
	
	private String clearComment(int number) {
		return clearList.get(number)? "clear": "";
	}
	
	private void addLabel(int number) {
		addMeritLabel(number);
		addCompleteLabel(number);
		addClearLabel(number);
	}
	
	private void addMeritLabel(int number) {
		add(meritLabel[number]);
		meritLabel[number].setFont(meritFont);
	}
	
	private void addCompleteLabel(int number) {
		add(completeLabel[number]);
		completeLabel[number].setHorizontalAlignment(JLabel.CENTER);
		completeLabel[number].setForeground(Color.GRAY);
		completeLabel[number].setFont(completeFont);
	}
	
	private void addClearLabel(int number) {
		add(clearLabel[number]);
		clearLabel[number].setHorizontalAlignment(JLabel.CENTER);
		clearLabel[number].setForeground(Color.RED);
		clearLabel[number].setFont(clearFont);
	}
	
	private void setLabel(int number) {
		meritLabel[number].setBounds(5, number * 70, 400, 70);
		completeLabel[number].setBounds(290, number * 70, 100, 70);
		clearLabel[number].setBounds(400, number * 70, 100, 70);
	}
}
