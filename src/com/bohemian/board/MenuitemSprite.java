package com.bohemian.board;

import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCSprite;

public class MenuitemSprite {
	private CCMenuItem item;
	private String status;
	
	public CCMenuItem getItem() {
		return item;
	}
	public void setItem(CCMenuItem item) {
		this.item = item;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public MenuitemSprite(CCMenuItem item,String status) {
		super();
		this.status = status;
		this.item = item;
	}
}
