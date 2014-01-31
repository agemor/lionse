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

	// 상위 클래스 참조
	public World world;

	// 입력 관련 변수
	public InputMultiplexer multiplexer;

	// 컴포넌트 스타일
	public TouchpadStyle touchpadStyle;
	public ButtonStyle openChatButtonStyle;
	public ButtonStyle openItemButtonStyle;

	// 컴포넌트
	public Stage component;

	public Touchpad TOUCHPAD;
	public Button OPEN_CHAT;
	public Button OPEN_ITEM;

	// UI 컨텍스트
	public Menu ITEM_SLOT;
	public ChatBox CHAT_BOX;

	// 입력 처리
	public int lastKey; // PC에서 실행할 때 사용되는 변수
	public int backKeyPressedCount = 0; // 뒤로가기 키를 누른 횟수
	public int previousDirection = -1;
	public boolean alert = false;
	public String alertMessage = "테스트 메시지";

	// 생성자
	public WorldUI(World world) {
		this.world = world;

		// 입력과 컴포넌트 초기화
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// 컨텍스트 초기화
		ITEM_SLOT = new Menu();
		CHAT_BOX = new ChatBox();

		// 스타일 설정
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

		// 컴포넌트 구성 요소 초기화
		TOUCHPAD = new Touchpad(40, touchpadStyle);
		OPEN_CHAT = new Button(openChatButtonStyle);
		OPEN_ITEM = new Button(openItemButtonStyle);

		// 구성 요소 설정
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
		// 멀티플렉서 설정
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);

		// 키보드 설정
		VirtualKeyboard.setEventListener(this);
		VirtualKeyboard.close();

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	// UI 랜더링 명령이 내려오기 전 상태 업데이트 메서드
	@Override
	public void update(float delta) {
		component.act(delta);
		component.draw();

		ITEM_SLOT.update(delta);
		CHAT_BOX.update(delta);
	}

	// UI 랜더링
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

	// 터치패드, 버튼 누를 시에 실행
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (actor == TOUCHPAD && !VirtualKeyboard.display) {
			float knobX = TOUCHPAD.getKnobX() - TOUCHPAD.getWidth() / 2;
			float knobY = TOUCHPAD.getKnobY() - TOUCHPAD.getHeight() / 2;
			int direction = getDirection((float) (Math.atan2(knobY, knobX) * 180 / Math.PI));

			// 키가 변했음
			if (previousDirection != direction) {

				// 정지일 경우
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

			// 채팅 메시지 입력 창 열기
		} else if (actor == OPEN_CHAT) {
			ITEM_SLOT.shrink();
			if (CHAT_BOX.opened) {
				CHAT_BOX.close();
				VirtualKeyboard.close();
			} else {
				CHAT_BOX.open();
				VirtualKeyboard.show();
			}// 아이템 창 열기
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

	// 경고 혹은 오류 메시지 띄우기
	public void showAlert(String message) {

		alert = true;
		alertMessage = message;
	}

	// 화면 터치다운시 키보드에 입력
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

		// 챗박스가 열려 있을 경우 자동으로 포커싱된다.
		if (CHAT_BOX.opened) {

			// 엔터를 누르면 보내진다.
			if (key.letter.equals("ENTER")) {

				String message = CHAT_BOX.INPUT.getText();

				if (message.equals(""))
					return;

				// 메시지 길이 제한. 최대 70자
				if (message.length() > 70) {
					message = message.substring(0, 70);
				}

				// 나쁜 말 제거
				message = BadWordsFilter.check(message);

				// 서버 전송 메서드
				Server.chat(message);

				// 자신이 보낸 메시지는 받지 못하므로 바로 랜더링
				CHAT_BOX.addMessage(Server.me.name + ":" + message);
				Server.me.character.talk(Server.me.name + ":" + message);

				// 입력 창 초기화
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

	// 화면 터치 업. 키보드 처리에 사용
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

			// 뒤로가기 키를 연속 세 번 누르면 게임 종료
			if (backKeyPressedCount > 0 && alert) {
				System.exit(0);
			}

			// 가상 키보드가 있는 상태에서 뒤로가기 키가 눌리면 키보드 끄기
			if (VirtualKeyboard.display) {
				VirtualKeyboard.close();
				CHAT_BOX.close();
				backKeyPressedCount = 0;
			} else {
				showAlert("    한 번 더 누르면 종료합니다.");
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
