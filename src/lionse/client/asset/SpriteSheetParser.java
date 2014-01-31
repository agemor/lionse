package lionse.client.asset;

import java.io.IOException;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class SpriteSheetParser {

	public static void parseAndInsert(Map<String, TextureRegion> asset, String spriteSheetFile) {
		try {
			XmlReader xmlReader = new XmlReader();
			Element entry = xmlReader.parse(Gdx.files.internal(spriteSheetFile));
			String imagePath = entry.getAttribute("imagePath");
			Texture texture = new Texture(imagePath);

			for (int i = 0; i < entry.getChildCount(); i++) {
				Element element = entry.getChild(i);
				String name = element.getAttribute("name");
				int width = Integer.parseInt(element.getAttribute("width"));
				int height = Integer.parseInt(element.getAttribute("height"));
				int x = Integer.parseInt(element.getAttribute("x"));
				int y = Integer.parseInt(element.getAttribute("y"));

				asset.put(name, new TextureRegion(texture, x, y, width, height));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseAndInsert(Map<String, TextureRegion[]> asset, String spriteSheetFile, boolean split) {
		try {
			XmlReader xmlReader = new XmlReader();
			Element entry = xmlReader.parse(Gdx.files.internal(spriteSheetFile));
			String imagePath = entry.getAttribute("imagePath");
			Texture texture = new Texture(imagePath);

			for (int i = 0; i < entry.getChildCount(); i++) {
				Element element = entry.getChild(i);
				String name = element.getAttribute("name");
				int width = Integer.parseInt(element.getAttribute("width"));
				int height = Integer.parseInt(element.getAttribute("height"));
				int x = Integer.parseInt(element.getAttribute("x"));
				int y = Integer.parseInt(element.getAttribute("y"));

				asset.put(name, splitTexture(new TextureRegion(texture, x, y, width, height)));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
