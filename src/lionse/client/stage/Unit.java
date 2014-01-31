package lionse.client.stage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Unit implements Renderable {

	// unit position
	public Point position;
	public int direction = 0;

	// unit type (npc, object, mob...)
	public int type;

	// texture cache
	public TextureRegion[] texture;

	public Unit(int type, TextureRegion[] graphic) {
		this.type = type;
		this.texture = graphic;
		position = new Point();
	}

	public static class Type {
		public static final int NPC = 0;
		public static final int OBJECT = 1;
		public static final int MOB = 2;
	}

}
