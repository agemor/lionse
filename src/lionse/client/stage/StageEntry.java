package lionse.client.stage;

public class StageEntry {

	// terrain entry
	public int[][] terrainType;
	public int[][] terrainAltitude;

	// unit entry
	public int[] unit;

	public StageEntry() {

	}

	public StageEntry(int size) {
		generateRandom(size);
	}

	public void loadFromFile(String fileName) {
	}

	public int getSize() {
		return terrainType.length;
	}

	public void generateRandom(int size) {

		terrainType = new int[size][size];
		terrainAltitude = new int[size][size];
		unit = new int[0];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				terrainType[i][j] = 0;
				terrainAltitude[i][j] = 0;
			}
		}
	}
}
