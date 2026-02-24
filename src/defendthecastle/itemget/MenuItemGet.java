package defendthecastle.itemget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import commonclass.CommonJPanel;
import commonclass.EditImage;
import defendthecastle.MainFrame;

//ガチャ本体
public class MenuItemGet extends CommonJPanel{
	private final MainFrame mainFrame;
	private final AutoRotate autoRotate;
	private final ItemGetImage itemGetImage;
	private final HoldMedal holdMedal;
	private final GachaInformation gachaInformation;
	private final OpenBallMotion openBallMotion;
	private final BallMotion ballMotion;
	private final HandleMotion handleMotion;
	private final JList<String> selectGachaJList = new JList<>();
	private final JScrollPane selectGachaScroll = new JScrollPane();
	private final JLabel medalLabel = new JLabel();
	private final JButton gachaDetailButton = new JButton();
	private final JButton repeatButton = new JButton();
	private final JButton returnButton = new JButton();
	private final Font largeFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font smallFont = new Font("ＭＳ ゴシック", Font.BOLD, 18);
	private boolean canPlay = true;
	
	public MenuItemGet(MainFrame mainFrame, ScheduledExecutorService scheduler) {
		this.mainFrame = mainFrame;
		autoRotate = createAutoRotate(scheduler);
		itemGetImage = createItemGetImage();
		holdMedal = createHoldMedal();
		gachaInformation = createGachaInformation();
		holdMedal.install(gachaInformation);
		openBallMotion = createOpenBallMotion(scheduler);
		ballMotion = createBallMotion(scheduler);
		handleMotion = createHandleMotion(scheduler);
		setMedalLabel();
		setButton(gachaDetailButton, "<html>ガチャ詳細", 350, 330, 210, 60, smallFont, this::gachaDetailButtonAction);
		setButton(repeatButton, repeatText(), 350, 400, 100, 60, smallFont, this::repeatButtonAction);
		setButton(returnButton, "<html>戻る", 460, 400, 100, 60, smallFont, this::returnButtonAction);
		setGachaScroll();
		movie(scheduler, defaultWhite());
	}
	
	AutoRotate createAutoRotate(ScheduledExecutorService scheduler) {
		return new AutoRotate(scheduler, this);
	}
	
	ItemGetImage createItemGetImage() {
		return new ItemGetImage();
	}
	
	HoldMedal createHoldMedal() {
		return new HoldMedal();
	}
	
	GachaInformation createGachaInformation() {
		return new GachaInformation(holdMedal.getSaveData());
	}
	
	OpenBallMotion createOpenBallMotion(ScheduledExecutorService scheduler) {
		return new OpenBallMotion(this, holdMedal, gachaInformation, itemGetImage, scheduler);
	}
	
	BallMotion createBallMotion(ScheduledExecutorService scheduler) {
		return new BallMotion(openBallMotion, scheduler);
	}
	
	HandleMotion createHandleMotion(ScheduledExecutorService scheduler) {
		return new HandleMotion(this, holdMedal, ballMotion, scheduler);
	}
	
	private void setMedalLabel() {
		setLabel(medalLabel, medalText(), 350, 20, 200, 30, largeFont);
		medalLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	String medalText() {
		return String.format("メダル: %d枚", holdMedal.getMedal());
	}
	
	void gachaDetailButtonAction(ActionEvent e) {
		new GachaLineup(gachaInformation);
	}
	
	String repeatText() {
		return String.format("<html>&nbsp;%d連<br>%d枚", gachaInformation.getRepeatNumber(), holdMedal.useMedal());
	}
	
	void repeatButtonAction(ActionEvent e) {
		gachaInformation.changeRepeatNumber();
		repeatButton.setText(repeatText());
	}
	
	void returnButtonAction(ActionEvent e) {
		mainFrame.mainMenuDraw();
	}
	
	private void setGachaScroll() {
		selectGachaJList.setListData(gachaInformation.getGachaName());
		selectGachaJList.setSelectedIndex(0);
		selectGachaJList.addListSelectionListener(this::selectAction);
		selectGachaJList.setFont(largeFont);
		setScroll(selectGachaScroll, 350, 60, 210, 260, selectGachaJList);
	}
	
	void selectAction(ListSelectionEvent e) {
		int selectIndex = selectGachaJList.getSelectedIndex();
		if(selectIndex < 0) {
			return;
		}
		gachaInformation.changeGachaMode(selectIndex);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(itemGetImage.getMachineImage().get(0), 55, 20, null);
		Point point = ballMotion.getBallPosition();
		g.drawImage(EditImage.rotateImage(itemGetImage.getBallImage(), ballMotion.getBallAngel()), point.x, point.y, null);
		g.drawImage(itemGetImage.getMachineImage().get(1), 55, 20, null);
		g.drawImage(EditImage.rotateImage(itemGetImage.getHandleImage(), handleMotion.angle()), 145, 220, null);
		if(openBallMotion.canRunTimer()) {
			List<Double> angle = openBallMotion.getBallAngle();
			List<Point> position = openBallMotion.getBallPosition();
			Consumer<Integer> drawBallOpen = (i) -> {
				g.drawImage(EditImage.rotateImage(itemGetImage.getHalfBallImage().get(i), angle.get(i)), position.get(i).x, position.get(i).y, null);
			};
			drawBallOpen.accept(0);
			drawBallOpen.accept(1);
			int expansion = openBallMotion.getExpansion();
			int color = openBallMotion.getColor();
			g.drawImage(EditImage.effectImage(itemGetImage.getEffectImage(), expansion, new Color(255, 255, color, color).getRGB()), 30 - expansion / 2, 210 - expansion / 2, null);
		}
		if(canPlay && holdMedal.canPossessMedal()) {
			g.drawImage(EditImage.rotateImage(itemGetImage.getTurnImage(), autoRotate.getAngle()), 105, 180, null);
		}
	}
	
	void activatePanel() {
		setPanel(true);
		medalLabel.setText(medalText());
	}
	
	void deactivatePanel() {
		setPanel(false);
	}
	
	private void setPanel(boolean canActivate) {
		returnButton.setEnabled(canActivate);
		gachaDetailButton.setEnabled(canActivate);
		repeatButton.setEnabled(canActivate);
		selectGachaJList.setEnabled(canActivate);
		canPlay = canActivate;
	}
}