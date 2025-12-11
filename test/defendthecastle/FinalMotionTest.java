package defendthecastle;

import static custommatcher.MatcherOfPeriodicChange.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FinalMotionTest {
	private FinalMotion FinalMotion;
	
	@BeforeEach
	void setUp() {
		FinalMotion = spy(new FinalMotion(10));
	}
	
	/**
	 * xは5の倍数ごとに値がループしていること、yは全て同じ値であることを確認。
	 */
	@Test
	void testFinalMotion() {
		List<Integer> positionX = new ArrayList<>();
		List<Integer> positionY = new ArrayList<>();
		IntStream.range(0, 30).forEach(i -> {
			FinalMotion = new FinalMotion(i);
			positionX.add(FinalMotion.getX());
			positionY.add(FinalMotion.getY());
		});
		assertThat(positionX, periodicChange(5));
		assertThat(positionY, everyItem(is(positionY.get(0))));
	}
	
	/**
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testFinalTimerStart() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		FinalMotion.finalTimerStart(mockScheduler);
		verify(mockScheduler).scheduleAtFixedRate(Mockito.any(Runnable.class), anyLong(), anyLong(), Mockito.any(TimeUnit.class));
	}
	
	/**
	 * y座標とcountが変化していることを確認。<br>
	 * タイマー停止操作が呼び出されているか確認。
	 */
	@Test
	void testFinalTimerProcess() {
		int y = FinalMotion.getY();
		int count = FinalMotion.getCount();
		FinalMotion.finalTimerProcess();
		assertThat(FinalMotion.getY(), is(not(y)));
		assertThat(FinalMotion.getCount(), is(not(count)));
		verify(FinalMotion).timerStop();
	}
	
	/**
	 * タイマー稼働していればfalseを返却することを確認。
	 */
	@Test
	void testCanEndFalse() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FinalMotion.setFinalFuture(mockFuture);
		assertThat(FinalMotion.canEnd(), is(false));
	}
	
	/**
	 * タイマーが基準に達すれば停止するか確認。
	 */
	@Test
	void testTimerStopTrue() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FinalMotion.setFinalFuture(mockFuture);
		FinalMotion.setCount(11);
		FinalMotion.timerStop();
		verify(mockFuture).cancel(true);
	}
	
	/**
	 * タイマーが基準に達していなければ稼働を続けているか確認。
	 */
	@Test
	void testTimerStopFalse() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FinalMotion.setFinalFuture(mockFuture);
		FinalMotion.setCount(10);
		FinalMotion.timerStop();
		verify(mockFuture, never()).cancel(true);
	}
}
