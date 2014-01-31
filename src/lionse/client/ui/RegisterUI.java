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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.Display;
import lionse.client.asset.Asset;
import lionse.client.net.Server;

/**
 * 
 * 회원가입 화면에서의 UI처리 클래스
 * 
 * @author 김현준
 * 
 */
public class RegisterUI extends ChangeListener implements UI, VirtualKeyEvent {

	// 상위 클래스 참조
	public Register register;

	// 입력 관련 변수
	public InputMultiplexer multiplexer;

	// UI 공통 인수
	public Image background;
	public ButtonStyle registerButtonStyle;
	public ButtonStyle cancelButtonStyle;
	public ButtonStyle duplicationButtonStyle;

	// 회원가입 컴포넌트
	public Stage component;

	public TextField ID;
	public TextField PASSWORD;
	public TextField EMAIL;
	public TextField CHARACTER;
	public Button CANCEL;
	public Button REGISTER;

	// 입력 처리
	public float stateTime = 0;
	public int backKeyPressedCount = 0;
	public boolean loading = false;
	public boolean alert = false;
	public String alertMessage = "테스트 메시지";

	// 텍스트 필드 버퍼
	private String id_buffer = "";
	private String password_buffer = "";
	private String email_buffer = "";
	private String character_buffer = "";

	public RegisterUI(Register register) {
		this.register = register;

		// UI 공통 인수 초기화
		background = new Image(Asset.UI.get("REGISTER"));

		// 버튼 스타일 설정
		registerButtonStyle = new ButtonStyle();
		registerButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_UP"));
		registerButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_DOWN"));

		cancelButtonStyle = new ButtonStyle();
		cancelButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("CANCEL_BUTTON_UP"));
		cancelButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("CANCEL_BUTTON_DOWN"));

		// 입력과 컴포넌트 초기화
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// 컴포넌트 구성 요소 초기화
		ID = new TextField("", Display.TEXTFIELD_STYLE);
		PASSWORD = new TextField("", Display.TEXTFIELD_STYLE);
		EMAIL = new TextField("", Display.TEXTFIELD_STYLE);
		CHARACTER = new TextField("", Display.TEXTFIELD_STYLE);
		CANCEL = new Button(cancelButtonStyle);
		REGISTER = new Button(registerButtonStyle);

		// 구성 요소 설정
		ID.setOnscreenKeyboard(new VirtualKeyboardHolder());
		PASSWORD.setOnscreenKeyboard(new VirtualKeyboardHolder());
		EMAIL.setOnscreenKeyboard(new VirtualKeyboardHolder());
		CHARACTER.setOnscreenKeyboard(new VirtualKeyboardHolder());

		ID.setPosition(100, 480 - 50 - 36);
		PASSWORD.setPosition(500, 480 - 50 - 36);
		EMAIL.setPosition(540, 480 - 110 - 36);
		CHARACTER.setPosition(140, 480 - 110 - 36);

		ID.setWidth(200);
		PASSWORD.setWidth(200);
		EMAIL.setWidth(200);
		CHARACTER.setWidth(200);
		CANCEL.setPosition(520, 480 - 160 - 80);
		REGISTER.setPosition(320, 480 - 160 - 80);

		CANCEL.addListener(this);
		REGISTER.addListener(this);

		component.addActor(background);
		component.addActor(ID);
		component.addActor(PASSWORD);
		component.addActor(EMAIL);
		component.addActor(CHARACTER);
		component.addActor(REGISTER);
		component.addActor(CANCEL);

	}

	@Override
	public void initialize() {

		// 입력 초기화
		id_buffer = "";
		password_buffer = "";
		email_buffer = "";
		character_buffer = "";

		// 멀티플렉서 설정
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);

		// 가상 키보드 설정
		VirtualKeyboard.setEventListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {
		if (actor == REGISTER) {
			if (!(ID.getText().length() < 2 || PASSWORD.getText().length() < 4 || EMAIL.getText().length() < 4 || CHARACTER.getText().length() < 2)) {
				loading = true;
				Server.register(ID.getText().trim(), PASSWORD.getText().trim(), CHARACTER.getText().trim(), EMAIL.getText().trim());
			} else {
				showAlert("제대로 입력되지 않은 정보가 있습니다.");
			}
		} else if (actor == CANCEL) {
			register.lionse.setScreen(register.lionse.login);
		}
	}

	@Override
	public void update(float delta) {
		component.act(delta);
		component.draw();
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		stateTime += delta;

		VirtualKeyboard.update(delta);
		VirtualKeyboard.draw(spriteBatch, delta);

		if (loading) {
			spriteBatch.draw(Display.DISABLED, 0, 0);
			Display.LOADING.draw(spriteBatch);
			Display.LOADING.setRotation(stateTime * 500);
		}

		if (alert) {
			spriteBatch.draw(Display.DISABLED, 0, 0);
			Display.ALERT.draw(spriteBatch);
			Display.FONT.setColor(Color.WHITE);
			Display.FONT.draw(spriteBatch, alertMessage, Display.ALERT.getX(), Display.ALERT.getY() + 50);
		}
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

		if (loading || alert || !VirtualKeyboard.display)
			return false;
		screenCoords.set(screenX, screenY);
		stageCoords = component.screenToStageCoordinates(screenCoords);
		VirtualKeyboard.touchDown(stageCoords.x, stageCoords.y, pointer, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (loading || alert || !VirtualKeyboard.display)
			return false;

		VirtualKeyboard.touchUp(screenX, screenY, pointer, button);
		return false;
	}

	@Override
	public void keyTyped(VirtualKeyButton key) {
		// 아이디 필드에 포커스가 있을 경우
		if (component.getKeyboardFocus() == ID) {
			id_buffer = VirtualKeyboard.addBuffer(id_buffer, key);
			String text = VirtualKeyboard.mix(id_buffer);
			ID.setText(text);
			ID.setCursorPosition(text.length() > 0 ? text.length() : 0);
			// 패스워드 필드에 포커스가 있을 경우
		} else if (component.getKeyboardFocus() == PASSWORD) {
			password_buffer = VirtualKeyboard.addBuffer(password_buffer, key);
			String text = VirtualKeyboard.mix(password_buffer);
			PASSWORD.setText(text);
			PASSWORD.setCursorPosition(text.length() > 0 ? text.length() : 0);
		} else if (component.getKeyboardFocus() == EMAIL) {
			email_buffer = VirtualKeyboard.addBuffer(email_buffer, key);
			String text = VirtualKeyboard.mix(email_buffer);
			EMAIL.setText(text);
			EMAIL.setCursorPosition(text.length() > 0 ? text.length() : 0);
		} else if (component.getKeyboardFocus() == CHARACTER) {
			character_buffer = VirtualKeyboard.addBuffer(character_buffer, key);
			String text = VirtualKeyboard.mix(character_buffer);
			CHARACTER.setText(text);
			CHARACTER.setCursorPosition(text.length() > 0 ? text.length() : 0);
		}

	}

	// 경고 혹은 오류 메시지 띄우기
	public void showAlert(String message) {
		alert = true;
		alertMessage = message;
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
				backKeyPressedCount = 0;
			} else {
				showAlert("    한 번 더 누르면 종료합니다.");
				backKeyPressedCount++;
			}
		}
		return false;
	}

	// *************************************************************

	@Override
	public boolean scrolled(int amount) {

		return false;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

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

}
