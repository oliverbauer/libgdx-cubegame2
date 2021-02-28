package io.github.libgdx.cubegame2.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;

import io.github.libgdx.cubegame2.blocks.Block;
import io.github.libgdx.cubegame2.blocks.Block.TYPE;
import io.github.libgdx.cubegame2.levels.Level1;
import io.github.libgdx.cubegame2.player.Player;
import io.github.libgdx.cubegame2.player.PlayerDirection;
import io.github.libgdx.cubegame2.utils.Colors;

public class Keyboard extends InputAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Keyboard.class);
	
	private PlayerDirection nextDir = PlayerDirection.NONE;
	private Level1 level;
	
	private boolean lastKeyAllowed = false;
	
	private Player player;
	
	public Keyboard(Level1 g, Player player) {
		this.level = g;
		this.player = player;
	}
	
	private String getKey(int keycode) {
		if (keycode == 51)
			return "W";
		if (keycode == 47)
			return "S";
		if (keycode == 29)
			return "A";
		if (keycode == 32)
			return "D";
		
		// Connect/Disconnect from/to moveable block
		if (keycode == 31)
			return "C";
		
		return "!"+keycode;
	}
	
	@Override
	public boolean keyDown(final int keycode) {
		// if player stands on a moveable ground and ground is moving (i.e. not connected to other ground-entities)
		if (!player.allowedToMove()) {
			nextDir = PlayerDirection.NONE;
			lastKeyAllowed = false;
			return false;
		}
		
		List<String> rows = new ArrayList<>();
		for (int x = 0; x<level.xdim; x++) {
			StringBuilder sb = new StringBuilder();
			for (int z = 0; z<level.zdim; z++) {
				Optional<Block> block = level.get(x, 2, z);
				
				if (block.isPresent()) {
					// player or movable block
					sb.append(block.get().getId()+" ");
				} else {
					block = level.get(x, 1, z);
					if (block.isPresent()) {
						sb.append(block.get().getId()+" ");
					} else {
						sb.append("-- ");
					}
				}
			}
			rows.add(sb.toString());
		}
		for (int i=rows.size()-1; i>=0; i--) {
			LOGGER.info(rows.get(i));
		}
		
		String key = getKey(keycode);
		if (!key.startsWith("!")) {
			LOGGER.info("-------------------------");
			LOGGER.info("keyDown "+getKey(keycode)+". Player is on position ("+player.x+","+player.y+","+player.z+")");
			if (player.isConnected()) {
				LOGGER.info("  connected ({},{},{})", player.getConnectedBlock().x,player.getConnectedBlock().y,player.getConnectedBlock().z);
			}
			// 51 = W, 47 = S, 29 = A, 32 = D
		}
		
		if (keycode == Input.Keys.W) {
			lastKeyAllowed = allowed(player, PlayerDirection.BACK);
		
			LOGGER.info("back allowed? {}", lastKeyAllowed);
			
			if (lastKeyAllowed) {
				nextDir = PlayerDirection.BACK;
			} else {
				nextDir = PlayerDirection.NONE;
			}
		} else if (keycode == Input.Keys.S) {
			lastKeyAllowed = allowed(player, PlayerDirection.FORWARD);
			
			LOGGER.info("forward allowed? {}", lastKeyAllowed);
						
			if (lastKeyAllowed) {
				nextDir = PlayerDirection.FORWARD;
			} else {
				nextDir = PlayerDirection.NONE;
			}
		}
		if (keycode == Input.Keys.A) {
			lastKeyAllowed = allowed(player, PlayerDirection.LEFT);
		
			LOGGER.info("left allowed? {}", lastKeyAllowed);
			
			if (lastKeyAllowed) {
				nextDir = PlayerDirection.LEFT;
			} else {
				nextDir = PlayerDirection.NONE;
			}
		}
		if (keycode == Input.Keys.D) {
			lastKeyAllowed = allowed(player, PlayerDirection.RIGHT);
			
			LOGGER.info("right allowed? {}", lastKeyAllowed);
			
			if (lastKeyAllowed) {
				nextDir = PlayerDirection.RIGHT;
			} else {
				nextDir = PlayerDirection.NONE;
			}
		}
		
		if (keycode == Input.Keys.C) {
			if (player.isConnected()) {
				player.disconnect(level);
				return false;
			}
			
			int x = player.x;
			int y = player.y;
			int z = player.z;
			
			int targetX;
			int targetZ;
			
			Optional<Block> adjacentMovableBlock = null;
			
			// left:
			targetX = x-1;
			targetZ = z;
			if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
				Optional<Block> left = level.get(x-1, y, z);
				if (left.isPresent() && left.get().getType() == TYPE.MOVEABLE) {
					adjacentMovableBlock = left;
				}
			}
			
			// right:
			targetX = x+1;
			targetZ = z;
			if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
				Optional<Block> right = level.get(x+1, y, z);
				if (right.isPresent() && right.get().getType() == TYPE.MOVEABLE) {
					adjacentMovableBlock = right;
				}
			}
			
			// front:
			targetX = x;
			targetZ = z+1;
			if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
				Optional<Block> front = level.get(x, y, z+1);
				if (front.isPresent() && front.get().getType() == TYPE.MOVEABLE) {
					adjacentMovableBlock = front;
				}
			}
			
			// back:
			targetX = x;
			targetZ = z-1;
			if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
				Optional<Block> back = level.get(x, y, z-1);
				if (back.isPresent() && back.get().getType() == TYPE.MOVEABLE) {
					adjacentMovableBlock = back;
				}
			}
			
			if (adjacentMovableBlock != null && adjacentMovableBlock.isPresent()) {
				LOGGER.info("connect/disconnect allowed? true: {}", adjacentMovableBlock);
				
				if (adjacentMovableBlock.get().color.equals(player.color)) {
					LOGGER.info("connect/disconnect allowed? true: {} with same color", adjacentMovableBlock);
					lastKeyAllowed = true;
					
					player.connectTo(adjacentMovableBlock.get());
				} else {
					LOGGER.info("connect/disconnect allowed? false: {} with different colors {} - {}", adjacentMovableBlock, 
						Colors.getColorname(adjacentMovableBlock.get().color), 
						Colors.getColorname(player.color));
					lastKeyAllowed = false;
				}
				
				nextDir = PlayerDirection.NONE;
			} else {
				lastKeyAllowed = false;
				LOGGER.info("connect/disconnect allowed? false - no movable block on 4 adjcanet tiles");
				nextDir = PlayerDirection.NONE;
			}
			
		}
		
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		if (!lastKeyAllowed) {
			return false;
		}
		
		if (keycode == Input.Keys.W && nextDir == PlayerDirection.BACK) {
			nextDir = PlayerDirection.NONE;
		} else if (keycode == Input.Keys.S && nextDir == PlayerDirection.FORWARD) {
			nextDir = PlayerDirection.NONE;
		}
		if (keycode == Input.Keys.A && nextDir == PlayerDirection.LEFT) {
			nextDir = PlayerDirection.NONE;
		}
		if (keycode == Input.Keys.D && nextDir == PlayerDirection.RIGHT) {
			nextDir = PlayerDirection.NONE;
		}
		return false;
	}

	public PlayerDirection getNextDirection() {
		return nextDir;
	}

	public boolean allowed(Player player, PlayerDirection direction) {
		int connectedTargetX = -4711;
		int connectedTargetY = -4711;
		int connectedTargetZ = -4711;

		if (player.isConnected()) {
			connectedTargetX = player.getConnectedBlock().x;
			connectedTargetY = player.getConnectedBlock().y;
			connectedTargetZ = player.getConnectedBlock().z;
		}
		
		int x = player.x;
		int y = player.y;
		int z = player.z;
		
		LOGGER.info("Ask if allowed. Player.pos ({},{},{}) id {}", x, y, z, player.getId());
		
		int targetX = x;
		int targetY = y;
		int targetZ = z;
		
		switch (direction) {
			case FORWARD:
				targetX -= 1;
				connectedTargetX -= 1;
				break;
			case BACK:
				targetX += 1;
				connectedTargetX += 1;
				break;
			case LEFT:
				targetZ -= 1;
				connectedTargetZ -= 1;
				break;
			case RIGHT:
				targetZ += 1;
				connectedTargetZ += 1;
				break;
			case NONE:
				throw new RuntimeException("allowed should not be asked for direcion=NONE");
		}
		
		LOGGER.info("Ask if allowed. Requested new player.pos ({},{},{})", targetX, targetY, targetZ);
		if (player.isConnected()) {
			LOGGER.info("Ask if allowed. Requested new connected.pos ({},{},{})", connectedTargetX, connectedTargetY, connectedTargetZ);
		}
		
		// Check level-bounds
		if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
			// potentially allowed
		} else {
			LOGGER.info("not allowed, target position not within bounds");
			return false;
		}
		if (player.isConnected()) {
			// Check level-bounds
			if (connectedTargetX >= 0 && connectedTargetX <level.xdim && connectedTargetZ >=0 && connectedTargetZ < level.zdim) {
				// potentially allowed
			} else {
				LOGGER.info("not allowed, target position not within bounds for connected");
				return false;
			}
		}
		
		
		Optional<Block> targetFloor = level.get(targetX, targetY - 1, targetZ);
		if (!targetFloor.isPresent()) {
			LOGGER.info("not allowed, target position has no stone on layer below");
			return false;
		} else if (targetFloor.get().getType() == TYPE.HOLE) {
			LOGGER.info("not allowed, target position is GOAL");
			return false;
		}
		
		if (player.isConnected()) {
			if (!level.get(connectedTargetX, connectedTargetY - 1, connectedTargetZ).isPresent()) {
				LOGGER.info("not allowed, target position has no stone on layer below for connected");
				return false;
			} else {
				if (level.get(connectedTargetX, connectedTargetY - 1, connectedTargetZ).get().getType() == TYPE.HOLE) {
					Color goalColor = level.get(connectedTargetX, connectedTargetY - 1, connectedTargetZ).get().color;
					Color connectedColor = player.getConnectedBlock().color;
					if (goalColor.equals(connectedColor)) {
						LOGGER.info("Allowed... connected has same color as goal");
						return true;
					} else {
						LOGGER.info("NOT Allowed... connected has same has color as goal");
						return false;
					}
				} else if (level.get(connectedTargetX, connectedTargetY - 1, connectedTargetZ).get().getType() == TYPE.MOVEABLE) {
					LOGGER.info("NOT Allowed to move a connected on a movable strone");
					return false;
				}
			}	
		}
		
		if (level.get(targetX, targetY, targetZ).isPresent()) {
			// Block verschieben?
			
			Block nextBlock = level.get(targetX, targetY, targetZ).get();
			LOGGER.info("inner check: {} id {}", nextBlock, nextBlock.getId());
			
			if (nextBlock instanceof Player) {
				return true;
			}
			
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
				case NONE:
					throw new RuntimeException("allowed should not be asked for direcion=NONE");
			}
			if (targetX >= 0 && targetX <level.xdim && targetZ >=0 && targetZ < level.zdim) {
				// der block auf der gleichen ebene kann weitergeschoben werden, wenn es nicht aus dem spielfeld fällt, 
				// aber auch nur wenn spieler + block gleiche arbe
				
				// TODO Ferner muss (wenn die untere Position null ist) die Farbe des verschobenen Steins die Farbe der darunterliegenden Ebene entsprechen
				
				// TODO wenn connected/disconnected?
				
				Optional<Block> lower = level.get(targetX, targetY - 1, targetZ);
				if (!lower.isPresent()) {
					return level.get(targetX, targetY - 2, targetZ).get().color.equals(nextBlock.color);
				}
				if (player.isConnected()) {
					
					return nextBlock.color.equals(player.color);
				} else {
					// First connect with "C"
					return false;
				}
			} else {
				return false;
			}
		}
		// Check if there exists a tile one layer below
		
		// allowed from same layer && allowed from one layer below
		return true;
	}
}
