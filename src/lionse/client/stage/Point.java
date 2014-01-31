package lionse.client.stage;

/**
 * 세계에서 위치를 나타내는 데이터형
 * 
 * @author 김현준
 * 
 */
public class Point {

	public float x;
	public float y;
	public float z;

	// constructor
	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point() {
		x = y = z = 0;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return x + "/" + y + "/" + z;
	}
}