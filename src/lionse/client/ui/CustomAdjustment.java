package com.lionse.ui;

import com.badlogic.gdx.Gdx;

public class CustomAdjustment {
	public static int getVirtualKeyboardX() {
		// return (Gdx.graphics.getWidth()-800)
		switch (Gdx.graphics.getWidth() * Gdx.graphics.getHeight()) {
		case 320 * 480: // Optimus One, Apple iPhone3G
			return -70;
		case 480 * 800: // Samsung GalaxyS2
			return 10;
		case 768 * 1024: // Apple iPad
			return 65;
		case 640 * 960: // Apple iPhone4G
			return 50;
		case 800 * 1280: // Samsung Galaxy Note, Pentech Vega LTE
			return 130;
		case 720 * 1280: // LG Optimus LTE
			return 130;
		default:
			return 50;
		}
	}

	public static int getVirtualKeyboardY() {
		switch (Gdx.graphics.getWidth() * Gdx.graphics.getHeight()) {
		case 320 * 480: // Optimus One, Apple iPhone3G
			return -120;
		case 480 * 800: // Samsung GalaxyS2
			return 0;
		case 768 * 1024: // Apple iPad
			return 70;
		case 640 * 960: // Apple iPhone4G
			return 60;
		case 800 * 1280: // Samsung Galaxy Note, Pentech Vega LTE
			return 145;
		case 720 * 1280: // LG Optimus LTE
			return 115;
		default:
			return 50;
		}
	}
}
