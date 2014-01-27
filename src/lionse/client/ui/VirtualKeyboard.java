package lionse.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class VirtualKeyboard {

	// English -> Korean board.
	public static final String KOR = "�����������ˤŤ����Ĥ����������Ǥä��Ӥ��������Ф̤Ѥ����������¤�";
	public static final String ENG = "qwertyuiopasdfghjklzxcvbnmQWERTOPQWERTYUIOPASDFGHJKLZXCVBNM";
	public static final String INITIAL_LIST = "rRseEfaqQtTdwWczxvg";
	public static final String[] MEDIAL_LIST = { "k", "o", "i", "O", "j", "p", "u", "P", "h", "hk",
			"ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l" };
	public static final String[] FINAL_LIST = { "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr",
			"fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x",
			"v", "g" };

	public static final int INITIAL = 0;
	public static final int MEDIAL = 1;
	public static final int FINAL = 2;

	// display values.
	public static boolean display = true;

	// keyboard sets
	public static VirtualKeySet Korean;
	public static VirtualKeySet English;
	private static VirtualKeySet selectedSet;

	// touched pointers
	private static Map<Integer, VirtualKeyButton> pointers;

	// event target
	private static VirtualKeyEvent target;

	public static void load() {

		Korean = new VirtualKeySet.Korean();
		English = new VirtualKeySet.English();

		pointers = new HashMap<Integer, VirtualKeyButton>();

		selectedSet = English;
		English.set(true, false);
	}

	public static void setEventListener(VirtualKeyEvent event) {
		target = event;
	}

	public static String addBuffer(String buffer, VirtualKeyButton key) {
		if (key.letter.equals("BACKSPACE") && buffer.length() > 0) {
			if (buffer.charAt(buffer.length() - 1) == '/') {
				buffer = buffer.substring(0, buffer.length() - 3);
			} else {
				buffer = buffer.substring(0, buffer.length() - 1);
			}

		} else if (key.letter.equals("SPACE")) {
			buffer += "/ /";
		} else if (!key.letter.equals("BACKSPACE")) {
			buffer += key.value;
		}
		return buffer;
	}

	public static String mix(String buffer) {

		String[] chunk = buffer.split("/");
		String result = "";
		for (int i = 0; i < chunk.length; i++) {
			if (i % 2 == 0) {
				if (chunk[i].equals(""))
					continue;
				String c = VirtualKeyboard.combine(chunk[i] + " ");
				result += c.substring(0, c.length());
			} else {
				result += chunk[i];
			}
		}
		return result;
	}

	public static void touchDown(float x, float y, int pointer, int button) {
		VirtualKeyButton touchedButton = selectedSet.getTouchedKey(x, y);
		if (touchedButton != null) {
			pointers.put(pointer, touchedButton);
			pointers.get(pointer).setTouched(true);
			if (!keyTouched(touchedButton)) {
				if (target != null)
					target.keyTyped(touchedButton);
			}
			Gdx.input.vibrate(50);
		}

	}

	public static void show() {
		display = true;
	}

	public static void close() {
		Gdx.app.log("sdsd", "closed");
		display = false;
	}

	public static void touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointers.get(pointer) != null) {
			pointers.get(pointer).setTouched(false);
			// target.keyTyped(touchedButton);
		}
	}

	private static boolean keyTouched(VirtualKeyButton touchedKey) {

		String letter = touchedKey.letter;

		if (letter.equals("IME-K")) {
			selectedSet = Korean;
			Korean.set(English.numSet, false);
			return true;
		} else if (letter.equals("IME-E")) {
			selectedSet = English;
			English.set(Korean.numSet, false);
			return true;
		} else if (letter.equals("SET-UP")) {
			selectedSet.set(selectedSet.numSet, true);
			return true;
		} else if (letter.equals("SET-DOWN")) {
			selectedSet.set(selectedSet.numSet, false);
			return true;
		} else if (letter.equals("NUM")) {
			selectedSet.set(true, selectedSet.shifted);
			return true;
		} else if (letter.equals("CHR")) {
			selectedSet.set(false, selectedSet.shifted);
			return true;
		}
		return false;
	}

	public static void update(float delta) {
		if (!display)
			return;
		for (int i = 0; i < selectedSet.set.length; i++) {
			for (int j = 0; j < selectedSet.set[i].length; j++) {
				if (selectedSet.set[i][j] == null)
					continue;
				selectedSet.set[i][j].update(delta);
			}
		}
	}

	public static void draw(SpriteBatch spriteBatch, float delta) {
		if (!display)
			return;
		for (int i = 0; i < selectedSet.set.length; i++) {
			for (int j = 0; j < selectedSet.set[i].length; j++) {
				if (selectedSet.set[i][j] == null)
					continue;
				selectedSet.set[i][j].draw(spriteBatch, delta);
			}
		}
	}

	public static String combine(String text) {
		int initialCode;
		int medialCode;
		int finalCode;
		int medial_temp;
		int final_temp;
		String temp = "";

		StringBuffer combined = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {
			initialCode = -1;
			medialCode = -1;
			finalCode = 0;

			String result = "";

			initialCode = getCode(INITIAL, substring(text, i, i + 1));
			if (initialCode < 0) { // ó���� ������ �ƴϴ�. ���� ��������
				medialCode = getCode(MEDIAL, substring(text, i, i + 1));
				if (medialCode < 0) { // ������ �ƴϴ�. (�ѱ� �ڸ� �ƴ�, �ٸ� ����)
					combined.append(substring(text, i, i + 1)); // �״�� �߰��ϰ�
																// continue;
					continue;
				}
			} else { // ������ ã�Ҵ�.
				if (getCode(INITIAL, substring(text, i + 1, i + 2)) >= 0) { // ����
																			// �̾�����
																			// ���ڰ�
																			// ��
																			// �����̸�
					if (getCode(FINAL, substring(text, i, i + 2)) >= 0 && i + 2 <= text.length()) { // ���޾�
																									// ������
																									// ����
																									// �߿�����
																									// �����̸�
						finalCode = getCode(FINAL, substring(text, i, i + 2)); // ������
						result = fromCharCode(12593 + finalCode - (finalCode < 7 ? 1 : 0));
						combined.append(result);
						i++;
					} else { // �̾����� �� ������ �ƴϴ�. ���� + ���� �� ����
						initialCode = initialCode / 21 / 28;
						combined.append(fromCharCode(12593
								+ initialCode
								+ (initialCode < 2 ? 0 : initialCode < 3 ? 1 : initialCode < 6 ? 3
										: initialCode < 9 ? 10 : 11)));
					}
					continue;
				}
			}
			// ó���� ã�� ���� ������ �����̴�.
			if (medialCode < 0) {
				i++;
			}

			if (i + 2 < text.length()) {
				medial_temp = getCode(MEDIAL, substring(text, i, i + 2));
			} else {
				medial_temp = -1;
			}
			// �ڵ� ���� ���� ���
			if (medial_temp >= 0) {
				// �ڵ� ���� �����ϰ� �ε����� �ٴ��� ���ڿ��� ����Ű�� �Ѵ�.
				medialCode = medial_temp;
				i += 2;
			} else { // �ڵ尪�� ���� ��� �ϳ��� ���ڿ� ���� �߼� �ڵ� ����
				medialCode = getCode(MEDIAL, substring(text, i, i + 1));
				i++;
			}
			// ���� ���ڿ� ���� ���ڸ� ���� ���ڿ��� ���� �ڵ� ����
			if (i + 2 < text.length()) {
				final_temp = getCode(FINAL, substring(text, i, i + 2));
			} else {
				final_temp = -1;
			}
			// �ڵ� ���� ���� ���
			if (final_temp >= 0) {
				// �ڵ� ���� �����Ѵ�
				finalCode = final_temp;
				// �� ������ �߼� ���ڿ� ���� �ڵ带 �����Ѵ�.
				medial_temp = getCode(MEDIAL, substring(text, i + 2, i + 3));
				// �ڵ� ���� ���� ���
				if (medial_temp >= 0) {
					// ���� �ڵ� ���� �����Ѵ�.
					finalCode = getCode(FINAL, substring(text, i, i + 1));
				} else {
					i++;
				}
			} else {
				if (i + 2 < text.length()) {
					medial_temp = getCode(MEDIAL, substring(text, i + 1, i + 2));
				} else {
					medial_temp = -1;
				}
				if (medial_temp >= 0) {
					finalCode = 0;
					i--;

				} else {
					finalCode = getCode(FINAL, substring(text, i, i + 1));

					if (finalCode < 0) {
						int initial_con = getCode(INITIAL, substring(text, i, i + 1));
						if (initial_con < 0) {
							temp = substring(text, i, i + 1);
						} else {
							i--;
						}
						finalCode = 0;
					}
				}
			}

			if (initialCode < 0) {
				if (medialCode >= 0) {
					result = fromCharCode(12623 + medialCode / 28);
				}
			} else {
				if (medialCode < 0) {
					initialCode = initialCode / 21 / 28;
					result = fromCharCode(12593
							+ initialCode
							+ (initialCode < 2 ? 0 : initialCode < 3 ? 1 : initialCode < 6 ? 3
									: initialCode < 9 ? 10 : 11));
				} else {
					result = fromCharCode(44032 + initialCode + medialCode + finalCode);
				}
			}
			combined.append(result + temp);
		}
		return combined.toString();
	}

	private static String substring(String text, int start, int end) {
		if (start <= text.length() && end <= text.length()) {
			return text.substring(start, end);
		} else {
			return "";
		}
	}

	private static String fromCharCode(int code) {
		return String.valueOf((char) code);
	}

	// get key code
	private static int getCode(int type, String character) {
		switch (type) {
		case INITIAL:
			return INITIAL_LIST.indexOf(character) * 21 * 28;
		case MEDIAL:
			for (int i = 0; i < MEDIAL_LIST.length; i++) {
				if (MEDIAL_LIST[i].equals(character)) {
					return i * 28;
				}
			}
			break;
		case FINAL:
			for (int j = 0; j < FINAL_LIST.length; j++) {
				if (FINAL_LIST[j].equals(character)) {
					return j + 1;
				}
			}
			break;
		}
		return -1;
	}

}
