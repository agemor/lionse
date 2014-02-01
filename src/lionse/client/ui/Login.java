package lionse.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lionse.client.Display;
import lionse.client.Main;
import lionse.client.net.Server;
import lionse.client.net.ServerEvent;

/**
 * �α����� ó���ϴ� ȭ��
 * 
 * @author ������
 * 
 */
public class Login implements Screen, ServerEvent {

	// �ý��� ����
	public GL20 gl = Gdx.graphics.getGL20();
	public Main lionse;

	// �׷��� ������ ����
	public SpriteBatch spriteBatch;

	// �α��� UI
	public LoginUI ui;

	public Login(Main lionse) {
		this.lionse = lionse;

		ui = new LoginUI(this);
		spriteBatch = new SpriteBatch();
	}

	// �α��� â�� ȭ�鿡 ������� �� ����
	@Override
	public void show() {

		// �̺�Ʈ ������ ���
		Server.setServerEventListener(this);

		// UI �ʱ�ȭ
		ui.initialize();

	}

	// �α��� UI�� ������ ����
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
	public void connected(boolean succeed) {
		ui.loading = false;
		if (succeed) {
		} else {
		}
	}

	@Override
	public void login(boolean succeed) {
		ui.loading = false;
		if (succeed) {
			Server.join();
		} else {
			ui.showAlert("       �α��ο� �����Ͽ����ϴ�.");
		}
	}

	@Override
	public void join(boolean succeed) {
		if (succeed) {
			ui.dispose();
			lionse.setScreen(lionse.world);
		}
	}

	@Override
	public void register(boolean succeed) {

	}

	@Override
	public void resize(int width, int height) {
		// ȭ�� �ػ󵵿� ���߾� ����
		ui.component.setViewport((float) Math.ceil(Display.SCALE * width), (float) Math.ceil(Display.SCALE * height), false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

}
