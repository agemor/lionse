package com.lionse.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lionse.asset.AssetEntry.Entry;
import com.lionse.ui.VirtualKeyboard;

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

	public static final int ClearGothic = 0;
	public static Map<String, TextureRegion> keyboard;
	public static Map<String, TextureRegion> keyboardBasement;
	public static List<TextureRegion[]> character;
	public static List<TextureRegion[]> game;
	public static List<TextureRegion> ui;
	public static List<BitmapFont> font;

	private static List<Texture> disposables;

	// Ŭ���� �ε� �� ������ �ʱ�ȭ�Ѵ�.
	static {
		keyboard = new HashMap<String, TextureRegion>();
		keyboardBasement = new HashMap<String, TextureRegion>();
		character = new ArrayList<TextureRegion[]>();
		game = new ArrayList<TextureRegion[]>();
		ui = new ArrayList<TextureRegion>();
		font = new ArrayList<BitmapFont>();
		disposables = new ArrayList<Texture>();
	}

	// �ڿ� �ҷ�����
	public static void load() {

		// ��Ʈ �ε�
		font.add(new BitmapFont(Gdx.files.internal("fonts/ClearGothic.fnt"), false));

		// ĳ���� �ε�
		Character.load();
		// ���� �ε�
		Game.load();
		// UI�ε�
		UI.load();
		// Ű���� �ε�
		loadKeyboard();
		VirtualKeyboard.load();
	}

	/**
	 * 
	 * �ؽ�ó�� 8����մϴ�. ������ �ؽ�ó�� ĳ����, ���� �׷��ȿ� ���˴ϴ�.
	 * 
	 * @param texture
	 *            8���� �������� ���� �ؽ�ó
	 * @return
	 */
	public static TextureRegion[] splitTexture(TextureRegion texture) {
		int width = texture.getRegionWidth() / 8;
		TextureRegion[] result = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			result[i] = new TextureRegion(texture, i * width, 0, width, texture.getRegionHeight());
		}

		return result;
	}

	/**
	 * Ű���� ���ҽ��� �ε��մϴ�.
	 */
	private static void loadKeyboard() {
		Texture texture = new Texture(Gdx.files.internal("graphics/keyboard.png"));

		// Ű ���̽� �ε�
		String[] basement = { "cb", "cg", "cu", "cr", "cw", "cs" };
		for (int i = 0; i < basement.length; i++) {
			if (basement[i].equals("cs")) {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 60 * 3 + 1, 100);
				keyboard.put(basement[i], region);
			} else {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 61, 100);
				keyboard.put(basement[i], region);
			}
		}

		for (int i = 0; i < 8; i++) {
			String[] asset = AssetEntry.KEYBOARD[i].split("`");
			for (int j = 0; j < asset.length; j++) {
				String[] chunk = asset[j].split("/");
				String color = chunk.length > 1 ? "c" + chunk[1] : "cw";
				keyboardBasement.put(chunk[0], keyboard.get(color));
				if (chunk[0].equals("SPACE")) {
					TextureRegion region = new TextureRegion(texture, 61 * 12, 55 * 1, 60 * 3 + 1, 50);
					keyboard.put("SPACE", region);
				} else {
					TextureRegion region = new TextureRegion(texture, 61 * j, 55 * i, 61, 50);
					keyboard.put(chunk[0], region);
				}

			}
		}
	}

	// ��Ŀ �������̽�
	public static interface AssetHolder {

	}

	// ���, ��ư, Ű����, ��ȭ ����(Dialog)
	public static class UI implements AssetHolder {
		public static Map<String, Integer> ID;

		public static int get(String id) {
			return ID.get(id);
		}

		public static void load() {
			ID = new HashMap<String, Integer>();
			Entry[] entries = AssetEntry.getEntry(AssetEntry.UI);
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] == null)
					break;
				Entry entry = entries[i];
				Texture texture = new Texture(entry.textureFile);
				disposables.add(texture);

				for (int j = 0; j < entry.identifier.length; j++) {
					if (entry.identifier[j] == null)
						break;
					TextureRegion textureRegion = new TextureRegion(texture, entry.vertex[j][0], entry.vertex[j][1], entry.vertex[j][2], entry.vertex[j][3]);
					ui.add(textureRegion);

					// �ڿ� Add �κ�
					ID.put(entry.identifier[j], ui.size() - 1);
				}
			}
		}
	}

	// NPC, �ڿ���, ����, ����
	public static class Game implements AssetHolder {
		public static Map<String, Integer> ID;

		public static int get(String id) {
			return ID.get(id);
		}

		// ĳ���� ���ҽ� �ε�
		public static void load() {
			// ���ҽ��� �ε����� �����ϱ� ���� �ؽø�
			ID = new HashMap<String, Integer>();
			Entry[] entries = AssetEntry.getEntry(AssetEntry.GAME);
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] == null)
					break;
				Entry entry = entries[i];
				Texture texture = new Texture(entry.textureFile);
				disposables.add(texture);

				for (int j = 0; j < entry.identifier.length; j++) {
					if (entry.identifier[j] == null)
						break;
					TextureRegion textureRegion = new TextureRegion(texture, entry.vertex[j][0], entry.vertex[j][1], entry.vertex[j][2], entry.vertex[j][3]);
					game.add(splitTexture(textureRegion));

					// �ڿ� Add �κ�
					ID.put(entry.identifier[j], game.size() - 1);
				}
			}
		}
	}

	// ĳ����, ����, ��, ��ü, �ٸ�
	public static class Character implements AssetHolder {

		public static Map<String, Integer> ID;

		public static int get(String id) {
			return ID.get(id);
		}

		// ĳ���� ���ҽ� �ε�
		public static void load() {
			// ���ҽ��� �ε����� �����ϱ� ���� �ؽø�
			ID = new HashMap<String, Integer>();
			// ĳ������ ��Ʈ���� ��� ���� �״�� �о�´�.
			Entry[] entries = AssetEntry.getEntry(AssetEntry.CHARACTER);
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] == null)
					break;
				Entry entry = entries[i];
				Texture texture = new Texture(entry.textureFile);
				disposables.add(texture);

				for (int j = 0; j < entry.identifier.length; j++) {
					if (entry.identifier[j] == null)
						break;
					TextureRegion textureRegion = new TextureRegion(texture, entry.vertex[j][0], entry.vertex[j][1], entry.vertex[j][2], entry.vertex[j][3]);
					character.add(splitTexture(textureRegion));

					// �ڿ� Add �κ�
					ID.put(entry.identifier[j], character.size() - 1);
				}
			}
		}

	}

	// ������ â, �ʵ� ��� ������
	public static class Item implements AssetHolder {

	}

	// ���� �� ����Ʈ �ִϸ��̼�
	public static class Effect implements AssetHolder {

	}

	// ����
	public static class Sounds implements AssetHolder {

	}
}
