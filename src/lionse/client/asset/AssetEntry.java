package lionse.client.asset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;

/**
 * 자원 파일을 자원 로더가 가공하기 용이하게 데이터를 변환해 주는 클래스입니다.
 * 
 * @author 김 현준
 */
public class AssetEntry {

	// 키보드 위치 상수
	public static String[] KEYBOARD = {
			"1/g`2/g`3/g`4/g`5/g`6/g`7/g`8/g`9/g`0/g`IME-K/b`IME-E/b`SET-UP/u`SET-DOWN/b`BACKSPACE/r",
			"~/g`!/g`@/g`^/g`(/g`)/g`-/g`?/g`,/g`./g`NUM/b`CHR/b`SPACE/s",
			"ㄱ`ㄴ`ㄷ`ㄹ`ㅁ`ㅂ`ㅅ`ㅇ`ㅈ`ㅊ`ㅋ`ㅌ`ㅍ`ㅎ`ㅃ/u", "ㅛ`ㅕ`ㅑ`ㅐ`ㅔ`ㅗ`ㅓ`ㅏ`ㅣ`ㅠ`ㅜ`ㅡ`ㅉ/u`ㄸ/u`ㄲ/u",
			"A`B`C`D`E`F`G`H`I`J`K`L`M`N`O", "P`Q`R`S`T`U`V`W`X`Y`Z`ㅆ/u`ㅒ/u`ㅖ/u",
			"a`b`c`d`e`f`g`h`i`j`k`l`m`n`o", "p`q`r`s`t`u`v`w`x`y`z" };

	// 기본 자원 파일 경로
	public static String UI = "ui.asset";
	public static String GAME = "game.asset";
	public static String CHARACTER = "character.asset";
	public static String ITEM = "item.asset";
	private static BufferedReader reader;
	private static Map<String, Entry> container = new HashMap<String, Entry>();

	/*
	 * <엔트리 파일의 구조> 텍스처 파일, 식별 ID, X, Y, WIDTH, HEIGHT
	 */

	/**
	 * 자원 파일을 읽기가 용이한 자원 구성 요소로 가공하여 출력합니다.
	 * 
	 * @param filePath
	 *            자원 파일(*.asset)의 경로입니다.
	 */
	public static Entry[] getEntry(String filePath) {
		try {
			// 사용을 위해 초기화
			container.clear();
			reader = new BufferedReader(Gdx.files.internal(filePath).reader());

			String data;

			// 에셋 데이터에서 한 줄씩 읽는다.
			while ((data = reader.readLine()) != null) {

				// 빈 칸 제거 후 버퍼에 분할
				String[] buffer = data.replaceAll("\\s", "").split(",");

				// 이미 키를 가지고 있다. (동일 텍스처 파일)
				if (container.containsKey(buffer[0])) {

					// 기존 엔트리에 텍스처 추가
					Entry entry = container.get(buffer[0]);
					entry.putTexture(buffer[1], Integer.parseInt(buffer[2]), Integer
							.parseInt(buffer[3]), Integer.parseInt(buffer[4]), Integer
							.parseInt(buffer[5]));

				} else {

					// 새 엔트리 만들기
					Entry entry = new Entry(buffer[0]);
					entry.putTexture(buffer[1], Integer.parseInt(buffer[2]), Integer
							.parseInt(buffer[3]), Integer.parseInt(buffer[4]), Integer
							.parseInt(buffer[5]));

					// 컨테이너에 엔트리를 추가
					container.put(buffer[0], entry);
				}

			}

			// 엔트리 출력을 위한 가공 변수 생성
			Iterator<String> iterator = container.keySet().iterator();
			Entry[] entries = new Entry[container.keySet().size()];
			int count = 0;

			// 컨테이너를 모두 읽어 배열에 쓴다.
			while (iterator.hasNext()) {
				String key = iterator.next();
				entries[count++] = container.get(key);
			}

			// 엔트리 출력
			return entries;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 가공된 자원 파일의 구성 요소입니다. 개개의 텍스처 파일로 이루어져 있습니다.
	 * 
	 */
	public static class Entry {

		public String textureFile;
		public String[] identifier;
		public int[][] vertex;
		public int index = 0;

		// 엔트리는 텍스처 파일 하나 당 생성된다.
		public Entry(String textureFile) {
			this.textureFile = textureFile;
			identifier = new String[100];
			vertex = new int[100][100];
		}

		// 텍스처 추가 메서드
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
