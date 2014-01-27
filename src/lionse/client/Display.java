package lionse.client;

import com.badlogic.gdx.Gdx;

public class Display {
	public static float ORIGINAL_WIDTH = 800;
	public static float ORIGINAL_HEIGHT = 480;
	public static float WIDTH = Gdx.graphics.getWidth();
	public static float HEIGHT = Gdx.graphics.getHeight();
	public static float SCALE_WIDTH = ORIGINAL_WIDTH / WIDTH;
	public static float SCALE_HEIGHT = ORIGINAL_HEIGHT / HEIGHT;
	public static float SCALE = SCALE_WIDTH > SCALE_HEIGHT ? SCALE_WIDTH : SCALE_HEIGHT;
}
