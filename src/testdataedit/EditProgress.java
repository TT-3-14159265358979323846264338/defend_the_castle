package testdataedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import defaultdata.DefaultStage;
import savedata.SaveGameProgress;

//クリア状況編集
class EditProgress extends JPanel{
	private JLabel medalLabel;
	private JSpinner medalSpinner;
	private JLabel[] nameLabel;
	private JRadioButton[] stage;
	private List<JRadioButton[]> merit;
	private List<BufferedImage> stageImage = DefaultStage.STAGE_DATA.stream().map(i -> i.getImage(20)).toList();
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private List<Boolean> clearStatus;
	private List<List<Boolean>> meritStatus;
	private int medal;
	private int sizeX = 110;
	private int sizeY = 70;
	
	protected EditProgress() {
		load();
		addLabel();
		addSpinner();
		addRadio();
		setPreferredSize(new Dimension(500, sizeY * stageImage.size()));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawImage(g);
		setLabel();
		setSpinner();
		setRadio();
	}
	
	private void load() {
		SaveGameProgress.load();
		clearStatus = SaveGameProgress.getClearStatus();
		meritStatus = SaveGameProgress.getMeritStatus();
		medal = SaveGameProgress.getMedal();
	}
	
	protected void save() {
		clearStatus = Stream.of(stage).map(i -> i.isSelected()).collect(Collectors.toList());
		meritStatus = merit.stream().map(i -> Stream.of(i).map(j -> j.isSelected()).collect(Collectors.toList())).collect(Collectors.toList());
		medal = (int) medalSpinner.getValue();
		SaveGameProgress.save(clearStatus, meritStatus, medal, 0);
	}
	
	private void addLabel() {
		Function<Integer, JLabel[]> initialize = count -> {
			return IntStream.range(0, count).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		};
		BiConsumer<JLabel, String> set = (label, name) -> {
			add(label);
			label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
			label.setText(name);
		};
		nameLabel = initialize.apply(stageImage.size());
		IntStream.range(0, DefaultStage.STAGE_DATA.size()).forEach(i -> set.accept(nameLabel[i], DefaultStage.STAGE_DATA.get(i).getName()));
		medalLabel = new JLabel();
		set.accept(medalLabel, "保有メダル");
	}
	
	private void setLabel() {
		IntStream.range(0, nameLabel.length).forEach(i -> nameLabel[i].setBounds(sizeX, (i + 1) * sizeY, sizeX, sizeY));
		medalLabel.setBounds(sizeX, 0, sizeX, sizeY);
	}
	
	private void addSpinner() {
		medalSpinner = new JSpinner();
		add(medalSpinner);
		medalSpinner.setModel(new SpinnerNumberModel(medal, 0, 100000, 100));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(medalSpinner);
		editor.getTextField().setEditable(false);
		editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		medalSpinner.setEditor(editor);
	}
	
	private void setSpinner() {
		medalSpinner.setBounds(sizeX * 2, 0, sizeX, sizeY);
		medalSpinner.setPreferredSize(medalSpinner.getSize());
		medalSpinner.setFont(new Font("Arail", Font.BOLD, 15));
	}
	
	private void addRadio() {
		Function<Integer, JRadioButton[]> initialize = count -> {
			return IntStream.range(0, count).mapToObj(i -> new JRadioButton()).toArray(JRadioButton[]::new);
		};
		BiConsumer<JRadioButton[], List<Boolean>> set = (radio, clear) -> {
			IntStream.range(0, radio.length).forEach(i -> {
				add(radio[i]);
				radio[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 10));
				radio[i].setOpaque(false);
				if(clear.get(i)) {
					radio[i].setSelected(true);
				}
			});
		};
		stage = initialize.apply(stageImage.size());
		set.accept(stage, clearStatus);
		Stream.of(stage).forEach(i -> i.setText("ステージクリア"));
		merit = DefaultStage.STAGE_DATA.stream().map(i -> initialize.apply(i.getMerit().size())).toList();
		IntStream.range(0, merit.size()).forEach(i -> {
			set.accept(merit.get(i), meritStatus.get(i));
			IntStream.range(0, merit.get(i).length).forEach(j -> merit.get(i)[j].setText("戦功" + (j + 1) + "クリア"));
		});
	}
	
	private void setRadio() {
		IntStream.range(0, stage.length).forEach(i -> stage[i].setBounds(sizeX + 100, (i + 1) * sizeY, sizeX, sizeY));
		IntStream.range(0, merit.size()).forEach(i -> IntStream.range(0, merit.get(i).length).forEach(j -> merit.get(i)[j].setBounds(100 + sizeX * (2 + j / 2), (i + 1) * sizeY + j % 2 * sizeY / 2, sizeX, sizeY / 2)));
	}
	
	private void drawImage(Graphics g) {
		IntStream.range(0, stageImage.size()).forEach(i -> g.drawImage(stageImage.get(i), 0, (i + 1) * sizeY, this));
	}
}