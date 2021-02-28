package io.github.libgdx.cubegame2.blocks.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import io.github.libgdx.cubegame2.blocks.Block;
import io.github.libgdx.cubegame2.levels.Level1;
import io.github.libgdx.cubegame2.player.Player;
import io.github.libgdx.cubegame2.utils.HeightOffsets;

public class MovingBlock extends Block {
	private static final Logger LOGGER = LoggerFactory.getLogger(MovingBlock.class);
	
	private Level1 level;
	
	public MovingBlock(Level1 level, ModelInstance model, Color color, List<Vector3> vectors, Vector3 tempPlayer, Vector3 tempBlock) {
		super(model, color, HeightOffsets.floortileHeight);
		
		setType(TYPE.MOVEABLE);
		
		if (vectors.size() != 2) {
			throw new RuntimeException("Requires size = 2");
		}
		
		this.level = level;
		this.vectors = vectors;
		
		start = this.vectors.get(0);
		end = this.vectors.get(1);
		
		tempBlockPos = tempBlock;
		tempPlayerPos = tempPlayer;
		
		currentPosition = start;
		wait = true;
		
		Vector3 s = new Vector3();
		s.x = start.x;
		s.y = start.y + yoffset;
		s.z = start.z;
		instance.transform.setToTranslation(s);
	}
	

	private float alpha = 0;
	private float speed = 1f;
	private Vector3 start;
	private Vector3 tempBlockPos;
	private Vector3 tempPlayerPos;
	
	private Vector3 end;
	
	private List<Vector3> vectors = new ArrayList<>();
	
	private boolean wait = false;
	private float waitTime = 0;
	
	/**
	 * Only if stopped
	 */
	private Vector3 currentPosition;
	
	String message = "";
	
	@Override
	public void render(ModelBatch modelBatch, Environment environment) {
		final float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
		alpha += delta * speed;
		
		if (wait) {
			waitTime += delta;
			if (waitTime > 2) {
				/*
				 * Player will be set on a temp position where no movement is possible.
				 */
				prepareNextMoveIteration();
			} else {
				/*
				 * Player will be set on the position of this block (one layer above of course)
				 * where the could move on. 
				 */
				waitForPossiblePlayerMovement(modelBatch, environment);
				return;
			}
		}
		
		currentPosition = null;
		
		if (alpha > 1) {
			/*
			 * There was a full start->end translation. Now we wait for 2 seconds (see above)
			 * and set the new start/end position.
			 */
			prepareNextWaitCycle();
			return;
		}

		level.set(start, null);
		level.set(end, null);
		level.set(tempBlockPos, this);
		
		Vector3 tmpV = new Vector3();
			
		Vector3 s = new Vector3();
		s.x = start.x;
		s.y = start.y + yoffset;
		s.z = start.z;
		Vector3 e = new Vector3();
		e.x = end.x;
		e.y = end.y + yoffset;
		e.z = end.z;
		tmpV.set(s).lerp(e, alpha);
		
		instance.transform.setToTranslation(tmpV);
		
		Optional<Block> player = getPlayer(tempPlayerPos);
		if (player.isPresent()) {
			tmpV.y = tmpV.y + 1 + HeightOffsets.playerHeightOffset - HeightOffsets.floortileHeight;
			((Player)player.get()).setToTranslation(tmpV);
		}
		
		modelBatch.render(instance, environment);
	}

	private void prepareNextWaitCycle() {
		alpha = 0; // repeat...
		
		Optional<Block> player = getPlayer(tempPlayerPos);
		
		if (start == this.vectors.get(0)) {
			currentPosition = vectors.get(1);
			end = vectors.get(0);
		} else if (start == vectors.get(1)) {
			currentPosition = vectors.get(0);
			end = vectors.get(1);
		} else {
			throw new RuntimeException("Should be first or last...");
		}
		start = currentPosition;
		
		
		level.set(start.x, start.y + 1, start.z, null);// maybe old player positonn, maybe null
		level.set(end, null);
		level.set(end.x, end.y + 1, end.z, null);// maybe old player positonn, maybe null
		
		level.set(tempBlockPos, null);
		if (player.isPresent()) {
			((Player)player.get()).setAllowedToMove(true); // for the next 2 seconds...
			level.set(start.x, start.y + 1, start.z, player.get());
		}
		level.set(tempPlayerPos, null);
		level.set(start, this);
		
		wait = true;
		
		
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Animation completed");
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (Vector3 v : this.vectors) {
				sb.append("("+(int)v.x+","+(int)v.y+","+(int)v.z+") ");
				
				Optional<Block> optional = level.get(v.x, v.y, v.z);
				
				if (!optional.isPresent()) {
					sb2.append("------- ");
				} else {
					sb2.append(optional.get().getId()+"     ");
				}
			}
			LOGGER.debug(sb.toString());
			LOGGER.debug(sb2.toString());
			if (player.isPresent()) {
				Block block = player.get();
				LOGGER.debug("("+block.x+","+block.y+","+block.z+") "+"( player position)");
			}
		}
	}

	private void waitForPossiblePlayerMovement(ModelBatch modelBatch, Environment environment) {
		level.set(tempBlockPos, null);

		Vector3 aboveCurrent = new Vector3(currentPosition);
		aboveCurrent.y += 1;
		Optional<Block> player = getPlayer(aboveCurrent);
		if (player.isPresent()) {
			String msg = "---- wait + wait<2 + player is present on current position "+aboveCurrent+" set to bee allowed to move";
			if (!message.equals(msg)) {
				LOGGER.debug(msg);
			}
			message = msg;
			
			((Player)player.get()).setAllowedToMove(true);
		} else {
			String msg = "---- wait + wait<2 + player is NOT present on current position "+aboveCurrent;
			if (!message.equals(msg)) {
				LOGGER.debug(msg);
			}
			message = msg;
		}
		
		modelBatch.render(instance, environment);
	}

	private void prepareNextMoveIteration() {
		waitTime = 0;
		alpha = 0;
		wait = false;
		
		// Is player on me?
		Vector3 aboveCurrent = new Vector3(currentPosition);
		aboveCurrent.y += 1;
		Optional<Block> player = getPlayer(aboveCurrent);
		if (player.isPresent()) {
			
			String msg = "---- wait + wait>2 + player is present on current position "+aboveCurrent+" set to NOT beeing allowed to move";
			if (!message.equals(msg)) {
				LOGGER.debug(msg);
			}
			message = msg;
			
			// Not allowed to move, while this block moves
			((Player)player.get()).setAllowedToMove(false);
			level.set(tempPlayerPos, player.get());
		} else {
			String msg = "---- wait + wait>2 + player is NOT present on current position "+aboveCurrent;
			if (!message.equals(msg)) {
				LOGGER.debug(msg);
			}
			message = msg;
		}
	}
	
	private Optional<Block> getPlayer(Vector3 pos) {
		Optional<Block> optional = level.get(pos.x, pos.y, pos.z);
		if (optional.isPresent() && optional.get() instanceof Player) {
			return optional;
		}
		return Optional.ofNullable(null);
	}
}
