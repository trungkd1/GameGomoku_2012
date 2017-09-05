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
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.bohemian.activity.GameGomokuActivity;
import com.bohemian.activity.R;
import com.bohemian.engine.CellStatus;
import com.bohemian.engine.Engine;
import com.bohemian.engine.EvalBoard;
import com.bohemian.engine.Point;

public class BoardAI extends CCColorLayer{
	private int CellWidth = 40;
	private int CellHeight = 40;
	private int numberCellCol;
	private int numberCellRow;
	private ArrayList<CCMenuItem> items_store;
	public static ArrayList<Cell> items;
	private Cell[][] cells;
	private Engine engine;
	private float boardHeight;
	private float boardWidth;
	public static boolean m_Boardexist;
	public static boolean isBegin = false;
	public static final int XFINISH_BOARD = 465;
	public static final int OFINISH_BOARD = 466;
	public static final int RESET_BOARD = 467;
	public static final int UNDO_BOARD = 468;
	public static ArrayList<DataBoard> storehashCell;
	private Cursor mCursor;
	public  DBAdapter mDB;
	private SoundPool sounds;
	private int sPlayerO;
	private int sPlayerX;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCColorLayer layer = new BoardAI(ccColor4B.ccc4(255, 255, 255, 255));
		Log.e("Trung", "CCScene 2");
		scene.addChild(layer);
		return scene;
	}
	CCMenu menu = null;
	Hashtable<CCMenuItem, Cell> hashCell;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent.getAction().equals(GameGomokuActivity.BROADCAST_ACTION_RESETAI)) {
            Log.d("RESET BROADCASTRECEIVER", "!!!!!!");
            mHandler.sendEmptyMessage(RESET_BOARD);
        	}else if(intent.getAction().equals(GameGomokuActivity.BROADCAST_ACTION_UNDOAI)){
        		Log.d("UNDO BROADCASTRECEIVER", "!!!!!!");
        		mHandler.sendEmptyMessage(UNDO_BOARD);
        	}
        }
    };
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
//	    		    	   menu.removeAllChildren(true);
	    		    	   resetBoard();
	    		    	   deleteDatabase();
		    		    	CCDirector.sharedDirector().popScene();
	    		       }
	    		   });
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
	
	private void undo(){
		if(storehashCell.size()>0){
			DataBoard cell = storehashCell.get(storehashCell.size()-2);
			DataBoard item_comp = storehashCell.get(storehashCell.size()-1);
			CGPoint pos = item_comp.getPos();
			
			CCMenuItemImage item_compss = item_comp.getItem();
			CCMenuItemImage item_comps_replace = CCMenuItemImage.item("field.png","field.png", this,"onClickItem");
			item_comp.setPos(pos);
			CCMenuItemImage item = cell.getItem();
			CCMenuItemImage replace = CCMenuItemImage.item("field.png","field.png", this,"onClickItem");
			Log.d("UNDO", cell.getPos().toString());
			menu.removeChild(item, true);
			removeChild(item_temp, true);
			menu.removeChild(item_comp.getItem(), true);
			if(item_array_temp.size()>0){
				removeChild(item_array_temp.get(item_array_temp.size()-1), true);
				item_array_temp.remove(item_array_temp.size()-1);
			}
			
			Cell cellaa  = hashCell.get(item);
			cellaa.setItem(replace);
			cellaa.setStatus(CellStatus.empty);
			
			Cell cellmachine  = hashCell.get(item_compss);
			cellmachine.setItem(item_comps_replace);
			cellmachine.setStatus(CellStatus.empty);
			
			replace.setPosition(item.getPosition());
			item_comps_replace.setPosition(pos);
			
			hashCell.put(replace, cellaa);
			hashCell.put(item_comps_replace, cellmachine);
			
			
			menu.addChild(replace);
			menu.addChild(item_comps_replace);
			storehashCell.remove(storehashCell.size()-1);
			storehashCell.remove(storehashCell.size()-1);
			
			
			int count = storehashCell.size();
			if(count == 0){
				Log.d("Storehashcell", "Size = 0");
				isBegin = false;
			}
		}
	}
	private void resetBoard(){
		for (CCSprite sprite : item_array_temp) {
			removeChild(sprite, true);
		}
		item_array_temp.clear();
		removeChild(item_temp, true);
		menu.removeAllChildren(true);
		engine.isFinish = false;
		storehashCell.clear();
		hashCell.clear();
		items_store.clear();
	}
	
	private void createNewBoard(){
		isBegin = false;
		Log.d("Create New Board", "!!!!!!!!!!!");
		for (int i = 0; i < numberCellCol -1; i++)
			for (int j = 0; j < numberCellRow -1; j++) {
				cells[i][j] = new Cell();
				CCMenuItemImage item = CCMenuItemImage.item("field.png",
						"field.png", this, "onClickItem");		
				item.setPosition(CGPoint.ccp((i+1) * 40.0f, (j+1) * 40.0f));	
				cells[i][j].setItem(item);
				cells[i][j].setStatus(CellStatus.empty);
				cells[i][j].setRow(j);
				cells[i][j].setCol(i);
				
				hashCell.put(item, cells[i][j]);
//				items.add(arr_cells[i][j].getItem());
				items.add(new Cell(j, i, cells[i][j].getStatus(), cells[i][j].getItem()));
				items_store.add(item);
				
			}
		menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
		addChild(menu);
		menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
		
		engine = new Engine(cells, numberCellRow-1, numberCellCol-1,menu);
	}
	protected BoardAI(ccColor4B color) {
		super(color);
		mDB = new DBAdapter(GameGomokuActivity.cxt);
		mDB.open();
		item_array_temp = new ArrayList<CCSprite>();
		
		sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        sPlayerO = sounds.load(GameGomokuActivity.cxt, R.raw.explosion, 1);
        sPlayerX = sounds.load(GameGomokuActivity.cxt, R.raw.sfx_wrong, 1);
        
		IntentFilter filter = new IntentFilter();
        filter.addAction(GameGomokuActivity.BROADCAST_ACTION_RESETAI);
        filter.addAction(GameGomokuActivity.BROADCAST_ACTION_UNDOAI);
        GameGomokuActivity.cxt.registerReceiver(receiver, filter);
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
		storehashCell = new ArrayList<DataBoard>();
		
		numberCellCol = (int) boardWidth / CellWidth;
		numberCellRow = (int) boardHeight / CellHeight;
		
		
		items = new ArrayList<Cell>();
		items_store = new ArrayList<CCMenuItem>();
		cells = new Cell[numberCellCol][numberCellRow];
		
		hashCell = new Hashtable<CCMenuItem, Cell>();
		Log.e("Trung", "boardHeight :"+ String.valueOf(boardHeight));
		Log.e("Trung", "boardWidth :"+ String.valueOf(boardWidth));
		Log.e("Trung", "CellRow :"+ String.valueOf(numberCellRow));
		Log.e("Trung", "CellCol :"+ String.valueOf(numberCellCol));
		
		storehashCell.clear();
		hashCell.clear();
		items_store.clear();
		
		if(getDataBoard() && MainMenu.mGetData){
			Log.d("Item_Board", "Get Data from sqlite!!");
			menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
			addChild(menu);
			menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
			eBoard = new EvalBoard(numberCellCol,numberCellRow);
			engine = new Engine(cells, numberCellRow-1, numberCellCol-1,menu);
			isBegin = true;
		}else{
			for (int i = 0; i < numberCellCol -1; i++)
				for (int j = 0; j < numberCellRow -1; j++) {
					cells[i][j] = new Cell();
					CCMenuItemImage item = CCMenuItemImage.item("field.png",
							"field.png", this, "onClickItem");		
					item.setPosition(CGPoint.ccp((i+1) * 40.0f, (j+1) * 40.0f));	
					cells[i][j].setItem(item);
					cells[i][j].setStatus(CellStatus.empty);
					cells[i][j].setRow(j);
					cells[i][j].setCol(i);			
					hashCell.put(item, cells[i][j]);
					items.add(new Cell(j, i, cells[i][j].getStatus(), cells[i][j].getItem()));
					items_store.add(cells[i][j].getItem());
					
				}
			 eBoard = new EvalBoard(numberCellCol,numberCellRow);
			menu =  CCMenu.menu((CCMenuItem[]) items_store.toArray(new CCMenuItem[items_store.size()]));
			addChild(menu);
			menu.setPosition(CGPoint.ccp(1.0f, 1.0f));
			engine = new Engine(cells, numberCellRow-1, numberCellCol-1,menu);
		}
	}
	CCSprite item_player_temp;
	ArrayList<CCSprite> item_array_temp;
	public void onClickItem() {
		CCMenuItem item = menu.getSelectedItem();
		if(hashCell.containsKey(item) == true && engine.isFinish == false){
			Cell cell = hashCell.get(item);
			
			CCMenuItemImage newitem = null;
			if(cell.getStatus() == CellStatus.empty){
				
					newitem = cell.getItem().item("field.png", "field.png",this, "onClickItem");
					item_player_temp = CCSprite.sprite("o_cur.png");
					item_player_temp.setPosition(item.getPosition());
					addChild(item_player_temp);
					item_array_temp.add(item_player_temp);
					menu.removeChild(item, true);
					cell.setStatus(CellStatus.playerO);
					EvalChessBoard(1, eBoard);
					newitem.setPosition(cell.getItem().getPosition());
					hashCell.put(newitem, cell);			
					menu.addChild(newitem);
					if(MainMenu.sound == 1){
						sounds.play(sPlayerO, 1.0f, 1.0f, 0, 0, 1.5f);
					}
					
					storehashCell.add(new DataBoard(cell.getItem(),cell.getItem().getPosition(),cell.getItem().getPosition().x,
							cell.getItem().getPosition().y,cell.getRow(),cell.getCol(),cell.getStatus().toString()));
					int type = engine.totalCheck(cell.getCol(),cell.getRow() );
					int value = engine.setFinish(type, cell.getCol(),cell.getRow());
					if(value == 0){
						mHandler.sendEmptyMessage(XFINISH_BOARD);
					}else if(value == 1){
						mHandler.sendEmptyMessage(OFINISH_BOARD);
					}
					
				finishTurn();
				isBegin = true;
			}			
		}
	}
	
	int _x, _y;
	CCSprite item_temp = null;
	private void finishTurn() {
			if(engine.isFinish == false){
				FindMove();
				  EvalChessBoard(2, eBoard);
			        Point temp = new Point();
			        temp = eBoard.MaxPos();
			        _x = temp.getX();
			        _y = temp.getY();		       
			        Log.e("Trung","COmPUTER_X :"+ String.valueOf(_x) );  Log.e("Trung","COmPUTER_Y :"+ String.valueOf(_y) );
			        Log.e("Trung","COmPUTER_X_CELL :"+ String.valueOf(cells[_x][_y].getCol()) );  Log.e("Trung","COmPUTER_Y_CELL :"+ String.valueOf(cells[_x][_y].getRow()) );
			        CCMenuItemImage newitem = CCMenuItemImage.item("x_cur.png", "x_cur.png",this, "onClickItem");
			        cells[_x][_y].setStatus(CellStatus.playerX);
					newitem.setPosition(CGPoint.ccp((_x+1) * 40.0f, (_y+1) * 40.0f));
					if(item_temp!=null){
						removeChild(item_temp, true);
					}
					item_temp = CCSprite.sprite("x_sign05.png");
					CCAnimation animation = CCAnimation
							.animation("dance", 0.1f);
					for (int i = 1; i <= 5; i++) {
						animation.addFrame(String.format("x_sign0%d.png", i));
						Log.e("Trung", "HTU");
					}
					CCIntervalAction interval = CCAnimate.action(animation);
					item_temp.setPosition(CGPoint.ccp((_x + 1) * 40.0f,
							(_y + 1) * 40.0f));

					item_temp.runAction(interval);
					addChild(item_temp);
					
					Cell cell = null;
					for(int i =0;i<items_store.size();i++){
						CGPoint pos = items_store.get(i).getPosition();
						if(CGPoint.ccp((_x+1) * 40.0f, (_y+1) * 40.0f).toString().equalsIgnoreCase(pos.toString())){
							Log.d("removeitem", "!!!!");
							cell  = hashCell.get(items_store.get(i));
							menu.removeChild(items_store.get(i), true);
							menu.addChild(newitem);
							break;
						}
					}
					if(MainMenu.sound == 1){
						sounds.play(sPlayerX, 1.0f, 1.0f, 0, 0, 1.5f);
					}
					
					cell.setItem(newitem);
					cell.setStatus(CellStatus.playerX);
					hashCell.put(newitem, cell);
					storehashCell.add(new DataBoard(newitem,newitem.getPosition(),newitem.getPosition().x,
							newitem.getPosition().y,_y,_x,String.valueOf(CellStatus.playerX)));
					int type = engine.totalCheck(_x,_y);
					int value = engine.setFinish(type, _x,_y);
					if(value == 0){
						mHandler.sendEmptyMessage(XFINISH_BOARD);
					}else if(value == 1){
						mHandler.sendEmptyMessage(OFINISH_BOARD);
					}
			}
    }
	CCMenuItemImage newitem1 = null;
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
	        			cells[col][row]  = new Cell();
	        			cells[col][row].setItem(playerX);
	        			cells[col][row].setRow(row);
	        			cells[col][row].setCol(col);
	        			cells[col][row].setStatus(CellStatus.playerX);
	        			items_store.add(playerX);
	        			hashCell.put(playerX, cells[col][row]);
	        		}else if(status.equalsIgnoreCase("playerO")){
	        			CCMenuItemImage playerO= CCMenuItemImage.item("o_cur.png", "o_cur.png",this,"onClickItem");
	        			playerO.setPosition(CGPoint.ccp(posX, posY));
	        			cells[col][row] = new Cell();
	        			cells[col][row].setItem(playerO);
	        			cells[col][row].setRow(row);
	        			cells[col][row].setCol(col);
	        			cells[col][row].setStatus(CellStatus.playerO);
	        			items_store.add(playerO);
	        			hashCell.put(playerO, cells[col][row]);
	        		}else if(status.equalsIgnoreCase("field")){
	        			CCMenuItemImage field= CCMenuItemImage.item("field.png", "field.png",this,"onClickItem");
	        			field.setPosition(CGPoint.ccp(posX, posY));
	        			cells[col][row] = new Cell();
	        			cells[col][row].setItem(field);
	        			cells[col][row].setRow(row);
	        			cells[col][row].setCol(col);
	        			cells[col][row].setStatus(CellStatus.empty);
	        			items_store.add(field);
	        			hashCell.put(field, cells[col][row]);
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
	
	@Override
	public void onExit() {
		Log.d("ONEXITCCLAYER", "!!!!!!");
		MainMenu.mGetData = false;
		isBegin = false;
		m_Boardexist = false;
		mDB.close();
		GameGomokuActivity.cxt.unregisterReceiver(receiver);
		super.onExit();
	}
	
	public int[] DScore = new int[] { 0, 1, 9, 81, 729 };
    
    public int[] AScore = new int[] { 0, 2, 18, 162, 1458 };
    
    EvalBoard eBoard;
	
    public static int maxDepth = 11;
    public static int maxMove = 3;
	
    private void FindMove()
    {
        Point temp = new Point();
        for (int i = 0; i < maxMove; i++)
        {
            temp = eBoard.MaxPos();
            eBoard.EBoard[temp.getX()][ temp.getY()] = 0;
        }
    }
	
	 private void EvalChessBoard(int player,EvalBoard eBoard)
     {
         int rw, cl, ePC, eHuman;
         eBoard.ResetBoard();
         Log.e("Trung", "EvalChessBoard");
         try{
         for (cl = 0; cl < numberCellCol-1; cl++)            
             for (rw = 0; rw < (numberCellRow - 4)-1; rw++)
             {
                 ePC = 0; eHuman = 0;
                 for (int i = 0; i < 5; i++)
                 {
                     if (cells[cl][ rw + i].getStatus() == CellStatus.playerO) eHuman++;
                     if (cells[cl][ rw + i].getStatus() == CellStatus.playerX) ePC++;
                 }

                 if (eHuman * ePC == 0 && eHuman != ePC)
                 {
                     for (int i = 0; i < 5; i++)
                     {
                         if (cells[cl][ rw + i].getStatus() == CellStatus.empty) // Neu o chua duoc danh
                         {
                             if (eHuman == 0)
                                 if (player == 1)
                                	 eBoard.EBoard[cl][rw + i] += DScore[ePC];
                                 else eBoard.EBoard[cl][rw + i] += AScore[ePC];
                             if (ePC == 0)
                                 if (player == 2)
                                     eBoard.EBoard[cl][rw + i] += DScore[eHuman];
                                 else eBoard.EBoard[cl][rw + i] += AScore[eHuman];
                             if (eHuman == 4 || ePC == 4)
                                 eBoard.EBoard[cl][ rw + i] *= 2;
                         }
                     }
                     
                 }                
              }
         }catch(Exception ex){Log.e("ABC", ex.getMessage());}
         Log.e("Trung", "//Danh gia theo hang");
         //Danh gia theo cot
         for (rw = 0; rw < numberCellRow-1; rw++)
             for (cl = 0; cl < (numberCellCol - 4)-1; cl++)
             {
                 ePC = 0; eHuman = 0;
                 for (int i = 0; i < 5; i++)
                 {
                     if (cells[cl + i][rw].getStatus() == CellStatus.playerO) eHuman++;
                     if (cells[cl + i][rw].getStatus() == CellStatus.playerX) ePC++;
                 }

                 if (eHuman * ePC == 0 && eHuman != ePC)
                 {
                     for (int i = 0; i < 5; i++)
                     {
                         if (cells[cl + i][rw].getStatus() == CellStatus.empty) // Neu o chua duoc danh
                         {
                             if (eHuman == 0)
                                 if (player == 1)
                                     eBoard.EBoard[cl + i][rw] += DScore[ePC];
                                 else eBoard.EBoard[cl + i][rw] += AScore[ePC];
                             if (ePC == 0)
                                 if (player == 2)
                                     eBoard.EBoard[cl + i][ rw] += DScore[eHuman];
                                 else eBoard.EBoard[cl + i][rw] += AScore[eHuman];
                             if (eHuman == 4 || ePC == 4)
                                 eBoard.EBoard[cl + i][rw] *= 2;
                         }
                     }

                 }
             }
         Log.e("Trung", "//Danh gia theo cot");
//         //Danh gia duong cheo xuong
         for (cl = 0; cl < (numberCellCol - 4)-1; cl++)
             for (rw = 0; rw < (numberCellRow - 4)-1; rw++)
             {
                 ePC = 0; eHuman = 0;
                 for (int i = 0; i < 5; i++)
                 {
                     if (cells[cl + i][rw + i].getStatus() == CellStatus.playerO) eHuman++;
                     if (cells[cl + i][rw + i].getStatus() == CellStatus.playerX) ePC++;
                 }

                 if (eHuman * ePC == 0 && eHuman != ePC)
                 {
                     for (int i = 0; i < 5; i++)
                     {
                         if (cells[cl + i][rw + i].getStatus() == CellStatus.empty) // Neu o chua duoc danh
                         {
                             if (eHuman == 0)
                                 if (player == 1)
                                     eBoard.EBoard[cl + i][rw + i] += DScore[ePC];
                                 else eBoard.EBoard[cl + i][rw + i] += AScore[ePC];
                             if (ePC == 0)
                                 if (player == 2)
                                     eBoard.EBoard[cl + i][rw + i] += DScore[eHuman];
                                 else eBoard.EBoard[cl + i][rw + i] += AScore[eHuman];
                             if (eHuman == 4 || ePC == 4)
                                 eBoard.EBoard[cl + i][rw + i] *= 2;
                         }
                     }

                 }
             }

//         //Danh gia duong cheo len
         for (cl = 4; cl < numberCellCol-1; cl++)
             for (rw = 0; rw < (numberCellRow - 4)-1; rw++)
             {
                 ePC = 0; eHuman = 0;
                 for (int i = 0; i < 5; i++)
                 {
                     if (cells[cl - i][rw + i].getStatus() == CellStatus.playerO) eHuman++;
                     if (cells[cl - i][rw + i].getStatus() == CellStatus.playerX) ePC++;
                 }

                 if (eHuman * ePC == 0 && eHuman != ePC)
                 {
                     for (int i = 0; i < 5; i++)
                     {
                         if (cells[cl - i][rw + i].getStatus() == CellStatus.empty) // Neu o chua duoc danh
                         {
                             if (eHuman == 0)
                                 if (player == 1)
                                     eBoard.EBoard[cl - i][rw + i] += DScore[ePC];
                                 else eBoard.EBoard[cl - i][rw + i] += AScore[ePC];
                             if (ePC == 0)
                                 if (player == 2)
                                     eBoard.EBoard[cl - i][rw + i] += DScore[eHuman];
                                 else eBoard.EBoard[cl - i][rw + i] += AScore[eHuman];
                             if (eHuman == 4 || ePC == 4)
                                 eBoard.EBoard[cl - i][rw + i] *= 2;
                         }
                     }

                 }
             }
     }
}

