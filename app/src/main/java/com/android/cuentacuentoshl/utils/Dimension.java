package com.android.cuentacuentoshl.utils;

public class Dimension {
	
	private int width;
	private int height;
	
	public Dimension(int x, int y) {
		
		width = x;
		height = y;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

}
