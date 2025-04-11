package com.chess.mainwindow.game.move ;

import java.io.Serializable; 

public class Move implements Serializable{
  int[] prevPosition = null;
  int[] currPosition = null;

  public Move(){
    
  }

  public void setPrevPosition(int row, int col){
    this.prevPosition = new int[]{row,col} ;
  }

  public void currPosition(int row, int col){
    this.currPosition = new int[]{row, col} ;
  }

  public int[] getPrevPosition(){
    return this.prevPosition ;
  }

  public int[] getcurrPosition(){
    return this.currPosition ;
  }

  public void transform(){
    
  }

  public void parse(String string){
    String[] moveSet = string.split("/");
    prevPosition = {Integer.parseInt(moveSet[1]), Integer.parseInt(moveSet[2])};
    currPosition = {Integer.parseInt(moveSet[3]), Integer.parseInt(moveSet[4])};
  }
}
