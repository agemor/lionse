package lionse.client.asset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;

/**
 * �ڿ� ������ �ڿ� �δ��� �����ϱ� �����ϰ� �����͸� ��ȯ�� �ִ� Ŭ�����Դϴ�.
 * 
 * @author �� ����
 */
public class AssetEntry {

	// Ű���� ��ġ ���
	public static String[] KEYBOARD = {
			"1/g`2/g`3/g`4/g`5/g`6/g`7/g`8/g`9/g`0/g`IME-K/b`IME-E/b`SET-UP/u`SET-DOWN/b`BACKSPACE/r",
			"~/g`!/g`@/g`^/g`(/g`)/g`-/g`?/g`,/g`./g`NUM/b`CHR/b`SPACE/s",
			"��`��`��`��`��`��`��`��`��`��`��`��`��`��`��/u", "��`��`��`��`��`��`��`��`��`��`��`��`��/u`��/u`��/u",
			"A`B`C`D`E`F`G`H`I`J`K`L`M`N`O", "P`Q`R`S`T`U`V`W`X`Y`Z`��/u`��/u`��/u",
			"a`b`c`d`e`f`g`h`i`j`k`l`m`n`o", "p`q`r`s`t`u`v`w`x`y`z" };

	// �⺻ �ڿ� ���� ���
	public static String UI = "ui.asset";
	public static String GAME = "game.asset";
	public static String CHARACTER = "character.asset";
	public static String ITEM = "item.asset";
	private static BufferedReader reader;
	private static Map<String, Entry> container = new HashMap<String, Entry>();

	/*
	 * <��Ʈ�� ������ ����> �ؽ�ó ����, �ĺ� ID, X, Y, WIDTH, HEIGHT
	 */

	/**
	 * �ڿ� ������ �бⰡ ������ �ڿ� ���� ��ҷ� �����Ͽ� ����մϴ�.
	 * 
	 * @param filePath
	 *            �ڿ� ����(*.asset)�� ����Դϴ�.
	 */
	public static Entry[] getEntry(String filePath) {
		try {
			// ����� ���� �ʱ�ȭ
			container.clear();
			reader = new BufferedReader(Gdx.files.internal(filePath).reader());

			String data;

			// ���� �����Ϳ��� �� �پ� �д´�.
			while ((data = reader.readLine()) != null) {

				// �� ĭ ���� �� ���ۿ� ����
				String[] buffer = data.replaceAll("\\s", "").split(",");

				// �̹� Ű�� ������ �ִ�. (���� �ؽ�ó ����)
				if (container.containsKey(buffer[0])) {

					// ���� ��Ʈ���� �ؽ�ó �߰�
					Entry entry = container.get(buffer[0]);
					entry.putTexture(buffer[1], Integer.parseInt(buffer[2]), Integer
							.parseInt(buffer[3]), Integer.parseInt(buffer[4]), Integer
							.parseInt(buffer[5]));

				} else {

					// �� ��Ʈ�� �����
					Entry entry = new Entry(buffer[0]);
					entry.putTexture(buffer[1], Integer.parseInt(buffer[2]), Integer
							.parseInt(buffer[3]), Integer.parseInt(buffer[4]), Integer
							.parseInt(buffer[5]));

					// �����̳ʿ� ��Ʈ���� �߰�
					container.put(buffer[0], entry);
				}

			}

			// ��Ʈ�� ����� ���� ���� ���� ����
			Iterator<String> iterator = container.keySet().iterator();
			Entry[] entries = new Entry[container.keySet().size()];
			int count = 0;

			// �����̳ʸ� ��� �о� �迭�� ����.
			while (iterator.hasNext()) {
				String key = iterator.next();
				entries[count++] = container.get(key);
			}

			// ��Ʈ�� ���
			return entries;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ������ �ڿ� ������ ���� ����Դϴ�. ������ �ؽ�ó ���Ϸ� �̷���� �ֽ��ϴ�.
	 * 
	 */
	public static class Entry {

		public String textureFile;
		public String[] identifier;
		public int[][] vertex;
		public int index = 0;

		// ��Ʈ���� �ؽ�ó ���� �ϳ� �� �����ȴ�.
		public Entry(String textureFile) {
			this.textureFile = textureFile;
			identifier = new String[100];
			vertex = new int[100][100];
		}

		// �ؽ�ó �߰� �޼���
		public void putTexture(String identifier, int x, int y, int width, int height) {
			this.identifier[index] = identifier;
			this.vertex[index][0] = x;
			this.vertex[index][1] = y;
			this.vertex[index][2] = width;
			this.vertex[index][3] = height;
			index++;
		}
	}
}
