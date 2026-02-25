package defendthecastle.battle.battledialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.JPanel;

import defaultdata.Enemy;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;
import defendthecastle.screendisplay.DisplayStatus;

class AllEnemy extends JPanel implements MouseListener{
	private List<EnemyData> enemyData;
	private List<BufferedImage> enemyImage;
	private List<Integer> enemyCount;
	private Font font = new Font("Arial", Font.BOLD, 30);
	private final int SIZE = 100;
	private final int COLUMN = 4;
	
	AllEnemy(StageData StageData) {
		Function<Integer, Integer> count = (number) -> {
			return (int) StageData.getEnemy().stream().filter(i -> i.get(0) == number).mapToInt(i -> (i.get(3) < 0)? -100000 :i.get(3) + 1).sum();
		};
		enemyData = StageData.getDisplayOrder().stream().map(j -> Enemy.getEnemyData(j)).toList();
		enemyImage = enemyData.stream().map(i -> i.getImage(2)).toList();
		enemyCount = StageData.getDisplayOrder().stream().map(i -> count.apply(i)).toList();
		setPreferredSize(new Dimension(100, (enemyImage.size() / COLUMN + 1) * SIZE));
		addMouseListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, enemyImage.size()).forEach(i -> {
			int x = i % COLUMN * SIZE;
			int y = i / COLUMN * SIZE;
			g.drawImage(enemyImage.get(i), x, y, this);
			g.setFont(font);
			g.drawString((enemyCount.get(i) < 0)? "∞": "" + enemyCount.get(i), 80 + x, 80 + y);
		});
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < enemyImage.size(); i++) {
			int x = i % COLUMN * SIZE + 10;
			int y = i / COLUMN * SIZE + 10;
			if(ValueRange.of(x, x + SIZE - 30).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE - 30).isValidIntValue(e.getY())){
				new DisplayStatus().enemy(enemyData.get(i));
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
