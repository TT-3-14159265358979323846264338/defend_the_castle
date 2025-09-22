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
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.DefaultStage;

//戦功情報
class MeritPanel extends JPanel{
	private JLabel[] meritLabel;
	private JLabel[] clearLabel;
	private JLabel[] rewardLabel;
	private SelectPanel SelectPanel;
	private List<List<Boolean>> meritStatus;
	private List<List<String>> meritInformation;
	private List<List<String>> rewardInformation;
	private Font meritFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private Font clearFont = new Font("Arail", Font.BOLD, 30);
	private Font rewardFont = meritFont;
	
	protected MeritPanel(SelectPanel SelectPanel, List<List<Boolean>> meritStatus) {
		this.SelectPanel = SelectPanel;
		this.meritStatus = meritStatus;
		meritLabel = IntStream.range(0, DefaultStage.STAGE_DATA.stream().mapToInt(i -> i.getMerit().size()).max().getAsInt()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		rewardLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		meritInformation = DefaultStage.STAGE_DATA.stream().map(i -> i.getMerit().stream().map(j -> wrap(j)).toList()).toList();
		rewardInformation = DefaultStage.STAGE_DATA.stream().map(i -> i.getReward()).toList();
		addLabel();
	}
	
	private String wrap(String comment) {
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
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setPreferredSize(new Dimension(200, 70 * meritInformation.get(SelectPanel.getSelelct()).size()));
		IntStream.range(0, meritLabel.length).forEach(i -> setLabel(i));
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(1));
		IntStream.range(0, meritLabel.length).forEach(i -> g.drawLine(0, 70 * i, 400, 70 * i));
	}
	
	private void addLabel() {
		Stream.of(meritLabel).forEach(i -> {
			add(i);
			i.setFont(meritFont);
		});
		Stream.of(clearLabel).forEach(i -> {
			add(i);
			i.setHorizontalAlignment(JLabel.CENTER);
			i.setForeground(Color.RED);
			i.setFont(clearFont);
		});
		Stream.of(rewardLabel).forEach(i -> {
			add(i);
			i.setHorizontalAlignment(JLabel.CENTER);
			i.setFont(rewardFont);
		});
	}
	
	private void setLabel(int number) {
		try{
			meritLabel[number].setText(meritInformation.get(SelectPanel.getSelelct()).get(number));
			meritLabel[number].setBounds(5, number * 70, 400, 70);
			clearLabel[number].setText(meritStatus.get(SelectPanel.getSelelct()).get(number)? "clear": "");
			clearLabel[number].setBounds(290, number * 70, 100, 70);
			rewardLabel[number].setText(rewardInformation.get(SelectPanel.getSelelct()).get(number));
			rewardLabel[number].setBounds(290, number * 70, 100, 70);
		}catch (Exception e) {
			meritLabel[number].setText("");
			clearLabel[number].setText("");
			rewardLabel[number].setText("");
		}
	}
}