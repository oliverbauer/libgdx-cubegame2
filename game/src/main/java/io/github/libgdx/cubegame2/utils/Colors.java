package io.github.libgdx.cubegame2.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;

public class Colors {
	private static Map<Color, java.awt.Color> gdxawt = new HashMap<>();
	private static Map<java.awt.Color, Color> awtgdx = new HashMap<>();
	
	static {
		add(Color.BLUE, java.awt.Color.BLUE);
		add(Color.RED, java.awt.Color.RED);
		add(Color.YELLOW, java.awt.Color.YELLOW);
		add(Color.GREEN, java.awt.Color.GREEN);
		add(Color.GRAY, java.awt.Color.GRAY);
		add(Color.ORANGE, java.awt.Color.ORANGE);
		add(Color.MAGENTA, java.awt.Color.MAGENTA);
		add(Color.BLACK, java.awt.Color.BLACK);
		
		add(Color.OLIVE, new java.awt.Color(Color.OLIVE.r, Color.OLIVE.g, Color.OLIVE.b));
		add(Color.CYAN, new java.awt.Color(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b));
		add(Color.BROWN, new java.awt.Color(Color.BROWN.r, Color.BROWN.g, Color.BROWN.b));
	}
	
	public static java.awt.Color from(Color color) {
		return gdxawt.get(color);
	}
	
	public static Color from(java.awt.Color color) {
		return awtgdx.get(color);
	}
	
	private static void add(Color gdx, java.awt.Color awt) {
		gdxawt.put(gdx, awt);
		awtgdx.put(awt, gdx);
	}
	
	public static String getColorname(Color color) {
		if (color ==Color.BLUE) {
			return "BLUE";
		} else if (color == Color.ORANGE) {
			return "ORANGE";
		} else if (color == Color.YELLOW) {
			return "YELLOW";
		} else if (color == Color.MAGENTA) {
			return "MAGENTA";
		} else if (color == Color.GREEN) {
			return "GREEN"; // == FLOOR
		} else if (color == Color.RED) {
			return "RED";
		}
		return "unknown color: "+color;
	}
	
	public static String getColorname(java.awt.Color color) {
		if (color == java.awt.Color.BLUE) {
			return "BLUE";
		} else if (color == java.awt.Color.ORANGE) {
			return "ORANGE";
		} else if (color == java.awt.Color.YELLOW) {
			return "YELLOW";
		} else if (color == java.awt.Color.MAGENTA) {
			return "MAGENTA";
		} else if (color == java.awt.Color.GREEN) {
			return "GREEN"; // == FLOOR
		} else if (color == java.awt.Color.RED) {
			return "RED";
		}
		return "unknown color: "+color;
	}
}
