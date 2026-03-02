package defendthecastle.battle.battledialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.stream.IntStream;

import commonclass.CommonJPanel;
import defaultdata.Enemy;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;
import defendthecastle.screendisplay.DisplayStatus;

class AllEnemy extends CommonJPanel implements MouseListener{
	private final List<EnemyData> enemyData;
	private final List<BufferedImage> enemyImage;
	private final List<Integer> enemyCount;
	private final int RATIO = 2;
	private final Font font = new Font("Arial", Font.BOLD, 30);
	private final int SIZE = 100;
	private final int COLUMN = 4;
	
	AllEnemy(StageData stageData) {
		enemyData = stageData.getDisplayOrder().stream().map(j -> Enemy.getLabel(j)).toList();
		enemyImage = enemyData.stream().map(i -> i.getImage(RATIO)).toList();
		enemyCount = stageData.getDisplayOrder().stream().map(i -> enemyCount(stageData, i)).toList();
		setPreferredSize(createDimension());
		addMouseListener(this);
		stillness(defaultWhite());
	}
	
	int enemyCount(StageData stageData, int number) {
		return (int) stageData.getEnemy().stream().filter(i -> i.get(0) == number).mapToInt(this::count).sum();
	}
	
	/**
	 * 各敵の総数を調べる。
	 * @param enemyInformation - 敵情報。
	 * @return 復活回数が負の値なら∞を表示させるため、過剰な負の値を返す。
	 * 正の値なら復活回数+1を返す。
	 */
	int count(List<Integer> enemyInformation) {
		return (enemyInformation.get(3) < 0)? -100000 :enemyInformation.get(3) + 1;
	}
	
	Dimension createDimension() {
		return new Dimension(SIZE, (enemyImage.size() / COLUMN + 1) * SIZE);
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
				displayEnemy(i);
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
	
	void displayEnemy(int number) {
		new DisplayStatus().enemy(enemyData.get(number));
	}
}
