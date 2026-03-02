package defendthecastle.battle.battledialog;

import java.util.stream.IntStream;

import javax.swing.JLabel;

import defaultdata.Stage;

class PauseClearPanel extends ClearMerit{
	PauseClearPanel(Stage stage) {
		beforeSet(stage);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(_ -> new JLabel()).toArray(JLabel[]::new);
		afterSet();
	}
}