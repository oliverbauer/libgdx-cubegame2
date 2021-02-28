package io.github.libgdx.cubegame2.blocks.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;

import io.github.libgdx.cubegame2.blocks.Block;
import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.Materials;
import io.github.libgdx.cubegame2.utils.HeightOffsets;

public class Lifeforce extends Block {

	public Lifeforce(Model finialize, Color yellow, int x, int y, int z) {
		super(finialize, yellow);
		super.x = x;
		super.y = y;
		super.z = z;
		
		yoffset = HeightOffsets.lifeforceHeightOffset;
		instance.transform.setToTranslation(x, (y - 1) + yoffset,z); // weil y=2
		
		yoffset = -0.5f;
	}
	
	@Override
	public void setColor(Color red) {
		this.color = red;
		this.instance.materials.get(0).set(Materials.withBorder(Colors.from(color)));
	}
}
