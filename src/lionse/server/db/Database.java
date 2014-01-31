package lionse.server.db;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lionse.server.net.Server;
import lionse.server.stage.Character;
import lionse.server.stage.Location;

public class Database {

	public static Connection connection;
	public static Statement statement;
	public static String location = "jdbc:mysql://localhost:3306/lionse";
	public static String id = "root";
	public static String password = "guswns96!";

	public static String query_user;
	public static String query_character;
	public static String query_item;
	public static String query_statics;

	private static boolean connected = false;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find Driver.");
		}
	}

	public static void connect() {
		try {
			connection = DriverManager.getConnection(location, id, password);
			statement = connection.createStatement();

			connected = true;
		} catch (SQLException e) {
			System.err.println("Cannot connect to server.");
		}
	}

	public static void setup() {
		if (!connected)
			return;
		try {
			char[] query_user_buffer = new char[1000];
			char[] query_character_buffer = new char[1000];
			FileReader query_user_file = new FileReader("./sql/user.sql");
			FileReader query_character_file = new FileReader(
					"./sql/character.sql");
			query_user_file.read(query_user_buffer);
			query_character_file.read(query_character_buffer);

			query_user = String.valueOf(query_user_buffer);
			query_character = String.valueOf(query_character_buffer);

			statement.executeUpdate(query_user);
			statement.executeUpdate(query_character);

			Server.trace("DATABASE SETUP COMPLETE.");

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

	}

	public static boolean login(String id, String password) {
		if (!connected)
			return false;
		String query = "SELECT * FROM `lionse`.`user` WHERE `id` = '" + id
				+ "'";
		try {
			ResultSet result = statement.executeQuery(query);

			while (result.next()) {
				if (result.getString("password").equals(password)) {
					String update = "UPDATE `lionse`.`user` SET `lastlogin`= CURRENT_TIMESTAMP WHERE `id` = '"
							+ id + "';";
					statement.executeUpdate(update);
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean newUser(String id, String password, String character,
			String email) {
		if (!connected)
			return false;
		String query0 = "SELECT * FROM `lionse`.`user` WHERE `id` = '" + id
				+ "' OR `character` = '" + character + "';";

		String query1 = "INSERT INTO `lionse`.`user` (`no`, `id`, `password`, `character`, `email`, `level`, `banned`, `date`, `lastlogin`) VALUES (NULL, '"
				+ id
				+ "', '"
				+ password
				+ "', '"
				+ character
				+ "', '"
				+ email
				+ "', '0', '0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

		String query2 = "INSERT INTO `lionse`.`character` (`name`, `level`, `maxhealth`, `health`, `money`, `hat`, `clothes`, `weapon`, `creature`, `item`, `quest`, `exp`, `location`) VALUES ('"
				+ character
				+ "', '1', '100', '100', '0', '1', '1', '1', '0', '0', '0', '0', '1:1:1:1');";

		try {
			ResultSet resultSet = statement.executeQuery(query0);
			int count = 0;
			while (resultSet.next()) {
				count++;
			}
			if (count > 0) {
				return false;
			}
			statement.execute(query1);
			statement.execute(query2);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static UserInfo getUserInfo(String id) {
		if (!connected)
			return null;
		String query = "SELECT * FROM `lionse`.`user` WHERE `id` = '" + id
				+ "'";

		try {
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				UserInfo userInfo = new UserInfo();
				userInfo.banned = result.getInt("banned") == 1 ? true : false;
				userInfo.character = result.getString("character");
				userInfo.lastlogin = result.getString("lastlogin");
				userInfo.level = result.getInt("level");

				return userInfo;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Character getCharacter(String name) {
		if (!connected)
			return null;
		String query = "SELECT * FROM `lionse`.`character` WHERE `name` = '"
				+ name + "'";
		Character character = new Character();
		try {
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				character.name = result.getString("name");
				character.level = result.getInt("level");
				character.maxHealth = result.getInt("maxhealth");
				character.health = result.getInt("health");
				character.money = result.getInt("money");
				character.equipment.hat = result.getInt("hat");
				character.equipment.clothes = result.getInt("clothes");
				character.equipment.weapon = result.getInt("weapon");
				character.location = new Location(result.getString("location"));
				return character;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void updateCharacter(Character character) {
		if (!connected)
			return;
		String query = "UPDATE `lionse`.`character` SET `level`='"
				+ character.level + "', `maxhealth` = '" + character.maxHealth
				+ "', `money` = '" + character.money + "', `hat` = '"
				+ character.equipment.hat + "', `clothes` = '"
				+ character.equipment.clothes + "', `weapon` = '"
				+ character.equipment.weapon + "', `exp` = '"
				+ character.experience + "', `location` = '"
				+ character.location.getPacket() + "'  WHERE `name` = '"
				+ character.name + "';";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void newCharacter() {
		if (!connected)
			return;
	}

	public static class UserInfo {

		public boolean banned = false;
		public int level = 0;
		public String lastlogin;
		public String character;

		public UserInfo() {

		}
	}
}
