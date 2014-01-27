package com.lionse.ui;

import com.lionse.asset.Asset;
import com.lionse.stage.Stage.Point;

public class VirtualKeySet {

	public VirtualKeyButton[][] set;
	public boolean shifted = false;
	public boolean numSet = true;

	public String[] numKeys = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "BACKSPACE" };
	public String[] charKeys = { "~", "!", "?", "@", "(", ")", "^", "-", ",", ".", "BACKSPACE" };

	public VirtualKeySet() {
		set = new VirtualKeyButton[4][11];
	}

	public void set(int setIndex, String[] keys) {
		for (int i = 0; i < keys.length; i++) {

			float keyX = i * 65 + setIndex * 20 + CustomAdjustment.getVirtualKeyboardX();
			float keyY = (4 - setIndex) * 45;

			VirtualKeyButton key = new VirtualKeyButton(keys[i]);
			int ik = VirtualKeyboard.KOR.indexOf(keys[i]);
			if (VirtualKeyboard.ENG.indexOf(keys[i]) >= 0) {
				key.value = "/" + keys[i] + "/";
			} else if (ik >= 0) {
				key.value = String.valueOf(VirtualKeyboard.ENG.charAt(ik));
			} else {
				key.value = keys[i];
			}

			key.graphics = Asset.keyboard.get(keys[i]);
			key.position = new Point(keyX, keyY - 40, 0);
			key.background = Asset.keyboardBasement.get(keys[i]);
			key.targetPosition = new Point(keyX, keyY, 0);
			key.originalY = keyY;
			key.barneySpeed = 0.2f;
			set[setIndex][i] = key;
		}
	}

	public void set(boolean num, boolean shift) {

	}

	public static class Korean extends VirtualKeySet {

		public String[] keySet1 = { "CHR", "げ", "じ", "ぇ", "ぁ", "さ", "に", "づ", "ち", "だ", "つ" };
		public String[] keySet2 = { "SET-UP", "け", "い", "し", "ぉ", "ぞ", "で", "っ", "た", "び", "NUM" };
		public String[] keySet3 = { "IME-E", "せ", "ぜ", "ず", "そ", "ば", "ぬ", "ぱ", "SPACE" };

		public String[] skeySet1 = { "CHR", "こ", "す", "え", "あ", "ざ", "に", "づ", "ち", "ぢ", "て" };
		public String[] skeySet2 = { "SET-DOWN", "け", "い", "し", "ぉ", "ぞ", "で", "っ", "た", "び", "NUM" };
		public String[] skeySet3 = { "IME-E", "せ", "ぜ", "ず", "そ", "ば", "ぬ", "ぱ", "SPACE" };

		public Korean() {
			super();

		}

		@Override
		public void set(boolean num, boolean shift) {
			if (num) {
				set(0, this.numKeys);
			} else {
				set(0, this.charKeys);
			}
			if (!shift) {
				set(1, keySet1);
				set(2, keySet2);
				set(3, keySet3);
			} else {
				set(1, skeySet1);
				set(2, skeySet2);
				set(3, skeySet3);
			}
		}
	}

	public VirtualKeyButton getTouchedKey(float x, float y) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < set[i].length; j++) {
				if (set[i][j] == null)
					continue;
				if (set[i][j].interacts(x, y)) {
					return set[i][j];
				}
			}
		}
		return null;
	}

	public static class English extends VirtualKeySet {

		public String[] keySet1 = { "CHR", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p" };
		public String[] keySet2 = { "SET-UP", "a", "s", "d", "f", "g", "h", "j", "k", "l", "NUM" };
		public String[] keySet3 = { "IME-K", "z", "x", "c", "v", "b", "n", "m", "SPACE" };

		public String[] skeySet1 = { "CHR", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P" };
		public String[] skeySet2 = { "SET-DOWN", "A", "S", "D", "F", "G", "H", "J", "K", "L", "NUM" };
		public String[] skeySet3 = { "IME-K", "Z", "X", "C", "V", "B", "N", "M", "SPACE" };

		public English() {
			super();
		}

		@Override
		public void set(boolean num, boolean shift) {
			if (num) {
				set(0, this.numKeys);
			} else {
				set(0, this.charKeys);
			}
			if (!shift) {
				set(1, keySet1);
				set(2, keySet2);
				set(3, keySet3);
			} else {
				set(1, skeySet1);
				set(2, skeySet2);
				set(3, skeySet3);
			}
		}
	}

}
