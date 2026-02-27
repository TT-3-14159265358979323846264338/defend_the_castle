package defendthecastle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

class MainTimer {
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> mainFuture;
	private final FallMotion[] fallMotion;
	private final FinalMotion[] finalMotion;
	private int count;
	
	MainTimer(ScheduledExecutorService scheduler, FallMotion[] fallMotion, FinalMotion[] finalMotion){
		this.scheduler = scheduler;
		this.fallMotion = fallMotion;
		this.finalMotion = finalMotion;
		mainFuture = createEffectTimer();
	}
	
	ScheduledFuture<?> createEffectTimer() {
		return scheduler.scheduleAtFixedRate(this::effectTimerProcess, 0, 300, TimeUnit.MILLISECONDS);
	}
	
	void effectTimerProcess() {
		try {
			fallMotion[count].fallTimerStart(scheduler);
		}catch(Exception ignore) {
			//これ以上新たに表示する画像がないので無視
		}
		count++;
		if(Stream.of(fallMotion).noneMatch(i -> i.canStart())) {
			Stream.of(finalMotion).forEach(i -> i.finalTimerStart(scheduler));
			mainFuture.cancel(true);
			mainFuture = null;
		}
	}
	
	boolean isEnd() {
		return mainFuture == null;
	}
}