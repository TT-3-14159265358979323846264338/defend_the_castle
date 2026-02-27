package defendthecastle.itemget;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

class AutoRotate implements AncestorListener{
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private double angle;
	
	AutoRotate(ScheduledExecutorService scheduler, MenuItemGet menuItemGet){
		this.scheduler = scheduler;
		menuItemGet.addAncestorListener(this);
	}
	
	@Override
    public void ancestorAdded(AncestorEvent event){
		future = mainFuture();
	}
	@Override
    public void ancestorMoved(AncestorEvent event) {
	}
	@Override
    public void ancestorRemoved(AncestorEvent event) {
		if(future != null) {
			future.cancel(true);
			future = null;
		}
	}

	ScheduledFuture<?> mainFuture() {
		return scheduler.scheduleAtFixedRate(this::mainFutureProcess, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	void mainFutureProcess() {
		angle += 0.03;
		if(Math.PI * 10000 < angle) {
			angle = 0;
		}
	}
	
	double getAngle() {
		return angle;
	}
}