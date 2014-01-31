package lionse.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import lionse.client.Display;
import lionse.client.Lionse;
import lionse.client.net.Server;
import lionse.client.net.ServerEvent;

/**
 * 회원가입 입력을 처리하는 화면
 * 
 * @author 김현준
 * 
 */

public class Register extends ChangeListener implements Screen, ServerEvent {

	// 시스템 변수
	public GL20 gl = Gdx.graphics.getGL20();
	public Lionse lionse;

	// 그래픽 랜더러 변수
	public SpriteBatch spriteBatch;

	// 회원가입 UI
	public RegisterUI ui;

	public Register(Lionse lionse) {
		this.lionse = lionse;

		ui = new RegisterUI(this);
		spriteBatch = new SpriteBatch();
	}

	// 회원가입 창이 화면에 띄워졌을 때 실행
	@Override
	public void show() {
		// 이벤트 리스너 등록
		Server.setServerEventListener(this);

		// UI 초기화
		ui.initialize();

	}

	// 화면 랜더링
	@Override
	public void render(float delta) {
		// 프레임버퍼 비우기
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 카메라 업데이트
		spriteBatch.setProjectionMatrix(ui.component.getCamera().combined);
		ui.update(delta);

		// 화면에 랜더링
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
			lionse.login.ui.showAlert("   회원 가입이 완료되었습니다!");
		} else {
			ui.showAlert("아이디 혹은 캐릭터명이 중복입니다.");
		}
	}

	@Override
	public void resize(int width, int height) {
		// 화면 해상도에 맞추어 정렬
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
