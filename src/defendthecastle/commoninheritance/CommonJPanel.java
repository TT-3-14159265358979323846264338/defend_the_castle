package defendthecastle.commoninheritance;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public abstract class CommonJPanel extends JPanel implements AncestorListener{
	private ScheduledExecutorService commonScheduler;
	private ScheduledFuture<?> commonFuture;
	
	/**
	 * 静止画としてJPanelを使用する。
	 * 必要に応じて{@link #repaintPanel}を呼び出せば再描写可能。
	 * @param color - JPanelの背景色。
	 */
	protected void stillness(Color color) {
		setBackground(color);
		setLayout(null);
		setFocusable(true);
	}
	
	/**
	 * JPanelの再描写を行う。
	 */
	protected void repaintPanel() {
		SwingUtilities.invokeLater(this::repaint);
	}
	
	/**
	 * JPanelが表示されると約60fpsで画面の更新を行う。
	 * 表示が終了すると更新は停止される。
	 * @param scheduler - futureで使用するscheduler。
	 * @param color - JPanelの背景色。
	 */
	protected void movie(ScheduledExecutorService scheduler, Color color) {
		commonScheduler = scheduler;
		addAncestorListener(this);
		setBackground(color);
		setLayout(null);
		setFocusable(true);
	}
	
	@Override
    public void ancestorAdded(AncestorEvent event){
		if(commonScheduler != null) {
			commonFuture = repaintFuture();
		}
	}
	@Override
    public void ancestorMoved(AncestorEvent event) {
	}
	@Override
    public void ancestorRemoved(AncestorEvent event) {
		if(commonFuture != null) {
			commonFuture.cancel(true);
		}
	}
	
	ScheduledFuture<?> repaintFuture(){
		return commonScheduler.scheduleAtFixedRate(this::repaintPanel, 0, 17, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 本ゲーム画面の基本的な背景。
	 * @return 茶色を返却する。
	 */
	protected Color brown() {
		return new Color(240, 170, 80);
	}
	
	/**
	 * デフォルトJPanelの背景。
	 * @return ほんの少し黒味のある白色を返却する。
	 */
	protected Color defaultWhite() {
		return new Color(240, 240, 240);
	}
	
	/**
	 * JLabelの設定を行い、JPanelに追加する。
	 * @param label - 設定するJLabel
	 * @param name - テキスト
	 * @param x - 配置するx座標
	 * @param y - 配置するy座標
	 * @param width - 横幅
	 * @param height - 高さ
	 * @param font - フォント
	 */
	protected void setLabel(JLabel label, String name, int x, int y, int width, int height, Font font) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(font);
		add(label);
	}
	
	/**
	 * JLabelの設定を行い、JPanelに追加する。
	 * @param label - 設定するJLabel
	 * @param name - テキスト
	 * @param font - フォント
	 */
	protected void setLabel(JLabel label, String name, Font font) {
		label.setText(name);
		label.setFont(font);
		add(label);
	}
	
	/**
	 * JButtonの設定を行い、JPanelに追加する。
	 * @param button - 設定するJButton
	 * @param name - テキスト
	 * @param x - 配置するx座標
	 * @param y - 配置するy座標
	 * @param width - 横幅
	 * @param height - 高さ
	 * @param font - フォント
	 */
	protected void setButton(JButton button, String name, int x, int y, int width, int height, Font font) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(font);
		button.setFocusable(false);
		add(button);
	}
	
	/**
	 * JScrollPaneの設定を行い、JPanelに追加する。
	 * @param scroll - 設定するJScrollPane
	 * @param x - 配置するx座標
	 * @param y - 配置するy座標
	 * @param width - 横幅
	 * @param height - 高さ
	 */
	protected void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
		scroll.setFocusable(false);
		add(scroll);
	}
}