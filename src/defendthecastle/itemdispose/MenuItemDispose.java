package defendthecastle.itemdispose;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.DefaultUnit;
import defendthecastle.MainFrame;

//アイテムのリサイクル
public class MenuItemDispose extends JPanel{
	private JLabel typeLabel = new JLabel();
	private JButton switchButton = new JButton();
	private JButton sortButton = new JButton();
	private JButton disposeButton = new JButton();
	private JButton returnButton = new JButton();
	private JScrollPane itemScroll = new JScrollPane();
	private ItemImagePanel CoreImagePanel = new ItemImagePanel();
	private ItemImagePanel WeaponImagePanel = new ItemImagePanel();
	private OperateData OperateData = new OperateData();
	private CreateDisplayList DisplayListCreation = new CreateDisplayList(OperateData);
	private List<BufferedImage> coreImageList = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(2)).toList();
	private List<BufferedImage> weaponImageList = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getImage(2)).toList();
	
	public MenuItemDispose(MainFrame MainFrame) {
		setBackground(new Color(240, 170, 80));
		add(typeLabel);
		addSwitchButton();
		addSortButton();
		addDisposeButton();
		addReturnButton(MainFrame);
		addScroll();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabel(typeLabel, (itemScroll.getViewport().getView() == CoreImagePanel)? "コアリスト": "武器リスト", 20, 10, 400, 30);
		setButton(switchButton, "表示切替", 20, 530, 150, 60);
		setButton(sortButton, "ソート", 190, 530, 150, 60);
		setButton(disposeButton, "リサイクル", 360, 530, 150, 60);
		setButton(returnButton, "戻る", 530, 530, 150, 60);
		setScroll(itemScroll, 20, 50, 660, 470);
		requestFocus();
	}
	
	private void addSwitchButton() {
		add(switchButton);
		switchButton.addActionListener(e->{
			itemScroll.getViewport().setView((itemScroll.getViewport().getView() == CoreImagePanel)? WeaponImagePanel: CoreImagePanel);
		});
	}
	
	private void addSortButton() {
		add(sortButton);
		sortButton.addActionListener(e->{
			if(itemScroll.getViewport().getView() == CoreImagePanel) {
				CoreImagePanel.updateList(DisplayListCreation.getCoreDisplayList());
			}else {
				WeaponImagePanel.updateList(DisplayListCreation.getWeaponDisplayList());
			}
		});
	}
	
	private void addDisposeButton() {
		add(disposeButton);
		disposeButton.addActionListener(e->{
			if(itemScroll.getViewport().getView() == CoreImagePanel) {
				OperateData.recycle(CoreImagePanel, OperateData.getCoreNumberList(), OperateData.getUsedCoreNumber(), coreImageList, IntStream.range(0, defaultdata.DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getRarity()).toList());
			}else {
				OperateData.recycle(WeaponImagePanel, OperateData.getWeaponNumberList(), OperateData.getUsedWeaponNumber(), weaponImageList, IntStream.range(0, defaultdata.DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getRarity()).toList());
			}
		});
	}
	
	private void addReturnButton(MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(e->{
			MainFrame.mainMenuDraw();
		});
	}
	
	private void addScroll() {
		CoreImagePanel.setImagePanel(coreImageList, DisplayListCreation.getInitialCoreDisplayList(), OperateData.getCoreNumberList(), true);
		WeaponImagePanel.setImagePanel(weaponImageList, DisplayListCreation.getInitialWeaponDisplayList(), OperateData.getWeaponNumberList(), false);
		itemScroll.getViewport().setView(CoreImagePanel);
		add(itemScroll);
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 25));
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
	}
}