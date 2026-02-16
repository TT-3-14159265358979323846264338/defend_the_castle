package defendthecastle.itemget;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defendthecastle.commoninheritance.CommonJPanel;
import defendthecastle.screendisplay.DisplayStatus;
import savedata.SaveHoldItem;

//ガチャ結果表示
class DrawResult extends CommonJPanel implements MouseListener{
	private ItemGetImage itemGetImage;
	private List<Integer> getCore = new ArrayList<>();
	private List<Point> corePosition = new ArrayList<>();
	private List<Integer> getWeapon = new ArrayList<>();
	private List<Point> weaponPosition = new ArrayList<>();
	private double total;
	private int position;
	private final int UNIT_SIZE = 80;
	
	DrawResult(ScheduledExecutorService scheduler, GachaInformation gachaInformation, HoldMedal holdMedal, ItemGetImage itemGetImage){
		this.itemGetImage = itemGetImage;
		addMouseListener(this);
		repaintTimer(scheduler, brown());
		switch(gachaInformation.getRepeatNumber()) {
		case 1:
			position = 435;
			break;
		case 5:
			position = 255;
			break;
		case 10:
			position = 20;
			break;
		default:
			break;
		}
		IntStream.range(0, gachaInformation.getRepeatNumber()).forEach(_ -> gacha(gachaInformation));
		save(holdMedal);
	}
	
	void gacha(GachaInformation gachaInformation) {
		Consumer<List<Point>> addPosition = (list) -> {
			list.add(new Point(position, 90));
		};
		double value = Math.random() * 100;
		total = 0;
		if(canGetItem(value, gachaInformation.getCoreLineup(), gachaInformation.getCoreRatio(), getCore)) {
			addPosition.accept(corePosition);
		}else if(canGetItem(value, gachaInformation.getWeaponLineup(), gachaInformation.getWeaponRatio(), getWeapon)) {
			addPosition.accept(weaponPosition);
		}
		position += 90;
	}
	
	boolean canGetItem(double value, List<Integer> lineupList, List<Double> ratioList, List<Integer> getList) {
		if(ratioList.size() != 0) {
			for(int i = 0; i < ratioList.size(); i++) {
				total += ratioList.get(i);
				if(value < total) {
					getList.add(lineupList.get(i));
					return true;
				}
			}
		}
		return false;
	}
	
	void save(HoldMedal holdMedal) {
		//保有アイテムの更新
		var saveHoldItem = createSaveHoldItem();
		saveHoldItem.load();
		saveHoldItem.setCoreNumberList(getItemList(saveHoldItem.getCoreNumberList(), getCore));
		saveHoldItem.setWeaponNumberList(getItemList(saveHoldItem.getWeaponNumberList(), getWeapon));
		saveHoldItem.save();
		//保有メダルの更新
		holdMedal.recountMedal();
		holdMedal.save();
	}
	
	SaveHoldItem createSaveHoldItem() {
		return new SaveHoldItem();
	}
	
	List<Integer> getItemList(List<Integer> dataList, List<Integer> getList){
		int[] count = new int[dataList.size()];
		getList.stream().forEach(i -> count[i]++);
		return IntStream.range(0, count.length).mapToObj(i -> dataList.get(i) + count[i]).collect(Collectors.toList());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g, getCore, itemGetImage.getCoreImageList(), corePosition);
		draw(g, getWeapon, itemGetImage.getWeaponImageList(), weaponPosition);
	}
	
	void draw(Graphics g, List<Integer> getList, List<BufferedImage> imageList, List<Point> position) {
		if(getList.size() != 0) {
			IntStream.range(0, getList.size()).forEach(i -> g.drawImage(imageList.get(getList.get(i)), position.get(i).x, position.get(i).y, null));
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		int selectNumber = getSelectNumber(e.getPoint(), corePosition);
		if(0 <= selectNumber) {
			displayCore(selectNumber);
			return;
		}
		selectNumber = getSelectNumber(e.getPoint(), weaponPosition);
		if(0 <= selectNumber) {
			displayWeapon(selectNumber);
			return;
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
	
	int getSelectNumber(Point point, List<Point> positionList) {
		for(int i = 0; i < positionList.size(); i++) {
			if(ValueRange.of(positionList.get(i).x, positionList.get(i).x + UNIT_SIZE).isValidIntValue(point.x)
					&& ValueRange.of(positionList.get(i).y, positionList.get(i).y + UNIT_SIZE).isValidIntValue(point.y)){
				return i;
			}
		}
		return -1;
	}
	
	void displayCore(int selectNumber) {
		new DisplayStatus().core(itemGetImage.getCoreImageList().get(getCore.get(selectNumber)), getCore.get(selectNumber));
	}
	
	void displayWeapon(int selectNumber) {
		new DisplayStatus().weapon(itemGetImage.getWeaponImageList().get(getWeapon.get(selectNumber)), getWeapon.get(selectNumber));
	}
}