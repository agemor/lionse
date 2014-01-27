package com.lionse;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lionse.asset.Asset;
import com.lionse.net.Server;
import com.lionse.stage.World;
import com.lionse.ui.Login;

public class Lionse extends Game {

	public World world;
	public Login login;

	public Lionse() {

	}

	@Override
	public void create() {

		Gdx.input.setCatchBackKey(true);
		Asset.load();

		world = new World();
		login = new Login(this);

		setScreen(login);

		Server.connect("182.221.65.170", 7343);
	}

}
