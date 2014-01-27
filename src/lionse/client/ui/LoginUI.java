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
 * �α��� ȭ�鿡�� UI ó�� Ŭ����
 * 
 * @author ������
 * 
 */
public class LoginUI extends ChangeListener implements UI, VirtualKeyEvent {

	// ���� Ŭ���� ����
	public Login login;

	// �Է� ���� ����
	public InputMultiplexer multiplexer;

	// ��׶��� ��ũ��
	public Image background;

	// �α��� ������Ʈ
	public Stage component;
	public TextFieldStyle textFieldStyle;
	public ButtonStyle buttonStyle;

	public TextField ID;
	public TextField PASSWORD;
	public Button LOGIN;
	public Button REGISTER;

	// �Է� ó��
	public int backKeyPressedCount = 0;

	// �ؽ�Ʈ �ʵ� ����
	private String id_buffer = "";
	private String password_buffer = "";

	// ������
	public LoginUI(Login login) {
		this.login = login;

		// ��� ����
		background = new Image(Asset.ui.get(Asset.UI.get("LOGIN_BACKGROUND")));

		// �Է°� ������Ʈ �ʱ�ȭ
		multiplexer = new InputMultiplexer();
		component = new Stage();

		// �Է� �ؽ�Ʈ ��Ÿ�� ����
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Asset.font.get(Asset.ClearGothic);
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.disabledFontColor = Color.GRAY;
		textFieldStyle.cursor = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("TEXT_CURSOR")));

		// ��ư ��Ÿ�� ����
		buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("LOGIN_UP")));
		buttonStyle.down = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("LOGIN_DOWN")));

		// ������Ʈ ���� ��� �ʱ�ȭ
		ID = new TextField("a", textFieldStyle);
		PASSWORD = new TextField("a", textFieldStyle);
		LOGIN = new Button(buttonStyle);

		// ���� ��� ����
		ID.setOnscreenKeyboard(new VirtualKeyboardHolder());
		PASSWORD.setOnscreenKeyboard(new VirtualKeyboardHolder());

		LOGIN.setPosition(600, 350);
		ID.setPosition(363, 400);
		PASSWORD.setPosition(363, 350);

		ID.setWidth(200);
		PASSWORD.setWidth(200);

		LOGIN.addListener(this);

		// ������Ʈ�� �߰�
		component.addActor(background);
		component.addActor(ID);
		component.addActor(PASSWORD);
		component.addActor(LOGIN);
	}

	// �α��� UI �ʱ�ȭ. ��Ƽ�÷��� ���� ���� ��
	@Override
	public void initialize() {
		// ��Ƽ�÷��� ����
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

			Debugger.log(ID.getText() + "/" + PASSWORD.getText());
			Server.login(ID.getText(), PASSWORD.getText());

			// ȸ������ ��ư�� ������ ��
		} else if (actor == REGISTER) {

		}

	}

	// �ڷΰ��� Ű(Back-Key) �� ó��
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
		VirtualKeyboard.update(delta);
		VirtualKeyboard.draw(spriteBatch, delta);
	}

	// ���� Ű������ ��ư�� ������ ��
	@Override
	public void keyTyped(VirtualKeyButton key) {
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
		}
	}

	// ȭ�� ��ġ�ٿ�� Ű���忡 �Է�
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		VirtualKeyboard.touchDown(screenX * Display.SCALE - (Display.WIDTH * Display.SCALE - Display.WIDTH) / 2, Display.HEIGHT
				- (screenY * Display.SCALE - (Display.HEIGHT * Display.SCALE - Display.HEIGHT) / 2), pointer, button);
		return false;
	}

	// ȭ�� ��ġ���� Ű���忡 �Է�
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
