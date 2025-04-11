package com.chess.subwindow ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.chess.mainwindow.game.board.* ;
import com.chess.main.Main ;
import com.chess.mainwindow.game.* ;

import javax.swing.* ;

public class ChatPanel extends JPanel implements Runnable{
  
  JTextArea incoming;
  JTextField outgoing;
  BufferedReader reader;
  PrintWriter writer;

  public ChatPanel(PrintWriter writer){
    setPreferredSize(new Dimension(350, Board.SQUARE_SIZE * Board.MAX_ROW));
    setBackground(Color.WHITE);
    this.writer = writer ;
    Game.chatpanel = this ;
  }

  public void launchClient(){
    Thread thread = new Thread(this) ;
    thread.start() ;
  }


  public class SendButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent ev) {
      try {
          String out = outgoing.getText() ;
          incoming.append(out + "\n");
          writer.println("c/" + out);
          writer.flush();
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      outgoing.setText("");
      outgoing.requestFocus();
    }
  }

  public void IncomingReader(String message){
    System.out.println("read " + message);
    incoming.append(message + "\n");
  }

  public void run(){
    
    incoming = new JTextArea(15, 15);
    incoming.setLineWrap(true);
    incoming.setWrapStyleWord(true);
    incoming.setEditable(false);

    JScrollPane qScroller = new JScrollPane(incoming);
    qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    outgoing = new JTextField(20);
    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());

    this.add(qScroller);
    this.add(outgoing);
    this.add(sendButton);

  }
}
