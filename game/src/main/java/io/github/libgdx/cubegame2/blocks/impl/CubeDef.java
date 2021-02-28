package io.github.libgdx.cubegame2.blocks.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import io.github.libgdx.cubegame2.utils.Colors;
import io.github.libgdx.cubegame2.utils.Materials;

public class CubeDef {
	
	public CubeDef(float width, float height, float depth) {
		// manual of the last
		final float hw = width * 0.5f;
		final float hh = height * 0.5f;
		final float hd = depth * 0.5f;
		final float x0 = - hw, y0 = - hh, z0 = - hd, x1 = + hw, y1 = + hh, z1 = + hd;
		
		corner000 = new Vector3().set(x0, y0, z0);
		corner010 = new Vector3().set(x0, y1, z0);
		corner100 = new Vector3().set(x1, y0, z0);
		corner110 = new Vector3().set(x1, y1, z0);
		corner001 = new Vector3().set(x0, y0, z1);
		corner011 = new Vector3().set(x0, y1, z1);
		corner101 = new Vector3().set(x1, y0, z1);
		corner111 = new Vector3().set(x1, y1, z1);
	}
	
	Vector3 corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111;
	
	public CubeDef alignLeftSideWithRightSideOf(CubeDef prev) {
		// meine linkse seite = rechte seite des letzten
		corner000 = prev.corner001;
		corner010 = prev.corner011;
		corner100 = prev.corner101;
		corner110 = prev.corner111;
		return this;
	}
	
	public CubeDef alighnRightSideWithBottomSideOf(CubeDef prev) {
		// meine rechte seite = untere seite des letzten
		corner001 = prev.corner001;
		corner011 = prev.corner000;
		corner101 = prev.corner101;
		corner111 = prev.corner100;
		return this;
	}
	
	public CubeDef alignBottomWithTopOf(CubeDef prev) {
		/*
		 * corner111
		 * corner110
		 * corner010 done
		 * corner011
		 */
		corner001 = prev.corner011;
		corner000 = prev.corner010;
		corner101 = prev.corner111;
		corner100 = prev.corner110;
		return this;
	}
	
	public enum MOD {
		ONE,
		TWO,
		THREE
	}
	
	public CubeDef modify(MOD mod, float width) {
		if (mod == MOD.ONE) {
			// den boden etwas weiter machen
			corner000.sub(width/2, 0, width/2); // vorne
			corner101.add(width/2, 0, width/2); // hinten
			corner001.add(-width/2, 0, width/2); // vorne rechts
			corner100.add(width/2, 0, -width/2); // vorne links
		}
		if (mod == MOD.TWO) {
			// oben eine spitze machen
			corner010.add(width/2, 0, width/2); // vorne
			corner111.sub(width/2, 0, width/2); // hinten
			corner011.sub(-width/2, 0, width/2); // vorne rechts
			corner110.sub(width/2, 0, -width/2); // vorne links
		}
		if (mod == MOD.THREE) {
			// etwas erhöhen die rechte seite
	//		corner001 // boden vorne
	//		corner101 // boden hinten
			corner011.add(0, 1, 0); // oben vorne
			corner111.add(0, 1, 0); // obne hinten
		}
		return this;
	}
	
	public Model finialize(Color color) {
		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		MeshPartBuilder mpb = builder.part("box", GL20.GL_TRIANGLES, 
				VertexAttributes.Usage.Position
				| VertexAttributes.Usage.ColorPacked
				| VertexAttributes.Usage.Normal 
				| VertexAttributes.Usage.TextureCoordinates, 
				Materials.withBorder(Colors.from(color)));
		
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
