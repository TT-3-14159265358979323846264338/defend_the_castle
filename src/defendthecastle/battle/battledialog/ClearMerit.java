package defendthecastle.battle.battledialog;

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

import commonclass.CommonJPanel;
import defaultdata.Stage;
import savedata.OneStageMeritData;
import savedata.SaveGameProgress;

abstract class ClearMerit extends CommonJPanel{
	protected int stageNumber;
	protected SaveGameProgress saveGameProgress;
	protected JLabel[] meritLabel;
	private JLabel[] rewardLabel;
	private JLabel[] completeLabel;
	protected JLabel[] clearLabel;
	private final Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font rewardFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font completeFont = new Font("ＭＳ ゴシック", Font.BOLD, 40);
	private final Font clearFont = new Font("Arail", Font.BOLD, 30);
	private final BasicStroke stroke = new BasicStroke(1);
	
	protected void beforeSet(Stage stage) {
		saveGameProgress = createSaveGameProgress();
		saveGameProgress.load();
		stageNumber = stage.getId();
		meritLabel = IntStream.range(0, stage.getLabel().getMerit().size()).mapToObj(i -> new JLabel(meritComment(i, stage))).toArray(JLabel[]::new);
		rewardLabel = stage.getLabel().getReward().stream().map(i -> new JLabel(i)).toArray(JLabel[]::new);
		completeLabel =  IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(completeComment(i))).toArray(JLabel[]::new);
	}
	
	SaveGameProgress createSaveGameProgress() {
		return new SaveGameProgress();
	}
	
	String meritComment(int number, Stage stage) {
		String comment = stage.getLabel().getMerit().get(number);
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
	
	protected boolean hasCleared(int number) {
		return getMeritData().getMeritClear(number);
	}
	
	protected OneStageMeritData getMeritData() {
		return saveGameProgress.getMeritData(stageNumber);
	}
	
	protected void afterSet() {
		IntStream.range(0, meritLabel.length).forEach(i -> addLabel(i));
		setPreferredSize(createDimension());
		stillness(defaultWhite());
	}
	
	void addLabel(int number) {
		addMeritLabel(number);
		addRewardLabel(number);
		addCompleteLabel(number);
		addClearLabel(number);
	}
	
	void addMeritLabel(int number) {
		meritLabel[number].setFont(meritFont);
		meritLabel[number].setBounds(5, number * 70, 400, 70);
		add(meritLabel[number]);
	}
	
	void addRewardLabel(int number) {
		rewardLabel[number].setFont(rewardFont);
		rewardLabel[number].setHorizontalAlignment(JLabel.CENTER);
		rewardLabel[number].setBounds(290, number * 70, 100, 70);
		add(rewardLabel[number]);
	}
	
	void addCompleteLabel(int number) {
		completeLabel[number].setHorizontalAlignment(JLabel.CENTER);
		completeLabel[number].setForeground(Color.GRAY);
		completeLabel[number].setFont(completeFont);
		completeLabel[number].setBounds(290, number * 70, 100, 70);
		add(completeLabel[number]);
	}
	
	void addClearLabel(int number) {
		clearLabel[number].setHorizontalAlignment(JLabel.CENTER);
		clearLabel[number].setForeground(Color.RED);
		clearLabel[number].setFont(clearFont);
		clearLabel[number].setBounds(400, number * 70, 100, 70);
		add(clearLabel[number]);
	}
	
	Dimension createDimension() {
		return new Dimension(200, 70 * meritLabel.length);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(stroke);
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 500, 70 * i));
	}
}
