package lionse.client.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.Display;
import lionse.client.asset.Asset;
import lionse.client.stage.Point;

/**
 * 
 * 채팅 담당 UI객체
 * 
 * @author 김현준
 * 
 */
public class ChatBox extends WidgetGroup implements EventListener {

	public TextureRegion CHAT_BOX;
	public TextureRegion INPUT_BOX;

	// UI
	public LabelStyle labelStyle;
	public TextFieldStyle textFieldStyle;

	public Label MESSAGE;
	public TextField INPUT;

	// 세팅 변수
	public Queue<String> messageQueue;
	public boolean opened = false;
	public String input_buffer = "";

	// 위치 세팅 변수
	public Point chatBoxPosition;
	public Point inputBoxPosition;

	public ChatBox() {

		chatBoxPosition = new Point(0, Display.HEIGHT * Display.SCALE - 200, 0);
		inputBoxPosition = new Point((Display.WIDTH * Display.SCALE - 600) / 2, Display.HEIGHT * Display.SCALE - 240, 0);

		// 그래픽 초기화
		labelStyle = new LabelStyle();
		labelStyle.font = Asset.Font.get(Asset.NanumGothic);
		labelStyle.fontColor = Color.BLACK;

		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Asset.Font.get(Asset.ClearGothic);
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.disabledFontColor = Color.WHITE;

		// UI 초기화
		CHAT_BOX = Asset.UI.get("CHAT_BOX");
		INPUT_BOX = Asset.UI.get("CHAT_INPUT");
		MESSAGE = new Label("", labelStyle);
		INPUT = new TextField("", textFieldStyle);
		INPUT.setOnscreenKeyboard(new VirtualKeyboardHolder());

		// UI 설정
		MESSAGE.setSize(300, 200);
		INPUT.setSize(600 - 20, 40);

		MESSAGE.setPosition(chatBoxPosition.x + 20, chatBoxPosition.y);
		INPUT.setPosition(inputBoxPosition.x + 10, inputBoxPosition.y + 3);

		addActor(MESSAGE);
		addActor(INPUT);

		// 메시지 큐 초기화
		messageQueue = new LinkedList<String>();
		for (int i = 0; i < 9; i++) {
			messageQueue.add("");
		}

	}

	public void open() {
		opened = true;
		INPUT.setText("");
		input_buffer = "";
	}

	public void close() {
		opened = false;
	}

	public void addMessage(String message) {

		String trail = message;
		// 메시지가 길면 여러 줄로 나누어 출력한다.
		int count = (int) Math.floor(message.length() / 20);

		if (count > 0) {
			for (int i = 0; i < count; i++) {
				messageQueue.add(trail.substring(i * 20, (i + 1) * 20));
				if (messageQueue.size() > 9)
					messageQueue.poll();
			}
			trail = trail.substring(count * 20, trail.length());
		}
		messageQueue.add(trail);
		if (messageQueue.size() > 9)
			messageQueue.poll();

		StringBuffer buffer = new StringBuffer();
		Iterator<String> iterator = messageQueue.iterator();

		while (iterator.hasNext()) {
			buffer.append(iterator.next() + "\n");
		}

		MESSAGE.setText(buffer.toString());
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		spriteBatch.draw(CHAT_BOX, chatBoxPosition.x, chatBoxPosition.y);
		if (opened)
			spriteBatch.draw(INPUT_BOX, inputBoxPosition.x, inputBoxPosition.y);
		MESSAGE.draw(spriteBatch, 1);
		if (opened)
			INPUT.draw(spriteBatch, 1);
	}

	public void update(float delta) {
	}

	public ButtonStyle packStyle(TextureRegion up, TextureRegion down) {
		return new ButtonStyle(new TextureRegionDrawable(up), new TextureRegionDrawable(down), new TextureRegionDrawable(down));
	}

	@Override
	public boolean handle(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

}
