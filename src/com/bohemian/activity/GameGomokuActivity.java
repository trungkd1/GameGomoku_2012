package com.bohemian.activity;

import java.io.File;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bohemian.board.Board;
import com.bohemian.board.BoardAI;
import com.bohemian.board.Cell;
import com.bohemian.board.DBAdapter;
import com.bohemian.board.DataBoard;
import com.bohemian.board.Information;
import com.bohemian.board.MainMenu;

public class GameGomokuActivity extends Activity {
    /** Called when the activity is first created. */
	public static Context cxt ;
	public static DBAdapter mDB;
	public Cursor mCursor;
	public static String BROADCAST_ACTION_RESET = "SendReset";
	public static String BROADCAST_ACTION_UNDO = "SendUndo";
	public static String BROADCAST_ACTION_RESETAI = "SendResetAI";
	public static String BROADCAST_ACTION_UNDOAI = "SendUndoAI";
    
    
protected CCGLSurfaceView _glSurfaceView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mDB = new DBAdapter(this);
		cxt = GameGomokuActivity.this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		_glSurfaceView = new CCGLSurfaceView(this);
		
		setContentView(_glSurfaceView);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		
		CCDirector.sharedDirector().attachInView(_glSurfaceView);
		CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
//		CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		
		CCDirector.sharedDirector().setDisplayFPS(false);
		
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
		
		
//		CCScene scene = Board.scene(this);
		CCScene scene = MainMenu.scene(this);                                                                
		CCDirector.sharedDirector().runWithScene(scene);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		CCDirector.sharedDirector().pause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		CCDirector.sharedDirector().resume();
	}
	
	@Override
	public void onStop()
	{
//		mDB.close();
		super.onStop();
//		CCDirector.sharedDirector().end();
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {  
//    	MenuInflater inflater = getMenuInflater();
//    	inflater.inflate(R.menu.menu, menu);
		menu.add(1, 1, 0, "").setIcon(R.drawable.bush);
		menu.add(1, 2, 0, "").setIcon(R.drawable.undo_button_selected);
    	return true;
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Log.d(this.getClass().getName(), "menu button pressed");
	    }
		else if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Log.d(this.getClass().getName(), "back button pressed");
	    	 if(MainMenu.m_exist_mainmenu && !Board.m_Boardexist  && !Information.m_infoexist && !BoardAI.m_Boardexist){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Are you sure to exit the game?")
	    		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("MainMenuCheck", "Here!!!");
	    		    	   MainMenu.m_exist_mainmenu = false;
	    		    	   SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("MainMenu", 0);
	    		           SharedPreferences.Editor prefsEditor = myPrefs.edit();
	    		           prefsEditor.putInt("Sound",MainMenu.sound );
	    		           prefsEditor.commit();
	    		           Log.d("StorePreference", "Success");
	    		    	   System.exit(0);
	    		       }
	    		   })
	    		   .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		            dialog.cancel();
	    		       }
	    		   });
	    		builder.show();
	    	}else if(Board.m_Boardexist && !com.bohemian.engine.Engine.isFinish && Board.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Do you want to save?")
	    		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	Board.m_Boardexist = false;
	    		    	MainMenu.mGetData = true;
	    		    	storeTurn();
	    		    	storeBoard();
	    		   		CCDirector.sharedDirector().popScene();
	    		    	   
	    		       }
	    		   })
	    		   .setNeutralButton("No", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	Board.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCDirector.sharedDirector().popScene();
	    		       }
	    		   })
	    		   ;
	    		builder.show();
	    	}else if(Board.m_Boardexist && !Board.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Your choice?")
	    		   .setPositiveButton("MainMenu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	Board.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCScene scene = MainMenu.scene(GameGomokuActivity.this);
	    		   		CCDirector.sharedDirector().replaceScene(scene);
	    		       }
	    		   })
	    		   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		            dialog.cancel();
	    		       }
	    		   });
	    		builder.show();
	    	}
	    	else if(Board.m_Boardexist && com.bohemian.engine.Engine.isFinish && Board.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Your choice?")
	    		   .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	   	sendMessageReset();
	    		    	   	}
	    		   })

	    		   .setNeutralButton("MainMenu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	Board.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCScene scene = MainMenu.scene(GameGomokuActivity.this);
	    		   		CCDirector.sharedDirector().replaceScene(scene);
	    		       }
	    		   })
	    		   ;
	    		builder.show();
	    	}
	    	else if(BoardAI.m_Boardexist && !com.bohemian.engine.Engine.isFinish && BoardAI.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Do you want to save?")
	    		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardAI", "Here!!!");
    		    	    BoardAI.m_Boardexist = false;
	    		    	MainMenu.mGetData = true;
//	    		    	storeTurn();
	    		    	storeBoardAI();
	    		   		CCDirector.sharedDirector().popScene();
	    		    	   
	    		       }
	    		   })
	    		   .setNeutralButton("No", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	   BoardAI.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCDirector.sharedDirector().popScene();
	    		       }
	    		   })
	    		   ;
	    		builder.show();
	    	}else if(BoardAI.m_Boardexist && !BoardAI.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Your choice?")
	    		   .setPositiveButton("MainMenu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	   BoardAI.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCScene scene = MainMenu.scene(GameGomokuActivity.this);
	    		   		CCDirector.sharedDirector().replaceScene(scene);
	    		    	   
	    		       }
	    		   })
	    		   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		            dialog.cancel();
	    		       }
	    		   });
	    		builder.show();
	    	}
	    	else if(BoardAI.m_Boardexist && com.bohemian.engine.Engine.isFinish && BoardAI.isBegin){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setTitle("Caro");
	    		builder.setIcon(R.drawable.ic_launcher);
	    		builder.setMessage("Your choice?")
	    		   .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	   	sendMessageResetAI();
	    		    	   	}
	    		   })

	    		   .setNeutralButton("MainMenu", new DialogInterface.OnClickListener() {
	    		       public void onClick(DialogInterface dialog, int id) {
	    		    	   Log.d("BoardCheck", "Here!!!");
	    		    	   BoardAI.m_Boardexist = false;
	    		    	deleteDatabase();
	    		    	CCScene scene = MainMenu.scene(GameGomokuActivity.this);
	    		   		CCDirector.sharedDirector().replaceScene(scene);
	    		       }
	    		   })
	    		   ;
	    		builder.show();
	    	}
	    	else if(Information.m_infoexist){
	    		Information.m_infoexist = false;
	    		CCDirector.sharedDirector().replaceScene(MainMenu.scene(this));
	    	}
	    }
	    return false;
	}
	

	private void sendMessageReset(){
		Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION_RESET);
        sendBroadcast(broadcast);
	}
	
	private void sendMessageUndo(){
		Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION_UNDO);
        sendBroadcast(broadcast);
	}
	
	private void sendMessageResetAI(){
		Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION_RESETAI);
        sendBroadcast(broadcast);
	}
	
	private void sendMessageUndoAI(){
		Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION_UNDOAI);
        sendBroadcast(broadcast);
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
	
	private void storeTurn(){
		SharedPreferences myPrefs = this.getSharedPreferences("BoardTurn", 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putInt("FlagTurn",Board.flag);
        prefsEditor.commit();
        Log.d("StorePreference", "Success");
	}
	
	private boolean checkPos(String pos){
		mCursor = mDB.search(pos);
		if (mCursor.getCount()>0) {
	        return true;
	    }
		
	    return false;
	}
	
	private void storeBoard(){
		new StoreDatabase().execute("");
	}
	
	private void storeBoardAI(){
		new StoreDatabaseAI().execute("");
	}
	
	public class StoreDatabase extends AsyncTask<String, Void, Void>{
		
		private ProgressDialog dialog = new ProgressDialog(GameGomokuActivity.cxt);
		
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Saving.......");
			this.dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			mDB.open();
			DataBoard board_cell;
			Cell item_board;
			for(int i =0;i<Board.items.size();i++){
				item_board = Board.items.get(i);
//				Log.d("POSITION",item_board.getPosition().toString());
//				Log.d("STATUS","field");
				mDB.createItem(item_board.getItem().getPosition().toString(),item_board.getItem().getPosition().x,
						item_board.getItem().getPosition().y,item_board.getRow(),item_board.getCol(),"field");
			}
			for (int i = 0; i < Board.storehashCell.size(); i++) {
				board_cell = Board.storehashCell.get(i);
				if(checkPos( board_cell.getPos().toString())){
					Log.d("Delete!!!!", "!!!!!!!!!!!!!");
					mDB.deleteCell(board_cell.getPos().toString());
				}
				Log.d("POSITION", board_cell.getPos().toString());
				Log.d("STATUS",board_cell.getStatus());
				mDB.createItem(board_cell.getPos().toString(),board_cell.getPosX(),
						board_cell.getPosY(),board_cell.getRow(),board_cell.getCol(), board_cell.getStatus());
			}
			mDB.close();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	        }
			super.onPostExecute(result);
		}
		
	}
	
