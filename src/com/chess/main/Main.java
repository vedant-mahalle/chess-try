package com.chess.main ;

import javax.swing.* ;
import java.awt.*;
import java.net.Socket;

import com.chess.mainwindow.* ;
import com.chess.mainwindow.game.board.* ;
import com.chess.subwindow.* ;

import java.io.*;

public class Main {

  public static final String host = "13.51.195.127" ;
  public static final int port = 5000 ;
  public static Socket socket;

  public static void main(String[] args) {

    while(socket == null || !socket.isConnected()){
      System.out.println("waiting for connection") ;
      try{
        socket = new Socket(host, port) ;
      }catch(Exception e){
        e.printStackTrace() ;
      }
      try{
        Thread.sleep(400);
      }catch(Exception e){
        e.printStackTrace();
      }
    }

    boolean isWhite  = false;
    String recieved = null ;

    BufferedReader reader = null;
    PrintWriter writer = null;

    System.out.println("connection established") ;
    try {
        InputStreamReader streamReader = new InputStreamReader(Main.socket.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(Main.socket.getOutputStream());
        System.out.println("networking established");
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    String message;
    try{
      recieved = reader.readLine();
    }catch(Exception e){  
      System.out.println("tumhi samjat kasla nahi") ;
    }
    System.out.println("Received from server: " + recieved);
    isWhite = recieved.equals("U white");

    JFrame subWindow = new JFrame("subWindow");
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel(reader, writer);
    ChatPanel chatPanel = new ChatPanel(writer);
    frameSetUp(chess, boardPanel);
    frameSetUp(subWindow, chatPanel);
    boardPanel.launchClient(isWhite);
    chatPanel.launchClient();
    chess.setLocationRelativeTo(null);
    Point point = chess.getLocation() ;
    subWindow.setLocation(point.x + 20 + Board.SQUARE_SIZE * Board.MAX_ROW, point.y) ;
    chess.setVisible(true);
    subWindow.setVisible(true);
  }

  public static void frameSetUp(JFrame frame, JPanel panel){
    panel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(0, 0, 0)));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}


