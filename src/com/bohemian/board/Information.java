package com.bohemian.board;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCParallaxNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.util.Log;
import android.view.MotionEvent;

public class Information extends CCLayer{
	
	public static boolean m_infoexist;
	private CGPoint previousLocation;
	private CCParallaxNode mParall;
	static final int kTagNode = 1;
	static final int kTagGrossini = 2;
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new Information();
		scene.addChild(layer);
		return scene;
	}
	
	protected Information() {
//		super(color);
		this.setIsTouchEnabled(true);
		mParall = CCParallaxNode.node();
		m_infoexist = true;
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		float ScaleX = winSize.width/480;
		float ScaleY = winSize.height/860;
		
		CCSprite item_information = CCSprite.sprite("info_bg_font_adding-phone.png");
		
		item_information.setScaleX(ScaleX);
		item_information.setScaleY(ScaleY);
		
		item_information.setPosition(CGPoint.ccp(winSize.width/2,winSize.height/2));
		
		addChild(item_information);
	}
	public void OnTouchedExit(){
		Log.d("TochedOnExit", "Yes!");
	}
	@Override
	protected void registerWithTouchDispatcher() {
		CCTouchDispatcher.sharedDispatcher().addDelegate(this, 1);
	}
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
//		previousLocation = CGPoint.make(event.getX(), event.getY());
        return CCTouchDispatcher.kEventHandled;
	}
	@Override
    public boolean ccTouchesEnded(MotionEvent event)
    {
        return CCTouchDispatcher.kEventHandled;
    }

    @Override
    public boolean ccTouchesCancelled(MotionEvent event)
    {
        return CCTouchDispatcher.kEventIgnored;
    }

	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
//		CGSize winSize = CCDirector.sharedDirector().displaySize();
//		
//		CGPoint diff = CGPoint.zero();
//		CGPoint touchLocation = CGPoint.ccp(event.getX(), event.getY());
//		
//		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(touchLocation.x, touchLocation.y));
//		CGPoint prevLocation = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(previousLocation.x, previousLocation.y));
//		
//		diff.x = location.x-prevLocation.x;
//        diff.y = location.y-prevLocation.y;
//        CGPoint newpos = CGPoint.ccp(mParall.getPosition().x+diff.x, mParall.getPosition().y+diff.y);
//        CGPoint maxPos = CGPoint.ccp(winSize.width*2, winSize.height*2);
//        
//        CCNode node = getChild(kTagNode);
//        node.setPosition(CGPoint.ccp(node.getPosition().x+diff.x, node.getPosition().y+diff.y));
//        previousLocation = touchLocation;
//        mParall.setPosition(CGPoint.ccp(mParall.getPosition().x+diff.x, mParall.getPosition().y+diff.y));
		
		return CCTouchDispatcher.kEventHandled;
	}

}
