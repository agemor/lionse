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
 * 게임에서 사용되는 모든 리소스를 관리합니다.
 * 
 * Usage: Asset.character.get(Asset.Character.get("GOLD_ARMOR"))
 * 
 * @author 김현준
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

	// 클래스 로드 시 변수를 초기화한다.
	static {
		keyboard = new HashMap<String, TextureRegion>();
		keyboardBasement = new HashMap<String, TextureRegion>();
		character = new ArrayList<TextureRegion[]>();
		game = new ArrayList<TextureRegion[]>();
		ui = new ArrayList<TextureRegion>();
		font = new ArrayList<BitmapFont>();
		disposables = new ArrayList<Texture>();
	}

	// 자원 불러오기
	public static void load() {

		// 폰트 로드
		font.add(new BitmapFont(Gdx.files.internal("fonts/ClearGothic.fnt"), false));

		// 캐릭터 로드
		Character.load();
		// 게임 로드
		Game.load();
		// UI로드
		UI.load();
		// 키보드 로드
		loadKeyboard();
		VirtualKeyboard.load();
	}

	/**
	 * 
	 * 텍스처를 8등분합니다. 조각난 텍스처는 캐릭터, 게임 그래픽에 사용됩니다.
	 * 
	 * @param texture
	 *            8개의 조각으로 나눌 텍스처
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
	 * 키보드 리소스를 로드합니다.
	 */
	private static void loadKeyboard() {
		Texture texture = new Texture(Gdx.files.internal("graphics/keyboard.png"));

		// 키 베이스 로드
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

	// 마커 인터페이스
	public static interface AssetHolder {

	}

	// 배경, 버튼, 키보드, 대화 상자(Dialog)
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

					// 자원 Add 부분
					ID.put(entry.identifier[j], ui.size() - 1);
				}
			}
		}
	}

	// NPC, 자연물, 지형, 구름
	public static class Game implements AssetHolder {
		public static Map<String, Integer> ID;

		public static int get(String id) {
			return ID.get(id);
		}

		// 캐릭터 리소스 로드
		public static void load() {
			// 리소스의 인덱스를 저장하기 위한 해시맵
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

					// 자원 Add 부분
					ID.put(entry.identifier[j], game.size() - 1);
				}
			}
		}
	}

	// 캐릭터, 모자, 얼굴, 몸체, 다리
	public static class Character implements AssetHolder {

		public static Map<String, Integer> ID;

		public static int get(String id) {
			return ID.get(id);
		}

		// 캐릭터 리소스 로드
		public static void load() {
			// 리소스의 인덱스를 저장하기 위한 해시맵
			ID = new HashMap<String, Integer>();
			// 캐릭터의 엔트리를 얻어 정보 그대로 읽어온다.
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

					// 자원 Add 부분
					ID.put(entry.identifier[j], character.size() - 1);
				}
			}
		}

	}

	// 아이템 창, 필드 드롭 아이템
	public static class Item implements AssetHolder {

	}

	// 게임 내 이펙트 애니메이션
	public static class Effect implements AssetHolder {

	}

	// 사운드
	public static class Sounds implements AssetHolder {

	}
}
