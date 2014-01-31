package lionse.client.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import lionse.client.Display;
import lionse.client.asset.Asset;

public class TalkBalloon implements Renderable {

	public static LabelStyle LABEL_STYLE;
	public static Sprite BALLOON;
	public static TextureRegion BALLOON_KNOB;

	static {
		LABEL_STYLE = new LabelStyle();
		LABEL_STYLE.font = Asset.Font.get(Asset.NanumGothic);
		LABEL_STYLE.fontColor = Color.WHITE;

		BALLOON = new Sprite(Asset.UI.get("TALK_BALLOON"));
		BALLOON_KNOB = Asset.UI.get("TALK_BALLOON_KNOB");
	}
	public Character character;

	public String message;
	public float remainingTime;

	public Label label;
	public boolean talking = false;
	public String[] lines = new String[9];
	public int lineCount = 0;

	public float width, height;

	public TalkBalloon(Character character) {
		this.character = character;

		label = new Label("", LABEL_STYLE);

	}

	public void show(String text) {

		if (text.length() >= 9 * 10) {
			message = text.substring(0, 9 * 10 - 1);
		} else {
			message = text;
		}

		// 변수 설정
		talking = true;
		remainingTime = 7;

		// #1 라인으로 나눈다.
		int i = 0;
		int k = 0;

		// 라인 초기화
		for (int j = 0; j < 9; j++) {
			lines[j] = "";
		}

		// len- 11 (10 글자)

		while (true) {
			lines[k] += message.charAt(i);
			i++;

			if (i > 10 * (k + 1)) {
				k++;
			}

			if (message.length() <= i)
				break;
		}
		i = 0;

		// #2 자른 라인에 맞게 라벨에 붙인다.
		StringBuffer buffer = new StringBuffer();
		for (int j = 0; j < 9; j++) {
			if (lines[j].equals(""))
				break;
			i++;
			buffer.append(lines[j] + "\n");
		}

		for (int j = 0; j < 9 - i; j++) {
			buffer.insert(0, '\n');
		}

		lineCount = i;

		label.setText(buffer.toString());

		height = i * 20 + 10;
		width = label.getPrefWidth() + 10;

	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		BALLOON.setPosition(character.position.x - label.getPrefWidth() / 2 + 27 - 5, Display.HEIGHT - character.position.y + 45);
		BALLOON.setSize(width, height);
		spriteBatch.draw(BALLOON_KNOB, character.position.x - 7.5f + 27, Display.HEIGHT - character.position.y + 16);

		BALLOON.draw(spriteBatch);

		label.draw(spriteBatch, 1);
	}

	@Override
	public void update(float delta) {

		remainingTime -= delta;

		if (remainingTime < 0) {
			talking = false;
		}

		label.setPosition(character.position.x - (label.getPrefWidth() / 2) + 27, Display.HEIGHT - character.position.y + 137);
	}

	@Override
	public Point getPosition() {
		return null;
	}

}
