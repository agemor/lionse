package lionse.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField.OnscreenKeyboard;

public class VirtualKeyboardHolder implements OnscreenKeyboard {

	@Override
	public void show(boolean visible) {
		if (visible) {
			VirtualKeyboard.show();
		} else {
			VirtualKeyboard.close();
		}
	}

}
