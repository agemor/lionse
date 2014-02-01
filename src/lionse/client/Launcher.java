package lionse.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Launcher {

	/**
	 * @param Launch
	 *            an application.
	 */
	public static final int[] IPHONE3G = { 320, 480 };
	public static final int[] IPHONE4G = { 640, 960 };
	public static final int[] IPAD = { 768, 1024 };
	public static final int[] GALAXYS2 = { 480, 800 };
	public static final int[] GALAXYNOTE = { 800, 1280 };
	public static final int[] OPTIMUSLTE = { 720, 1280 };
	public static final int[] VEGALTE = { 800, 1280 };
	public static final int[] GALAXYS3 = { 800, 1280 };
	public static final int[] OPTIMUS_VIEW = { 800, 1280 };

	public static int[][] DEVICE = { IPHONE3G, IPHONE4G, IPAD, GALAXYS2, GALAXYNOTE, OPTIMUSLTE, VEGALTE, GALAXYS3,
			OPTIMUS_VIEW };

	public static int[] device = DEVICE[3];

	public static void main(String[] args) {

		Main main = new Main();

		new LwjglApplication(main, "Lionse", (int) device[1], (int) device[0], true);
	}

}
