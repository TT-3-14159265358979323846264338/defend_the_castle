package testdataedit;

import javax.swing.JDialog;

//セーブデータ編集ダイアログ
public class TestDataEdit extends JDialog{
	public TestDataEdit() {
		setTitle("テスト用セーブデータ編集");
		setSize(785, 640);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		add(createTestPanel());
		setVisible(true);
	}
	
	TestPanel createTestPanel() {
		return new TestPanel(this);
	}
	
	void disposeDialog() {
		dispose();
	}
}