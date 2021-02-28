package io.github.libgdx.cubegame2.levels;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import io.github.libgdx.cubegame2.blocks.Block;
import io.github.libgdx.cubegame2.blocks.Block.TYPE;
import io.github.libgdx.cubegame2.blocks.impl.CubeDef;
import io.github.libgdx.cubegame2.blocks.impl.GoalMesh;
import io.github.libgdx.cubegame2.blocks.impl.Lifeforce;
import io.github.libgdx.cubegame2.blocks.impl.MovingBlock;
import io.github.libgdx.cubegame2.blocks.impl.Renderable;
import io.github.libgdx.cubegame2.player.Player;
import io.github.libgdx.cubegame2.player.PlayerDirection;
import io.github.libgdx.cubegame2.utils.HeightOffsets;

public class Level1 implements Renderable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Level1.class);
	private Block[][][] field;
	
	public int xdim = 5;
	public int ydim = 7;
	public int zdim = 11;

	@Override
	public void render(ModelBatch modelBatch, Environment environment) {
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				for (int k = 0; k < zdim; k++) {
					if (field[i][j][k] != null) {
						field[i][j][k].render(modelBatch, environment);
					}
				}
			}
		}
	}
	
	public void createLevel() {
		float w = 0.95f;
		float h = HeightOffsets.floortileHeight;
		float d = 0.95f;
		
		Color color = Color.GREEN;

		field = new Block[xdim][ydim][zdim];
		for (int x=0; x<xdim; x++) {
			for (int y=1; y<2; y++) {
				for (int z=0; z<zdim; z++) {
					set(x, y, z, new Block(w, h, d, color));
				}
			}
		}
		for (int i=0; i<=xdim-1; i++) {
			field[i][1][5] = null;
			field[i][1][6] = null;
			field[i][1][7] = null;
		}
		
		field[0][1][7] = new MovingBlock(
			this, 
			new ModelInstance(new CubeDef(w,h,d).finialize(color)
			),
			color, 
			Arrays.asList(
					new Vector3(0, 1 ,7),
					new Vector3(0, 1, 5)
					),
			new Vector3(3, 5, 6),
			new Vector3(3, 5, 7)
		);
		field[1][1][8] = null;
		field[1][1][9] = null;
		
		field[1][1][10] = null;
		field[2][1][10] = null;
		field[3][1][10] = null;
		field[4][1][10] = null;
		
		field[0][1][10] = new MovingBlock(
				this, 
				new ModelInstance(new CubeDef(w,h,d).finialize(color)
				),
				color, 
				Arrays.asList(
						new Vector3(0, 1 ,10),
						new Vector3(2, 1, 10)
						),
				new Vector3(4, 5, 6),
				new Vector3(4, 5, 7)
			);
		
		Block red = new Block(new CubeDef(w,h,d).finialize(Color.RED), Color.RED, TYPE.COLORCHANGER);
		set(2, 1, 2, red);
		
		Block yellow = new Block(new CubeDef(w,h,d).finialize(Color.YELLOW), Color.YELLOW, TYPE.COLORCHANGER);
		set(3, 1, 9, yellow);
		
		Block blue = new Block(new CubeDef(w,h,d).finialize(Color.BLUE), Color.BLUE, TYPE.COLORCHANGER);
		set(1, 1, 2, blue);
		
		// moveable stone
		w -= 0.2f;
		d -= 0.2f;
		field[3][2][3] = new Lifeforce(new CubeDef(w,HeightOffsets.lifeforceHeight,d).finialize(Color.YELLOW), Color.YELLOW, 3, 2, 3);
		field[3][2][3].x = 3;
		field[3][2][3].y = 2;
		field[3][2][3].z = 3;
		field[3][2][3].setType(TYPE.MOVEABLE);
		LOGGER.info("moveable has id {}", field[3][2][3].getId());
		// moveable stone
		field[1][2][3] = new Lifeforce(new CubeDef(w,HeightOffsets.lifeforceHeight,d).finialize(Color.YELLOW), Color.YELLOW, 1, 2, 3);
		field[1][2][3].x = 1;
		field[1][2][3].y = 2;
		field[1][2][3].z = 3;
		field[1][2][3].setType(TYPE.MOVEABLE);
		LOGGER.info("moveable has id {}", field[1][2][3].getId());
		
		field[3][1][4] = new Block(new GoalMesh().main(Color.BLUE), Color.BLUE);
		field[3][1][4].x = 3;
		field[3][1][4].y = 1;
		field[3][1][4].z = 4;
		field[3][1][4].instance.transform.translate(3, 2f, 4);
		field[3][1][4].setType(TYPE.HOLE);
		LOGGER.info("goal has id {}", field[3][1][4].getId());
		
		field[3][1][5] = new Block(new GoalMesh().main(Color.RED), Color.RED);
		field[3][1][5].x = 3;
		field[3][1][5].y = 1;
		field[3][1][5].z = 5;
		field[3][1][5].instance.transform.translate(3, 2f, 5);
		field[3][1][5].setType(TYPE.HOLE);
		LOGGER.info("goal has id {}", field[3][1][5].getId());
							
		w = 0.95f;
		d = 0.95f;
		set(3, 1, 6, new Block(w, h, d, Color.GREEN));
	}
	
	public boolean isWithinBounds(Player player, PlayerDirection direction) {
		int x = player.x;
		int z = player.z;
		
		int targetX = x;
		int targetZ = z;
		
		switch (direction) {
			case FORWARD:
				targetX--;
				break;
			case BACK:
				targetX++;
				break;
			case LEFT:
				targetZ--;
				break;
			case RIGHT:
				targetZ++;
				break;
			default:
				break;
		}
		
		// Check level-bounds
		return targetX >= 0 && targetX < xdim && targetZ >=0 && targetZ < zdim;
	}

	public Optional<Block> get(int x, int y, int z) {
		return Optional.ofNullable(field[x][y][z]);
	}

	public Optional<Block> get(float x, float y, float z) {
		return Optional.ofNullable(field[(int)x][(int)y][(int)z]);
	}
	
	public void set(int x, int y, int z, Block block) {
		field[x][y][z] = block;
		if (block != null) {
			block.updateLevelPosition(x, y, z);
		}
	}
	
	public void setWithout(int x, int y, int z, Block block) {
		field[x][y][z] = block;
	}
	
	public void setWithout(float x, float y, float z, Block block) {
		field[(int)x][(int)y][(int)z] = block;
	}
	
	public void set(float x, float y, float z, Block block) {
		field[(int)x][(int)y][(int)z] = block;
		if (block != null) {
			block.updateLevelPosition((int)x, (int)y, (int)z);
		}
	}
	
	public void set(Vector3 v, Block block) {
		field[(int)v.x][(int)v.y][(int)v.z] = block;
		if (block != null) {
			block.updateLevelPosition((int)v.x, (int)v.y, (int)v.z);
		}
	}
}
