package com.lionse.debug;

public class Debugger {

	private static long startTime = System.nanoTime();
	private static int frames;

	public static void log(Object message) {
		System.out.println(message);
	}

	public static void fps() {
		frames++;
		if (System.nanoTime() - startTime >= 1000000000) {
			log("[debug] fps: "+frames);
			frames = 0;
			startTime = System.nanoTime();
		}
	}

}
