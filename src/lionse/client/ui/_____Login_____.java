package com.lionse.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.lionse.Display;
import com.lionse.Lionse;
import com.lionse.asset.Asset;
import com.lionse.debug.Debugger;
import com.lionse.net.Server;
import com.lionse.net.ServerEvent;

public class _____Login_____ implements Screen, VirtualKeyEvent, ServerEvent {

	/******* System Variables *******/
	public GL20 gl = Gdx.graphics.getGL20();

	/******* Graphic Variables *******/
	public SpriteBatch spriteBatch;
	public Camera camera;

	public Texture backgroundTexture;
	public Image background;

	public Lionse lionse;

	/******* Input Variables ********/
	public InputMultiplexer multiplexer;
	public LoginUI inputHandler;
	public TextFieldStyle textFieldStyle;
	public Stage ui;
	public TextField idInput;
	public TextField pwInput;
	public Button loginButton;

	private String idBuffer = "";
	private String pwBuffer = "";

	public _____Login_____(Lionse lionse) {

		this.lionse = lionse;

		// initialize graphic variables.
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		backgroundTexture = new Texture(Gdx.files.internal("graphics/login.png"));
		background = new Image(backgroundTexture);

		// initialize input variables.
		//inputHandler = new LoginUI(this);

		// initialize ui
		ui = new Stage();
		multiplexer = new InputMultiplexer();

		// initialize text style
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Asset.font.get(Asset.ClearGothic);
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.disabledFontColor = Color.GRAY;
		textFieldStyle.cursor = new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("TEXT_CURSOR")));

		loginButton = new Button(new TextureRegionDrawable(Asset.ui.get(Asset.UI.get("LOGIN_UP"))), new TextureRegionDrawable(Asset.ui.get(Asset.UI
				.get("LOGIN_DOWN"))));

		idInput = new TextField("a", textFieldStyle);
		pwInput = new TextField("a", textFieldStyle);

		loginButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Debugger.log(idInput.getText() + "/" + pwInput.getText());
				Server.login(idInput.getText(), pwInput.getText());
			}
		});

		idInput.setOnscreenKeyboard(new VirtualKeyboardHolder());
		pwInput.setOnscreenKeyboard(new VirtualKeyboardHolder());

		loginButton.setPosition(600, 350);
		idInput.setPosition(363, 400);
		pwInput.setPosition(363, 350);

		idInput.setWidth(200);
		pwInput.setWidth(200);
	}

	@Override
	public void show() {
		// tv
		// VirtualKeyboard.show();
		Server.setServerEventListener(this);

		Gdx.input.setInputProcessor(multiplexer);

		multiplexer.addProcessor(inputHandler);
		multiplexer.addProcessor(ui);

		VirtualKeyboard.setEventListener(this);

		ui.addActor(background);
		ui.addActor(idInput);
		ui.addActor(pwInput);
		ui.addActor(loginButton);

	}

	@Override
	public void login(boolean succeed) {
		if (succeed) {
			Server.join();
			// lionse.setScreen(new Lobby());
		}
	}

	@Override
	public void join(boolean succeed) {
		if (succeed) {
			lionse.setScreen(lionse.world);
		}
	}

	@Override
	public void keyTyped(VirtualKeyButton key) {

		if (ui.getKeyboardFocus() == idInput) {
			idBuffer = VirtualKeyboard.addBuffer(idBuffer, key);
			String text = VirtualKeyboard.mix(idBuffer);
			idInput.setText(text);
			idInput.setCursorPosition(text.length() > 0 ? text.length() : 0);
		} else {
			pwBuffer = VirtualKeyboard.addBuffer(pwBuffer, key);
			String text = VirtualKeyboard.mix(pwBuffer);
			pwInput.setText(text);
			pwInput.setCursorPosition(text.length() > 0 ? text.length() : 0);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		ui.act(delta);
		ui.draw();

		// update camera and set projection
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		// draw virtual keyboard.
		spriteBatch.begin();

		VirtualKeyboard.update(delta);
		VirtualKeyboard.draw(spriteBatch, delta);
		spriteBatch.end();

	}

	@Override
	public void resize(int width, int height) {
		float newWidth;
		float newHeight;
		newWidth = (float) Math.ceil(Display.SCALE * width);
		newHeight = (float) Math.ceil(Display.SCALE * height);
		camera.viewportHeight = newHeight;
		camera.viewportWidth = newWidth;
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		ui.setViewport(newWidth, newHeight, false);

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

	@Override
	public void dispose() {
		ui.dispose();
		spriteBatch.dispose();
	}

	@Override
	public void register(boolean succeed) {
	}

}
