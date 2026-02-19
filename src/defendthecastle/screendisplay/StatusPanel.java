package defendthecastle.screendisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import defendthecastle.commoninheritance.CommonJPanel;

//ステータス表示
abstract class StatusPanel extends CommonJPanel{
	private JLabel imageLabel;
	protected final JLabel[] item;
	protected final JLabel[] unitName;
	protected final JLabel[] explanation;
	protected final JLabel[] weapon;
	protected final JLabel[] unit;
	protected final JLabel[] cut;
	protected final Font defaultFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final int START_X = 20;
	private final int START_Y = 20;
	protected final int SIZE_X = 110;
	private final int SIZE_Y = 30;
	
	StatusPanel(){
		item = initialize(3);
		unitName = initialize(3);
		explanation = initialize(3);
		weapon = initialize(27);
		unit = initialize(12);
		cut = initialize(24);
	}
	
	JLabel[] initialize(int size) {
		return IntStream.range(0, size).mapToObj(_ -> new JLabel()).toArray(JLabel[]::new);
	}
	
	void setStatusPanel(BufferedImage image) {
		imageLabel = new JLabel(new ImageIcon(image));
		addLabel();
		setLabelPosition();
		stillness(brown());
		displayDialog();
	}
	
	void displayDialog() {
		new StstusDialog(this);
	}
	
	void addLabel() {
		add(imageLabel);
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		labelMethod(item, labelMethod(defaultFont()));
		labelMethod(unitName, labelMethod(defaultFont()));
		labelMethod(explanation, labelMethod(defaultFont()));
		labelMethod(weapon, labelMethod(fontAdjustment()));
		labelMethod(unit, labelMethod(fontAdjustment()));
		labelMethod(cut, labelMethod(fontAdjustment()));
	}
	
	void labelMethod(JLabel[] label, Consumer<JLabel> method) {
		Stream.of(label).forEach(method);
	}
	
	Consumer<JLabel> labelMethod(Function<JLabel, Font> fontMethod){
		return label -> {
			label.setFont(fontMethod.apply(label));
			label.setHorizontalAlignment(JLabel.CENTER);
			add(label);
		};
	}
	
	Function<JLabel, Font> defaultFont(){
		return _ -> defaultFont;
	}
	
	Function<JLabel, Font> fontAdjustment() {
		return label -> {
			float fontSize = defaultFont.getSize();
			Font adjustmentFont = defaultFont.deriveFont(fontSize);
			int width = label.getFontMetrics(adjustmentFont).stringWidth(label.getText());
			while(SIZE_X < width) {
				fontSize -= 1f;
				adjustmentFont = adjustmentFont.deriveFont(fontSize);
				width = label.getFontMetrics(adjustmentFont).stringWidth(label.getText());
			}
			return adjustmentFont;
		};
	}
	
	void setLabelPosition() {
		item[0].setBounds(START_X, START_Y, SIZE_X, SIZE_Y);
		item[1].setBounds(START_X + SIZE_X * 3, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y);
		item[2].setBounds(START_X, START_Y + SIZE_Y * 16, SIZE_X * 3, SIZE_Y);
		setBoundsArrayLabel(unitName, i -> unitName[i].setBounds(START_X + SIZE_X * (4 - i * 2), START_Y + SIZE_Y, SIZE_X * 2, SIZE_Y));
		setBoundsArrayLabel(explanation, i -> explanation[i].setBounds(START_X + SIZE_X * (4 - i * 2) + 5, START_Y + SIZE_Y * 2, SIZE_X * 2, SIZE_Y * 2));
		imageLabel.setBounds(START_X, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y * 10);
		setBoundsArrayLabel(weapon, i -> weapon[i].setBounds(START_X + (i / 9 + 3) * SIZE_X, START_Y + (i % 9 + 6) * SIZE_Y, SIZE_X, SIZE_Y));
		setBoundsArrayLabel(unit, i -> unit[i].setBounds(START_X + (i / 6) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y));
		IntStream.range(0, cut.length / 2).forEach(i -> {
			cut[i].setBounds(START_X + (i / 6 * 2 + 2) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y);
			cut[i + cut.length / 2].setBounds(START_X + (i / 6 * 2 + 3) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y);
		});
	}
	
	void setBoundsArrayLabel(JLabel[] label, IntConsumer consumer) {
		IntStream.range(0, label.length).forEach(consumer);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);	
		g.fillRect(START_X, START_Y, SIZE_X * 6, SIZE_Y * 4);
		g.fillRect(START_X, START_Y + SIZE_Y * 5, SIZE_X * 6, SIZE_Y * 10);
		g.fillRect(START_X, START_Y + SIZE_Y * 16, SIZE_X * 6, SIZE_Y * 7);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(START_X + SIZE_X * 3, START_Y + SIZE_Y * 6, SIZE_X * 3, SIZE_Y);
		g.fillRect(START_X + SIZE_X * 3, START_Y + SIZE_Y * 7, SIZE_X, SIZE_Y * 8);
		IntStream.range(0, 3).forEach(i -> g.fillRect(START_X + SIZE_X * i * 2, START_Y + SIZE_Y * 17, SIZE_X, SIZE_Y * 6));
		g.setColor(Color.YELLOW);
		g.fillRect(START_X + SIZE_X * 4, START_Y + SIZE_Y * 7, SIZE_X * 2, SIZE_Y * 8);
		IntStream.range(0, 3).forEach(i -> g.fillRect(START_X + SIZE_X * (i * 2 + 1), START_Y + SIZE_Y * 17, SIZE_X, SIZE_Y * 6));
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(2));
		g.drawRect(START_X, START_Y, SIZE_X * 6, SIZE_Y * 4);
		g.drawRect(START_X, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y * 10);
		g.drawRect(START_X + SIZE_X * 3, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y * 10);
		g.drawRect(START_X, START_Y + SIZE_Y * 16, SIZE_X * 6, SIZE_Y * 7);
		g2.setStroke(new BasicStroke(1));
		if(!explanation[1].getText().equals("")) {
			IntStream.range(1, explanation.length).forEach(i -> g.drawLine(START_X + SIZE_X * 2 * i, START_Y + SIZE_Y * 2, START_X + SIZE_X * 2 * i, START_Y + SIZE_Y * 4));
		}
		g.drawLine(START_X + SIZE_X * 3, START_Y + SIZE_Y * 7, START_X + SIZE_X * 4, START_Y + SIZE_Y * 6);
		IntStream.range(0, 9).forEach(i -> g.drawLine(START_X + SIZE_X * 3, START_Y + SIZE_Y * (6 + i), START_X + SIZE_X * 6, START_Y + SIZE_Y * (6 + i)));
		IntStream.range(0, 2).forEach(i -> g.drawLine(START_X + SIZE_X * (4 + i), START_Y + SIZE_Y * 6, START_X + SIZE_X * (4 + i), START_Y + SIZE_Y * 15));
		IntStream.range(0, 6).forEach(i -> g.drawLine(START_X, START_Y + SIZE_Y * (17 + i), START_X + SIZE_X * 6, START_Y + SIZE_Y * (17 + i)));
		IntStream.range(0, 5).forEach(i -> g.drawLine(START_X + SIZE_X * (1 + i), START_Y + SIZE_Y * 17, START_X + SIZE_X * (1 + i), START_Y + SIZE_Y * 23));
	}
}