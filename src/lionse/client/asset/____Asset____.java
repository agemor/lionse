package com.lionse.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lionse.ui.VirtualKeyboard;

public class ____Asset____ {

	public static List<TextureRegion[]> textures;
	public static List<TextureRegion> ui;
	public static List<Texture> papers;
	public static List<BitmapFont> fonts;
	public static Map<String, TextureRegion> keys;
	public static Map<String, TextureRegion> keyColor;

	public static final int HAT = 0;
	public static final int FACE = 1;
	public static final int BODY = 2;
	public static final int LEG = 3;
	public static final int STEP1 = 4;
	public static final int STEP2 = 5;
	public static final int STEP3 = 6;
	public static final int STEP4 = 7;
	public static final int HAND = 8;
	public static final int TREE = 9;
	public static final int TERRAIN = 0;
	public static final int ClearGothic = 0;

	public static final int qLOGIN_UP = 0;
	public static final int qLOGIN_DOWN = 1;
	public static final int qTEXTFIELD_CURSOR = 2;

	public static String[][] texture_assets = { { "hat0.png", "HAT" }, { "face.png", "FACE" }, { "body.png", "BODY" }, { "leg-stand.png", "LEG" },
			{ "step1.png", "STEP1" }, { "step2.png", "STEP2" }, { "step3.png", "STEP3" }, { "step4.png", "STEP4" }, { "hand.png", "HAND" },
			{ "tree.png", "TREE" } };

	public static String[][] paper_assets = { { "terrain.png", "TERRAIN" } };

	public static String[][] font_assets = { { "ClearGothic.fnt", "ClearGothic" } };

	public static String[] keyboard_assets = { "1/g`2/g`3/g`4/g`5/g`6/g`7/g`8/g`9/g`0/g`IME-K/b`IME-E/b`SET-UP/u`SET-DOWN/b`BACKSPACE/r",
			"~/g`!/g`@/g`^/g`(/g`)/g`-/g`?/g`,/g`./g`NUM/b`CHR/b`SPACE/s", "ぁ`い`ぇ`ぉ`け`げ`さ`し`じ`ず`せ`ぜ`そ`ぞ`こ/u", "に`づ`ち`だ`つ`で`っ`た`び`ば`ぬ`ぱ`す/u`え/u`あ/u",
			"A`B`C`D`E`F`G`H`I`J`K`L`M`N`O", "P`Q`R`S`T`U`V`W`X`Y`Z`ざ/u`ぢ/u`て/u", "a`b`c`d`e`f`g`h`i`j`k`l`m`n`o", "p`q`r`s`t`u`v`w`x`y`z" };

	public static void load() {

		textures = new ArrayList<TextureRegion[]>();
		papers = new ArrayList<Texture>();
		fonts = new ArrayList<BitmapFont>();
		keys = new HashMap<String, TextureRegion>();
		keyColor = new HashMap<String, TextureRegion>();
		ui = new ArrayList<TextureRegion>();
		loadAssets();
		VirtualKeyboard.load();
	}

	public static TextureRegion[] getTexture(int index) {
		return textures.get(index);
	}

	public static Texture getPaper(int index) {
		return papers.get(index);
	}

	public static BitmapFont getFont(int index) {
		return fonts.get(index);
	}

	// split textures so that i can use it later.
	public static TextureRegion[] splitTexture(Texture texture) {
		int width = texture.getWidth() / 8;
		TextureRegion[] result = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			result[i] = new TextureRegion(texture, i * width, 0, width, texture.getHeight());
		}

		return result;
	}

	private static void loadAssets() {
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/hat0.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/face.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/body.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/leg-stand.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/step1.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/step2.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/step3.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/step4.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/hand.png"))));
		textures.add(splitTexture(new Texture(Gdx.files.internal("graphics/tree.png"))));
		papers.add(new Texture(Gdx.files.internal("graphics/terrain.png")));
		fonts.add(new BitmapFont(Gdx.files.internal("fonts/ClearGothic.fnt"), false));
		loadKeys();
		loadUI();
	}

	private static void loadUI() {
		Texture texture = new Texture(Gdx.files.internal("graphics/button.png"));
		ui.add(new TextureRegion(texture, 0, 0, 140, 80));
		ui.add(new TextureRegion(texture, 141, 0, 140, 80));
		ui.add(new TextureRegion(texture, 0, 200, 4, 40));
	}

	private static void loadKeys() {
		Texture texture = new Texture(Gdx.files.internal("graphics/keyboard.png"));

		// load basements
		String[] basement = { "cb", "cg", "cu", "cr", "cw", "cs" };
		for (int i = 0; i < basement.length; i++) {
			if (basement[i].equals("cs")) {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 60 * 3 + 1, 100);
				keys.put(basement[i], region);
			} else {
				TextureRegion region = new TextureRegion(texture, 61 * i, 55 * 8, 61, 100);
				keys.put(basement[i], region);
			}

		}

		for (int i = 0; i < 8; i++) {
			String[] asset = keyboard_assets[i].split("`");
			for (int j = 0; j < asset.length; j++) {
				String[] chunk = asset[j].split("/");
				String color = chunk.length > 1 ? "c" + chunk[1] : "cw";

				keyColor.put(chunk[0], keys.get(color));
				if (chunk[0].equals("SPACE")) {
					TextureRegion region = new TextureRegion(texture, 61 * 12, 55 * 1, 60 * 3 + 1, 50);
					keys.put("SPACE", region);
				} else {
					TextureRegion region = new TextureRegion(texture, 61 * j, 55 * i, 61, 50);
					keys.put(chunk[0], region);
				}

			}
		}

	}

	public static void show() {
		for (int i = 0; i < texture_assets.length; i++) {
			System.out.println("textures.add(splitTexture(new Texture(Gdx.files.internal(\"graphics/" + texture_assets[i][0] + "\"))));");
		}

		for (int i = 0; i < paper_assets.length; i++) {
			System.out.println("papers.add(new Texture(Gdx.files.internal(\"graphics/" + paper_assets[i][0] + "\")));");
		}

		for (int i = 0; i < font_assets.length; i++) {
			System.out.println("fonts.add(new BitmapFont(Gdx.files.internal(\"fonts/" + font_assets[i][0] + "\"), false));");
		}

		for (int i = 0; i < texture_assets.length; i++) {
			System.out.println("public static final int " + texture_assets[i][1] + " = " + i + ";");
		}

		for (int i = 0; i < paper_assets.length; i++) {
			System.out.println("public static final int " + paper_assets[i][1] + " = " + i + ";");
		}
		for (int i = 0; i < font_assets.length; i++) {
			System.out.println("public static final int " + font_assets[i][1] + " = " + i + ";");
		}
	}

}
