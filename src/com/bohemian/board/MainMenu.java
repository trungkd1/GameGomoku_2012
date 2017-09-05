package com.bohemian.board;

import java.io.File;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.bohemian.activity.GameGomokuActivity;
import com.bohemian.activity.R;

public class MainMenu extends CCColorLayer{
	
	private CCMenu main_menu;
	public static boolean m_exist_mainmenu;
	public static int sound=1;
	private Context cxt;
	public static boolean mGetData = false;
	CCMenuItem item_resume=null;
	
	public static CCScene scene(Context cxt) {
		CCScene scene = CCScene.node();
		CCColorLayer layer = new MainMenu(ccColor4B.ccc4(255, 255, 255, 255),cxt);
		scene.addChild(layer);
		return scene;
	}

	protected MainMenu(ccColor4B color,Context cxt) {
		super(color);
		this.setIsTouchEnabled(true);
		this.cxt = cxt;
		m_exist_mainmenu = true;
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		CCSprite item_board = CCSprite.sprite("bg.png");
		item_board.setPosition(CGPoint.ccp(winSize.width/2,winSize.height/2));
		float ScaleX = winSize.width/480;
		float ScaleY = winSize.height/860;
		item_board.setScaleX(ScaleX);
		item_board.setScaleY(ScaleY);
		addChild(item_board);
		
		CCMenuItem item_exit = CCMenuItemImage.item("exit.png", "exit.png", this, "OnTouchedExit");
		item_exit.setPosition(CGPoint.ccp(65.0f,winSize.height-50.0f));
//		item_exit.setScaleX(2.0f);
//		item_exit.setScaleY(2.0f);
		
		
		CCMenuItem item_caro = CCMenuItemImage.item("caro_text.png", "caro_text.png");
		item_caro.setPosition(CGPoint.ccp(winSize.width/2,winSize.height*0.80f));
		
		
		CCMenuItem player_vs_com = CCMenuItemImage.item("player_vs_com.png", "player_vs_com_big.png", this, "OnTouchedAI");
		player_vs_com.setPosition(CGPoint.ccp(winSize.width*0.5f,winSize.height*0.55f));
		
		CCMenuItem player_vs_player = CCMenuItemImage.item("player_vs_player.png", "player_vs_player_big.png", this, "OnTouchedPlayer");
		player_vs_player.setPosition(CGPoint.ccp(winSize.width*0.5f,winSize.height*0.40f));
		
		
		
		CCMenuItem item_information = CCMenuItemImage.item("info_text.png", "info_text_big.png", this, "OnTouchedInfo");
		item_information.setPosition(CGPoint.ccp(winSize.width*0.5f,winSize.height*0.25f));
		
		main_menu = CCMenu.menu(item_exit,item_caro,player_vs_player,player_vs_com,item_information);
		main_menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
		
//		if(checkgetDataBoard()){
//			item_resume = CCMenuItemImage.item("resume_text.png", "resume_text.png", this, "OnTouchedResume");
//			item_resume.setPosition(CGPoint.ccp(winSize.width*0.7f,winSize.height*0.30f));
//			main_menu.addChild(item_resume);
//		}
		addChild(main_menu);
		getDataFromPreference(winSize);
		
	}
	  

	private void getDataFromPreference(CGSize winSize){
		SharedPreferences myPrefs = cxt.getSharedPreferences("MainMenu", 0);
		CCMenuItem item_sound = null;
		if(myPrefs.contains("Flag") && myPrefs.contains("Sound")){
			Log.d("MainMenu", "SharePreference not null");
	        sound = myPrefs.getInt("Sound", 0);
	        
	        if(sound == 1){
	        	item_sound = CCMenuItemImage.item("sound_icon.png", "sound_icon.png",this,"OnTouchedSound");
	        }else {
	        	item_sound = CCMenuItemImage.item("sound_icon_off.png", "sound_icon_off.png",this,"OnTouchedSound");
	        }
	        item_sound.setPosition(CGPoint.ccp(winSize.width-50f,winSize.height-60.0f));
			
			main_menu.addChild(item_sound);
			
			
	        Log.d("sound", String.valueOf(sound));
		}else{
			Log.d("MainMenu", "SharePreference null");
			item_sound = CCMenuItemImage.item("sound_icon.png", "sound_icon.png",this,"OnTouchedSound");
			
			
			item_sound.setPosition(CGPoint.ccp(winSize.width-50f,winSize.height-60.0f));
	        
			main_menu.addChild(item_sound);
		}
	}
	
