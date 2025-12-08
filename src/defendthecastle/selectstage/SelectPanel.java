package defendthecastle.selectstage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.EditImage;

//ステージ切り替え
class SelectPanel extends JPanel implements MouseListener{
	private JLabel[] nameLabel;
	private JLabel[] clearLabel;
	private JLabel otherStageLabel = new JLabel();
	private List<BufferedImage> stageImage;
	private List<Boolean> clearStatus;
	private List<String> stageNameList;
	private int select = 0;
	private Font stageFont = new Font("Arial", Font.BOLD, 20);
	private Font clearFont = new Font("Arial", Font.BOLD, 30);
	private Font otherFont = new Font("ＭＳ ゴシック", Font.BOLD, 10);
	
	protected SelectPanel(ProgressData ProgressData, List<BufferedImage> stageImage) {
		this.stageImage = stageImage.stream().map(i -> EditImage.scalingImage(i, 3.5)).toList();
		clearStatus = ProgressData.getClearStatus();
		stageNameList = ProgressData.getStageName();
		select = ProgressData.getSelectStage();
		nameLabel = IntStream.range(0, ProgressData.getActivateStage().size()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		IntStream.range(0, nameLabel.length).forEach(i -> addNameLabel(i));
		clearLabel = IntStream.range(0, nameLabel.length).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		IntStream.range(0, clearLabel.length).forEach(i -> addClearLabel(i));
		addOtherLabel(ProgressData);
		addMouseListener(this);
		setPreferredSize(new Dimension(100, 85 * stageImage.size() + 30));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, nameLabel.length).forEach(i -> setNameLabel(i));
		IntStream.range(0, clearLabel.length).forEach(i -> setClearLabel(i));
		setOtherStageLabel();
		IntStream.range(0, stageImage.size()).forEach(i -> drawField(i, g));
	}
	
	private void addNameLabel(int number) {
		add(nameLabel[number]);
		nameLabel[number].setHorizontalAlignment(JLabel.CENTER);
		nameLabel[number].setText(stageNameList.get(number));
		nameLabel[number].setFont(stageFont);
	}
	
	private void addClearLabel(int number) {
		add(clearLabel[number]);
		clearLabel[number].setHorizontalAlignment(JLabel.CENTER);
		clearLabel[number].setText(clearStatus.get(number)? "clear": "");
		clearLabel[number].setFont(clearFont);
		clearLabel[number].setForeground(Color.RED);
	}
	
	private void addOtherLabel(ProgressData ProgressData) {
		add(otherStageLabel);
		otherStageLabel.setHorizontalAlignment(JLabel.CENTER);
		otherStageLabel.setText(ProgressData.canAllActivate()? "全ステージ解放済": "条件により新ステージ解放");
		otherStageLabel.setFont(otherFont);
	}
	
	private void setNameLabel(int number) {
		nameLabel[number].setBounds(0, 25 + 85 * number, 130, 30);
	}
	
	private void setClearLabel(int number) {
		clearLabel[number].setBounds(30, 50 + 85 * number, 130, 30);
	}
	
	private void setOtherStageLabel(){
		otherStageLabel.setBounds(0, 85 * nameLabel.length, 130, 30);
	}
	
	private void drawField(int number, Graphics g) {
		if(select == number) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 85 * number, 135, 85);
		}
		g.drawImage(stageImage.get(number), 10, 10 + 85 * number, this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < stageImage.size(); i++) {
			if(ValueRange.of(10, 125).isValidIntValue(e.getX())
					&& ValueRange.of(10 + 85 * i, -10 + 85 * (i + 1)).isValidIntValue(e.getY())) {
				select = i;
				break;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	protected int getSelelct() {
		return select;
	}
}