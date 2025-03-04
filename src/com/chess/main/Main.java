
package com.chess.main ;

import javax.swing.* ;
import java.awt.*;

import com.chess.mainwindow.* ;
import com.chess.mainwindow.game.board.* ;

import java.io.*;

public class Main {

  public static void main(String[] args) {

    JFrame subWindow = new JFrame("subWindow");
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel();
    frameSetUp(chess, boardPanel);
    boardPanel.launchClient();
    chess.setLocationRelativeTo(null);
    Point point = chess.getLocation() ;
    subWindow.setLocation(point.x + 20 + Board.SQUARE_SIZE * Board.MAX_ROW, point.y) ;
    chess.setVisible(true);
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


