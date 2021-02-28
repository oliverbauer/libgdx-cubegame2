package io.github.libgdx.cubegame2;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import io.github.libgdx.cubegame2.blocks.impl.Tunnel;
import io.github.libgdx.cubegame2.blocks.impl.Tree;
import io.github.libgdx.cubegame2.blocks.impl.Renderable;
import io.github.libgdx.cubegame2.control.Keyboard;
import io.github.libgdx.cubegame2.levels.Level1;
import io.github.libgdx.cubegame2.player.Player;

public class CubeGame2 extends ApplicationAdapter {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1920;
		config.height = 1080;
		
		config.width = 1280;
		config.height = 768;
		
		new LwjglApplication(new CubeGame2(), config);
	}

	private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;

	private Player player;
	private Keyboard keyboard;
	private Level1 field;
	
	private List<Renderable> renderables;
	
	@Override
	public void create() {
		environment = new Environment();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.set(
			new Color(1.0f, 0.8f, 0.8f, 0.7f), 
			-1f, 
			-2.8f,
			-0.5f
		);
		environment.add(directionalLight);
		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(-3f, 7f, -3f);
		cam.lookAt(0, 0, 0);
		cam.update();

		createPlayer(0, 2, 0, Color.MAGENTA);
		
		field = new Level1();
		field.createLevel();
		field.set(player.x, player.y, player.z, player);
		
		keyboard = new Keyboard(field, player);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(keyboard);
		inputMultiplexer.addProcessor(new CameraInputController(cam));
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		renderables = new ArrayList<>();
		renderables.add(new Tree(4.5f, 0f, -0.5f));
		renderables.add(new Tree(4.5f, 0f,  0.75f));
		renderables.add(new Tree(4.5f, 0f,  2f));
		renderables.add(new Tree(4.5f, 0f,  3.25f));
		renderables.add(new Tree(4.5f, 0f,  4.5f));
		
		renderables.add(new Tunnel(2f,   1, -0.6f));
		renderables.add(new Tunnel(4.5f, 1f, -0.6f));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 0f); // Background r,g,b,a... without: black
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		
		field.render(modelBatch, environment);
		
		player.act(field, cam, keyboard.getNextDirection());
		
		modelBatch.render(player.instance, environment);

		for (Renderable r : renderables) {
			r.render(modelBatch, environment);
		}
		
		modelBatch.end();
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
	}
	
	private void createPlayer(int x, int y, int z, Color color) {
		player = new Player(null, color);
		player.setInstance(player.create(color));
		player.updateLevelPosition(x,y,z);
		player.setColor(color);
		player.setTranslation();
	}
}
