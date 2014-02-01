package lionse.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import lionse.client.Display;
import lionse.client.Main;
import lionse.client.net.Server;
import lionse.client.net.ServerEvent;

/**
 * ȸ���� �Է��� ó���ϴ� ȭ��
 * 
 * @author ������
 * 
 */

public class Register extends ChangeListener implements Screen, ServerEvent {

	// �ý��� ����
	public GL20 gl = Gdx.graphics.getGL20();
	public Main lionse;

	// �׷��� ������ ����
	public SpriteBatch spriteBatch;

	// ȸ���� UI
	public RegisterUI ui;

	public Register(Main lionse) {
		this.lionse = lionse;

		ui = new RegisterUI(this);
		spriteBatch = new SpriteBatch();
	}

	// ȸ���� â�� ȭ�鿡 ������� �� ����
	@Override
	public void show() {
		// �̺�Ʈ ������ ���
		Server.setServerEventListener(this);

		// UI �ʱ�ȭ
		ui.initialize();

	}

	// ȭ�� ������
	@Override
	public void render(float delta) {
		// �����ӹ��� ����
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// ī�޶� ������Ʈ
		spriteBatch.setProjectionMatrix(ui.component.getCamera().combined);
		ui.update(delta);

		// ȭ�鿡 ������
		spriteBatch.begin();
		ui.draw(spriteBatch, delta);
		spriteBatch.end();
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

	}

	@Override
	public void register(boolean succeed) {
		ui.loading = false;
		if (succeed) {
			lionse.setScreen(lionse.login);
			lionse.login.ui.showAlert("   ȸ�� ������ �Ϸ�Ǿ���ϴ�!");
		} else {
			ui.showAlert("���̵� Ȥ�� ĳ���͸��� �ߺ��Դϴ�.");
		}
	}

	@Override
	public void resize(int width, int height) {
		// ȭ�� �ػ󵵿� ���߾� ����
		ui.component.setViewport((float) Math.ceil(Display.SCALE * width), (float) Math.ceil(Display.SCALE * height), false);
	}

	// ******************************************************************
	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void connected(boolean succeed) {

	}

	@Override
	public void login(boolean succeed) {

	}

	@Override
	public void join(boolean succeed) {

	}

}
