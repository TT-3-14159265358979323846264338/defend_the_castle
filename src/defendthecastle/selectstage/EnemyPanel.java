package defendthecastle.selectstage;

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
import defaultdata.Stage;
import defaultdata.enemy.EnemyData;
import defendthecastle.screendisplay.DisplayStatus;

//敵兵情報
class EnemyPanel extends CommonJPanel implements MouseListener{
	private final List<List<EnemyData>> enemyData;
	private final List<List<BufferedImage>> enemyImage;
	private final List<List<Integer>> enemyCount;
	private final Font font = new Font("Arial", Font.BOLD, 30);
	private final int SIZE = 100;
	private final int COLUMN = 3;
	private final Dimension dimension = new Dimension();
	private final int PANEL_SIZE =  200;
	private int select;
	
	EnemyPanel(ProgressData progressData) {
		enemyData = enemyData(progressData);
		enemyImage = enemyImage();
		enemyCount = enemyCount(progressData);
		addMouseListener(this);
		stillness(defaultWhite());
	}
	
	List<List<EnemyData>> enemyData(ProgressData progressData){
		return progressData.getActivateStage().stream().map(i -> enemyDataList(i)).toList();
	}
	
	List<EnemyData> enemyDataList(Stage stage){
		return stage.getStageData().getDisplayOrder().stream().map(j -> Enemy.getEnemyData(j)).toList();
	}
	
	List<List<BufferedImage>> enemyImage(){
		return enemyData.stream().map(i -> imageList(i)).toList();
	}
	
	List<BufferedImage> imageList(List<EnemyData> enemyList){
		return enemyList.stream().map(j -> j.getImage(2)).toList();
	}
	
	List<List<Integer>> enemyCount(ProgressData progressData){
		return progressData.getActivateStage().stream().map(i -> enemyCount(i)).toList();
	}
	
	List<Integer> enemyCount(Stage stage){
		return stage.getStageData().getDisplayOrder().stream().map(j -> count(j, stage.getStageData().getEnemy())).toList();
	}
	
	int count(int enemyNumber, List<List<Integer>> enemyList) {
		return (int) enemyList.stream().filter(i -> i.get(0) == enemyNumber).mapToInt(i -> (i.get(3) < 0)? -100000 :i.get(3) + 1).sum();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, enemyImage.get(select).size()).forEach(i -> {
			int x = i % COLUMN * SIZE;
			int y = i / COLUMN * SIZE;
			g.drawImage(enemyImage.get(select).get(i), x, y, this);
			g.setFont(font);
			g.drawString((enemyCount.get(select).get(i) < 0)? "∞": "" + enemyCount.get(select).get(i), 80 + x, 80 + y);
		});
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < enemyImage.get(select).size(); i++) {
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
		new DisplayStatus().enemy(enemyData.get(select).get(number));
	}
	
	void changeSelect(int select) {
		this.select = select;
		dimension.setSize(PANEL_SIZE, dimensionHeight());
		revalidate();
		repaintPanel();
	}
	
	int dimensionHeight() {
		return (enemyImage.get(select).size() / COLUMN + 1) * SIZE;
	}
}