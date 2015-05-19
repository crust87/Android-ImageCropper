package com.mabi87.imagecroper;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.mabi87.imagecroper.ImageCroper.ACTION_LIST;

public class Anchor {
	public static final int ANCHOR_SIZE = 60;
	public static final int ANCHOR_SIZE_HALF = ANCHOR_SIZE/2;
	public static final int MIN_BOX_SIZE = 50;
	
	protected float x;
	protected float y;
	protected float radius;
	
	public Anchor(float pX, float pY, float pWidth) {
		x = pX;
		y = pY;
		radius = pWidth;
	}
	
	public void move(float pX, float pY) {
		x -= pX;
		y -= pY;
	}
	
	public void draw(Canvas pCanvas, Paint pPaint) {
		pCanvas.drawCircle(x, y, ANCHOR_SIZE_HALF, pPaint);
	}
	
	public void setLocation(double pX, double pY) {
		x = (float)pX;
		y = (float)pY;
	}
	
	public ACTION_LIST getAction() {
		return ACTION_LIST.anchor;
	}
	
	public ACTION_LIST contains(float pX, float pY) {
		if((pX >= x - ANCHOR_SIZE && pX <= x + ANCHOR_SIZE) && (pY >= y - ANCHOR_SIZE && pY <= y + ANCHOR_SIZE)) {
			return ACTION_LIST.anchor;
		} else {
			return ACTION_LIST.none;
		}		
	}
	
	public float getLeft() {
		return x;
	}
	
	public float getTop() {
		return y;
	}
	
	public float getRight() {
		return x + radius;
	}
	
	public float getBottom() {
		return y + radius;
	}

}
