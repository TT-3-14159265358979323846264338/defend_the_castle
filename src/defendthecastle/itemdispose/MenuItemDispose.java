package defendthecastle.itemdispose;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import commonclass.CommonJPanel;
import commonclass.ImagePanel;
import defendthecastle.MainFrame;

//アイテムのリサイクル
public class MenuItemDispose extends CommonJPanel{
	private final MainFrame mainFrame;
	private final OperateData operateData;
	private final CreateDisplayList createDisplayList;
	private final DefaultDisposeData defaultDisposeData;
	private final ImagePanel coreImagePanel;
	private final ImagePanel weaponImagePanel;
	private final JLabel typeLabel = new JLabel();
	private final JButton switchButton = new JButton();
	private final JButton sortButton = new JButton();
	private final JButton disposeButton = new JButton();
	private final JButton returnButton = new JButton();
	private final JScrollPane itemScroll = new JScrollPane();
	private final Font largeFont = new Font("ＭＳ ゴシック", Font.BOLD, 25);
	private final Font smallFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	public MenuItemDispose(MainFrame mainFrame, ScheduledExecutorService scheduler) {
		this.mainFrame = mainFrame;
		operateData = createOperateData();
		createDisplayList = createDisplayList();
		defaultDisposeData = createDefaultDisposeData();
		coreImagePanel = createImagePanel(scheduler, defaultDisposeData.getCoreImageList(), createDisplayList.initialCoreDisplayList(), operateData.getCoreNumberList(), true);
		weaponImagePanel = createImagePanel(scheduler, defaultDisposeData.getWeaponImageList(), createDisplayList.initialWeaponDisplayList(), operateData.getWeaponNumberList(), false);
		setLabel(typeLabel, typeName(), 20, 10, 400, 30, largeFont);
		setButton(switchButton, "表示切替", 20, 530, 150, 60, smallFont, this::switchButtonAction);
		setButton(sortButton, "ソート", 190, 530, 150, 60, smallFont, this::sortButtonAction);
		setButton(disposeButton, "リサイクル", 360, 530, 150, 60, smallFont, this::disposeButtonAction);
		setButton(returnButton, "戻る", 530, 530, 150, 60, smallFont, this::returnButtonAction);
		setScroll(itemScroll, 20, 50, 660, 470, coreImagePanel);
		stillness(brown());
	}
	
	OperateData createOperateData() {
		return new OperateData();
	}
	
	CreateDisplayList createDisplayList() {
		return new CreateDisplayList(operateData);
	}
	
	DefaultDisposeData createDefaultDisposeData() {
		return new DefaultDisposeData();
	}
	
	ImagePanel createImagePanel(ScheduledExecutorService scheduler, List<BufferedImage> imageList, List<Integer> displayList, List<Integer> numberList, boolean exists) {
		return new ImagePanel(scheduler, imageList, displayList, numberList, exists, 5);
	}
	
	String typeName() {
		return hasDisplayedPanel()? "コアリスト": "武器リスト";
	}
	
	boolean hasDisplayedPanel() {
		return itemScroll.getViewport().getView() == coreImagePanel;
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView(switchItemImagePanel());
		typeLabel.setText(typeName());
	}
	
	ImagePanel switchItemImagePanel() {
		return hasDisplayedPanel()? weaponImagePanel: coreImagePanel;
	}
	
	void sortButtonAction(ActionEvent e) {
		if(hasDisplayedPanel()) {
			coreImagePanel.setDisplayList(createDisplayList.getCoreDisplayList());
		}else {
			weaponImagePanel.setDisplayList(createDisplayList.getWeaponDisplayList());
		}
	}
	
	void disposeButtonAction(ActionEvent e) {
		if(hasDisplayedPanel()) {
			operateData.recycle(coreImagePanel, operateData.getCoreNumberList(), operateData.getUsedCoreNumber(), defaultDisposeData.getCoreImageList(), defaultDisposeData.getCoreRarityList());
		}else {
			operateData.recycle(weaponImagePanel, operateData.getWeaponNumberList(), operateData.getUsedWeaponNumber(), defaultDisposeData.getWeaponImageList(), defaultDisposeData.getWeaponRarityList());
		}
	}
	
	void returnButtonAction(ActionEvent e) {
		mainFrame.mainMenuDraw();
	}
}