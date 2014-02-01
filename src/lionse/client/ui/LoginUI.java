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
 * �α��� ȭ�鿡�� UI ó�� Ŭ����
 * 
 * @author ������
 * 
 */
public class LoginUI extends ChangeListener implements UI, VirtualKeyEvent {

	// ���� Ŭ���� ����
	public Login login;

	// �Է� ��� ����
	public InputMultiplexer multiplexer;

	// UI �ؽ�ó
	public Image background;
	public ButtonStyle loginButtonStyle;
	public ButtonStyle registerButtonStyle;

	// �α��� ������Ʈ
	public Stage component;

	public TextField ID;
	public TextField PASSWORD;
	public Button LOGIN;
	public Button REGISTER;

	// �Է� ó��
	public float stateTime = 0;
	public int backKeyPressedCount = 0;
	public boolean loading = false;
	public boolean alert = false;
	public String alertMessage = "�׽�Ʈ �޽���";

	// �ؽ�Ʈ �ʵ� ����
	private String id_buffer = "";
	private String password_buffer = "";

	// ����
	public LoginUI(Login login) {
		this.login = login;

		// UI ���� �μ� �ʱ�ȭ
		background = new Image(Asset.UI.get("LOGIN"));

		// ��ư ��Ÿ�� ����
		loginButtonStyle = new ButtonStyle();
		loginButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("LOGIN_BUTTON_UP"));
		loginButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("LOGIN_BUTTON_DOWN"));

		registerButtonStyle = new ButtonStyle();
		registerButtonStyle.up = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_UP"));
		registerButtonStyle.down = new TextureRegionDrawable(Asset.UI.get("REGISTER_BUTTON_DOWN"));

		// �Է°� ������Ʈ �ʱ�ȭ
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// ������Ʈ ���� ��� �ʱ�ȭ
		ID = new TextField("", Display.TEXTFIELD_STYLE);
		PASSWORD = new TextField("", Display.TEXTFIELD_STYLE);
		LOGIN = new Button(loginButtonStyle);
		REGISTER = new Button(registerButtonStyle);

		// ���� ��� ����
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

		// ������Ʈ�� �߰�
		component.addActor(background);
		component.addActor(ID);
		component.addActor(PASSWORD);
		component.addActor(LOGIN);
		component.addActor(REGISTER);
	}

	// �α��� UI �ʱ�ȭ. ��Ƽ�÷��� ���� ���� ��
	@Override
	public void initialize() {
		// ��Ƽ�÷��� ����
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);

		// ���� Ű���� ����
		VirtualKeyboard.setEventListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(component);
	}

	// ��ư�� ������ �� �����
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		// �α��� ��ư�� ������ ��
		if (actor == LOGIN) {
			loading = true;
			Debugger.log(ID.getText() + "/" + PASSWORD.getText());
			Server.login(ID.getText().trim(), PASSWORD.getText().trim());

			// ȸ���� ��ư�� ������ ��
		} else if (actor == REGISTER) {
			login.lionse.setScreen(login.lionse.register);
		}

	}

	// ��� Ȥ�� ���� �޽��� ����
	public void showAlert(String message) {
		alert = true;
		alertMessage = message;
	}

	// �ڷΰ��� Ű(Back-Key) �� ó��
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
				backKeyPressedCount = 0;
			} else {
				showAlert("    �� �� �� ������ �����մϴ�.");
				backKeyPressedCount++;
			}
		}
		return false;
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

	// ���� Ű������ ��ư�� ������ ��
	@Override
	public void keyTyped(VirtualKeyButton key) {

		if (loading || alert)
			return;

		// ���̵� �ʵ忡 ��Ŀ���� ���� ���
		if (component.getKeyboardFocus() == ID) {
			id_buffer = VirtualKeyboard.addBuffer(id_buffer, key);
			String text = VirtualKeyboard.mix(id_buffer);
			ID.setText(text);
			ID.setCursorPosition(text.length() > 0 ? text.length() : 0);

			// �н����� �ʵ忡 ��Ŀ���� ���� ���
		} else {

			password_buffer = VirtualKeyboard.addBuffer(password_buffer, key);
			String text = VirtualKeyboard.mix(password_buffer);
			PASSWORD.setText(text);
			PASSWORD.setCursorPosition(text.length() > 0 ? text.length() : 0);

			// �н����� �ʵ� �Է� �� ���Ͱ� ������ �α��� ó��
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

	// ȭ�� ��ġ�ٿ�� Ű���忡 �Է�
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

	// ȭ�� ��ġ���� Ű���忡 �Է�
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (loading || alert || !VirtualKeyboard.display)
			return false;

		VirtualKeyboard.touchUp(screenX, screenY, pointer, button);
		return false;
	}

	// *********************** ������ �ʴ� �޼��� ***********************

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
