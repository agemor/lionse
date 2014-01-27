package lionse.client.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.asset.Asset;
import lionse.client.debug.Debugger;
import lionse.client.stage.World;

public class WorldUI extends ChangeListener implements UI {

	// ���� Ŭ���� ����
	public World world;

	// �Է� ���� ����
	public InputMultiplexer multiplexer;

	// ������Ʈ
	public Stage component;
	public TouchpadStyle touchpadStyle;

	public Touchpad TOUCHPAD;

	// �Է� ó��
	public int lastKey; // PC���� ������ �� ���Ǵ� ����
	public int backKeyPressedCount = 0; // �ڷΰ��� Ű�� ���� Ƚ��
	public int previousDirection = -1;

	// ������
	public WorldUI(World world) {
		this.world = world;

		// �Է°� ������Ʈ �ʱ�ȭ
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// ��ġ�е� ��Ÿ�� ����
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = new TextureRegionDrawable(Asset.ui.get(Asset.UI
				.get("TOUCHPAD_BACKGROUND")));
		touchpadStyle.knob = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("TOUCHPAD_KNOB")));

		// ������Ʈ ���� ��� �ʱ�ȭ
		TOUCHPAD = new Touchpad(40, touchpadStyle);

		// ���� ��� ����
		TOUCHPAD.setPosition(50, 50);
		TOUCHPAD.addListener(this);

		component.addActor(TOUCHPAD);
	}

	public void initialize() {
		// ��Ƽ�÷��� ����
		Gdx.input.setInputProcessor(multiplexer);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	// UI ������ ����� �������� �� ���� ������Ʈ �޼���
	@Override
	public void update(float delta) {
		component.act(delta);
		component.draw();
	}

	// UI ������
	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

	}

	// ��ġ�е�, ��ư ���� �ÿ� ����
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		float knobX = TOUCHPAD.getKnobX() - TOUCHPAD.getWidth() / 2;
		float knobY = TOUCHPAD.getKnobY() - TOUCHPAD.getHeight() / 2;
		int direction = getDirection((float) (Math.atan2(knobY, knobX) * 180 / Math.PI));

		// Ű�� ������
		if (previousDirection != direction) {

			// ������ ���
			if (direction == -1) {
				world.stage.me.moving = false;
				world.stage.me.stop();
			} else {
				world.stage.me.moving = true;
				world.stage.me.direction = direction;
				world.stage.me.move(direction);
			}

			// Debugger.log(direction);
			previousDirection = direction;
		}

	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.BACK || keycode == Keys.F1) {

			// �ڷΰ��� Ű�� ���� �� �� ������ ���� ����
			if (backKeyPressedCount > 3) {
				System.exit(0);
			}

			// ���� Ű���尡 �ִ� ���¿��� �ڷΰ��� Ű�� ������ Ű���� ����
			if (VirtualKeyboard.display) {
				VirtualKeyboard.close();
			} else {
				backKeyPressedCount++;
			}
		}

		switch (keycode) {
		case Keys.UP:
			world.stage.me.moving = true;
			world.stage.me.direction = 4;
			world.stage.me.move(4);
			lastKey = Keys.UP;
			break;
		case Keys.DOWN:
			world.stage.me.moving = true;
			world.stage.me.direction = 0;
			world.stage.me.move(0);
			lastKey = Keys.DOWN;
			break;
		case Keys.LEFT:
			world.stage.me.moving = true;
			world.stage.me.direction = 2;
			world.stage.me.move(2);
			lastKey = Keys.LEFT;
			break;
		case Keys.RIGHT:
			world.stage.me.moving = true;
			world.stage.me.direction = 6;
			world.stage.me.move(6);
			lastKey = Keys.RIGHT;
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == lastKey) {
			world.stage.me.moving = false;
			world.stage.me.stop();
		}
		return false;
	}

	private final float K = 90f / 4f;

	public int getDirection(float A) {
		if (A == 0) {
			return -1;
		} else if (K <= A && A < 3 * K) {
			return 4;
		} else if (3 * K <= A && A < 5 * K) {
			return 3;
		} else if (5 * K <= A && A < 7 * K) {
			return 2;
		} else if (7 * K <= Math.abs(A) && Math.abs(A) < 8 * K) {
			return 1;
		} else if (-7 * K <= A && A < -5 * K) {
			return 0;
		} else if (-5 * K <= A && A < -3 * K) {
			return 7;
		} else if (-3 * K <= A && A < -K) {
			return 6;
		} else {
			return 5;
		}
	}

	// ***********************************************************************

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
