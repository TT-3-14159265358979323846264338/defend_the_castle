package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FallMotionTest {
	@Spy
	private FallMotion FallMotion;
	
	/**
	 * タイマーが起動するとtrueとなることを確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testFallTimerStart() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		FallMotion.fallTimerStart(mockScheduler);
		assertThat(FallMotion.canStart(), is(true));
		verify(mockScheduler).scheduleAtFixedRate(Mockito.any(Runnable.class), anyLong(), anyLong(), Mockito.any(TimeUnit.class));
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
		assertThat(FallMotion.getAngle(), not(initialAngle));
		assertThat(FallMotion.getY(), not(initialY));
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
		assertThat(FallMotion.canStart(), is(true));
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
		assertThat(FallMotion.canStart(), is(false));
		verify(mockFuture).cancel(true);
	}
	
	/**
	 * 角度が2πラジアン以内であるか確認。
	 */
	@Test
	void testRandomAngle() {
		List<Double> angle = new ArrayList<>();
		IntStream.range(0, 100).forEach(i -> angle.add(FallMotion.randomAngle()));
		assertThat(angle, allOf(everyItem(lessThan(Math.PI * 2)), everyItem(greaterThanOrEqualTo(0.0))));
	}
	
	/**
	 * 落下位置が画面内にあるか確認。
	 */
	@Test
	void testRandomX() {
		List<Integer> position = new ArrayList<>();
		IntStream.range(0, 100).forEach(i -> position.add(FallMotion.randomX()));
		assertThat(position, allOf(everyItem(lessThan(400)), everyItem(greaterThanOrEqualTo(0))));
	}
}