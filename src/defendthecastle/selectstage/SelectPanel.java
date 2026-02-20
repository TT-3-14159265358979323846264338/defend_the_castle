package defendthecastle.selectstage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JLabel;

import commonclass.CommonJPanel;

//ステージ切り替え
class SelectPanel extends CommonJPanel implements MouseListener{
	private final StageImage stageImage;
	private final MeritPanel meritPanel;
	private final GameCondition gameCondition;
	private final EnemyPanel enemyPanel;
	private final MenuSelectStage menuSelectStage;
	private final List<Boolean> clearStatus;
	private final List<String> stageNameList;
	private final JLabel[] nameLabel;
	private final JLabel[] clearLabel;
	private final JLabel otherStageLabel = new JLabel();
	private final Font stageFont = new Font("Arial", Font.BOLD, 20);
	private final Font clearFont = new Font("Arial", Font.BOLD, 30);
	private final Font otherFont = new Font("ＭＳ ゴシック", Font.BOLD, 10);
	private int select = 0;
	
	SelectPanel(ProgressData progressData, StageImage stageImage, MeritPanel meritPanel, GameCondition gameCondition, EnemyPanel enemyPanel, MenuSelectStage menuSelectStage) {
		this.stageImage = stageImage;
		this.meritPanel = meritPanel;
		this.gameCondition = gameCondition;
		this.enemyPanel = enemyPanel;
		this.menuSelectStage = menuSelectStage;
		clearStatus = progressData.getClearStatus();
		stageNameList = progressData.getStageName();
		select = progressData.getSelectStage();
		changeSelect();
		nameLabel = IntStream.range(0, progressData.getActivateStage().size()).mapToObj(this::addNameLabel).toArray(JLabel[]::new);
		clearLabel = IntStream.range(0, nameLabel.length).mapToObj(this::addClearLabel).toArray(JLabel[]::new);
		addOtherLabel(progressData);
		addMouseListener(this);
		setPreferredSize(stageDimension());
		stillness(defaultWhite());
	}
	
	private JLabel addNameLabel(int number) {
		var label = new JLabel();
		setLabel(label, stageNameList.get(number), 0, 25 + 85 * number, 130, 30, stageFont);
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}
	
	private JLabel addClearLabel(int number) {
		var label = new JLabel();
		setLabel(label, clearComment(number), 30, 50 + 85 * number, 130, 30, clearFont);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.RED);
		return label;
	}
	
	String clearComment(int number) {
		return clearStatus.get(number)? "clear": "";
	}
	
	private void addOtherLabel(ProgressData progressData) {
		setLabel(otherStageLabel, stageComment(progressData), 0, 85 * clearLabel.length, 130, 30, otherFont);
		otherStageLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	String stageComment(ProgressData progressData) {
		return progressData.canAllActivate()? "全ステージ解放済": "条件により新ステージ解放";
	}
	
	Dimension stageDimension() {
		return new Dimension(100, 85 * stageImage.imageSize() + 30);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, stageImage.imageSize()).forEach(i -> drawField(i, g));
	}
	
	private void drawField(int number, Graphics g) {
		if(select == number) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 85 * number, 135, 85);
		}
		g.drawImage(stageImage.getSelectImage(number), 10, 10 + 85 * number, this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		for(int i = 0; i < stageImage.imageSize(); i++) {
			if(ValueRange.of(10, 125).isValidIntValue(e.getX())
					&& ValueRange.of(10 + 85 * i, -10 + 85 * (i + 1)).isValidIntValue(e.getY())) {
				if(select != i){
					select = i;
					changeSelect();
					repaintPanel();
				}
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
	
	int getSelelct() {
		return select;
	}
	
	void changeSelect() {
		meritPanel.changeSelect(select);
		gameCondition.changeSelect(select);
		enemyPanel.changeSelect(select);
		menuSelectStage.repaintPanel();
	}
}