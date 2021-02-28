package io.github.libgdx.cubegame2.blocks.impl;

import java.util.ArrayList;
import java.util.List;

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

public class Typ2 {
	public static class COORD {
		Vector3 bottomFront;
		Vector3 topFront;
		Vector3 bottomBack;
		Vector3 topBack;
		
		public COORD(Vector3 bottomFront, Vector3 topFront, Vector3 bottomBack, Vector3 topBack) {
			this.bottomBack = bottomBack;
			this.bottomFront = bottomFront;
			this.topFront = topFront;
			this.topBack = topBack;
		}
	}
	
	public static List<Model> getModels(COORD... coord) {
		List<Model> models = new ArrayList<>();
		for (int i=0; i<=coord.length-2; i++) {
			COORD c1 = coord[i];
			COORD c2 = coord[i+1];
			
			corner000 = c1.bottomFront;
			corner010 = c1.topFront;
			corner001 = c1.bottomBack;
			corner011 = c1.topBack;
			
			corner100 = c2.bottomFront;
			corner110 = c2.topFront;
			corner101 = c2.bottomBack;
			corner111 = c2.topBack;
			
			Model m1 = finialize(Color.YELLOW);
			models.add(m1);
		}
		return models;
	}
	
	static Vector3 corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111;
	
	public static Model finialize(Color color) {
		BlendingAttribute blendingAttribute = new BlendingAttribute();
		blendingAttribute.opacity = .9f;

		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		MeshPartBuilder mpb = builder.part("box", GL20.GL_TRIANGLES, 
				VertexAttributes.Usage.Position
				| VertexAttributes.Usage.ColorPacked
				| VertexAttributes.Usage.Normal 
				| VertexAttributes.Usage.TextureCoordinates, 
				Materials.withoutBorder(Colors.from(color)));
		
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

		return builder.end();
	}
}
