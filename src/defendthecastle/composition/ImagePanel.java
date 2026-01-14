package defendthecastle.composition;

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

import javax.swing.JPanel;

import screendisplay.DisplayStatus;

//ユニット表示パネル
class ImagePanel extends JPanel implements MouseListener{
	private List<BufferedImage> imageList;
	private List<Integer> displayList;
	private List<Integer> numberList;
	private boolean existsWhich;
	private int selectNumber;
	private final int DRAW_SIZE = 120;
	private final int COLUMN = 3;
	
	ImagePanel() {
		resetSelectNumber();
		addMouseListener(this);
	}
	
	void setImagePanel(List<BufferedImage> imageList, List<Integer> displayList, List<Integer> numberList, boolean existsWhich) {
		this.imageList = imageList;
		this.displayList = displayList;
		this.numberList = numberList;
		this.existsWhich = existsWhich;
	}

	void setDisplayList(List<Integer> displayList) {
		this.displayList = displayList;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setPreferredSize(new Dimension(100, (displayList.size() / COLUMN + 1) * DRAW_SIZE));
		IntStream.range(0, displayList.size()).forEach(i -> {
			int x = i % COLUMN * DRAW_SIZE;
			int y = i / COLUMN * DRAW_SIZE;
			if(selectNumber == displayList.get(i)) {
				g.setColor(Color.WHITE);
				g.fillRect(x, y, 90, 90);
			}
			g.drawImage(imageList.get(displayList.get(i)), x, y, this);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("" + numberList.get(displayList.get(i)), 80 + x, 80 + y);
		});
	}
	
	void resetSelectNumber() {
		selectNumber = -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < displayList.size(); i++) {
			int x = i % COLUMN * DRAW_SIZE + 10;
			int y = i / COLUMN * DRAW_SIZE + 10;
			if(ValueRange.of(x, x + MenuComposition.SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + MenuComposition.SIZE).isValidIntValue(e.getY())){
				if(selectNumber == displayList.get(i)) {
					if(existsWhich) {
						new DisplayStatus().core(imageList.get(selectNumber), selectNumber);
					}else {
						new DisplayStatus().weapon(imageList.get(selectNumber), selectNumber);
					}
				}else {
					setSelectNumber(displayList.get(i));
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
	
	int getSelectNumber() {
		return selectNumber;
	}
	
	void setSelectNumber(int number) {
		selectNumber = number;
	}
}