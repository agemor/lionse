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
import lionse.client.debug.Debugger;
import lionse.client.net.Server;

/**
 * 로그인 화면에서 UI 처리 클래스
 * 
 * @author 김현준
 * 
 */
public class LoginUI extends ChangeListener implements UI, VirtualKeyEvent {

	// 상위 클래스 참조
	public Login login;

	// 입력 관련 변수
	public InputMultiplexer multiplexer;

	// UI 텍스처
	public Image background;
	public ButtonStyle loginButtonStyle;
	public ButtonStyle registerButtonStyle;

	// 로그인 컴포넌트
	public Stage component;

	public TextField ID;
	public TextField PASSWORD;
	public Button LOGIN;
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

	// 생성자
	public LoginUI(Login login) {
		this.login = login;

		// UI 공통 인수 초기화
		background = new Image(Asset.UI.get("LOGIN"));

		// 버튼 스타일 설정
		loginButtonStyle = new ButtonStyle();
		loginButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("LOGIN_BUTTON_UP"));
		loginButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("LOGIN_BUTTON_DOWN"));

		registerButtonStyle = new ButtonStyle();
		registerButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_UP"));
		registerButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_DOWN"));

		// 입력과 컴포넌트 초기화
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// 컴포넌트 구성 요소 초기화
		ID = new TextField("", Display.TEXTFIELD_STYLE);
		PASSWORD = new TextField("", Display.TEXTFIELD_STYLE);
		LOGIN = new Button(loginButtonStyle);
		REGISTER = new Button(registerButtonStyle);

		// 구성 요소 설정
		ID.setOnscreenKeyboard(new VirtualKeyboardHolder());
		PASSWORD.setOnscreenKeyboard(new VirtualKeyboardHolder());

		PASSWORD.setPasswordCharacter('●');
		PASSWORD.setPasswordMode(true);

		REGISTER.setPosition(600, 250);
		LOGIN.setPosition(600, 350);
		ID.setPosition(363, 400);
		PASSWORD.setPosition(363, 350);

		ID.setWidth(200);
		PASSWORD.setWidth(200);

		LOGIN.addListener(this);
		REGISTER.addListener(this);

		// 컴포넌트에 추가
		component.addActor(background);
		component.addActor(ID);
		component.addActor(PASSWORD);
		component.addActor(LOGIN);
		component.addActor(REGISTER);
	}

	// 로그인 UI 초기화. 멀티플렉서 설정 등을 함
	@Override
	public void initialize() {
		// 멀티플렉서 설정
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);

		// 가상 키보드 설정
		VirtualKeyboard.setEventListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	// 버튼이 눌렸을 때 실행됨
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		// 로그인 버튼을 눌렀을 때
		if (actor == LOGIN) {
			loading = true;
			Debugger.log(ID.getText() + "/" + PASSWORD.getText());
			Server.login(ID.getText().trim(), PASSWORD.getText().trim());

			// 회원가입 버튼을 눌렀을 때
		} else if (actor == REGISTER) {
			login.lionse.setScreen(login.lionse.register);
		}

	}

	// 경고 혹은 오류 메시지 띄우기
	public void showAlert(String message) {
		alert = true;
		alertMessage = message;
	}

	// 뒤로가기 키(Back-Key) 를 처리
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

	// UI 랜더링 명령이 내려오기 전 상태 업데이트 메서드
	@Override
	public void update(float delta) {
		component.act(delta);
		component.draw();

	}

	// UI 랜더링
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

	// 가상 키보드의 버튼이 눌렸을 때
	@Override
	public void keyTyped(VirtualKeyButton key) {

		if (loading || alert)
			return;

		// 아이디 필드에 포커스가 있을 경우
		if (component.getKeyboardFocus() == ID) {
			id_buffer = VirtualKeyboard.addBuffer(id_buffer, key);
			String text = VirtualKeyboard.mix(id_buffer);
			ID.setText(text);
			ID.setCursorPosition(text.length() > 0 ? text.length() : 0);

			// 패스워드 필드에 포커스가 있을 경우
		} else {

			password_buffer = VirtualKeyboard.addBuffer(password_buffer, key);
			String text = VirtualKeyboard.mix(password_buffer);
			PASSWORD.setText(text);
			PASSWORD.setCursorPosition(text.length() > 0 ? text.length() : 0);

			// 패스워드 필드 입력 중 엔터가 눌리면 로그인 처리
			if (key.letter.equals("ENTER")) {
				loading = true;
				Debugger.log(ID.getText() + "/" + PASSWORD.getText());
				Server.login(ID.getText().trim(), PASSWORD.getText().trim());
			}
		}
	}

	@Override
	public void dispose() {
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

	// 화면 터치업시 키보드에 입력
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (loading || alert || !VirtualKeyboard.display)
			return false;

		VirtualKeyboard.touchUp(screenX, screenY, pointer, button);
		return false;
	}

	// *********************** 사용되지 않는 메서드 ***********************

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
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

}
