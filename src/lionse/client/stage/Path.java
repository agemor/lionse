package com.lionse.stage;

import com.lionse.stage.Stage.Point;

/**
 * 
 * ĳ������ �̵� ������ ��Ÿ��. �� �������� ĳ������ ���ÿ� ����
 * 
 * @author ������
 * 
 */
public class Path {

	// ���ͷ���, ����� ũ�⸦ ����
	public int direction;
	public Point target;
	public boolean disabled = false;

	public float velocityX;
	public float velocityY;

	private float previousDistance = 100000000;

	public Path(int direction, Point target) {
		this.direction = direction;
		this.target = target;
	}

	public boolean check(Point position) {
		if (target == null)
			return false;

		float distance = (float) Math.sqrt(Math.pow(position.x - target.x, 2) + Math.pow(position.y - target.y, 2));

		if (distance < 4 || distance > previousDistance) {
			return true;
		}

		previousDistance = distance;
		return false;

	}

	public void disable() {
		disabled = true;
	}
}
