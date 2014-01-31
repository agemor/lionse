package lionse.client.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lionse.client.Display;
import lionse.client.stage.Point;
import lionse.client.stage.Renderable;

public class VirtualKeyButton implements Renderable {
	// padding (for interpretation)
	public float PADDING_X = Display.WIDTH * Display.SCALE - 790;
	public float PADDING_Y = Display.HEIGHT * Display.SCALE - 500;

	// positions
	public Point position;
	public Point targetPosition;
	public float originalY; // for restrain errors.
	public String letter;
	public float keyWidth = 60;
	public float hitArea = 0;

	// glyph
	public String value;

	// graphics
	public TextureRegion graphics;
	public TextureRegion background;
	public float barneySpeed = 0.2f;

	// touched?
	public boolean touched = false;

	public VirtualKeyButton(String letter) {
		this.letter = letter;

		if (letter.equals("SPACE")) {
			keyWidth = 60 * 3;
			hitArea = 40;
		}

		if ("ZXCVBNMzxcvbnmせぜずそばぬぱ".indexOf(letter) > 0 || letter.contains("IME")) {
			hitArea = 40;
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		// Debugger.log(letter);
		if (graphics == null || background == null)
			return;
		spriteBatch.draw(background, position.x + PADDING_X, position.y - 90 + PADDING_Y);
		spriteBatch.draw(graphics, position.x + PADDING_X, position.y + PADDING_Y);

	}

	@Override
	public void update(float delta) {
		position.x += barneySpeed * (targetPosition.x - position.x);
		position.y += barneySpeed * (targetPosition.y - position.y);
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
		this.barneySpeed = 0.3f;
		if (touched) {
			targetPosition.y = originalY - 30;
		} else {
			targetPosition.y = originalY;
		}
	}

	@Override
	public Point getPosition() {
		return position;
	}

	public boolean interacts(float x, float y) {
		// Debugger.log(position.x + PADDING_X + "/" + position.y);
		if (position.x + PADDING_X < x && x < position.x + PADDING_X + keyWidth && position.y + PADDING_Y - hitArea < y && y < position.y + 45 + PADDING_Y) {
			return true;
		}
		return false;
	}
}
