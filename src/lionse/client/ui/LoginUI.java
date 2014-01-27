package com.lionse.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.lionse.Display;
import com.lionse.asset.Asset;
import com.lionse.debug.Debugger;
import com.lionse.net.Server;

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

	// 백그라운드 스크린
	public Image background;

	// 로그인 컴포넌트
	public Stage component;
	public TextFieldStyle textFieldStyle;
	public ButtonStyle buttonStyle;

	public TextField ID;
	public TextField PASSWORD;
	public Button LOGIN;
	public Button REGISTER;

	// 입력 처리
	public int backKeyPressedCount = 0;

	// 텍스트 필드 버퍼
	private String id_buffer = "";
	private String password_buffer = "";

	// 생성자
	public LoginUI(Login login) {
		this.login = login;

		// 배경 설정
		background = new Image(Asset.ui.get(Asset.UI.get("LOGIN_BACKGROUND")));

		// 입력과 컴포넌트 초기화
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// 입력 텍스트 스타일 설정
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Asset.font.get(Asset.ClearGothic);
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.disabledFontColor = Color.GRAY;
		textFieldStyle.cursor = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("TEXT_CURSOR")));

		// 버튼 스타일 설정
		buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("LOGIN_UP")));
		buttonStyle.down = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("LOGIN_DOWN")));

		// 컴포넌트 구성 요소 초기화
		ID = new TextField("a", textFieldStyle);
		PASSWORD = new TextField("a", textFieldStyle);
		LOGIN = new Button(buttonStyle);

		// 구성 요소 설정
		ID.setOnscreenKeyboard(new VirtualKeyboardHolder());
		PASSWORD.setOnscreenKeyboard(new VirtualKeyboardHolder());

		LOGIN.setPosition(600, 350);
		ID.setPosition(363, 400);
		PASSWORD.setPosition(363, 350);

		ID.setWidth(200);
		PASSWORD.setWidth(200);

		LOGIN.addListener(this);

		// 컴포넌트에 추가
		component.addActor(background);
		component.addActor(ID);
		component.addActor(PASSWORD);
		component.addActor(LOGIN);
	}

	// 로그인 UI 초기화. 멀티플렉서 설정 등을 함
	@Override
	public void initialize() {
		// 멀티플렉서 설정
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

			Debugger.log(ID.getText() + "/" + PASSWORD.getText());
			Server.login(ID.getText(), PASSWORD.getText());

			// 회원가입 버튼을 눌렀을 때
		} else if (actor == REGISTER) {

		}

	}

	// 뒤로가기 키(Back-Key) 를 처리
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.F1) {

			// 뒤로가기 키를 연속 세 번 누르면 게임 종료
			if (backKeyPressedCount > 3) {
				System.exit(0);
			}

			// 가상 키보드가 있는 상태에서 뒤로가기 키가 눌리면 키보드 끄기
			if (VirtualKeyboard.display) {
				VirtualKeyboard.close();
			} else {
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
		VirtualKeyboard.update(delta);
		VirtualKeyboard.draw(spriteBatch, delta);
	}

	// 가상 키보드의 버튼이 눌렸을 때
	@Override
	public void keyTyped(VirtualKeyButton key) {
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
		}
	}

	// 화면 터치다운시 키보드에 입력
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		VirtualKeyboard.touchDown(screenX * Display.SCALE - (Display.WIDTH * Display.SCALE - Display.WIDTH) / 2, Display.HEIGHT
				- (screenY * Display.SCALE - (Display.HEIGHT * Display.SCALE - Display.HEIGHT) / 2), pointer, button);
		return false;
	}

	// 화면 터치업시 키보드에 입력
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
