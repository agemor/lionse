package lionse.client.stage;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import lionse.client.Display;
import lionse.client.asset.Asset;
import lionse.client.net.Header;
import lionse.client.net.Server;

public class Character implements Renderable {

	// character format constant values
	public static final float RESOLUTION = 1.5f;
	public static final float C_HEAD = 0 * RESOLUTION;
	public static final float C_FACE = 10 * RESOLUTION;
	public static final float C_BODY = 24 * RESOLUTION;
	public static final float C_HAND = 29 * RESOLUTION;
	public static final float C_LEG = 30 * RESOLUTION;
	public static LabelStyle NAME_TAG_STYLE;

	public static float[] SINE_WAVE = { 0, 0.27f, 0.52f, 0.74f, 0.89f, 0.98f, 0.99f, 0.93f, 0.79f, 0.59f, 0.35f, 0.08f, -0.19f, -0.45f, -0.67f, -0.85f, -0.96f,
			-0.99f, -0.95f, -0.84f, -0.66f, -0.43f, -0.17f };

	static {
		NAME_TAG_STYLE = new LabelStyle();
		NAME_TAG_STYLE.font = Asset.Font.get(Asset.NanumGothic);
		NAME_TAG_STYLE.fontColor = Color.BLACK;
	}

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
	public Label nameTag;

	// ��ǳ�� ���
	public TalkBalloon talkBalloon;

	// ��ũ ���߱� ���
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

		this.talkBalloon = new TalkBalloon(this);
		
		this.nameTag = new Label(name, NAME_TAG_STYLE);
		this.path = new ArrayList<Path>();

		cache();
	}

	// cache values. if this method is not called when texture value is
	// changed.noting happens to the graphic
	public void cache() {
		texture_head = Asset.Character.get("HEAD");
		texture_face = Asset.Character.get("FACE");
		texture_body = Asset.Character.get("BODY");
		texture_hand = Asset.Character.get("HAND");
		texture_leg_stop = Asset.Character.get("LEG_STAND");
		texture_leg_step1 = Asset.Character.get("LEG_STEP1");
		texture_leg_step2 = Asset.Character.get("LEG_STEP2");
		texture_leg_step3 = Asset.Character.get("LEG_STEP3");
		texture_leg_step4 = Asset.Character.get("LEG_STEP4");
	}

	public void talk(String message) {
		talkBalloon.show(message);
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

		// draw name tag
		nameTag.draw(spriteBatch, 1);

		// draw talk balloon
		if (talkBalloon.talking)
			talkBalloon.draw(spriteBatch, delta);
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

		// �̵����̶�� ��ǥ�� ������Ʈ�Ѵ�.
		// if (moving) {
		// updatePosition(delta);
		// �̵� ���� �� ��ũ ������ �߻��Ѵ�.

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

				// ���� ��ǥ�� Ÿ�� ��ǥ�� �˻��ؼ� ���� �ȿ� ������ �Ϸ�
				if (path.check(position)) {
					// �Ϸ� ��, ���ÿ��� �����Ͱ� 0�̸� ������ �ʱ�ȭ�Ѵ� (�޸� ���)
					if (this.path.size() - 1 <= pathIndex) {
						this.path.clear();
						pathIndex = 0;
					} else {
						pathIndex++;
					}

					// ���̰� �ʹ� ũ�� �� ��� �ϵ弼���Ѵ�.
				} else if (path.previousDistance > 2000 && path.target != null) {
					// ������ ���� ����
					position.x = path.target.x;
					position.y = path.target.y;
				}
			}

		}

		nameTag.setPosition(position.x - (nameTag.getWidth() / 2) + 27, Display.HEIGHT - position.y + 27);
		
		if (talkBalloon.talking)
			talkBalloon.update(delta);

		// ���� ������ ���� ������ ���� �Ųٱ� ���� ��δ� �������� �̺�Ʈ�� ���� �� �̸� ����� ���´�.

		// ���� �Ųٴ� ���� move �̺�Ʈ�� ���Դٸ� ���ÿ� �״´�.

		// ��� ������ �����̹Ƿ�, ��ǥ üũ�� �����ϴ�! ^^

		// animation effect. (sine wave)
		p_head = C_HEAD + SINE_WAVE[next] * 2f;
		p_face = C_FACE + SINE_WAVE[next] * 2f;
		p_body = C_BODY + SINE_WAVE[next] * 2;
		p_hand = C_HAND + SINE_WAVE[next] * 2;

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
		int xdet = 0; // -1-����, 0-���� ����, 1-������
		int ydet = 0; // -1-�Ʒ���, 0-���� ����, 1-����
		if (point.x - position.x > 0) { // �����ʿ� �ִ�.
			xdet = 1;
		} else if (point.x == position.x) { // ����.
		} else {// ���ʿ� �ִ�.
			xdet = -1;
		}

		if (point.y - position.y > 0) { // ���ʿ� �ִ�.
			ydet = -1;
		} else if (point.y == position.y) { // ����.
		} else {// �Ʒ��ʿ� �ִ�.
			ydet = 1;
		}
		if (xdet > 0) { // ������
			if (ydet > 0) { // ������ ��
				return 4;
			} else if (ydet == 0) { // �׳� ������
				return 5;
			} else { // ������ �Ʒ�
				return 6;
			}
		} else if (xdet == 0) { // y�� ������
			if (ydet > 0) { // ��
				return 3;
			} else if (ydet == 0) { // �� ������ (����Ʈ ��ũ!)
				return -1;
			} else { // �Ʒ�
				return 7;
			}
		} else { // ����
			if (ydet > 0) { // ���� ��
				return 2;
			} else if (ydet == 0) { // �׳� ����
				return 1;
			} else { // ���� �Ʒ�
				return 0;
			}
		}
	}
}
