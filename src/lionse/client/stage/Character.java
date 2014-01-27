package com.lionse.stage;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lionse.Display;
import com.lionse.asset.Asset;
import com.lionse.debug.Debugger;
import com.lionse.net.Header;
import com.lionse.net.Server;
import com.lionse.stage.Stage.Point;

public class Character implements Renderable {

	// character format constant values
	public static final float RESOLUTION = 1.5f;
	public static final float C_HEAD = 0 * RESOLUTION;
	public static final float C_FACE = 10 * RESOLUTION;
	public static final float C_BODY = 24 * RESOLUTION;
	public static final float C_HAND = 29 * RESOLUTION;
	public static final float C_LEG = 30 * RESOLUTION;

	public static float[] SINE_WAVE = { 0, 0.27f, 0.52f, 0.74f, 0.89f, 0.98f, 0.99f, 0.93f, 0.79f, 0.59f, 0.35f, 0.08f, -0.19f, -0.45f, -0.67f, -0.85f, -0.96f,
			-0.99f, -0.95f, -0.84f, -0.66f, -0.43f, -0.17f };

	public boolean me = false;

	// character property
	public String name;
	public int head;
	public int face;
	public int body;
	public int clothes;
	public int weapon;

	public float speed = 200f;

	// positioning property
	public Point position;
	public Point target;
	public Point bottom;

	// character graphics
	public List<Path> path;
	public int pathIndex = 0;
	public int direction = 0;
	public boolean moving = false;
	private int step = 0;

	// graphics variables
	private float stepTime = 0;
	private int next = -1;

	// graphics cache
	private TextureRegion[] texture_head;
	private TextureRegion[] texture_face;
	private TextureRegion[] texture_body;
	private TextureRegion[] texture_hand;
	private TextureRegion[] texture_leg_stop;
	private TextureRegion[] texture_leg_step1;
	private TextureRegion[] texture_leg_step2;
	private TextureRegion[] texture_leg_step3;
	private TextureRegion[] texture_leg_step4;

	// animated character format (sine-wave)
	private float p_head;
	private float p_face;
	private float p_body;
	private float p_hand;

	// name, head, face, body, clothes, weapon
	public Character(String name, int head, int face, int body, int weapon) {
		this.name = name;
		this.head = head;
		this.face = face;
		this.body = body;
		this.weapon = weapon;

		this.position = new Point();
		this.bottom = new Point();

		this.path = new ArrayList<Path>();

		cache();
	}

