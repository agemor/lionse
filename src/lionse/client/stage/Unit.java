package lionse.client.stage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lionse.client.asset.Asset;
import lionse.client.stage.Stage.Point;

public abstract class Unit implements Renderable {

	// unit position
	public Point position;
	public int direction = 0;

	// unit type (npc, object, mob...)
	public int type;

	// texture cache
	public TextureRegion[] texture;

	public Unit(int type, int graphic) {
		this.type = type;
		this.texture = Asset.game.get(graphic);
		position = new Point();
	}

	public static class Type {
		public static final int NPC = 0;
		public static final int OBJECT = 1;
		public static final int MOB = 2;
	}

}
