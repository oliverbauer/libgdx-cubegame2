package io.github.libgdx.cubegame2.player;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import io.github.libgdx.cubegame2.blocks.Block;
import io.github.libgdx.cubegame2.blocks.impl.CubeDef;
import io.github.libgdx.cubegame2.levels.Level1;
import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.HeightOffsets;
import io.github.libgdx.cubegame2.utils.Materials;
import io.github.libgdx.cubegame2.utils.Textures;

public class Player extends Block {
	private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

	private boolean allowedToMove = true;

	private int nextPlayerX = -1;
	private int nextPlayerY = -1;
	private int nextPlayerZ = -1;

	private Vector3 axis;
	
	private float alpha = 0;
	private float cubeRotationSpeed = 3f;
	private float toAngle = 90;
	private boolean moving = false;
	
	private PlayerDirection direction = PlayerDirection.NONE;

	private Block connectedBlock = null;
	
	public Player(ModelInstance mi, Color color) {
		super(mi, color, HeightOffsets.playerHeightOffset);
	}
	
	public ModelInstance create(Color c2) {
		super.color = c2;

		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		float d = 0.5f;
		int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
		
		Material m = Materials.withBorder(Colors.from(super.color));
		// TODO Optimize....
		builder.part("front", GL20.GL_TRIANGLES, attr, m).rect(-d, -d, -d, -d, d, -d, d, d, -d, d, -d, -d, 0, 0, -1);
		builder.part("back", GL20.GL_TRIANGLES, attr, m).rect(-d, d, d, -d, -d, d, d, -d, d, d, d, d, 0, 0, 1);
		builder.part("bottom", GL20.GL_TRIANGLES, attr, m).rect(-d, -d, d, -d, -d, -d, d, -d, -d, d, -d, d,	0, -1, 0);
		builder.part("top", GL20.GL_TRIANGLES, attr, m).rect(-d , d, -d, -d , d, d, d , d, d, d , d, -d, 0,	1, 0);
		builder.part("left", GL20.GL_TRIANGLES, attr, m).rect(-d, -d, d,-d, d, d,-d, d, -d,	-d, -d, -d,-1, 	0, 0);
		builder.part("right", GL20.GL_TRIANGLES, attr, m).rect(	d, -d, -d,	d, d, -d, d, d, d,d, -d, d,1, 0, 0);
		
		if (isConnected()) {
			Material material = Materials.withBorder(Colors.from(Color.BLACK));
			MeshPartBuilder mpb = builder.part("box", GL20.GL_TRIANGLES, attr, material);
	
			float width = 0.15f;
			float depth = 0.75f;
			foo(-0.575f,0f,0,width,1f,depth, mpb);
			foo(+0.575f,0f,0,width,1f,depth, mpb);
			
			foo(0,0f,-0.575f,0.75f,1f,0.1f, mpb);
			foo(0,0f,+0.575f,0.75f,1f,0.1f, mpb);
			
			connectedBlock.setColor(super.color);
		}
		
		TextureAttribute textureAttribute = TextureAttribute.createDiffuse(Textures.createTexture(Colors.from(super.color), true));
		ModelInstance mi = new ModelInstance(builder.end());
		mi.materials.get(0).set(textureAttribute);
		
		return mi;
	}
	
	public void act(Level1 level, PerspectiveCamera cam, PlayerDirection nextDirection) {
		if (direction == PlayerDirection.NONE && nextDirection == PlayerDirection.NONE) {
			nextPlayerX = -1;
			nextPlayerY = -1;
			nextPlayerZ = -1;
			
		} else if (direction == PlayerDirection.NONE) {
			moving = true;
			direction = nextDirection;

			nextPlayerX = x;
			nextPlayerY = y;
			nextPlayerZ = z;
			
			if (direction == PlayerDirection.RIGHT) {
				nextPlayerZ++;
				toAngle = + 90;
				axis = Vector3.X;
			} else if (direction == PlayerDirection.FORWARD) {
				nextPlayerX--;
				toAngle = + 90;
				axis = Vector3.Z;
			} else if (direction == PlayerDirection.LEFT) {
				nextPlayerZ--;
				toAngle = - 90;
				axis = Vector3.X;
			} else if (direction == PlayerDirection.BACK) {
				nextPlayerX++;
				toAngle = - 90;
				axis = Vector3.Z;
			}
			
			// disable rotation when connected
			if (isConnected()) {
				axis = null;
			}
			
			alpha = 0;
		} else {
			if (moving) {
				boolean temp = level.isWithinBounds(this, direction);

				if (!temp) {
					moving = false;
					direction = PlayerDirection.NONE;
					return;
				}
				
				final float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
				alpha += delta * cubeRotationSpeed;
				float angle = alpha * toAngle;
				Vector3 tmpV = new Vector3();

				Vector3 currentPos  = new Vector3(x, y + yoffset, z);
				Vector3 newPosition = new Vector3(nextPlayerX, nextPlayerY + yoffset, nextPlayerZ);
				
				tmpV.set(currentPos).lerp(newPosition, alpha);

				if (axis != null) {
					instance.transform.setToRotation(axis, angle);
				}
				
				instance.transform.setTranslation(tmpV);

				boolean stop = alpha > 1;
				
				if (isConnected() && !stop) {
					currentPos  = new Vector3(connectedBlock.x, connectedBlock.y + connectedBlock.yoffset, connectedBlock.z);
					newPosition = new Vector3(connectedBlock.x, connectedBlock.y + connectedBlock.yoffset, connectedBlock.z);
					if (direction == PlayerDirection.RIGHT) {
						newPosition.add(0, 0, 1);
					} else if (direction == PlayerDirection.FORWARD) {
						newPosition.add(-1, 0, 0);
					} else if (direction == PlayerDirection.LEFT) {
						newPosition.add(0, 0, -1);
					} else if (direction == PlayerDirection.BACK) {
						newPosition.add(1, 0, 0);
					}	
					
					tmpV.set(currentPos).lerp(newPosition, alpha);

					connectedBlock.instance.transform.setTranslation(tmpV);
				}
				
				if (stop) {
					moving = false;
					
					level.set(x, y, z, null);
					
					updateLevelPosition(nextPlayerX, nextPlayerY, nextPlayerZ);
					setTranslation();
					
					level.set(nextPlayerX, nextPlayerY, nextPlayerZ, this);
					
					if (!level.get(nextPlayerX, nextPlayerY - 1, nextPlayerZ).isPresent()) {
						LOGGER.error("Moved to fast... this needs to be fixed!!!");
					}
					
					/* Color change */
					level.get(nextPlayerX, nextPlayerY - 1, nextPlayerZ).get().blockMovedOnThis(cam, this, level);
					
					if (isConnected()) {
						currentPos  = new Vector3(connectedBlock.x, connectedBlock.y, connectedBlock.z);
						newPosition = new Vector3(connectedBlock.x, connectedBlock.y, connectedBlock.z);
						if (direction == PlayerDirection.RIGHT) {
							newPosition.add(0, 0, 1);
						} else if (direction == PlayerDirection.FORWARD) {
							newPosition.add(-1, 0, 0);
						} else if (direction == PlayerDirection.LEFT) {
							newPosition.add(0, 0, -1);
						} else if (direction == PlayerDirection.BACK) {
							newPosition.add(1, 0, 0);
						}	
						
						level.set(currentPos.x, currentPos.y, currentPos.z, null);
						level.set(newPosition.x, newPosition.y, newPosition.z, connectedBlock);
						
						LOGGER.info("Connected block set on ({},{},{})", connectedBlock.x, connectedBlock.y, connectedBlock.z);
					}
					
					direction = PlayerDirection.NONE;
				}
			}
		}
	}
	
