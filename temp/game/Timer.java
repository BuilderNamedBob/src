package temp.game;

import java.util.ArrayList;

public class Timer {
	
	public static ArrayList<Timer> runningTimers = new ArrayList<Timer>();
	
	public double startingTime;
	public double timer;
	
	public Timer(float startingTime) {
		this.startingTime = (double)startingTime;
		this.timer = this.startingTime;
	}
	
	public static void update() {
		ArrayList<Timer> finishedTimers = new ArrayList<Timer>();
		for (Timer t : runningTimers) {
			t.timer -= (double)Time.getDifference() / 1000000000;
			if (t.timer <= 0) {
				finishedTimers.add(t);
			}
		}
		for (Timer t : finishedTimers) {
			t.reset();
		}
	}
	
	public void start() {
		if (!runningTimers.contains(this)) {
			runningTimers.add(this);
		} else {
			System.out.println("Timer already running.");
		}
	}
	
	public void reset() {
		timer = startingTime;
		runningTimers.remove(this);
	}
	
	public boolean isRunning() {
		if (runningTimers.contains(this)) {
			return true;
		} else {
			return false;
		}
	}
	
	public float progress() {
		return (float)(startingTime - timer);
	}
}
