package lionse.client.stage;

import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lionse.client.Display;
import lionse.client.asset.Asset;
import lionse.client.net.Server;
import lionse.client.net.UserEvent;
import lionse.client.net.user.User;
import lionse.client.stage.unit.Natural;
import lionse.client.ui.WorldUI;

public class World implements Screen, UserEvent {

	// �ý��� ����
	public GL20 gl = Gdx.graphics.getGL20();
	public boolean initialized = false;

	// �׷��� ����
	public SpriteBatch spriteBatch;
	public SpriteBatch notificationRenderer;

	// ��������
	public Stage stage;

	// UI ó��
	public WorldUI ui;

	// ������
	public World() {

		// �׷��� ���� �ʱ�ȭ.
		spriteBatch = new SpriteBatch();
		notificationRenderer = new SpriteBatch();

		// �������� �ʱ�ȭ
		stage = new Stage();
		stage.load(new StageEntry(20));

		// UI �ʱ�ȭ
		ui = new WorldUI(this);
	}

	@Override
	public void show() {

		// ���� ������ ����
		Server.setUserEventListener(this);

		// UI �غ�
		ui.initialize();

		// ���������� ���� �߰� (�׽�Ʈ��)
		for (int i = 0; i < 40; i++) {
			Unit unit = new Natural(Asset.Game.get("TREE"));
			unit.position.x = (float) (Math.random() * 700) + 600;
			unit.position.y = (float) (Math.random() * 600);
			stage.units.add(unit);
			stage.renderables.add(unit);
		}

		// �ʱ�ȭ �Ϸ�. ������ ����
		initialized = true;

	}

	@Override
	public void render(float delta) {

		if (!initialized || !Server.loaded)
			return;
		// �����ӹ��� �ʱ�ȭ
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// �������� ������
		spriteBatch.begin();
		stage.update(delta);
		stage.draw(spriteBatch, delta);
		spriteBatch.end();

		// ������Ʈ�� ���� UI������
		ui.update(delta);

		// �ֻ��� UI������
		notificationRenderer.setProjectionMatrix(ui.component.getCamera().combined);
		notificationRenderer.begin();
		ui.draw(notificationRenderer, delta);
		notificationRenderer.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void resize(int width, int height) {
		// ȭ�� �ػ󵵿� ���߾� ����
		stage.camera.viewportWidth = (float) Math.ceil(Display.SCALE * width);
		stage.camera.viewportHeight = (float) Math.ceil(Display.SCALE * height);

		stage.camera.position.set(Display.WIDTH / 2, Display.HEIGHT / 2, 0);
		((WorldUI) ui).component.setViewport(stage.camera.viewportWidth, stage.camera.viewportHeight, false);
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {

	}

	@Override
	public void arrive(User user) {
		user.character.position.x = user.position.x;
		user.character.position.y = user.position.y;
		user.character.position.z = user.position.z;
		stage.characters.add(user.character);
		stage.renderables.add(user.character);

	}

	@Override
	public void leave(User user) {
		stage.characters.remove(user.character);
		stage.renderables.remove(user.character);
	}

	@Override
	public void load(User me) {
		stage.me = me.character;
		stage.characters.add(stage.me);
		stage.renderables.add(stage.me);
	}

	@Override
	public void userList(Map<String, User> users) {
		Iterator<String> iterator = users.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			User user = users.get(key);

			if (user == Server.me)
				continue;

			user.character.position.x = user.position.x;
			user.character.position.y = user.position.y;
			user.character.position.z = user.position.z;
			stage.characters.add(user.character);
			stage.renderables.add(user.character);
		}
	}

	@Override
	public void chat(User user) {
		ui.CHAT_BOX.addMessage(user.name + ":" + user.message);
		user.character.talk(user.name + ":" + user.message);
	}

	@Override
	public void move(User user) {
		user.character.moving = true;
	}

	@Override
	public void stop(User user) {
		user.character.moving = false;
	}

}
