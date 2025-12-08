package testdataedit;

import javax.swing.JDialog;

//セーブデータ編集ダイアログ
public class TestDataEdit extends JDialog{
	public TestDataEdit() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("テスト用セーブデータ編集");
		setSize(785, 640);
		setLocationRelativeTo(null);
		add(new TestPanel(this));
		setVisible(true);
	}
	
	protected void disposeDialog() {
		dispose();
	}
}