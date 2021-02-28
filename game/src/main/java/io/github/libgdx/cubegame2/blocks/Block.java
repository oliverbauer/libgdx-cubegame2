package io.github.libgdx.cubegame2.blocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import io.github.libgdx.cubegame2.blocks.impl.CubeDef;
import io.github.libgdx.cubegame2.levels.Level1;
import io.github.libgdx.cubegame2.particles.Particles;
import io.github.libgdx.cubegame2.player.Player;
import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.Materials;
import io.github.libgdx.cubegame2.utils.HeightOffsets;

public class Block implements Disposable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Block.class);
	
	public enum TYPE {
		GROUND,
		HOLE, // == GOAL
		MOVEABLE,
		COLORCHANGER
	}
	
	private TYPE type = TYPE.GROUND;
	
	private static int gloabalIdCounter = 10;
	
	// For logging/debugging purposes
	private int id;
	
	private Model model;
	public ModelInstance instance;

	public int x = -1;
	public int y = -1;
	public int z = -1;
	
	public Color color;
	
	public float yoffset = 0;
	
	public Block() {
		gloabalIdCounter++;
		id = gloabalIdCounter;
	}
	
	public Block(Model model, Color color) {
		this();
		
		yoffset = HeightOffsets.floortileHeight;
		
		this.model = model;
		this.instance = new ModelInstance(model);
		
		this.color = color;
	}
	
	public Block(float w, float h, float d, Color color) {
		this(new CubeDef(w, h, d).finialize(color), color);
	}
	
	
	public Block(Model model, Color color, TYPE type) {
		this(model, color);
		this.type = type;
	}
	
	public Block(ModelInstance mi, Color color, float yOffset) {
		this();
		
		this.yoffset = yOffset;
		this.instance = mi;
		if (this.instance != null)
			this.instance.transform.translate(0, yoffset, 0);
		
		this.color = color;
	}
	
	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getId() {
		return this.id;
	}
	
	public void setTranslation() {
		if (instance != null)
			instance.transform = new Matrix4().translate(x, y+yoffset, z);
	}
	
	public void translate() {
		instance.transform.translate(x, y, z);
	}
	
	public void setToTranslation() {
		instance.transform.setToTranslation(x, y, z);
	}
	
	public void setToTranslation(Vector3 t) {
		instance.transform.setToTranslation(t);
	}
	
	List<Particles> p = new ArrayList<>();
	public void blockMovedOnThis(PerspectiveCamera cam, Block block, Level1 level) {
		LOGGER.info("Block ("+block.id+") moved on my position ("+x+","+y+","+z+","+Colors.getColorname(color)+") (id="+id+")");
		
		if (block instanceof Player) {
			if (color != Color.GREEN) {
				Player player = ((Player)block);

//				p.add(new Particles(cam, player.x -0.5f, player.y - 4, player.z -0.5f, player.color));
				
				p.add(new Particles(cam, player.x -0.5f + 0, player.y, player.z -0.5f + 1, color));
				p.add(new Particles(cam, player.x -0.5f + 1, player.y, player.z -0.5f + 0, color));
				p.add(new Particles(cam, player.x -0.5f - 1, player.y, player.z -0.5f + 0, color));
				p.add(new Particles(cam, player.x -0.5f + 0 , player.y, player.z -0.5f -1, color));
				
				particleTime = 0;
						
				player.instance = player.create(color);
				player.updateLevelPosition(player.x, player.y, player.z);
				player.setTranslation();
				player.color = color;
			}
		}
	}
	
	public void setColor(Color red) {
		this.color = red;
		this.instance.materials.get(0).set(Materials.withBorder(Colors.from(color)));
		this.instance.transform.translate(0, yoffset, 0);
	}
	
	public void setInstance(ModelInstance model) {
		this.instance = model;
		this.instance.transform.translate(0, yoffset, 0);
	}
	
	public Vector3 getPosition() {
		return new Vector3(x, y, z);
	}
	
	public ModelInstance getInstance() {
		return instance;
	}

	float particleTime = 0;
	public void render(ModelBatch modelBatch, Environment environment) {
		if (!p.isEmpty()) {
			for (Particles part : p) {
				part.render(modelBatch, environment);
				
			}
			particleTime += Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
			if (particleTime > 0.3) {
				p.clear();
			}
		}
		modelBatch.render(instance, environment);
	}
	
	public void dispose() {
		try {
			model.dispose();
		} catch (Exception e) {
			
		}
	}

	@Override
	public String toString() {
		return "Block [x="	+ x + ", y=" + y + ", z=" + z + "]";
	}

	public void updateLevelPosition(int x2, int y2, int z2) {
		this.x = x2;
		this.y = y2;
		this.z = z2;
		setTranslation();
	}
	
	public void updateLevelPosition(float x2, float y2, float z2) {
		updateLevelPosition((int)x2, (int)y2, (int)z2);
	}
}