public class StoreDatabaseAI extends AsyncTask<String, Void, Void>{
		
		private ProgressDialog dialog = new ProgressDialog(GameGomokuActivity.cxt);
		
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Saving.......");
			this.dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			mDB.open();
			DataBoard board_cell;
			Cell item_board;
			for(int i =0;i<BoardAI.items.size();i++){
				item_board = BoardAI.items.get(i);
//				Log.d("POSITION",item_board.getItem().getPosition().toString());
//				Log.d("STATUS","field");
				mDB.createItem(item_board.getItem().getPosition().toString(),item_board.getItem().getPosition().x,
						item_board.getItem().getPosition().y,item_board.getRow(),item_board.getCol(),"field");
			}
			for (int i = 0; i < BoardAI.storehashCell.size(); i++) {
				board_cell = BoardAI.storehashCell.get(i);
				if(checkPos( board_cell.getPos().toString())){
					Log.d("Delete!!!!", "!!!!!!!!!!!!!");
					mDB.deleteCell(board_cell.getPos().toString());
				}
				Log.d("POSITION", board_cell.getPos().toString());
				Log.d("STATUS",board_cell.getStatus());
				mDB.createItem(board_cell.getPos().toString(),board_cell.getPosX(),
						board_cell.getPosY(),board_cell.getRow(),board_cell.getCol(), board_cell.getStatus());
			}
			mDB.close();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	        }
			super.onPostExecute(result);
		}
		
	}
 
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
 
    	switch (item.getItemId()) {
    	case 1 : {
    		if(Board.m_Boardexist && !BoardAI.m_Boardexist){
    			sendMessageReset();
    		}else{
    			sendMessageResetAI();
    		}
    		
    		break;
    	}
    	case 2: {
    		if(Board.m_Boardexist && !BoardAI.m_Boardexist){
    			sendMessageUndo();
    		}else{
    			sendMessageUndoAI();
    		}
    		
    		break;
    	}
    	default : {
    		Toast.makeText(getApplicationContext(), "Selected menu item", Toast.LENGTH_SHORT).show();
    	}
    	}
 
    	return super.onMenuItemSelected(featureId, item);
    }
 
    @Override
    protected void onDestroy() {
//    	mDB.close();
    	super.onDestroy();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(MainMenu.m_exist_mainmenu){
    		menu.setGroupVisible(1, false);
    	}if(Information.m_infoexist){
    		menu.setGroupVisible(1, false);
    	}if(Board.m_Boardexist){
    		menu.setGroupVisible(1, true);
    	}if(BoardAI.m_Boardexist){
    		menu.setGroupVisible(1, true);
    	}
    	
    	
    	return super.onPrepareOptionsMenu(menu);
    }
}
