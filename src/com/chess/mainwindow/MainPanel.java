package com.chess.mainwindow ;

import java.io.* ;
import javax.swing.* ;
import java.awt.* ;
import com.chess.eventhandlers.* ;
import com.chess.mainwindow.game.* ;
import com.chess.mainwindow.game.board.* ;

public class MainPanel extends JPanel {

  public static final int WIDTH = Board.SQUARE_SIZE * Board.MAX_COL;
  public static final int HEIGHT = Board.SQUARE_SIZE * Board.MAX_ROW;
  public MyMouse mouse;
  public Game game;
  // public Promotion promotion ;

  public MainPanel(BufferedReader reader, PrintWriter writer) {
    // this.add(promotion = new Promotion()) ;
    mouse = new MyMouse();
    game = new Game(this, mouse, reader, writer);
    setPreferredSize((new Dimension(WIDTH, HEIGHT)));
    setBackground(Color.WHITE);
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
  }

  public void launchClient(boolean isWhite) {
    game.launch(isWhite);
    // chat.launch();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    game.render(g2d);
  }
}
