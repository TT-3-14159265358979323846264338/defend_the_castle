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
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import defaultdata.DefaultStage;
import defaultdata.EditImage;

//ステージ切り替え
class SelectPanel extends JPanel implements MouseListener{
	private JLabel[] nameLabel = IntStream.range(0, DefaultStage.STAGE_DATA.size()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
	private JLabel[] clearLabel = IntStream.range(0, DefaultStage.STAGE_DATA.size()).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
	private List<BufferedImage> stageImage;
	private List<Boolean> clearStatus;
	private List<String> stageNameList;
	private int select = 0;
	
	protected SelectPanel(List<BufferedImage> stageImage, List<Boolean> clearStatus, int select) {
		Stream.of(nameLabel).forEach(i -> addNameLabel(i));
		Stream.of(clearLabel).forEach(i -> addClearLabel(i));
		this.stageImage = stageImage.stream().map(i -> EditImage.scalingImage(i, 3.5)).toList();
		this.clearStatus = clearStatus;
		stageNameList = DefaultStage.STAGE_DATA.stream().map(i -> i.getName()).toList();
		this.select = select;
		addMouseListener(this);
		setPreferredSize(new Dimension(100, 85 * stageImage.size()));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, nameLabel.length).forEach(i -> setNameLabel(i));
		IntStream.range(0, clearLabel.length).forEach(i -> setClearLabel(i));
		IntStream.range(0, stageImage.size()).forEach(i -> drawField(i, g));
	}
	
	private void addNameLabel(JLabel label) {
		add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void addClearLabel(JLabel label) {
		add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.RED);
	}
	
	private void setNameLabel(int number) {
		nameLabel[number].setText(stageNameList.get(number));
		nameLabel[number].setBounds(0, 25 + 85 * number, 130, 30);
		nameLabel[number].setFont(new Font("Arial", Font.BOLD, 20));
	}
	
	private void setClearLabel(int number) {
		clearLabel[number].setText(clearStatus.get(number)? "clear": "");
		clearLabel[number].setBounds(30, 50 + 85 * number, 130, 30);
		clearLabel[number].setFont(new Font("Arial", Font.BOLD, 30));
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