package lionse.client.stage.unit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lionse.client.Display;
import lionse.client.stage.Point;
import lionse.client.stage.Unit;

public class Natural extends Unit {

	public Point bottom;

	public Natural(TextureRegion[] graphic) {
		super(Unit.Type.OBJECT, graphic);

		bottom = new Point();
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		spriteBatch.draw(texture[direction], position.x, Display.HEIGHT - position.y);
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public Point getPosition() {
		bottom.x = position.x;
		bottom.y = position.y + 100;
		bottom.z = position.z;
		return bottom;
	}
}
