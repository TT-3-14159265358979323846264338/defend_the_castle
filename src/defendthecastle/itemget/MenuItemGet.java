package defendthecastle.itemget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.DefaultOther;
import defaultdata.EditImage;
import defendthecastle.MainFrame;

//ガチャ本体
public class MenuItemGet extends JPanel{
	private JLabel medalLabel = new JLabel();
	private JButton gachaDetailButton = new JButton();
	private JButton repeatButton = new JButton();
	private JButton returnButton = new JButton();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	private HoldMedal HoldMedal = new HoldMedal();
	private GachaInformation GachaInformation = new GachaInformation(HoldMedal.getSaveData());
	private OpenBallMotion OpenBallMotion = new OpenBallMotion(this, HoldMedal, GachaInformation, scheduler);
	private BallMotion BallMotion = new BallMotion(OpenBallMotion, scheduler);
	private HandleMotion HandleMotion = new HandleMotion(this, HoldMedal, BallMotion, scheduler);
	private JList<String> selectGachaJList = new JList<>(GachaInformation.getGachaName());
	private JScrollPane selectGachaScroll = new JScrollPane();
	private BufferedImage ballImage = new DefaultOther().getBallImage(2);
	private List<BufferedImage> halfBallImage = new ArrayList<>(new DefaultOther().getHalfBallImage(2));
	private BufferedImage handleImage = new DefaultOther().getHandleImage(2);
	private List<BufferedImage> machineImage = new ArrayList<>(new DefaultOther().getMachineImage(2));
	private BufferedImage turnImage = new DefaultOther().getTurnImage(2);
	private BufferedImage effectImage = new DefaultOther().getEffectImage(1);
	private double angle;
	private boolean canPlay = true;
	
	public MenuItemGet(MainFrame MainFrame) {
		HoldMedal.install(GachaInformation);
		addMedalLabel();
		addGachaDetailButton();
		addRepeatButton();
		addReturnButton(MainFrame);
		addGachaScroll();
		mainTimer();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabel(medalLabel, "メダル: " + HoldMedal.getMedal() + "枚", 350, 20, 200, 30);
		setButton(gachaDetailButton, "<html>ガチャ詳細", 350, 330, 210, 60);
		setButton(repeatButton, "<html>&nbsp;" + GachaInformation.getRepeatNumber() + "連<br>" + HoldMedal.useMedal() + "枚", 350, 400, 100, 60);
		setButton(returnButton, "<html>戻る", 460, 400, 100, 60);
		selectGachaJList.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		setScroll(selectGachaScroll, 350, 60, 210, 260);
		drawGachaImage(g);
		GachaInformation.changeGachaMode(selectGachaJList.getSelectedIndex());
		requestFocus();
	}
	
	private void addMedalLabel() {
		add(medalLabel);
		medalLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void addGachaDetailButton() {
		add(gachaDetailButton);
		gachaDetailButton.addActionListener(_ ->{
			new GachaLineup(GachaInformation);
		});
	}
	
	private void addRepeatButton() {
		add(repeatButton);
		repeatButton.addActionListener(_ ->{
			GachaInformation.changeRepeatNumber();
		});
	}
	
	private void addReturnButton(MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(_ ->{
			MainFrame.mainMenuDraw();
		});
	}
	
	private void addGachaScroll() {
		selectGachaScroll.getViewport().setView(selectGachaJList);
		add(selectGachaScroll);
		selectGachaJList.setSelectedIndex(0);
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(0, 0, 200, 30);
		label.setBounds(x, y, width, height);
		label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 18));
	}
	
	private void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
	}

	protected void activatePanel() {
		setPanel(true);
	}
	
	protected void deactivatePanel() {
		setPanel(false);
	}
	
	private void setPanel(boolean canActivate) {
		returnButton.setEnabled(canActivate);
		gachaDetailButton.setEnabled(canActivate);
		repeatButton.setEnabled(canActivate);
		selectGachaJList.setEnabled(canActivate);
		canPlay = canActivate;
	}
	
	private void drawGachaImage(Graphics g) {
		g.drawImage(machineImage.get(0), 55, 20, null);
		Point point = BallMotion.getBallPosition();
		g.drawImage(EditImage.rotateImage(ballImage, BallMotion.getBallAngel()), point.x, point.y, null);
		g.drawImage(machineImage.get(1), 55, 20, null);
		g.drawImage(EditImage.rotateImage(handleImage, HandleMotion.angle()), 145, 220, null);
		if(OpenBallMotion.canRunTimer()) {
			List<Double> angle = OpenBallMotion.getBallAngle();
			List<Point> position = OpenBallMotion.getBallPosition();
			Consumer<Integer> drawBallOpen = (i) -> {
				g.drawImage(EditImage.rotateImage(halfBallImage.get(i), angle.get(i)), position.get(i).x, position.get(i).y, null);
			};
			drawBallOpen.accept(0);
			drawBallOpen.accept(1);
			int expansion = OpenBallMotion.getExpansion();
			int color = OpenBallMotion.getColor();
			g.drawImage(EditImage.effectImage(effectImage, expansion, new Color(255, 255, color, color).getRGB()), 30 - expansion / 2, 210 - expansion / 2, null);
		}
		if(canPlay && HoldMedal.canPossessMedal()) {
			g.drawImage(EditImage.rotateImage(turnImage, angle), 105, 180, null);
		}
	}
	
	private void mainTimer() {
		scheduler.scheduleAtFixedRate(() -> {
			angle += 0.03;
			if(Math.PI * 10000 < angle) {
				angle = 0;
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}
}