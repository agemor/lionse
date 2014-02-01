package lionse.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import lionse.client.asset.Asset;
import lionse.client.net.Server;
import lionse.client.stage.World;
import lionse.client.ui.Login;
import lionse.client.ui.Register;

public class Main extends Game {

	public static Main self;

	public World world;
	public Login login;
	public Register register;

	public Main() {

	}

	@Override
	public void create() {

		Gdx.input.setCatchBackKey(true);
		Asset.load();
		Display.initizlize();

		world = new World();
		login = new Login(this);
		register = new Register(this);

		if (!Server.connected) {
			Server.connect("localhost", 7343);
			login.ui.loading = true;
		}
		setScreen(login);

	}
}
