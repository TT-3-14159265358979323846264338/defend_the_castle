package defendthecastle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FallMotionTest {
	private FallMotion FallMotion;

	@BeforeEach
	void setUp() {
		FallMotion = spy(new FallMotion());
	}
	
	/**
	 * タイマーが起動するとtrueとなることを確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testFallTimerStart() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		FallMotion.fallTimerStart(mockScheduler);
		assertTrue(FallMotion.canStart());
		verify(mockScheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
	}
	
	/**
	 * 角度と位置が変化していることを確認。<br>
	 * タイマー停止操作が呼び出されているか確認。
	 */
	@Test
	void testFallTimerProcess() {
		double initialAngle = FallMotion.getAngle();
		int initialY = FallMotion.getY();
		FallMotion.fallTimerProcess();
		assertTrue(initialAngle != FallMotion.getAngle());
		assertTrue(initialY != FallMotion.getY());
		verify(FallMotion).timerStop();
	}
	
	/**
	 * yの値が基準より小さければタイマーは動作を継続しているか確認。
	 */
	@Test
	void testTimerStopFalse() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FallMotion.setStart(true);
		FallMotion.setFallFuture(mockFuture);
		FallMotion.setY(100);
		FallMotion.timerStop();
		assertTrue(FallMotion.canStart());
		verify(mockFuture, never()).cancel(true);
	}
	
	/**
	 * yの値が基準より大きければタイマーは動作を停止させたか確認。
	 */
	@Test
	void testTimerStopTrue() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FallMotion.setStart(true);
		FallMotion.setFallFuture(mockFuture);
		FallMotion.setY(500);
		FallMotion.timerStop();
		assertFalse(FallMotion.canStart());
		verify(mockFuture).cancel(true);
	}
}