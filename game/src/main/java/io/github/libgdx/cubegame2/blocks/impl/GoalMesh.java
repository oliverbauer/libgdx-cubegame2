package io.github.libgdx.cubegame2.blocks.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.Materials;
import io.github.libgdx.cubegame2.utils.HeightOffsets;

public class GoalMesh {
	public Model main(Color color) {
		BlendingAttribute blendingAttribute = new BlendingAttribute();
		blendingAttribute.opacity = .9f;

		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		MeshPartBuilder mpb = builder.part("box", GL20.GL_TRIANGLES, 
				VertexAttributes.Usage.Position
				| VertexAttributes.Usage.Normal 
				| VertexAttributes.Usage.ColorPacked
				| VertexAttributes.Usage.TextureCoordinates, 
				Materials.withBorder(Colors.from(color)));

		mpb.setColor(color);
		
		float width = 0.1f;
		float height = HeightOffsets.floortileHeight;
		float depth = 0.95f;
		
		foo(-0.425f,-1f,0,width,height,depth, mpb);
		foo(0.425f,-1f,0,width,height,depth, mpb);
		
		width = 0.95f;
		depth = 0.1f;
		
		foo(0f,-1f,-0.425f,width,height,depth, mpb);
		foo(0f,-1f,0.425f,width,height,depth, mpb);
		
		return builder.end();
	}
	
	
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

		Vector3 nor = tmpV1.set(corner000).lerp(corner110, normalm).sub(tmpV2.set(corner001).lerp(corner111, normalm)).nor();
		mpb.rect(corner000, corner010, corner110, corner100, nor);
		
		mpb.rect(corner011, corner001, corner101, corner111, nor.scl(-1));
		nor = tmpV1.set(corner000).lerp(corner101, normalm).sub(tmpV2.set(corner010).lerp(corner111, normalm)).nor();
		
		mpb.rect(corner001, corner000, corner100, corner101, nor);

		mpb.rect(corner010, corner011, corner111, corner110, nor.scl(-1));
		nor = tmpV1.set(corner000).lerp(corner011, normalm).sub(tmpV2.set(corner100).lerp(corner111, normalm)).nor();

		mpb.rect(corner001, corner011, corner010, corner000, nor);

		mpb.rect(corner100, corner110, corner111, corner101, nor.scl(-1));
	}
}
