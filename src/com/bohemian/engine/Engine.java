package com.bohemian.engine;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.types.CGPoint;

import android.util.Log;

import com.bohemian.board.Cell;



public class Engine {

	private Cell[][] board;
	
	private int numberCellRow;
	private int numberCellCol;

	public static boolean isFinish = false;

	private CCMenu menu = null;
	public Engine(Cell[][] board,int numberCellRow,int numberCellCol,CCMenu menu){
		this.board = board;
		this.numberCellCol = numberCellCol;
		this.numberCellRow = numberCellRow;
		this.menu = menu;
	}
	
	public  Boolean checkVertical(int a, int b) {
		int count = 1;
		int col = a + 1;
//		Log.e("Vertical", "a :"+String.valueOf(a)+" ,b :"+ String.valueOf(b));
		while (col < numberCellCol
				&& board[a][b].getStatus() == board[col][b].getStatus()) {
			count++;
			col++;
		}
		col = a - 1;
		while (col >= 0 && board[a][b].getStatus() == board[col][b].getStatus()) {
			count++;
			col--;
		}
		return checkCount(count);
	}

	public Boolean checkHorizontal(int a, int b) {
		int count = 1;
		int row = b + 1;
//		Log.e("HOrizol", "a :"+String.valueOf(a)+" ,b :"+ String.valueOf(b));
		while (row < numberCellRow
				&& board[a][b].getStatus() == board[a][row].getStatus()) {
			count++;
			row++;
		}
		
		row = b - 1;
		while (row >= 0 && board[a][b].getStatus() == board[a][row].getStatus()) {
			count++;
			row--;
		}
		return checkCount(count);
	}

	public Boolean checkMainDiagonal(int a, int b) {
		int count = 1;
		int col = a + 1;
		int row = b + 1;
		Log.e("checkMainDiagonal", "a :"+String.valueOf(a)+" ,b :"+ String.valueOf(b));
		while (row < numberCellRow && col < numberCellCol
				&& board[a][b].getStatus() == board[col][row].getStatus()) {
			count = count + 1;
			row++;
			col++;
		}
		col = a - 1;
		row = b - 1;
		while (row >= 0 && col >= 0
				&& board[a][b].getStatus() == board[col][row].getStatus()) {
			count = count + 1;
			row--;
			col--;
		}
		return checkCount(count);

	}

	public Boolean checkSecondDiagonal(int a, int b) {
		int count = 1;
		int col = a + 1;
		int row = b - 1;
		while (col < numberCellCol && row >= 0
				&& board[a][b].getStatus() == board[col][row].getStatus()) {
			count++;
			row--;
			col++;
		}
		col = a - 1;
		row = b + 1;
		while (col >= 0 && row < numberCellRow
				&& board[a][b].getStatus() == board[col][row].getStatus()) {
			count++;
			col--;
			row++;
		}
		return checkCount(count);
	}
	/**
	 * return true if count >= 5
	 * @param count number of same cells 
	 */
	public static boolean checkCount(int count) {Log.e("count", "Count :"+String.valueOf(count));
		return (count >= 5) ? true : false;
	}

