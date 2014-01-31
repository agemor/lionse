package lionse.client.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lionse.client.ui.VirtualKeyboard;

/**
 * 
 * ���ӿ��� ���Ǵ� ��� ���ҽ��� �����մϴ�.
 * 
 * Usage: Asset.character.get(Asset.Character.get("GOLD_ARMOR"))
 * 
 * @author ������
 * 
 */
public class Asset {

	public static String[] KEYBOARD = { "1/g`2/g`3/g`4/g`5/g`6/g`7/g`8/g`9/g`0/g`IME-K/b`IME-E/b`SET-UP/u`SET-DOWN/b`BACKSPACE/r",
			"~/g`!/g`@/g`^/g`(/g`)/g`-/g`?/g`,/g`./g`NUM/b`CHR/b`SPACE/s", "��`��`��`��`��`��`��`��`��`��`��`��`��`��`��/u", "��`��`��`��`��`��`��`��`��`��`��`��`��/u`��/u`��/u",
			"A`B`C`D`E`F`G`H`I`J`K`L`M`N`O", "P`Q`R`S`T`U`V`W`X`Y`Z`��/u`��/u`��/u", "a`b`c`d`e`f`g`h`i`j`k`l`m`n`o", "p`q`r`s`t`u`v`w`x`y`z`ENTER/b" };

	public static final int ClearGothic = 0;
	public static final int NanumGothic = 1;
	public static Map<String, TextureRegion> Keyboard;
	public static Map<String, TextureRegion> KeyboardBasement;
	public static Map<String, TextureRegion[]> Character;
	public static Map<String, TextureRegion[]> Game;
	public static Map<String, TextureRegion> UI;
	public static List<BitmapFont> Font;

	// Ŭ���� �ε� �� ������ �ʱ�ȭ�Ѵ�.
	static {
		Keyboard = new HashMap<String, TextureRegion>();
		KeyboardBasement = new HashMap<String, TextureRegion>();
		Character = new HashMap<String, TextureRegion[]>();
		Game = new HashMap<String, TextureRegion[]>();
		UI = new HashMap<String, TextureRegion>();
		Font = new ArrayList<BitmapFont>();
	}

	// �ڿ� �ҷ�����
	public static void load() {
		SpriteSheetParser.parseAndInsert(UI, "UI.xml");
		SpriteSheetParser.parseAndInsert(Character, "CHARACTER.xml", true);
		SpriteSheetParser.parseAndInsert(Game, "GAME.xml", true);
		SpriteSheetParser.parseAndInsert(UI, "BACKGROUND.xml");

		// ��Ʈ �ε�
		Font.add(new BitmapFont(Gdx.files.internal("fonts/ClearGothic.fnt"), false));
		Font.add(new BitmapFont(Gdx.files.internal("fonts/NanumGothic.fnt"), false));

		loadKeyboard();
		VirtualKeyboard.load();
	}

	/**
	 * Ű���� ���ҽ��� �ε��մϴ�.
	 */
	private static void loadKeyboard() {
		Texture texture = new Texture(Gdx.files.internal("keyboard.png"));

		// Ű ���̽� �ε�
		String[] basement = { "cb", "cg", "cu", "cr", "cw", "cs" };
		for (int i = 0; i < basement.length; i++) {
			if (basement[i].equals("cs")) {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 60 * 3 + 1, 100);
				Keyboard.put(basement[i], region);
			} else {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 61, 100);
				Keyboard.put(basement[i], region);
			}
		}

		for (int i = 0; i < 8; i++) {
			String[] asset = Asset.KEYBOARD[i].split("`");
			for (int j = 0; j < asset.length; j++) {
				String[] chunk = asset[j].split("/");
				String color = chunk.length > 1 ? "c" + chunk[1] : "cw";
				KeyboardBasement.put(chunk[0], Keyboard.get(color));
				if (chunk[0].equals("SPACE")) {
					TextureRegion region = new TextureRegion(texture, 61 * 12, 55 * 1, 60 * 3 + 1, 50);
					Keyboard.put("SPACE", region);
				} else {
					TextureRegion region = new TextureRegion(texture, 61 * j, 55 * i, 61, 50);
					Keyboard.put(chunk[0], region);
				}

			}
		}
	}

}
