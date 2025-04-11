package com.chess.mainwindow.game ;

import java.io.* ;
import java.util.ArrayList ;
import javax.swing.* ;
import java.awt.* ;
import java.util.concurrent.locks.*;
import com.chess.mainwindow.game.chesspieces.* ;
import com.chess.mainwindow.game.board.* ;
import com.chess.mainwindow.* ;
import com.chess.eventhandlers.* ;
import com.chess.subwindow.* ;

public class Game implements Runnable {
  private final int TARGET_FPS = 100;
  private final int FRAME_TIME = 1000 / TARGET_FPS;
  private boolean running;
  private Thread gameThread;
  public Board board;
  public ArrayList<ChessPiece> pieces = new ArrayList<>();
  public MyMouse mouse;
  public ChessPiece activePiece;
  public boolean check;
  public boolean turn = true;
  public static int moveNumber = 0;
  public MainPanel boardPanel;
  public KingPiece whiteKing ;
  public KingPiece blackKing ;
  public Lock lock = new ReentrantLock() ;
  public static boolean promotionFlag = false ;
  public boolean playing_as_white;
  public BufferedReader reader = null ;
  public PrintWriter writer = null ;
  public static ChatPanel chatpanel = null;

  public Game(MainPanel boardPanel, MyMouse mouse, BufferedReader reader,PrintWriter writer) {
    this.mouse = mouse;
    board = new Board(pieces);
    this.boardPanel = boardPanel;
    this.reader = reader ;
    this.writer = writer ;
  }

  public synchronized void launch(boolean isWhite) {
    if (running)
      return;

    playing_as_white = isWhite;
    running = true;
    gameThread = new Thread(this);
    gameThread.start();
  }

  public void init() {
    lock.lock();
    try{
      setPieces();
    }catch(Exception e){
      e.printStackTrace();
    }
    lock.unlock();
  }

  public void setPieces() {
    boolean color;
    ChessPiece p;
      color = !playing_as_white; // set black pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set black pawns
        p = new PawnPiece(color, 1, i, board, pieces);
        pieces.add(p);
        board.state[1][i] = p;
      }

      // Accounting for white privilages
      if(color) {
        pieces.add(p = new KingPiece(color, 0, 3, board, pieces));
        blackKing = (KingPiece)p ;
        board.state[0][3] = p;
        pieces.add(p = new QueenPiece(color, 0, 4, board, pieces));
        board.state[0][4] = p;
      } else {
        pieces.add(p = new KingPiece(color, 0, 4, board, pieces));
        blackKing = (KingPiece)p ;
        board.state[0][4] = p;
        pieces.add(p = new QueenPiece(color, 0, 3, board, pieces));
        board.state[0][3] = p;
      }
      
