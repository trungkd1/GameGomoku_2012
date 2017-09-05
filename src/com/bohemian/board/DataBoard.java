package com.bohemian.board;

import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.types.CGPoint;


public class DataBoard {
	private CGPoint pos;
	
	private float posX,posY;
	private String status;
	private int row,col;
	private CCMenuItemImage item;
	

	public CCMenuItemImage getItem() {
		return item;
	}
	public void setItem(CCMenuItemImage item) {
		this.item = item;
	}

	

	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getPosX() {
		return posX;
	}
	public void setPosX(float posX) {
		this.posX = posX;
	}
	public CGPoint getPos() {
		return pos;
	}
	public void setPos(CGPoint pos) {
		this.pos = pos;
	}
	public float getPosY() {
		return posY;
	}
	public void setPosY(float posY) {
		this.posY = posY;
	}

	public DataBoard(CCMenuItemImage item,CGPoint pos, float posX, float posY,
			 int row, int col,String status) {

	
		super();
		this.item = item;
		this.pos = pos;
		
		this.posX = posX;
		this.posY = posY;
		this.status = status;
		this.row = row;
		this.col = col;
	}
	
}
