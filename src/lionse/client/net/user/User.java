package lionse.client.net.user;

import lionse.client.net.Header;
import lionse.client.net.Server;
import lionse.client.security.crypto.BASE64;
import lionse.client.stage.Path;
import lionse.client.stage.Point;
import lionse.client.stage.Character;

public class User {

	public static final int WAIT = 0;
	public static final int MOVE = 1;

	// user identifier
	public String name; // Deciphered code
	public String code;

	// user values;
	public int level = 0; // higher, the better
	public int experience;
	public int maxHealth;
	public int health;
	public int money;
	public int stage;
	public Point position;
	
	// user equipments
	public int head;
	public int face;
	public int body;
	public int weapon;
	

	// in-game data
	public String message;
	public int direction;
	public float speed;
	public int state; // move, stop, run, jump, attack...

	// character
	public Character character;
	public Path movePath;

	public User(String code) {
		this.code = code;
		this.name = BASE64.decodeString(code);
	}

	public void move(int direction) {
		this.direction = direction;
		Server.send(Header.MOVE + Server.H_L + direction + Server.H_L + speed);
	}
}
