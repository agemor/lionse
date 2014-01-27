package lionse.server.stage;

public class Location {

	public int stage;
	public int x;
	public int y;
	public int z;

	public Location() {

	}

	public Location(String locationData) {
		String[] chunk = locationData.split(":");
		stage = Integer.parseInt(chunk[0]);
		x = Integer.parseInt(chunk[1]);
		y = Integer.parseInt(chunk[2]);
		z = Integer.parseInt(chunk[3]);
	}

	public String getPacket() {
		return stage + ":" + x + ":" + y + ":" + z;
	}
}
