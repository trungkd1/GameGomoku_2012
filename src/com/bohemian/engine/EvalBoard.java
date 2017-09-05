package com.bohemian.engine;


public class EvalBoard {

	 public int height, width;
     public int[][] EBoard;
     public EvalBoard(int numberCellCol,int numberCellRow)
     {
         height = numberCellCol;
         width = numberCellRow;
         EBoard = new int[height][width];
         ResetBoard();
     }

     public void ResetBoard()
     {
         for (int r = 0; r < height; r++)
             for (int c = 0; c < width; c++)
                 EBoard[r][c] = 0;
     }

     public Point MaxPos()
     {
         int Max = 0;
         Point p = new Point(); 
         for (int i = 0; i < height; i++)
         {
             for (int j = 0; j < width; j++)
             {
                 if (EBoard[i][j] > Max)
                 {
                     p.setX(i); p.setY(j);
                     Max = EBoard[i][ j];
                 }
                 
             }
         }
         return p;
     }




}
