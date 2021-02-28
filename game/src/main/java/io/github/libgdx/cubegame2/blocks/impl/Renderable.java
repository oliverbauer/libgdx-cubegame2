package io.github.libgdx.cubegame2.blocks.impl;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Renderable {
	public void render(ModelBatch modelBatch, Environment environment);
}
