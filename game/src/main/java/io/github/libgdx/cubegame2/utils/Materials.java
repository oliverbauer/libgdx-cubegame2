package io.github.libgdx.cubegame2.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class Materials {
	private static Map<Color, Material> withBorder = new HashMap<>();
	private static Map<Color, Material> withoutBorder = new HashMap<>();
	
	public static Material withoutBorder(java.awt.Color c) {
		return withoutBorder.computeIfAbsent(c,  k -> new Material(TextureAttribute.createDiffuse(Textures.createTexture(c, false))));
	}
	
	public static Material withBorder(java.awt.Color c) {
		return withBorder.computeIfAbsent(c,  k -> new Material(TextureAttribute.createDiffuse(Textures.createTexture(c, true))));
	}
}
