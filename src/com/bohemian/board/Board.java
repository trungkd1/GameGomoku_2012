package com.bohemian.board;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.bohemian.activity.GameGomokuActivity;
import com.bohemian.activity.R;
import com.bohemian.engine.CellStatus;
import com.bohemian.engine.Engine;


public class Board extends CCColorLayer{

	private int CellWidth = 40;
	private int CellHeight = 40;
	private int numberCellCol;
	private int numberCellRow;
	public static ArrayList<Cell> items;
	private ArrayList<CCMenuItem>items_store = null;
	private Cell[][] arr_cells;
	private Engine engine;
	private float boardHeight;
	private float boardWidth;
	public static int flag=1;
	public static boolean m_Boardexist;
	private Cursor mCursor;
	public  DBAdapter mDB;
	public static final int XFINISH_BOARD = 365;
	public static final int OFINISH_BOARD = 366;
	public static final int RESET_BOARD = 367;
	public static final int UNDO_BOARD = 368;
	public static boolean isBegin = false;
	private SoundPool sounds;
	private int sPlayerO;
	private int sPlayerX;
	private boolean isUndo = false;
	private CGPoint previousLocation;
	
	private BroadcastReceiver mreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent.getAction().equals(GameGomokuActivity.BROADCAST_ACTION_RESET)) {
            Log.d("RESET BROADCASTRECEIVER", "!!!!!!");
            mHandler.sendEmptyMessage(RESET_BOARD);
        	}else if(intent.getAction().equals(GameGomokuActivity.BROADCAST_ACTION_UNDO)){
        		Log.d("UNDO BROADCASTRECEIVER", "!!!!!!");
        		mHandler.sendEmptyMessage(UNDO_BOARD);
        	}
        }
    };

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCColorLayer layer = new Board(ccColor4B.ccc4(255, 255, 255, 255));
		Log.e("Trung", "CCScene");
		scene.addChild(layer);
		return scene;
	}
	
	CCMenu menu = null;
	Hashtable<CCMenuItem, Cell> hashCell;
	public static ArrayList<DataBoard> storehashCell;
	
	public Board(ccColor4B color) {
		super(color);
		mDB = new DBAdapter(GameGomokuActivity.cxt);
		IntentFilter filter = new IntentFilter();
        filter.addAction(GameGomokuActivity.BROADCAST_ACTION_RESET);
        filter.addAction(GameGomokuActivity.BROADCAST_ACTION_UNDO);
        GameGomokuActivity.cxt.registerReceiver(mreceiver, filter);
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        sPlayerO = sounds.load(GameGomokuActivity.cxt, R.raw.explosion, 1);
        sPlayerX = sounds.load(GameGomokuActivity.cxt, R.raw.sfx_wrong, 1);
		items_store = new ArrayList<CCMenuItem>();
		mDB.open();
		m_Boardexist = true;
		this.setIsTouchEnabled(true);
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		boardHeight = winSize.getHeight();
		boardWidth = winSize.getWidth();
		float ScaleX = winSize.width/480;
		float ScaleY = winSize.height/860;
		CCSprite sprite_board = CCSprite.sprite("bg_play.png");
		sprite_board.setPosition(CGPoint.ccp(boardWidth/2,boardHeight/2));
		sprite_board.setScaleX(ScaleX);
		sprite_board.setScaleY(ScaleY);
		addChild(sprite_board);
		numberCellCol = (int) boardWidth / CellWidth;
		numberCellRow = (int) boardHeight / CellHeight;
		items = new ArrayList<Cell>();
		arr_cells = new Cell[numberCellCol][numberCellRow];
		
		hashCell = new Hashtable<CCMenuItem, Cell>();
		
		storehashCell = new  ArrayList<DataBoard>();
		Log.e("Trung", "boardHeight :"+ String.valueOf(boardHeight));
		Log.e("Trung", "boardWidth :"+ String.valueOf(boardWidth));
		Log.e("Trung", "CellRow :"+ String.valueOf(numberCellRow));
		Log.e("Trung", "CellCol :"+ String.valueOf(numberCellCol));
		
		storehashCell.clear();
		hashCell.clear();
		items_store.clear();
		
		if(getDataBoard() && MainMenu.mGetData){
			Log.d("Item_Board", "Get Data from sqlite!!");
			flag = getstoreTurn();
			menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
			addChild(menu);
			menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
			isBegin = true;
		}else{
			Log.d("Item_Board", "Create Data!!");
			for (int i = 0; i < numberCellCol -1; i++)
				for (int j = 0; j < numberCellRow -1; j++) {
					//set all field in board empty
					arr_cells[i][j] = new Cell();
					CCMenuItemImage item = CCMenuItemImage.item("field.png",
							"field.png", this, "onClickItem");		
					item.setPosition(CGPoint.ccp((i+1) * 40.0f, (j+1) * 40.0f));	
					arr_cells[i][j].setItem(item);
					arr_cells[i][j].setStatus(CellStatus.empty);
					arr_cells[i][j].setRow(j);
					arr_cells[i][j].setCol(i);
					
					hashCell.put(item, arr_cells[i][j]);
					items.add(new Cell(j, i, arr_cells[i][j].getStatus(), arr_cells[i][j].getItem()));
					items_store.add(item);
				}
			menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
			addChild(menu);
			menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
		}
		engine = new Engine(arr_cells, numberCellRow-1, numberCellCol-1,menu);
		
	}
	
	MenuitemSprite temp_menuitem = null;
	CCSprite item_temp = null;
	public void onClickItem() {
		Log.d("OnClickItem", "!!!!!!!!!!!!!!!");
		
		CCMenuItem item = menu.getSelectedItem();
		if(temp_menuitem!=null && !isUndo){
			Log.d("temp_menu", "!!!!!");
//			removeChild(item_temp, true);
//			menu.removeChild(temp_menuitem.getItem(), true);
			temp_menuitem.getItem().removeChild(item_temp, true);
			String status = temp_menuitem.getStatus();
			CCSprite newsprite = null;
			if(status.equalsIgnoreCase("PlayerX")){
				newsprite = CCSprite.sprite("x_cur.png");
			}else{
				newsprite = CCSprite.sprite("o_cur.png");
			}
			newsprite.setPosition(CGPoint.ccp(temp_menuitem.getItem().getContentSize().width/2,temp_menuitem.getItem().getContentSize().height/2));
			temp_menuitem.getItem().addChild(newsprite);
		}
		if(hashCell.containsKey(item) == true && engine.isFinish == false){
			Log.d("OnClickItem", "Stage2!!!!!!!!!!!!!!!");
			Cell cell = hashCell.get(item);
			Log.d("StatusofCell", cell.getStatus().toString());
			CCMenuItem newitem = null;
			if(cell.getStatus() == CellStatus.empty){
				Log.d("OnClickItem", "Stage3!!!!!!!!!!!!!!!");
				if(flag == 1){
						item_temp = CCSprite.sprite("o_sign05.png");
						CCAnimation animation = CCAnimation
								.animation("dance", 0.1f);
						for (int i = 1; i <= 5; i++) {
							animation.addFrame(String.format("o_sign0%d.png", i));
						}
						CCIntervalAction interval = CCAnimate.action(animation);
						item_temp.setPosition(CGPoint.ccp(item.getContentSize().width/2, item.getContentSize().height/2));
						item_temp.runAction(interval);
						item.addChild(item_temp);
						
						newitem = cell.getItem().item("o_cur.png", "o_win.png",this, "onClickItem");
						cell.setStatus(CellStatus.playerO);
						flag = 0;
						if(MainMenu.sound == 1){
							sounds.play(sPlayerO, 1.0f, 1.0f, 0, 0, 1.5f);
						}
						
						Log.e("board", "Game O   ___"+ "numberCellRow :"+ cell.getRow() + ",numberCellCol :"+ cell.getCol() );
						temp_menuitem = new MenuitemSprite(item,"PlayerO");
					
				}else{
						item_temp = CCSprite.sprite("x_sign05.png");
						
						CCAnimation animation = CCAnimation
								.animation("dance", 0.1f);
						for (int i = 1; i <= 5; i++) {
							animation.addFrame(String.format("x_sign0%d.png", i));
						}
						CCIntervalAction interval = CCAnimate.action(animation);
						item_temp.setPosition(CGPoint.ccp(item.getContentSize().width/2, item.getContentSize().height/2));
						item_temp.runAction(interval);
						item.addChild(item_temp);
						
						newitem = cell.getItem().item("x_cur.png", "x_win.png",this, "onClickItem");
						cell.setStatus(CellStatus.playerX);
						flag = 1;
						if(MainMenu.sound == 1){
							sounds.play(sPlayerX, 1.0f, 1.0f, 0, 0, 1.5f);
						}
						
						Log.e("board", "Game X   ___"+ "numberCellRow :"+ cell.getRow() + ",numberCellCol :"+ cell.getCol() );
						temp_menuitem = new MenuitemSprite(item,"PlayerX");
					
				}
				isBegin = true;
				hashCell.put(newitem, cell);	
				storehashCell.add(new DataBoard(cell.getItem(),cell.getItem().getPosition(),cell.getItem().getPosition().x,
							cell.getItem().getPosition().y,cell.getRow(),cell.getCol(),cell.getStatus().toString()));
				isUndo = false;
				
				Log.d("cell.getCol()", String.valueOf(cell.getCol()));
				Log.d("cell.getRow()", String.valueOf(cell.getRow()));
				int type = engine.totalCheck(cell.getCol(),cell.getRow());
				int value = engine.setFinish(type, cell.getCol(),cell.getRow());
				if(value == 0){
					mHandler.sendEmptyMessage(XFINISH_BOARD);
				}else if(value == 1){
					mHandler.sendEmptyMessage(OFINISH_BOARD);
				}
			}			
		}
	}
	
	private void resetBoard(){
		removeChild(item_temp, true);
		menu.removeAllChildren(true);
		engine.isFinish = false;
		storehashCell.clear();
		hashCell.clear();
		items.clear();
		items_store.clear();
	}
	
	private void createNewBoard(){
		isBegin = false;
		Log.d("Create New Board", "!!!!!!!!!!!");
		for (int i = 0; i < numberCellCol -1; i++)
			for (int j = 0; j < numberCellRow -1; j++) {
				arr_cells[i][j] = new Cell();
				CCMenuItemImage item = CCMenuItemImage.item("field.png",
						"field.png", this, "onClickItem");		
				item.setPosition(CGPoint.ccp((i+1) * 40.0f, (j+1) * 40.0f));	
				arr_cells[i][j].setItem(item);
				arr_cells[i][j].setStatus(CellStatus.empty);
				arr_cells[i][j].setRow(j);
				arr_cells[i][j].setCol(i);
				
				hashCell.put(item, arr_cells[i][j]);
				items.add(new Cell(j, i, arr_cells[i][j].getStatus(), arr_cells[i][j].getItem()));
				items_store.add(item);
				
			}
		menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
		addChild(menu);
		menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
		
		engine = new Engine(arr_cells, numberCellRow-1, numberCellCol-1,menu);
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
	
	private void undo(){
		if(storehashCell.size()>0){
			isUndo = true;
			DataBoard cell = storehashCell.get(storehashCell.size()-1);
			CCMenuItemImage item = cell.getItem();
			CCMenuItemImage replace = CCMenuItemImage.item("field.png","field.png", this,"onClickItem");
			Log.d("UNDO", cell.getPos().toString());
			menu.removeChild(item, true);
			
			Cell cellaa  = hashCell.get(item);
			cellaa.setItem(replace);
			cellaa.setStatus(CellStatus.empty);
			
			replace.setPosition(item.getPosition());
			hashCell.put(replace, cellaa);
			menu.addChild(replace);
			
			storehashCell.remove(storehashCell.size()-1);
			int count = storehashCell.size();
			if(count == 0){
				Log.d("Storehashcell", "Size = 0");
				isBegin = false;
			}
			if(flag == 1){
				flag = 0;
			}else if(flag == 0){
				flag = 1;
			}
		}
	}
	public Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case XFINISH_BOARD:
				AlertDialog.Builder builder=new AlertDialog.Builder(GameGomokuActivity.cxt);
				builder.setTitle("Caro");
				builder.setIcon(R.drawable.ic_launcher);
				builder.setMessage("Player X win Game!");
				builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	mHandler.sendEmptyMessage(RESET_BOARD);
	                }
	            });
				builder.setNeutralButton("Main Menu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Board.m_Boardexist = false;
	    		    	   resetBoard();
		    		    	deleteDatabase();
		    		    	CCDirector.sharedDirector().popScene();
	    		       }
	    		   });
				;
				builder.show();
				break;
			case OFINISH_BOARD:
				AlertDialog.Builder builder1=new AlertDialog.Builder(GameGomokuActivity.cxt);
				builder1.setTitle("Caro");
				builder1.setIcon(R.drawable.ic_launcher);
				builder1.setMessage("Player O win Game!");
				builder1.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	mHandler.sendEmptyMessage(RESET_BOARD);
	                }
	            });
				builder1.setNeutralButton("Main Menu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Board.m_Boardexist = false;
	    		    	   resetBoard();
		    		    	deleteDatabase();
		    		    	CCDirector.sharedDirector().popScene();
	    		       }
	    		   });
				;
				builder1.show();
				break;

			case RESET_BOARD:
				if(storehashCell.size() > 0){
					resetBoard();
	            	createNewBoard();
				}
				break;
			case UNDO_BOARD:
				undo();
				break;
			}
        };
	};
	private int getstoreTurn(){
		SharedPreferences myPrefs = GameGomokuActivity.cxt.getSharedPreferences("BoardTurn", 0);
		 int flag = myPrefs.getInt("FlagTurn", 0);
		 return flag;
	}
	
	@Override
	public void onExit() {
		Log.d("ONEXITCCLAYER", "!!!!!!");
		GameGomokuActivity.cxt.unregisterReceiver(mreceiver);
		temp_menuitem = null;
		MainMenu.mGetData = false;
		m_Boardexist = false;
		isBegin = false;
		mDB.close();
		super.onExit();
	}
	
	private boolean getDataBoard(){
		try {
			mCursor = mDB.getAllBoard();
			int count = mCursor.getCount();
			if(count == 0){
				Log.d("Database ", "Don't have data!!");
				return false;
			}
	    	if (mCursor.moveToFirst()){
	    		do{
	    			float posX = mCursor.getFloat(mCursor.getColumnIndex(DBAdapter.KEY_POSX));
	    			float posY = mCursor.getFloat(mCursor.getColumnIndex(DBAdapter.KEY_POSY));
	    			int row = mCursor.getInt(mCursor.getColumnIndex(DBAdapter.KEY_ROW));
	    			int col = mCursor.getInt(mCursor.getColumnIndex(DBAdapter.KEY_COL));
	        		String status = mCursor.getString(mCursor.getColumnIndex(DBAdapter.KEY_STATUS));
	        		if(status.equalsIgnoreCase("playerX")){
	        			CCMenuItemImage playerX= CCMenuItemImage.item("x_cur.png", "x_cur.png",this,"onClickItem");
	        			playerX.setPosition(CGPoint.ccp(posX, posY));
	        			arr_cells[col][row]  = new Cell();
	        			arr_cells[col][row].setItem(playerX);
	        			arr_cells[col][row].setRow(row);
	        			arr_cells[col][row].setCol(col);
	        			arr_cells[col][row].setStatus(CellStatus.playerX);
	        			items_store.add(playerX);
	        			hashCell.put(playerX, arr_cells[col][row]);
	        		}else if(status.equalsIgnoreCase("playerO")){
	        			CCMenuItemImage playerO= CCMenuItemImage.item("o_cur.png", "o_cur.png",this,"onClickItem");
	        			playerO.setPosition(CGPoint.ccp(posX, posY));
	        			arr_cells[col][row] = new Cell();
	        			arr_cells[col][row].setItem(playerO);
	        			arr_cells[col][row].setRow(row);
	        			arr_cells[col][row].setCol(col);
	        			arr_cells[col][row].setStatus(CellStatus.playerO);
	        			items_store.add(playerO);
	        			hashCell.put(playerO, arr_cells[col][row]);
	        		}else if(status.equalsIgnoreCase("field")){
	        			CCMenuItemImage field= CCMenuItemImage.item("field.png", "field.png",this,"onClickItem");
	        			field.setPosition(CGPoint.ccp(posX, posY));
	        			arr_cells[col][row] = new Cell();
	        			arr_cells[col][row].setItem(field);
	        			arr_cells[col][row].setRow(row);
	        			arr_cells[col][row].setCol(col);
	        			arr_cells[col][row].setStatus(CellStatus.empty);
	        			items_store.add(field);
	        			hashCell.put(field, arr_cells[col][row]);
	        		}
	    		   }while(mCursor.moveToNext());
	    		mDB.close();
	    		return true;
	    	}
		} catch (Exception e) {
			Log.d("mCusor", "No Data",e);
			mDB.close();
			return false;
		}
		return false;
	}
	
}