	// cache values. if this method is not called when texture value is
	// changed.noting happens to the graphic
	public void cache() {
		texture_head = Asset.character.get(Asset.Character.get("HEAD"));
		texture_face = Asset.character.get(Asset.Character.get("FACE"));
		texture_body = Asset.character.get(Asset.Character.get("BODY"));
		texture_hand = Asset.character.get(Asset.Character.get("HAND"));
		texture_leg_stop = Asset.character.get(Asset.Character.get("LEG"));
		texture_leg_step1 = Asset.character.get(Asset.Character.get("LEG_STEP1"));
		texture_leg_step2 = Asset.character.get(Asset.Character.get("LEG_STEP2"));
		texture_leg_step3 = Asset.character.get(Asset.Character.get("LEG_STEP3"));
		texture_leg_step4 = Asset.character.get(Asset.Character.get("LEG_STEP4"));
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		// Debugger.log(texture_head.length);
		// draw leg
		if (moving) {
			switch (step) {
			case 0:
				spriteBatch.draw(texture_leg_step1[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			case 1:
				spriteBatch.draw(texture_leg_step2[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			case 2:
				spriteBatch.draw(texture_leg_stop[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			case 3:
				spriteBatch.draw(texture_leg_step3[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			case 4:
				spriteBatch.draw(texture_leg_step4[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			case 5:
				spriteBatch.draw(texture_leg_step3[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
				break;
			}
		} else {
			spriteBatch.draw(texture_leg_stop[direction], position.x, Display.HEIGHT - (position.y + C_LEG));
		}

		// draw body
		spriteBatch.draw(texture_body[direction], position.x, Display.HEIGHT - (position.y + p_body));

		// draw hands
		spriteBatch.draw(texture_hand[direction], position.x, Display.HEIGHT - (position.y + p_hand));

		// draw face
		spriteBatch.draw(texture_face[direction], position.x, Display.HEIGHT - (position.y + p_face));

		// draw head
		spriteBatch.draw(texture_head[direction], position.x, Display.HEIGHT - (position.y + p_head));
	}

	@Override
	public void update(float delta) {
		// update time values.
		stepTime += delta;
		next += 1;

		if (next >= SINE_WAVE.length)
			next = 0;

		// increase step if moving.
		if (stepTime > 0.05f && moving) {
			step += 1;
			stepTime = 0;
			if (step > 5)
				step = 0;
		}

		// 이동중이라면 좌표를 업데이트한다.
		// if (moving) {
		// updatePosition(delta);
		// 이동 중일 때 싱크 스택이 발생한다.

		if (me && moving) {
			updatePosition(delta);

		} else if (this.path.size() > 0 && !me && this.path.size() > pathIndex) {

			Path path = this.path.get(pathIndex);

			if (path.disabled) {
				pathIndex++;
			} else {
				this.direction = path.direction;
				if (path.target == null) {
					updatePosition(delta);
				} else {
					// moveTo(path, delta);
					updatePosition(delta);
				}

				// 현재 좌표와 타겟 좌표를 검사해서 범위 안에 들어오면 완료
				if (path.check(position)) {
					// 포지션 강제 세팅
					// position.x = path.target.x;
					// position.y = path.target.y;
					// 완료 후, 스택에서 포인터가 0이면 스택을 초기화한다 (메모리 관리상)
					if (this.path.size() - 1 <= pathIndex) {
						this.path.clear();
						pathIndex = 0;
					} else {
						pathIndex++;
					}
				}
			}

		}

		// 정지 지점과 현재 지점의 갭을 매꾸기 위한 경로는 서버에서 이벤트를 받을 때 미리 계산해 놓는다.

		// 갭을 매꾸는 도중 move 이벤트가 들어왔다면 스택에 쌓는다.

		// 모든 동작은 선형이므로, 좌표 체크가 간편하다! ^^

		// animation effect. (sine wave)
		p_head = C_HEAD + SINE_WAVE[next] * 2f;
		p_face = C_FACE + SINE_WAVE[next] * 2f;
		p_body = C_BODY + SINE_WAVE[next] * 2;
		p_hand = C_HAND + SINE_WAVE[next] * 2;

	}

	private void moveTo(Path path, float delta) {
		position.x += path.velocityX * speed * delta;
		position.y += path.velocityY * speed * delta;
	}

	private void updatePosition(float delta) {
		switch (direction) {
		case 0:
			position.x -= speed * delta;
			position.y += speed * delta / 2;
			break;
		case 1:
			position.x -= speed * delta;
			break;
		case 2:
			position.x -= speed * delta;
			position.y -= speed * delta / 2;
			break;
		case 3:
			position.y -= speed * delta / 2;
			break;
		case 4:
			position.x += speed * delta;
			position.y -= speed * delta / 2;
			break;
		case 5:
			position.x += speed * delta;
			break;
		case 6:
			position.x += speed * delta;
			position.y += speed * delta / 2;
			break;
		case 7:
			position.y += speed * delta / 2;
			break;
		}
	}

	@Override
	public Point getPosition() {
		bottom.x = position.x;
		bottom.y = position.y + 150;
		bottom.z = position.z;
		return bottom;
	}

	public void move(int targetDirection) {
		this.direction = targetDirection;
		Server.send(Header.MOVE + Server.H_L + direction + Server.H_L + Math.round(speed) + Server.H_L + Math.round(position.x) + Server.H_L
				+ Math.round(position.y) + Server.H_L + Math.round(position.z));
	}

	public void stop() {
		Server.send(Header.STOP + Server.H_L + Math.round(position.x) + Server.H_L + Math.round(position.y) + Server.H_L + Math.round(position.z));
	}

	public int getDirection(Point point) {
		int xdet = 0; // -1-왼쪽, 0-변위 없음, 1-오른쪽
		int ydet = 0; // -1-아래쪽, 0-변위 없음, 1-위쪽
		if (point.x - position.x > 0) { // 오른쪽에 있다.
			xdet = 1;
		} else if (point.x == position.x) { // 같다.
		} else {// 왼쪽에 있다.
			xdet = -1;
		}

		if (point.y - position.y > 0) { // 위쪽에 있다.
			ydet = -1;
		} else if (point.y == position.y) { // 같다.
		} else {// 아래쪽에 있다.
			ydet = 1;
		}
		if (xdet > 0) { // 오른쪽
			if (ydet > 0) { // 오른쪽 위
				return 4;
			} else if (ydet == 0) { // 그냥 오른쪽
				return 5;
			} else { // 오른쪽 아래
				return 6;
			}
		} else if (xdet == 0) { // y만 움직임
			if (ydet > 0) { // 위
				return 3;
			} else if (ydet == 0) { // 안 움직임 (퍼펙트 싱크!)
				return -1;
			} else { // 아래
				return 7;
			}
		} else { // 왼쪽
			if (ydet > 0) { // 왼쪽 위
				return 2;
			} else if (ydet == 0) { // 그냥 왼쪽
				return 1;
			} else { // 왼쪽 아래
				return 0;
			}
		}
	}
}
