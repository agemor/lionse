package lionse.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lionse.client.Display;
import lionse.client.Lionse;
import lionse.client.net.Server;
import lionse.client.net.ServerEvent;

public class Login implements Screen, ServerEvent {

	// �ý��� ����
	public GL20 gl = Gdx.graphics.getGL20();
	public Lionse lionse;

	// �׷��� ������ ����
	public SpriteBatch spriteBatch;
	public Camera camera;

	// �α��� UI
	public UI ui;

	public Login(Lionse lionse) {
		this.lionse = lionse;

		ui = new LoginUI(this);
		camera = new OrthographicCamera();
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
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		ui.update(delta);

		// ȭ�鿡 ������
		spriteBatch.begin();
		ui.draw(spriteBatch, delta);
		spriteBatch.end();
	}

	@Override
	public void login(boolean succeed) {
		if (succeed) {
			Server.join();
		}
	}

	@Override
	public void join(boolean succeed) {
		if (succeed) {
			lionse.setScreen(lionse.world);
		}
	}

	@Override
	public void register(boolean succeed) {

	}

	@Override
	public void resize(int width, int height) {
		// ȭ�� �ػ󵵿� ���߾� ����
		camera.viewportWidth = (float) Math.ceil(Display.SCALE * width);
		camera.viewportHeight = (float) Math.ceil(Display.SCALE * height);

		camera.position.set(Display.WIDTH / 2, Display.HEIGHT / 2, 0);
		((LoginUI) ui).component.setViewport(camera.viewportWidth, camera.viewportHeight, false);
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
