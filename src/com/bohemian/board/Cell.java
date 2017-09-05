package com.bohemian.board;

import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;

import com.bohemian.engine.CellStatus;

public class Cell  {

	
	private int row;
	private int col;

	private CellStatus status;
	
	public Cell(){
		
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

	public CellStatus getStatus() {
		return status;
	}

	public void setStatus(CellStatus status) {
		this.status = status;
	}

	
	private CCMenuItemImage item;

	public CCMenuItemImage getItem() {
		return item;
	}

	public void setItem(CCMenuItemImage item) {
		this.item = item;
	}

	public Cell(int row, int col, CellStatus status, CCMenuItemImage item) {
		super();
		this.row = row;
		this.col = col;
		this.status = status;
		this.item = item;
	}
	
	
	

	
	
}
