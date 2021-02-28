package io.github.libgdx.cubegame2.blocks.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import io.github.libgdx.cubegame2.blocks.impl.CubeDef.MOD;
import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.Textures;

public class Tree implements Renderable {
	private ModelInstance movingHead;
	private ModelInstance root;
	
	public Tree(float offsetX, float offsetY, float offsetZ) {
		movingHead = createBoard3(Color.GREEN);
		
		float width = 0.1f;
		float height = 1f;
		float depth = 0.1f;
		root = new ModelInstance(new CubeDef(width,height,depth).finialize(Color.BROWN));
		
		movingHead.transform.setTranslation(offsetX, offsetY + 2.5f, offsetZ);
		root.transform.setTranslation(offsetX, offsetY + 1.5f, offsetZ);
	}
	
	@Override
	public void render(ModelBatch modelBatch, Environment environment) {
//		// "A rotation around a point is the same as translating to that point, rotating and then translating back."
//		// Cf. https://stackoverflow.com/questions/26656334/libgdx-set-rotation-point-3d

//		movingHead.transform
//			.translate(x, y, z) // Positon of the pyramid
//			.rotate(0, 0.1f, 0, 45*Gdx.graphics.getDeltaTime())
//			.translate(-x, -y, -z); // Positon of the pyramid

		movingHead.transform
			.rotate(0, 0.1f, 0, 45*Gdx.graphics.getDeltaTime());
		
		
		modelBatch.render(movingHead, environment);
		modelBatch.render(root, environment);
	}
	
	private ModelInstance createBoard3(Color color) {
		BlendingAttribute blendingAttribute = new BlendingAttribute();
		blendingAttribute.opacity = .9f;

		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		
		float width = 1;
		float height = 1;
		float depth = 1;

		Model finialize = new CubeDef(width, height, depth).modify(MOD.TWO, 1).finialize(Color.RED);
		ModelInstance mi = new ModelInstance(finialize);
		mi.materials.get(0).set(TextureAttribute.createDiffuse(Textures.createTexture(Colors.from(color), true)));

		return mi;
	}
}