	public void StorePreference(){
		SharedPreferences myPrefs = cxt.getSharedPreferences("MainMenu", 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putInt("Sound",sound );
        prefsEditor.commit();
        Log.d("StorePreference", "Success");
	}
	public void OnTouchedSound(){
		CCMenuItem item = main_menu.getSelectedItem();
		CCMenuItemImage newitem = null;
		if(sound == 1){
			newitem = CCMenuItemImage.item("sound_icon_off.png",
					"sound_icon_off.png", this, "OnTouchedSound");
			
			sound = 0;
		}else{
			newitem = CCMenuItemImage.item("sound_icon_on.png",
					"sound_icon_on.png", this, "OnTouchedSound");
			sound = 1;
		}
		newitem.setPosition(item.getPosition());
		main_menu.removeChild(item, true);
		main_menu.addChild(newitem);
	}
	public void OnTouchedInfo(){
		StorePreference();
		Log.d("OnTouchedInfo", "Yes!");
		CCScene scene = Information.scene();
		CCDirector.sharedDirector().runWithScene(scene);
	}
	public void OnTouchedPlayer(){
		StorePreference();
		if(checkgetDataBoard()){
			AlertDialog.Builder builder1=new AlertDialog.Builder(GameGomokuActivity.cxt);
			builder1.setTitle("MainMenu");
			builder1.setIcon(R.drawable.ic_launcher);
			builder1.setMessage("Do you want to resume the last board?");
			builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	mGetData = true;
                	CCScene scene = Board.scene();
    		   		CCDirector.sharedDirector().runWithScene(scene);
                }
            });
			
			builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		       public void onClick(DialogInterface dialog, int id) {
    		    	   deleteDatabase();
    		    	   CCScene scene = Board.scene();
    		   		CCDirector.sharedDirector().runWithScene(scene);
    		       }
    		   });
			builder1.show();
		}else{
			CCScene scene = Board.scene();
			CCDirector.sharedDirector().runWithScene(scene);
		}
	}
	
	public void OnTouchedAI(){
		StorePreference();
		if(checkgetDataBoard()){
			AlertDialog.Builder builder1=new AlertDialog.Builder(GameGomokuActivity.cxt);
			builder1.setTitle("MainMenu");
			builder1.setIcon(R.drawable.ic_launcher);
			builder1.setMessage("Do you want to resume the last board?");
			builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	mGetData = true;
                	CCScene scene = BoardAI.scene();
    		   		CCDirector.sharedDirector().runWithScene(scene);
                }
            });
			
			builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		       public void onClick(DialogInterface dialog, int id) {
    		    	deleteDatabase();
    		    	CCScene scene = BoardAI.scene();
    		   		CCDirector.sharedDirector().runWithScene(scene);
    		       }
    		   });
			builder1.show();
		}else{
			CCScene scene = BoardAI.scene();
			CCDirector.sharedDirector().runWithScene(scene);
		}
	}
	
	public void OnTouchedExit(){
		StorePreference();
		Log.d("TochedOnExit", "Yes!");
		AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
		builder.setTitle("Caro");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage("Are you sure to exit the game?")
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		    	   m_exist_mainmenu =false;
		    	   System.exit(0);
		       }
		   })
		   .setNegativeButton("No", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		            dialog.cancel();
		       }
		   });
		AlertDialog alert = builder.create();
		alert.show();
	}
	private boolean checkgetDataBoard(){
		String path = "/data/data/com.bohemian.activity/databases/Database_Board.db";
		File file = new File(path);
		if(file.exists()){
			Log.d("DataExist", "!!!!!");
			return true;
		}else{
			Log.d("Data doesn't Exist", "!!!!!");
			return false;
		}
	}
	
	private void deleteDatabase(){
		String path = "/data/data/com.bohemian.activity/databases/Database_Board.db";
		File file = new File(path);
		if(file.exists()){
			GameGomokuActivity.cxt.deleteDatabase(path);
			Log.d("FileDatabase", "Remove successful!!");
		}else{
			Log.d("FileDatabase", "Does not exists!!");
		}
		
	}
}
