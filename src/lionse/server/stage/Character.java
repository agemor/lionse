package lionse.server.stage;

import lionse.server.net.user.User;

public class Character {

	// name
	public String name;

	// level and experience
	public int level;
	public int experience;

	// health point
	public int maxHealth;
	public int health;

	// money
	public int money;

	// equipments
	public Equipment equipment;

	// location
	public Location location;

	// ____ not used ____
	public String creature;
	public String item;
	public String quest;

	public Character() {
		equipment = new Equipment();
	}

	public String getPacket() {
		String packet = name + User.H_L + level + User.H_L + experience + User.H_L + maxHealth
				+ User.H_L + health + User.H_L + money + User.H_L + equipment.hat + User.H_L
				+ equipment.clothes + User.H_L + equipment.weapon + User.H_L + location.stage
				+ User.H_L + location.x + User.H_L + location.y + User.H_L + location.z;
		return packet;
	}

	public static class Equipment {

		public int hat;
		public int clothes;
		public int weapon;

		public Equipment() {

		}
	}

}
