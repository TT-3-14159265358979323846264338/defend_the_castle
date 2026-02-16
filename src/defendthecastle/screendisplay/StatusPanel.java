package defendthecastle.screendisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

//ステータス表示
class StatusPanel extends JPanel{
	private JLabel imageLabel;
	private Function<Integer, JLabel[]> initialize = (size) -> {
		return IntStream.range(0, size).mapToObj(_ -> new JLabel()).toArray(JLabel[]::new);
	};
	protected JLabel[] item = initialize.apply(3);
	protected JLabel[] unitName = initialize.apply(3);
	protected JLabel[] explanation = initialize.apply(3);
	protected JLabel[] weapon = initialize.apply(27);
	protected JLabel[] unit = initialize.apply(12);
	protected JLabel[] cut = initialize.apply(24);
	protected Font defaultFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final int START_X = 20;
	private final int START_Y = 20;
	protected final int SIZE_X = 110;
	private final int SIZE_Y = 30;
	
	protected void setStatusPanel(BufferedImage image) {
		setBackground(new Color(240, 170, 80));
		this.imageLabel = new JLabel(new ImageIcon(image));
		addLabel();
		setLabelFont();
		setLabelHorizontal();
		new StstusDialog(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabelPosition();
		drawBackground(g);
	}
	
	private void addLabel() {
		Consumer<JLabel[]> addLabel = (label) -> {
			Stream.of(label).forEach(i -> add(i));
		};
		add(imageLabel);
		addLabel.accept(item);
		addLabel.accept(unitName);
		addLabel.accept(explanation);
		addLabel.accept(weapon);
		addLabel.accept(unit);
		addLabel.accept(cut);
	}
	
	private void setLabelFont() {
		BiConsumer<JLabel[], Integer> setLabel = (label, size) -> {
			String fontName = "ＭＳ ゴシック";
			int bold = Font.BOLD;
			Stream.of(label).forEach(i -> {
				int fontSize = 15;
				int width = getFontMetrics(new Font(fontName, bold, fontSize)).stringWidth(i.getText());
				while(size < width) {
					fontSize--;
					width = getFontMetrics(new Font(fontName, bold, fontSize)).stringWidth(i.getText());
				}
				i.setFont(new Font(fontName, bold, fontSize));
			});
		};
		Stream.of(item).forEach(i -> i.setFont(defaultFont));
		Stream.of(unitName).forEach(i -> i.setFont(defaultFont));
		Stream.of(explanation).forEach(i -> i.setFont(defaultFont));
		setLabel.accept(weapon, SIZE_X);
		setLabel.accept(unit, SIZE_X);
		setLabel.accept(cut, SIZE_X);
	}
	
	private void setLabelHorizontal() {
		Consumer<JLabel[]> setLabel = (label) -> {
			Stream.of(label).forEach(i -> i.setHorizontalAlignment(JLabel.CENTER));
		};
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		setLabel.accept(unitName);
		setLabel.accept(weapon);
		setLabel.accept(unit);
		setLabel.accept(cut);
	}
	
	private void setLabelPosition() {
		item[0].setBounds(START_X, START_Y, SIZE_X, SIZE_Y);
		item[1].setBounds(START_X + SIZE_X * 3, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y);
		item[2].setBounds(START_X, START_Y + SIZE_Y * 16, SIZE_X * 3, SIZE_Y);
		IntStream.range(0, unitName.length).forEach(i -> unitName[i].setBounds(START_X + SIZE_X * (4 - i * 2), START_Y + SIZE_Y, SIZE_X * 2, SIZE_Y));
		IntStream.range(0, explanation.length).forEach(i -> explanation[i].setBounds(START_X + SIZE_X * (4 - i * 2) + 5, START_Y + SIZE_Y * 2, SIZE_X * 2, SIZE_Y * 2));
		imageLabel.setBounds(START_X, START_Y + SIZE_Y * 5, SIZE_X * 3, SIZE_Y * 10);
		IntStream.range(0, weapon.length).forEach(i -> weapon[i].setBounds(START_X + (i / 9 + 3) * SIZE_X, START_Y + (i % 9 + 6) * SIZE_Y, SIZE_X, SIZE_Y));
		IntStream.range(0, unit.length).forEach(i -> unit[i].setBounds(START_X + (i / 6) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y));
		IntStream.range(0, cut.length / 2).forEach(i -> {
			cut[i].setBounds(START_X + (i / 6 * 2 + 2) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y);
			cut[i + cut.length / 2].setBounds(START_X + (i / 6 * 2 + 3) * SIZE_X, START_Y + (i % 6 + 17) * SIZE_Y, SIZE_X, SIZE_Y);
		});
	}
	
	private void drawBackground(Graphics g) {
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