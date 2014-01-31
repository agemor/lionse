package lionse.client.stage;

/**
 * ���迡�� ��ġ�� ��Ÿ���� ��������
 * 
 * @author ������
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