package lionse.client.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface UI extends InputProcessor {
	
	public void initialize();
	
	public void update(float delta);

	public void draw(SpriteBatch spriteBatch, float delta);
	
	public void dispose();
}
