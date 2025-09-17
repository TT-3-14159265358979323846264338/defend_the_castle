package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.stage.StageData;
import savedata.SaveGameProgress;

class ClearMerit extends JPanel{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private StageData StageData;
	private BattleUnit[] UnitMainData;
	private BattleUnit[] UnitLeftData;
	private BattleFacility[] FacilityData;
	private BattleEnemy[] EnemyData;
	private GameData GameData;
	private JLabel[] meritLabel;
	private Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private JLabel[] clearLabel;
	private Font clearFont = new Font("Arail", Font.BOLD, 30);
	
	
	
	
	protected ClearMerit(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		SaveGameProgress.load();
		this.StageData = StageData;
		this.UnitMainData = UnitMainData;
		this.UnitLeftData = UnitLeftData;
		this.FacilityData = FacilityData;
		this.EnemyData = EnemyData;
		this.GameData = GameData;
		meritLabel = IntStream.range(0, StageData.getMerit().size()).mapToObj(i -> new JLabel(meritComment(i))).toArray(JLabel[]::new);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		IntStream.range(0, meritLabel.length).forEach(i -> addLabel(i));
		setPreferredSize(new Dimension(200, 70 * meritLabel.length));
		
		
		
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i));
		
		
		
		
		
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
	
	private String clearComment(int number) {
		//ステージ番号が分からない　どうしようなな
		if(SaveGameProgress.getMeritStatus().get(number).get(number)) {
			return "clear";
		}
		return "";
	}
	
	private void addLabel(int number) {
		addMeritLabel(number);
		addClearLabel(number);
		
		
		
		
	}
	
	private void addMeritLabel(int number) {
		add(meritLabel[number]);
		meritLabel[number].setFont(meritFont);
	}
	
	private void addClearLabel(int number) {
		add(clearLabel[number]);
		clearLabel[number].setHorizontalAlignment(JLabel.CENTER);
		clearLabel[number].setForeground(Color.RED);
		clearLabel[number].setFont(clearFont);
	}
	
	private void setLabel(int number) {
		meritLabel[number].setBounds(5, number * 70, 400, 70);
		clearLabel[number].setBounds(290, number * 70, 100, 70);
		
		
		
		
	}
	
	
	
	
	
	
	
	
}
