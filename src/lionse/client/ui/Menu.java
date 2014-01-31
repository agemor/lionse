package lionse.client.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lionse.client.asset.Asset;
import lionse.client.stage.Point;

/**
 * 
 * 아이템 슬롯 랜더링 담당 UI객체
 * 
 * @author 김현준
 * 
 */
public class Menu extends WidgetGroup implements EventListener {

	public Sprite PANNEL;
	public MenuButton MY_INFO;
	public MenuButton UNIT;
	public MenuButton MATRIAL;
	public MenuButton ITEM;
	public MenuButton MENU;

	// 페이지 변수
	public MyInfoPage myInfoPage;

	// 위치 세팅 변수
	public boolean shrinked = true;
	public Point position_pop = new Point(0, 1, 0);
	public Point position_shrink = new Point(0, 481, 0);
	public Point position_target = position_shrink; // 레퍼런스 변수

	public Point position;

	public Menu() {

		position = new Point(position_shrink.x, position_shrink.y, 0);

		// 그래픽 초기화
		PANNEL = new Sprite(Asset.UI.get("MENU_PANNEL"));

		MY_INFO = new MenuButton(this, packStyle(Asset.UI.get("MENU_MY_INFO_BUTTON_UP"), Asset.UI.get("MENU_MY_INFO_BUTTON_DOWN")));
		UNIT = new MenuButton(this, packStyle(Asset.UI.get("MENU_UNIT_BUTTON_UP"), Asset.UI.get("MENU_UNIT_BUTTON_DOWN")));
		MATRIAL = new MenuButton(this, packStyle(Asset.UI.get("MENU_MATRIAL_BUTTON_UP"), Asset.UI.get("MENU_MATRIAL_BUTTON_DOWN")));
		ITEM = new MenuButton(this, packStyle(Asset.UI.get("MENU_ITEM_BUTTON_UP"), Asset.UI.get("MENU_ITEM_BUTTON_DOWN")));
		MENU = new MenuButton(this, packStyle(Asset.UI.get("MENU_MENU_BUTTON_UP"), Asset.UI.get("MENU_MENU_BUTTON_DOWN")));

		// 페이지 초기화
		myInfoPage = new MyInfoPage(this);

		// 그래픽 세팅
		PANNEL.setSize(701, 480);

		this.addActor(MY_INFO);
		this.addActor(UNIT);
		this.addActor(MATRIAL);
		this.addActor(ITEM);
		this.addActor(MENU);

	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		PANNEL.draw(spriteBatch);
		MY_INFO.draw(spriteBatch, 1);
		UNIT.draw(spriteBatch, 1);
		MATRIAL.draw(spriteBatch, 1);
		ITEM.draw(spriteBatch, 1);
		MENU.draw(spriteBatch, 1);

		myInfoPage.draw(spriteBatch, delta);
	}

	private static final int padding = 25;
	private static final int space = 10;

	public void update(float delta) {

		position.x += 0.3f * (position_target.x - position.x);
		position.y += 0.3f * (position_target.y - position.y);

		PANNEL.setPosition(0, position.y);
		MY_INFO.setPosition(padding + 120 * 0 + position.x + space * 0, 420 + position.y);
		UNIT.setPosition(padding + 120 * 1 + position.x + space * 1, 420 + position.y);
		MATRIAL.setPosition(padding + 120 * 2 + position.x + space * 2, 420 + position.y);
		ITEM.setPosition(padding + 120 * 3 + position.x + space * 3, 420 + position.y);
		MENU.setPosition(padding + 120 * 4 + position.x + space * 4, 420 + position.y);

		myInfoPage.update(delta);
	}

	public void shrink() {
		shrinked = true;
		position_target = position_shrink;
	}

	public void pop() {
		shrinked = false;
		position_target = position_pop;
	}

	public ButtonStyle packStyle(TextureRegion up, TextureRegion down) {
		return new ButtonStyle(new TextureRegionDrawable(up), new TextureRegionDrawable(down), new TextureRegionDrawable(down));
	}

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ChangeEvent))
			return false;

		return false;
	}

}