	// TODO Optimize... maybe use CubeDef-class
	Vector3 corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111;
	private void foo(float x, float y, float z, float width, float height, float depth, MeshPartBuilder mpb) {
		// manual of the last
		final float hw = width * 0.5f;
		final float hh = height * 0.5f;
		final float hd = depth * 0.5f;
		final float x0 = x - hw, y0 = y - hh, z0 = z - hd, x1 = x + hw, y1 = y + hh, z1 = z + hd;
		
		corner000 = new Vector3().set(x0, y0, z0);
		corner010 = new Vector3().set(x0, y1, z0);
		corner100 = new Vector3().set(x1, y0, z0);
		corner110 = new Vector3().set(x1, y1, z0);
		corner001 = new Vector3().set(x0, y0, z1);
		corner011 = new Vector3().set(x0, y1, z1);
		corner101 = new Vector3().set(x1, y0, z1);
		corner111 = new Vector3().set(x1, y1, z1);
		
		
		Vector3 tmpV1 = new Vector3();
		Vector3 tmpV2 = new Vector3();

		mpb.ensureVertices(24);
		mpb.ensureRectangleIndices(6);

		float normalm = 0.5f;

		mpb.setColor(Color.ORANGE);
		Vector3 nor = tmpV1.set(corner000).lerp(corner110, normalm).sub(tmpV2.set(corner001).lerp(corner111, normalm)).nor();
		mpb.rect(corner000, corner010, corner110, corner100, nor);
		
		mpb.setColor(Color.ORANGE);
		mpb.rect(corner011, corner001, corner101, corner111, nor.scl(-1));
		nor = tmpV1.set(corner000).lerp(corner101, normalm).sub(tmpV2.set(corner010).lerp(corner111, normalm)).nor();
		
		mpb.setColor(Color.ORANGE);
		mpb.rect(corner001, corner000, corner100, corner101, nor);

		mpb.setColor(Color.ORANGE);
		mpb.rect(corner010, corner011, corner111, corner110, nor.scl(-1));
		nor = tmpV1.set(corner000).lerp(corner011, normalm).sub(tmpV2.set(corner100).lerp(corner111, normalm)).nor();

		mpb.setColor(Color.ORANGE);
		mpb.rect(corner001, corner011, corner010, corner000, nor);

		mpb.setColor(Color.ORANGE);
		mpb.rect(corner100, corner110, corner111, corner101, nor.scl(-1));
	}
	
	public void connectTo(Block adjacentMovableBlock) {
		connectedBlock = adjacentMovableBlock;
		instance = create(super.color);
		updateLevelPosition(x, y, z);
		setTranslation();
	}

	public boolean isConnected() {
		return connectedBlock != null;
	}
	
	public Block getConnectedBlock() {
		return connectedBlock;
	}

	public void disconnect(Level1 level) {
		if (isConnected()) {
			int x = connectedBlock.x;
			int y = connectedBlock.y;
			int z = connectedBlock.z;
			Optional<Block> ground = level.get(x, y-1, z);
			if (ground.isPresent()) {
				Block block = ground.get();
				if (block.getType() == TYPE.HOLE) {
					
					level.set(x, y, z, null); // remove lifeforce from level
					
					float width = 0.95f;
					float depth = 0.95f;
					Block b = new Block(new CubeDef(width,HeightOffsets.floortileHeight,depth).finialize(Color.GREEN), Color.GREEN);
					b.x = x;
					b.y = y-1;
					b.z = z;
					
					level.set(x, y-1, z, b);
				}
			}
		}
		
		connectedBlock = null;
		instance = create(super.color);
		updateLevelPosition(x, y, z);
	}

	public boolean allowedToMove() {
		return allowedToMove;
	}
	
	public void setAllowedToMove(boolean b) {
		this.allowedToMove = b;
	}
}
