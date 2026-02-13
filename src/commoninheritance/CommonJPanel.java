package commoninheritance;

import java.awt.Color;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public abstract class CommonJPanel extends JPanel implements AncestorListener{
	private ScheduledExecutorService commonScheduler;
	private ScheduledFuture<?> commonFuture;
	
	/**
	 * JPanelが表示されると約60fpsで画面の更新を行う。
	 * 表示が終了すると更新は停止される。
	 * @param scheduler - futureで使用するscheduler。
	 */
	protected void repaintTimer(ScheduledExecutorService scheduler, Color color) {
		commonScheduler = scheduler;
		setBackground(color);
		addAncestorListener(this);
		setLayout(null);
		requestFocus();
	}
	
	/**
	 * 画面の基本的な背景。
	 * {@link #repaintTimer}で指定する。
	 * @return 茶色を返却する。
	 */
	protected Color brown() {
		return new Color(240, 170, 80);
	}
	
	/**
	 * デフォルト画面の背景。
	 * {@link #repaintTimer}で指定する。
	 * @return ほんの少し黒味のある白色を返却する。
	 */
	protected Color defaultWhite() {
		return new Color(240, 240, 240);
	}
	
	@Override
    public void ancestorAdded(AncestorEvent event){
		commonFuture = commonScheduler.scheduleAtFixedRate(this::repaintTimerProcess, 0, 16, TimeUnit.MILLISECONDS);
	}
	@Override
    public void 	ancestorMoved(AncestorEvent event) {
	}
	@Override
    public void ancestorRemoved(AncestorEvent event) {
		if(commonFuture != null) {
			commonFuture.cancel(true);
		}
	}
	
	void repaintTimerProcess() {
		SwingUtilities.invokeLater(this::repaint);
	}
}