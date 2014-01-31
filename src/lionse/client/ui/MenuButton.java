package lionse.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class MenuButton extends Button {

	public Menu itemSlot;
	public boolean selected = false;

	public MenuButton(Menu itemSlot, ButtonStyle style) {

		super(style);
		this.itemSlot = itemSlot;

	}
}