      pieces.add(p = new RookPiece(color, 0, 0, board, pieces));
      board.state[0][0] = p;
      pieces.add(p = new RookPiece(color, 0, 7, board, pieces));
      board.state[0][7] = p;
      pieces.add(p = new KnightPiece(color, 0, 1, board, pieces));
      board.state[0][1] = p;
      pieces.add(p = new KnightPiece(color, 0, 6, board, pieces));
      board.state[0][6] = p;
      pieces.add(p = new BishopPiece(color, 0, 2, board, pieces));
      board.state[0][2] = p;
      pieces.add(p = new BishopPiece(color, 0, 5, board, pieces));
      board.state[0][5] = p;
      color = playing_as_white; // set white pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set white pawns
        p = new PawnPiece(color, 6, i, board, pieces);
        pieces.add(p);
        board.state[6][i] = p;
      }

      // Accounting for white privilages
      if(!color) {
        pieces.add(p = new KingPiece(color, 7, 3, board, pieces));
        whiteKing = (KingPiece)p ;
        board.state[7][3] = p;
        pieces.add(p = new QueenPiece(color, 7, 4, board, pieces));
        board.state[7][4] = p;
      } else {
        pieces.add(p = new KingPiece(color, 7, 4, board, pieces));
        whiteKing = (KingPiece)p ;
        board.state[7][4] = p;
        pieces.add(p = new QueenPiece(color, 7, 3, board, pieces));
        board.state[7][3] = p;
      }
      pieces.add(p = new RookPiece(color, 7, 0, board, pieces));
      board.state[7][0] = p;
      pieces.add(p = new RookPiece(color, 7, 7, board, pieces));
      board.state[7][7] = p;
      pieces.add(p = new KnightPiece(color, 7, 1, board, pieces));
      board.state[7][1] = p;
      pieces.add(p = new KnightPiece(color, 7, 6, board, pieces));
      board.state[7][6] = p;
      pieces.add(p = new BishopPiece(color, 7, 2, board, pieces));
      board.state[7][2] = p;
      pieces.add(p = new BishopPiece(color, 7, 5, board, pieces));
      board.state[7][5] = p;

  }

  public void update() {
    if(turn!=playing_as_white) return;
    synchronized (pieces) {
      if (mouse.pressed && !check) {
        ChessPiece piece = board.pieceAtPosition(mouse.pressedY, mouse.pressedX);
        if (piece != null && piece.color == turn) {
          activePiece = piece;
          board.setActiveBlock(mouse.pressedY, mouse.pressedX);
          mouse.x = mouse.pressedX;
          mouse.y = mouse.pressedY;
          check = true;
        }
      }
      if (!mouse.pressed && check) {
        if (activePiece !=null &&activePiece.canMove(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x), pieces, true)) {
          String move = "m/" + activePiece.row + "/" + activePiece.col + "/" +
          activePiece.getRow(mouse.y) + "/" + activePiece.getCol(mouse.x);
          try{
            writer.println(move) ;
            writer.flush() ;
          }catch(Exception e){
            
          }
          System.out.println(move) ;
          activePiece.update(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x));
          if(activePiece.path.contains("pawn")){
            PawnPiece temp = (PawnPiece) activePiece ; 
            temp.firstMove = false ;
          }
          pieces.remove(board.enemyPieceAtPosition(mouse.y, mouse.x, turn));
          board.updateActiveBlock(mouse.y, mouse.x);
          turn = !turn;
          boardPanel.repaint() ;
          if(promotionFlag == true){
            PawnPiece pawn = (PawnPiece)activePiece ;
            pawn.promote() ;
          }
          activePiece.lastMoveNumber = Game.moveNumber;
          Game.moveNumber++;


          if(blackKing.checkMate(pieces)){
            System.out.println("white wins") ;
          }
          else if( whiteKing.checkMate(pieces)){
            System.out.println("black wins") ;
          }
        } else {
          activePiece.originalPosition();
        }
        check = false;
        activePiece = null;
      }

      if (activePiece != null) {
        activePiece.drag(mouse.y, mouse.x);
      }
    }
  }

  // public void opponentMove(int prow, int pcol, int crow, int ccol){
  //   //transform
  //   int prevrow = 7 - prow ;
  //   int prevcol = 7 - pcol ;
  //   int currrow = 7 - crow ;
  //   int currcol = 7 - ccol ;
  //   //find active piece
  //   ChessPiece activePiece = board.pieceAtPosition(Board.SQUARE_SIZE*prevrow, Board.SQUARE_SIZE*prevcol);
  //   board.setActiveBlock(prevrow, prevcol);
  //   activePiece.update(currrow, currcol);

  //   if(activePiece.path.contains("pawn")){
  //     PawnPiece temp = (PawnPiece) activePiece ; 
  //     temp.firstMove = false ;
  //   }
  //   pieces.remove(board.enemyPieceAtPosition(Board.SQUARE_SIZE*currrow, Board.SQUARE_SIZE*currcol, turn));
  //   board.updateActiveBlock(Board.SQUARE_SIZE*currrow, Board.SQUARE_SIZE*currcol);
  //   turn = !turn;
  //   boardPanel.repaint() ;
  //   if(promotionFlag == true){
  //     PawnPiece pawn = (PawnPiece)activePiece ;
  //     pawn.promote() ;
  //   }
  //   activePiece.lastMoveNumber = Game.moveNumber;
  //   Game.moveNumber++;

  //   if(blackKing.checkMate(pieces)){
  //     System.out.println("white wins") ;
  //   }
  //   else if( whiteKing.checkMate(pieces)){
  //     System.out.println("black wins") ;
  //   }
  // }

  public void opponentMove(int prow, int pcol, int crow, int ccol){
    //transform
    int prevrow = 7 - prow ;
    int prevcol = 7 - pcol ;
    int currrow = 7 - crow ;
    int currcol = 7 - ccol ;


    ChessPiece piece = board.pieceAtPosition(prevrow*Board.SQUARE_SIZE, prevcol*Board.SQUARE_SIZE);
    if(piece == null) {
      System.out.println("NULL");
      return;
    }
    activePiece = piece;
    board.setActiveBlock(prevrow*Board.SQUARE_SIZE, prevcol*Board.SQUARE_SIZE);

    if(activePiece == null) return;
    activePiece.update(activePiece.getRow(currrow*Board.SQUARE_SIZE), activePiece.getCol(currcol*Board.SQUARE_SIZE));
    if(activePiece.path.contains("pawn")){
      PawnPiece temp = (PawnPiece) activePiece ; 
      temp.firstMove = false ;
    }
    pieces.remove(board.enemyPieceAtPosition(currrow*Board.SQUARE_SIZE, currcol*Board.SQUARE_SIZE, turn));
    board.updateActiveBlock(currrow*Board.SQUARE_SIZE, currcol*Board.SQUARE_SIZE);
    turn = !turn;
    boardPanel.repaint() ;
    activePiece.lastMoveNumber = Game.moveNumber;
    Game.moveNumber++;


    if(blackKing.checkMate(pieces)){
      System.out.println("white wins") ;
    }
    else if( whiteKing.checkMate(pieces)){
      System.out.println("black wins") ;
    }
    activePiece = null;
  }

  public void drawPieces(Graphics2D g2d) {
      for (ChessPiece piece : pieces) {
        piece.draw(g2d);
      }
      if (activePiece != null)
        activePiece.draw(g2d);
   
    // lock.unlock() ;
  }

  public void render(Graphics2D g2d) {
    board.draw(g2d);
    drawPieces(g2d);
  }

  public void run() {
    init();
    long startTime;
    long renderTime;
    while (running) {
      startTime = System.currentTimeMillis();
      // update
      update();
      try{
        if(reader.ready()){
          String message = null ;
          try{
            message = reader.readLine() ;
            System.out.println(message) ;
          }catch(Exception e){
          }
          if(message.split("/")[0].equals("c")){
            chatpanel.IncomingReader(message.split("/")[1]) ;
          }
          else if(message.split("/")[0].equals("m")){
            int prevrow = Integer.parseInt(message.split("/")[1]) ;
            int prevcol = Integer.parseInt(message.split("/")[2]) ;
            int currrow = Integer.parseInt(message.split("/")[3]) ;
            int currcol = Integer.parseInt(message.split("/")[4]) ;
            System.out.println(prevrow+ prevcol+ currrow+ currcol);
            opponentMove(prevrow, prevcol, currrow, currcol);
          }
        }
      }catch(Exception e){
        e.printStackTrace();
      }

      
      // render
      boardPanel.repaint();
      renderTime = System.currentTimeMillis() - startTime;

      if (renderTime < FRAME_TIME) {
        try {
          Thread.sleep(FRAME_TIME - renderTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    }
  }
}
