package defendthecastle.itemget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JPanel;

import defaultdata.DefaultUnit;
import savedata.SaveHoldItem;
import screendisplay.DisplayStatus;

//ガチャ結果表示
class DrawResult extends JPanel implements MouseListener{
	private List<Integer> getCore = new ArrayList<>();
	private List<Point> corePosition = new ArrayList<>();
	private List<Integer> getWeapon = new ArrayList<>();
	private List<Point> weaponPosition = new ArrayList<>();
	private double total;
	private int position;
	private List<BufferedImage> coreImageList = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(2)).toList();
	private List<BufferedImage> weaponImageList = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getImage(2)).toList();
	private int unitSize = 80;
	
	protected DrawResult(DefaultLineup DefaultLineup){
		addMouseListener(this);
		setBackground(new Color(240, 170, 80));
		switch(DefaultLineup.getRepeatNumber()) {
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
		IntStream.range(0, DefaultLineup.getRepeatNumber()).forEach(i -> gacha(DefaultLineup));
		save(DefaultLineup);
	}
	
	private void gacha(DefaultLineup DefaultLineup) {
		Consumer<List<Point>> addPosition = (list) -> {
			list.add(new Point(position, 90));
		};
		double value = Math.random() * 100;
		total = 0;
		if(result(value, DefaultLineup.getCoreLineup(), DefaultLineup.getCoreRatio(), getCore)) {
			addPosition.accept(corePosition);
		}else if(result(value, DefaultLineup.getWeaponLineup(), DefaultLineup.getWeaponRatio(), getWeapon)) {
			addPosition.accept(weaponPosition);
		}
		position += 90;
	}
	
	private boolean result(double value, List<Integer> lineupList, List<Double> ratioList, List<Integer> getList) {
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
	
	private void save(DefaultLineup DefaultLineup) {
		//保有アイテムの更新
		SaveHoldItem SaveHoldItem = new SaveHoldItem();
		SaveHoldItem.load();
		SaveHoldItem.save(getItemList(SaveHoldItem.getCoreNumberList(), getCore), getItemList(SaveHoldItem.getWeaponNumberList(), getWeapon));
		//保有メダルの更新
		HoldMedal HoldMedal = new HoldMedal(DefaultLineup);
		HoldMedal.recountMedal();
		HoldMedal.save();
	}
	
	private List<Integer> getItemList(List<Integer> dataList, List<Integer> getList){
		int[] count = new int[dataList.size()];
		getList.stream().forEach(i -> count[i]++);
		return IntStream.range(0, count.length).mapToObj(i -> dataList.get(i) + count[i]).collect(Collectors.toList());
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g, getCore, coreImageList, corePosition);
		draw(g, getWeapon, weaponImageList, weaponPosition);
	}
	
	private void draw(Graphics g, List<Integer> getList, List<BufferedImage> imageList, List<Point> position) {
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
			new DisplayStatus().core(coreImageList.get(getCore.get(selectNumber)), getCore.get(selectNumber));
			return;
		}
		selectNumber = getSelectNumber(e.getPoint(), weaponPosition);
		if(0 <= selectNumber) {
			new DisplayStatus().weapon(weaponImageList.get(getWeapon.get(selectNumber)), getWeapon.get(selectNumber));
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
	
	private int getSelectNumber(Point point, List<Point> positionList) {
		for(int i = 0; i < positionList.size(); i++) {
			if(ValueRange.of(positionList.get(i).x, positionList.get(i).x + unitSize).isValidIntValue(point.x)
					&& ValueRange.of(positionList.get(i).y, positionList.get(i).y + unitSize).isValidIntValue(point.y)){
				return i;
			}
		}
		return -1;
	}
}