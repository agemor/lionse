package lionse.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.Display;
import lionse.client.asset.Asset;
import lionse.client.net.Server;
import lionse.client.stage.World;

public class WorldUI extends ChangeListener implements UI, VirtualKeyEvent {

	// ���� Ŭ���� ����
	public World world;

	// �Է� ���� ����
	public InputMultiplexer multiplexer;

	// ������Ʈ ��Ÿ��
	public TouchpadStyle touchpadStyle;
	public ButtonStyle openChatButtonStyle;
	public ButtonStyle openItemButtonStyle;

	// ������Ʈ
	public Stage component;

	public Touchpad TOUCHPAD;
	public Button OPEN_CHAT;
	public Button OPEN_ITEM;

	// UI ���ؽ�Ʈ
	public Menu ITEM_SLOT;
	public ChatBox CHAT_BOX;

	// �Է� ó��
	public int lastKey; // PC���� ������ �� ���Ǵ� ����
	public int backKeyPressedCount = 0; // �ڷΰ��� Ű�� ���� Ƚ��
	public int previousDirection = -1;
	public boolean alert = false;
	public String alertMessage = "�׽�Ʈ �޽���";

	// ������
	public WorldUI(World world) {
		this.world = world;

		// �Է°� ������Ʈ �ʱ�ȭ
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// ���ؽ�Ʈ �ʱ�ȭ
		ITEM_SLOT = new Menu();
		CHAT_BOX = new ChatBox();

		// ��Ÿ�� ����
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = new TextureRegionDrawable(Asset.UI.get("TOUCHPAD_BACKGROUND"));
		touchpadStyle.knob = new TextureRegionDrawable(Asset.UI.get("TOUCHPAD_KNOB"));

		openChatButtonStyle = new ButtonStyle();
		openChatButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("OPEN_CHAT_BUTTON_UP"));
		openChatButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("OPEN_CHAT_BUTTON_DOWN"));
		openChatButtonStyle.checked = openChatButtonStyle.down;

		openItemButtonStyle = new ButtonStyle();
		openItemButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("OPEN_MENU_BUTTON_UP"));
		openItemButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("OPEN_MENU_BUTTON_DOWN"));
		openItemButtonStyle.checked = openItemButtonStyle.down;

		// ������Ʈ ���� ��� �ʱ�ȭ
		TOUCHPAD = new Touchpad(40, touchpadStyle);
		OPEN_CHAT = new Button(openChatButtonStyle);
		OPEN_ITEM = new Button(openItemButtonStyle);

		// ���� ��� ����
		TOUCHPAD.setPosition(50, 50);
		OPEN_CHAT.setPosition(Display.WIDTH * Display.SCALE - 100, Display.HEIGHT * Display.SCALE - 120);
		OPEN_ITEM.setPosition(Display.WIDTH * Display.SCALE - 100, Display.HEIGHT * Display.SCALE - 60);

		TOUCHPAD.addListener(this);
		OPEN_CHAT.addListener(this);
		OPEN_ITEM.addListener(this);

		component.addActor(CHAT_BOX);
		component.addActor(TOUCHPAD);
		component.addActor(ITEM_SLOT);
		component.addActor(OPEN_ITEM);
		component.addActor(OPEN_CHAT);
	}

	public void initialize() {
		// ��Ƽ�÷��� ����
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);

		// Ű���� ����
		VirtualKeyboard.setEventListener(this);
		VirtualKeyboard.close();

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	// UI ������ ����� �������� �� ���� ������Ʈ �޼���
	@Override
	public void update(float delta) {
		component.act(delta);
		component.draw();

		ITEM_SLOT.update(delta);
		CHAT_BOX.update(delta);
	}

	// UI ������
	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		VirtualKeyboard.update(delta);
		VirtualKeyboard.draw(spriteBatch, delta);

		if (alert) {
			spriteBatch.draw(Display.DISABLED, 0, 0);
			Display.ALERT.draw(spriteBatch);
			Display.FONT.setColor(Color.WHITE);
			Display.FONT.draw(spriteBatch, alertMessage, Display.ALERT.getX(), Display.ALERT.getY() + 50);
		}
	}

	// ��ġ�е�, ��ư ���� �ÿ� ����
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (actor == TOUCHPAD && !VirtualKeyboard.display) {
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
				previousDirection = direction;
			}

			// ä�� �޽��� �Է� â ����
		} else if (actor == OPEN_CHAT) {
			ITEM_SLOT.shrink();
			if (CHAT_BOX.opened) {
				CHAT_BOX.close();
				VirtualKeyboard.close();
			} else {
				CHAT_BOX.open();
				VirtualKeyboard.show();
			}// ������ â ����
		} else if (actor == OPEN_ITEM) {
			CHAT_BOX.close();
			VirtualKeyboard.close();
			if (ITEM_SLOT.shrinked) {
				ITEM_SLOT.pop();
			} else {
				ITEM_SLOT.shrink();
			}
		}
	}

	// ��� Ȥ�� ���� �޽��� ����
	public void showAlert(String message) {

		alert = true;
		alertMessage = message;
	}

	// ȭ�� ��ġ�ٿ�� Ű���忡 �Է�
	private Vector2 screenCoords = new Vector2();
	private Vector2 stageCoords;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if (alert) {
			alert = false;
			return false;
		}

		if (alert || !VirtualKeyboard.display)
			return false;
		screenCoords.set(screenX, screenY);
		stageCoords = component.screenToStageCoordinates(screenCoords);
		VirtualKeyboard.touchDown(stageCoords.x, stageCoords.y, pointer, button);
		return false;
	}

	@Override
	public void keyTyped(VirtualKeyButton key) {
		if (alert || !VirtualKeyboard.display)
			return;

		// ê�ڽ��� ���� ���� ��� �ڵ����� ��Ŀ�̵ȴ�.
		if (CHAT_BOX.opened) {

			// ���͸� ������ ��������.
			if (key.letter.equals("ENTER")) {

				String message = CHAT_BOX.INPUT.getText();

				if (message.equals(""))
					return;

				// �޽��� ���� ����. �ִ� 70��
				if (message.length() > 70) {
					message = message.substring(0, 70);
				}

				// ���� �� ����
				message = BadWordsFilter.check(message);

				// ���� ���� �޼���
				Server.chat(message);

				// �ڽ��� ���� �޽����� ���� ���ϹǷ� �ٷ� ������
				CHAT_BOX.addMessage(Server.me.name + ":" + message);
				Server.me.character.talk(Server.me.name + ":" + message);

				// �Է� â �ʱ�ȭ
				CHAT_BOX.INPUT.setText("");
				CHAT_BOX.input_buffer = "";

				return;
			}

			CHAT_BOX.input_buffer = VirtualKeyboard.addBuffer(CHAT_BOX.input_buffer, key);
			String text = VirtualKeyboard.mix(CHAT_BOX.input_buffer);
			CHAT_BOX.INPUT.setText(text);
			CHAT_BOX.INPUT.setCursorPosition(text.length() > 0 ? text.length() : 0);

		}

		return;
	}

	// ȭ�� ��ġ ��. Ű���� ó���� ���
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (alert || !VirtualKeyboard.display)
			return false;

		VirtualKeyboard.touchUp(screenX, screenY, pointer, button);
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.BACK || keycode == Keys.F1) {

			// �ڷΰ��� Ű�� ���� �� �� ������ ���� ����
			if (backKeyPressedCount > 0 && alert) {
				System.exit(0);
			}

			// ���� Ű���尡 �ִ� ���¿��� �ڷΰ��� Ű�� ������ Ű���� ����
			if (VirtualKeyboard.display) {
				VirtualKeyboard.close();
				CHAT_BOX.close();
				backKeyPressedCount = 0;
			} else {
				showAlert("    �� �� �� ������ �����մϴ�.");
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

	@Override
	public void dispose() {
	}

}
