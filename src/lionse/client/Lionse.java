package lionse.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import lionse.client.asset.Asset;
import lionse.client.net.Server;
import lionse.client.stage.World;
import lionse.client.ui.Login;

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
