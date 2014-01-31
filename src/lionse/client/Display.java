package lionse.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.asset.Asset;

public class Display {
	public static float ORIGINAL_WIDTH = 800;
	public static float ORIGINAL_HEIGHT = 480;
	public static float WIDTH = Gdx.graphics.getWidth();
	public static float HEIGHT = Gdx.graphics.getHeight();
	public static float SCALE_WIDTH = ORIGINAL_WIDTH / WIDTH;
	public static float SCALE_HEIGHT = ORIGINAL_HEIGHT / HEIGHT;
	public static float SCALE = SCALE_WIDTH > SCALE_HEIGHT ? SCALE_WIDTH : SCALE_HEIGHT;

	// UI �ؽ�ó
	public static Texture DISABLED;
	public static Sprite ALERT;
	public static Sprite LOADING;
	public static BitmapFont FONT;
	public static TextFieldStyle TEXTFIELD_STYLE;

	// ���÷��� �ڿ� �ʱ�ȭ
	public static void initizlize() {

		// �ε� �� �ʱ�ȭ
		LOADING = new Sprite(Asset.UI.get("LOADING"));
		LOADING.setPosition((Display.WIDTH - LOADING.getWidth()) / 2, (Display.HEIGHT - LOADING.getHeight()) / 2);
		LOADING.scale(Display.SCALE);

		// ��Ȱ��ȭ ��� �ʱ�ȭ
		Color pixmapColor = new Color(0, 0, 0, 0.5f);
		Pixmap DISABLED_PIXMAP = new Pixmap((int) Display.WIDTH, (int) Display.HEIGHT, Pixmap.Format.RGBA4444);
		DISABLED_PIXMAP.setColor(pixmapColor);
		DISABLED_PIXMAP.fill();
		DISABLED = new Texture(DISABLED_PIXMAP);

		// ���â �ʱ�ȭ
		ALERT = new Sprite(Asset.UI.get("ALERT_BACKGROUND"));
		ALERT.setPosition((Display.WIDTH * Display.SCALE - 500) / 2, (Display.HEIGHT * Display.SCALE - 64) / 2);

		// ��Ʈ �ʱ�ȭ
		FONT = Asset.Font.get(Asset.ClearGothic);
		FONT.setColor(Color.WHITE);

		// �Է� �ؽ�Ʈ ��Ÿ�� ����
		TEXTFIELD_STYLE = new TextFieldStyle();
		TEXTFIELD_STYLE.font = Asset.Font.get(Asset.ClearGothic);
		TEXTFIELD_STYLE.fontColor = Color.BLACK;
		TEXTFIELD_STYLE.disabledFontColor = Color.GRAY;
		TEXTFIELD_STYLE.cursor = new TextureRegionDrawable(Asset.UI.get("TEXT_FIELD_POINTER"));

	}
}
