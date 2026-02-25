package commonclass;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import defendthecastle.screendisplay.DisplayStatus;

//ユニット表示パネル
public class ImagePanel extends CommonJPanel implements MouseListener{
	public static final int UNIT_SIZE = 60;
	private final List<BufferedImage> imageList;
	private final List<Integer> numberList;
	private List<Integer> displayList;
	private final boolean existsWhich;
	private final int columns;
	private int selectNumber;
	private final Font font = new Font("Arial", Font.BOLD, 30);
	private final Dimension dimension;
	private final int DRAW_SIZE = 120;
	private final int PANEL_SIZE = 100;
	
	public ImagePanel(ScheduledExecutorService scheduler, List<BufferedImage> imageList, List<Integer> displayList, List<Integer> numberList, boolean exists, int columns) {
		this.imageList = imageList;
		this.displayList = displayList;
		this.numberList = numberList;
		this.existsWhich = exists;
		this.columns = columns;
		dimension = createDimension();
		setPreferredSize(dimension);
		resetSelectNumber();
		addMouseListener(this);
		movie(scheduler, defaultWhite());
	}
	
	Dimension createDimension() {
		return new Dimension(PANEL_SIZE, dimensionHeight());
	}
	
	int dimensionHeight() {
		return (displayList.size() / columns + 1) * DRAW_SIZE;
	}

	public void setDisplayList(List<Integer> displayList) {
		this.displayList = displayList;
		dimension.setSize(PANEL_SIZE, dimensionHeight());
		revalidate();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, displayList.size()).forEach(i -> {
			int x = i % columns * DRAW_SIZE;
			int y = i / columns * DRAW_SIZE;
			if(selectNumber == displayList.get(i)) {
				g.setColor(Color.WHITE);
				g.fillRect(x, y, 90, 90);
			}
			g.drawImage(imageList.get(displayList.get(i)), x, y, this);
			g.setColor(Color.BLACK);
			g.setFont(font);
			g.drawString("" + numberList.get(displayList.get(i)), 80 + x, 80 + y);
		});
	}
	
	public int getSelectNumber() {
		return selectNumber;
	}
	
	public void resetSelectNumber() {
		selectNumber = -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < displayList.size(); i++) {
			int x = i % columns * DRAW_SIZE + 10;
			int y = i / columns * DRAW_SIZE + 10;
			if(ValueRange.of(x, x + UNIT_SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + UNIT_SIZE).isValidIntValue(e.getY())){
				if(selectNumber == displayList.get(i)) {
					if(existsWhich) {
						displayCore();
					}else {
						displayWeapon();
					}
				}else {
					selectNumber = displayList.get(i);
				}
				return;
			}
		}
		resetSelectNumber();
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
	
	void displayCore() {
		new DisplayStatus().core(imageList.get(selectNumber), selectNumber);
	}
	
	void displayWeapon() {
		new DisplayStatus().weapon(imageList.get(selectNumber), selectNumber);
	}
}