package io.github.libgdx.cubegame2.blocks.impl;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Tunnel implements Renderable {
	int x = 0;
	int y = 0;
	int z = 0;
	
	float width = 1;
	float height = 0.1f;
	float depth = 1;
	
	List<ModelInstance> mis = new ArrayList<>();
	
	public Tunnel(float offsetX, float offsetY, float offsetZ) {
		float h = 0.5f;
		float w = 0.5f;
		float d = 0.1f;
		/*
		 * Vector3 bottomFront;
 		 * Vector3 topFront;
		 * Vector3 bottomBack;
		 * Vector3 topBack;
		 */
		List<Model> models = Typ2.getModels( 
			new Typ2.COORD(
				new Vector3(0, 0, 0),
				new Vector3(0, 1*h, 0),
				new Vector3(0, 0, d),
				new Vector3(0, 1*h, d)
			),
			new Typ2.COORD(				
				new Vector3(w, 0, 0),
				new Vector3(w, 1*h, 0),
				new Vector3(w, 0, d),
				new Vector3(w, 1*h, d)
			),
			new Typ2.COORD(				
				new Vector3(3*w, 2*h, 0), // bottom
				new Vector3(2*w, 2*h, 0),
				new Vector3(3*w, 2*h, d), // bottom
				new Vector3(2*w, 2*h, d)
			),
			new Typ2.COORD(				
				new Vector3(3*w, 3*h, 0),
				new Vector3(2*w, 3*h, 0),
				new Vector3(3*w, 3*h, d),
				new Vector3(2*w, 3*h, d)
			),
			new Typ2.COORD(				
				new Vector3(1*w, 5*h, 0),
				new Vector3(1*w, 4*h, 0),
				new Vector3(1*w, 5*h, d),
				new Vector3(1*w, 4*h, d)
			)
			,
			new Typ2.COORD(				
				new Vector3(0*w, 5*h, 0),
				new Vector3(0*w, 4*h, 0),
				new Vector3(0*w, 5*h, d),
				new Vector3(0*w, 4*h, d)
			)
		);
		for (Model m : models) {
			mis.add(new ModelInstance(m));
		}

		for (ModelInstance mi : mis) {
			mi.transform.setToRotation(Vector3.Z, 90);
			mi.transform.setTranslation(offsetX, offsetY, offsetZ);
		}
	}
	
	@Override
	public void render(ModelBatch modelBatch, Environment environment) {
		for (ModelInstance mi : mis) {
			modelBatch.render(mi, environment);
		}
	}
}
