package temp.game;

//https://stackoverflow.com/questions/7486012/static-classes-in-java

public final class Time {
	
	private static final int DAMPING_CONSTANT = 16700000;
	private static long lastFrameTime, currentTime;
	
	private Time() {
		
	}
	
	public static void init() {
		currentTime = getTime();
	}
	
	public static void update() {
		lastFrameTime = currentTime;
		currentTime = getTime();
		Timer.update();
	}
	
	public static float getDelta() {
		long nanosecondDifference = getDifference();
		if (nanosecondDifference < 40000000) {
			return (float)getDifference() / DAMPING_CONSTANT;	
		} else {
			return 0f;
		}
	}
	
	private static long getTime() {
		return System.nanoTime();
	}
	
	public static long getDifference() {
		return currentTime - lastFrameTime;
	}
}
