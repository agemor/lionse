package com.lionse.stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lionse.Display;
import com.lionse.asset.Asset;

public class Stage implements Renderable {

	// renderer variables
	public static float SCALE_Z = (float) Math.sqrt(2);
	public Camera camera;
	public Point smoothCamera;

	// stage is always square
	public int size;

	// list values
	public List<Terrain> terrain;
	public List<Unit> units;
	public List<Character> characters;
	public List<Renderable> renderables;

	// game values
	public Character me; // it's me!

	// when you add a character or unit, you must add the object to the
	// renderables list.

	// comparator variable
	private Comparator<Renderable> renderablesComparator;

	public Stage() {

		// initialize camera
		camera = new OrthographicCamera(Display.WIDTH, Display.HEIGHT);
		camera.position.set(Display.WIDTH / 2, Display.HEIGHT / 2, 0.4f);
		smoothCamera = new Point(Display.WIDTH / 2, Display.HEIGHT / 2, 0);

		// initialize data collection
		terrain = new ArrayList<Terrain>();
		units = new ArrayList<Unit>();
		characters = new ArrayList<Character>();
		renderables = new ArrayList<Renderable>();

		// initialize comparator
		renderablesComparator = new RenderablesComparator();
	}

	// load stages. (stage size, terrain, units, npcs...)
	public void load(StageEntry stageEntry) {

		// set starting positions.
		int startingX = Terrain.WIDTH / 2 * stageEntry.getSize();
		int startingY = Terrain.HEIGHT / 2;

		for (int i = 0; i < stageEntry.getSize(); i++) {

			// update to next point.
			startingX -= Terrain.WIDTH / 2;
			startingY += Terrain.HEIGHT / 2;

			for (int j = 0; j < stageEntry.getSize(); j++) {

				// set coordinations.
				int x = startingX + (Terrain.WIDTH / 2 * j);
				int y = startingY + (Terrain.HEIGHT / 2 * j);

				// set terrain.
				Terrain grid = new Terrain(x, y, stageEntry.terrainAltitude[i][j], new Terrain.Index(j, i), null);

				// determine terrain graphics and altitude.
				if (stageEntry.terrainType[i][j] == 0) {
					grid.setTexture(Asset.game.get(Asset.Game.get("TERRAIN"))[0]);
				}

				// add terrain.
				terrain.add(grid);
			}
		}
	}

	public Terrain getGrid(Point point) {
		float dx = -(terrain.get(0).x - (Terrain.WIDTH / 2));
		float dy = -terrain.get(0).y;

		float px = Terrain.RADIAN_45 * (point.x + (2 * point.y) + dx + (2 * dy));
		float py = Terrain.RADIAN_45 * (-point.x + (2 * point.y) + (-dx) + (2 * dy)) + (90 * 1.5f);

		float gridSize = Terrain.HEIGHT / Terrain.RADIAN_45;
		int tx = (int) Math.floor(px / gridSize);
		int ty = (int) Math.floor(py / gridSize);

		return terrain.get((int) (ty * Math.sqrt(terrain.size()) + tx));
	}

	// render stage
	public void draw(SpriteBatch spriteBatch, float delta) {

		// update camera
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		// render terrain
		for (int i = 0; i < terrain.size(); i++) {
			Terrain piece = terrain.get(i);
			spriteBatch.draw(piece.getTexture(), piece.x, Display.HEIGHT - piece.y + piece.z * SCALE_Z);
		}

		// render units and characters.

		// first, sort renderable objects by their y coordinate.
		Collections.sort(renderables, renderablesComparator);

		for (int i = 0; i < renderables.size(); i++) {
			renderables.get(i).draw(spriteBatch, delta);
		}

	}

	@Override
	public void update(float delta) {

		// set camera position (character is located in the center.)
		smoothCamera.x += (0.05f * (me.position.x - smoothCamera.x));
		smoothCamera.y += (0.05f * ((Display.HEIGHT - me.position.y) - smoothCamera.y));

		camera.position.x = Math.round(smoothCamera.x);
		camera.position.y = Math.round(smoothCamera.y);

		// update all characters.
		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).update(delta);
		}

		// update all units in the field.
		for (int i = 0; i < units.size(); i++) {
			units.get(i).update(delta);
		}
	}

	@Override
	public Point getPosition() {
		return null;
	}

	public class RenderablesComparator implements Comparator<Renderable> {
		@Override
		public int compare(Renderable r1, Renderable r2) {
			return (int) (r1.getPosition().y - r2.getPosition().y);
		}

	}

	// define new structure class.
	// point class will be used to sorting objects and holding coordinates.
	public static class Point {

		public float x;
		public float y;
		public float z;

		// constructor
		public Point(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Point() {
			x = y = z = 0;
		}

		@Override
		public String toString() {
			return x + "/" + y + "/" + z;
		}
	}

}
