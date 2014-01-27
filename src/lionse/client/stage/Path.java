package com.lionse.stage;

import com.lionse.stage.Stage.Point;

/**
 * 
 * 캐릭터의 이동 궤적을 나타냄. 이 정보들은 캐릭터의 스택에 쌓임
 * 
 * @author 김현준
 * 
 */
public class Path {

	// 벡터량임, 방향과 크기를 가짐
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
