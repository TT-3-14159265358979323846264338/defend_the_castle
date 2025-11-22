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
	private ScheduledExecutorService mockScheduler;

	@BeforeEach
	void setUp() {
		FallMotion = new FallMotion();
		mockScheduler = mock(ScheduledExecutorService.class);
	}
	
	/**
	 * タイマーが起動するとtrueとなることを確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testFallTimerStart() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		doReturn(mockFuture).when(mockScheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
		FallMotion.fallTimerStart(mockScheduler);
		assertTrue(FallMotion.canRunTimer());
		assertEquals(mockFuture, FallMotion.getFallFuture());
	}
	/**
	 * 角度と位置が変化していることを確認。
	 */
	@Test
	void testFallTimerProcess() {
		double initialAngle = FallMotion.getAngle();
		int initialY = FallMotion.getY();
		FallMotion.fallTimerProcess();
		assertTrue(initialAngle != FallMotion.getAngle());
		assertTrue(initialY != FallMotion.getY());
	}
}