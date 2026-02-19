package defendthecastle.selectstage;

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

import commoninheritance.CommonJPanel;
import defaultdata.DefaultStage;

//戦功情報
class MeritPanel extends CommonJPanel{
	private final List<List<Boolean>> meritStatus;
	private final List<List<String>> meritInformation;
	private final List<List<String>> rewardInformation;
	private final JLabel[] meritLabel;
	private final JLabel[] clearLabel;
	private final JLabel[] rewardLabel;
	private final Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font clearFont = new Font("Arail", Font.BOLD, 30);
	private final Font rewardFont = meritFont;
	private final Dimension dimension = new Dimension();
	private final int PANEL_SIZE =  200;
	private final BasicStroke stroke = new BasicStroke(1);
	
	MeritPanel(ProgressData progressData) {
		meritStatus = progressData.getMeritStatus();
		meritInformation = progressData.getActivateStage().stream().map(i -> informationList(i)).toList();
		rewardInformation = progressData.getActivateStage().stream().map(i -> DefaultStage.STAGE_DATA.get(i).getReward()).toList();
		meritLabel = IntStream.range(0, labelNumber(progressData)).mapToObj(this::addMeritLabel).toArray(JLabel[]::new);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(this::addClearLabel).toArray(JLabel[]::new);
		rewardLabel = IntStream.range(0, meritLabel.length).mapToObj(this::addRewardLabel).toArray(JLabel[]::new);
		setPreferredSize(dimension);
		stillness(defaultWhite());
	}
	
	int labelNumber(ProgressData progressData) {
		return progressData.getActivateStage().stream()
				.mapToInt(i -> DefaultStage.STAGE_DATA.get(i).getMerit().size())
				.max()
				.getAsInt();
	}
	
	List<String> informationList(int number){
		return DefaultStage.STAGE_DATA.get(number).getMerit().stream().map(j -> wrap(j)).toList();
	}
	
	String wrap(String comment) {
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
	
	JLabel addMeritLabel(int number) {
		var label = new JLabel();
		setLabel(label, "", 5, number * 70, 400, 70, meritFont);
		return label;
	}
	
	JLabel addClearLabel(int number) {
		var label = new JLabel();
		setLabel(label, "", 290, number * 70, 100, 70, clearFont);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.RED);
		return label;
	}
	
	JLabel addRewardLabel(int number) {
		var label = new JLabel();
		setLabel(label, "", 290, number * 70, 100, 70, rewardFont);
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(stroke);
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 400, 70 * i));
	}
	
	void changeSelect(int select) {
		dimension.setSize(PANEL_SIZE, dimensionHeight(select));
		changeLabelText(select);
		revalidate();
		repaintPanel();
	}
	
	int dimensionHeight(int select) {
		return 70 * meritInformation.get(select).size();
	}
	
	void changeLabelText(int select) {
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i, select));
	}
	
	void setLabel(int number, int select) {
		try{
			meritLabel[number].setText(meritInformation.get(select).get(number));
			clearLabel[number].setText(meritStatus.get(select).get(number)? "clear": "");
			rewardLabel[number].setText(rewardInformation.get(select).get(number));
		}catch (Exception e) {
			meritLabel[number].setText("");
			clearLabel[number].setText("");
			rewardLabel[number].setText("");
		}
	}
}