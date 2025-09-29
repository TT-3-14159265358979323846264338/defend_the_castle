package defendthecastle.selectstage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import javax.swing.JPanel;

import defaultdata.DefaultEnemy;
import defaultdata.DefaultStage;
import defaultdata.enemy.EnemyData;
import screendisplay.DisplayStatus;

//敵兵情報
class EnemyPanel extends JPanel implements MouseListener{
	private SelectPanel SelectPanel;
	private List<List<EnemyData>> enemyData;
	private List<List<BufferedImage>> enemyImage;
	private List<List<Integer>> enemyCount;
	private Font font = new Font("Arial", Font.BOLD, 30);
	private final int SIZE = 100;
	private final int COLUMN = 3;
	
	protected EnemyPanel(SelectPanel SelectPanel) {
		BiFunction<Integer, List<List<Integer>>, Integer> count = (enemy, enemyList) -> {
			return (int) enemyList.stream().filter(i -> i.get(0) == enemy).count();
		};
		this.SelectPanel = SelectPanel;
		enemyData = DefaultStage.STAGE_DATA.stream().map(i -> i.getDisplayOrder().stream().map(j -> DefaultEnemy.DATA_MAP.get(j)).toList()).toList();
		enemyImage = enemyData.stream().map(i -> i.stream().map(j -> j.getImage(2)).toList()).toList();
		enemyCount = DefaultStage.STAGE_DATA.stream().map(i -> i.getDisplayOrder().stream().map(j -> count.apply(j, i.getEnemy())).toList()).toList();
		addMouseListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setPreferredSize(new Dimension(100, (enemyImage.get(SelectPanel.getSelelct()).size() / COLUMN + 1) * SIZE));
		IntStream.range(0, enemyImage.get(SelectPanel.getSelelct()).size()).forEach(i -> {
			int x = i % COLUMN * SIZE;
			int y = i / COLUMN * SIZE;
			g.drawImage(enemyImage.get(SelectPanel.getSelelct()).get(i), x, y, this);
			g.setFont(font);
			g.drawString("" + enemyCount.get(SelectPanel.getSelelct()).get(i), 80 + x, 80 + y);
		});
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < enemyImage.get(SelectPanel.getSelelct()).size(); i++) {
			int x = i % COLUMN * SIZE + 10;
			int y = i / COLUMN * SIZE + 10;
			if(ValueRange.of(x, x + SIZE - 30).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE - 30).isValidIntValue(e.getY())){
				new DisplayStatus().enemy(enemyData.get(SelectPanel.getSelelct()).get(i));
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
}