	/**
	 * @param a
	 *            the cell's position in row.
	 * @param b
	 *            the cell's position in col.
	 */
	public int setFinish(int type, int a, int b) {
		int row, col;
		Log.e("Trung", "GameBoard__setFinish__tyep :"+String.valueOf(type));
		switch (type) {
		case 1:// finish in Horizontal direction
			Log.i("board", "finish Horizontal direction");
			row = b + 1;
			while (row < numberCellRow
					&& board[a][b].getStatus() == board[a][row].getStatus()) {
				changeToWinStatus(board[a][row]);
				row++;
			}
			row = b - 1;
			while (row >= 0
					&& board[a][b].getStatus() == board[a][row].getStatus()) {
				changeToWinStatus(board[a][row]);
				row--;
			}
			changeToWinStatus(board[a][b]);
			isFinish = true;
			if(board[a][b].getStatus() == CellStatus.playerXWin){
				return 0;
			}else if(board[a][b].getStatus() == CellStatus.playerOWin){
				return 1;
//				mHandler.sendEmptyMessage(OFINISH_BOARD);
			}
			break;
		case 2:// finish in column direction
			Log.i("board", "finish in Verticle direction");
			col = a + 1;
			while (col < numberCellCol
					&& board[a][b].getStatus() == board[col][b].getStatus()) {
				changeToWinStatus(board[col][b]);
				col++;
			}
			col = a - 1;
			while (col >= 0
					&& board[a][b].getStatus() == board[col][b].getStatus()) {
				changeToWinStatus(board[col][b]);
				col--;
			}
			changeToWinStatus(board[a][b]);
			isFinish = true;
			if(board[a][b].getStatus() == CellStatus.playerXWin){
//				mHandler.sendEmptyMessage(XFINISH_BOARD);
				return 0;
			}else if(board[a][b].getStatus() == CellStatus.playerOWin){
				return 1;
//				mHandler.sendEmptyMessage(OFINISH_BOARD);
			}
			break;
		case 3: // finish in Main diagonal direction
			Log.i("board", "finish in Main diagonal direction");
			col = a + 1;
			row = b + 1;
			while (row < numberCellRow && col < numberCellCol
					&& board[a][b].getStatus() == board[col][row].getStatus()) {
				changeToWinStatus(board[col][row]);
				row++;
				col++;
			}

			col = a - 1;
			row = b - 1;
			while (row >= 0 && col >= 0
					&& board[a][b].getStatus() == board[col][row].getStatus()) {
				changeToWinStatus(board[col][row]);
				row--;
				col--;
			}
			changeToWinStatus(board[a][b]);
			isFinish = true;
			if(board[a][b].getStatus() == CellStatus.playerXWin){
//				mHandler.sendEmptyMessage(XFINISH_BOARD);
				return 0;
			}else if(board[a][b].getStatus() == CellStatus.playerOWin){
				return 1;
//				mHandler.sendEmptyMessage(OFINISH_BOARD);
			}
			break;
		case 4:
			Log.i("board", "finish in Second diagonal direction");
			col = a + 1;
			row = b - 1;
			while (col < numberCellCol && row >= 0
					&& board[a][b].getStatus() == board[col][row].getStatus()) {
				changeToWinStatus(board[col][row]);

				col++;
				row--;
			}
			col = a - 1;
			row = b + 1;
			while (col >= 0 && row < numberCellRow
					&& board[a][b].getStatus() == board[col][row].getStatus()) {
				changeToWinStatus(board[col][row]);
				col--;
				row++;
			}
			changeToWinStatus(board[a][b]);
			isFinish = true;
			if(board[a][b].getStatus() == CellStatus.playerXWin){
//				mHandler.sendEmptyMessage(XFINISH_BOARD);
				return 0;
			}else if(board[a][b].getStatus() == CellStatus.playerOWin){
//				mHandler.sendEmptyMessage(OFINISH_BOARD);
				return 1;
			}
			break;
		}
		return 2;
	}

	private void changeToWinStatus(Cell c){
		Log.e("Finish!!", "playerXWin THang gui```!!!");
		if (c.getStatus() == CellStatus.playerX) {
			c.setStatus(CellStatus.playerXWin);	
			CGPoint point = c.getItem().getPosition();
			menu.removeChild(c.getItem(), true);
			CCMenuItem item_replace = CCMenuItemImage.item("x_win.png", "x_win.png");
			item_replace.setPosition(point);
			menu.addChild(item_replace);
			Log.e("Finish!!", "playerXWin THang gui```!!!");
		} else if (c.getStatus() == CellStatus.playerO) {
			c.setStatus(CellStatus.playerOWin);
			CGPoint point = c.getItem().getPosition();
			menu.removeChild(c.getItem(), true);
			CCMenuItem item_replace = CCMenuItemImage.item("o_win.png", "o_win.png");
			item_replace.setPosition(point);
			menu.addChild(item_replace);
;			Log.e("Finish!!", "playerOWin THang gui```!!!");
		}
//		c.invalidate();
	}
	
	public  int totalCheck(int x, int y) {
		if (checkHorizontal(x, y))
			return 1; // finish in horizontal
		if (checkVertical(x, y))
			return 2; // finish in vertical
		if (checkMainDiagonal(x, y))
			return 3;
		if (checkSecondDiagonal(x, y))
			return 4;
		return -1; // Unknown
	}

}
