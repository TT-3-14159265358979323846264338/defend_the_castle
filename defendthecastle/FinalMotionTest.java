package defendthecastle;

import static org.junit.Assert.*;
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
		assertTrue(canChangeInMultiplesOf5(positionX));
		assertTrue(positionY.stream().allMatch(i -> i == positionY.get(0).intValue()));
	}
	
	boolean canChangeInMultiplesOf5(List<Integer> position) {
		int loop = 5;
		for(int i = 0; i < loop; i++) {
			if(canLoopEveryCycle(i, position, loop)) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	boolean canLoopEveryCycle(int number, List<Integer> position, int cycle) {
		int index = number + cycle;
		do{
			if(position.get(number) == position.get(index).intValue()) {
				index += cycle;
				continue;
			}
			return false;
		}while(index < position.size());
		return true;
	}
	
	/**
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testFinalTimerStart() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		FinalMotion.finalTimerStart(mockScheduler);
		verify(mockScheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
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
		assertTrue(y != FinalMotion.getY());
		assertTrue(count != FinalMotion.getCount());
		verify(FinalMotion).timerStop();
	}
	
	/**
	 * タイマー稼働していればfalseを返却することを確認。
	 */
	@Test
	void testCanEndFalse() {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		FinalMotion.setFinalFuture(mockFuture);
		assertFalse(FinalMotion.canEnd());
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
