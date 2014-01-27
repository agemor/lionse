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

	// 시스템 변수
	public GL20 gl = Gdx.graphics.getGL20();
	public Lionse lionse;

	// 그래픽 랜더러 변수
	public SpriteBatch spriteBatch;
	public Camera camera;

	// 로그인 UI
	public UI ui;

	public Login(Lionse lionse) {
		this.lionse = lionse;

		ui = new LoginUI(this);
		camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
	}

	// 로그인 창이 화면에 띄워졌을 때 실행
	@Override
	public void show() {

		// 이벤트 리스너 등록
		Server.setServerEventListener(this);

		// UI 초기화
		ui.initialize();
	}

	// 로그인 UI의 랜더링 실행
	@Override
	public void render(float delta) {
		// 프레임버퍼 비우기
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 카메라 업데이트
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		ui.update(delta);

		// 화면에 랜더링
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
		// 화면 해상도에 맞추어 정렬
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
