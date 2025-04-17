package com.chess.subwindow;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import com.chess.mainwindow.game.board.*; // Import Board class
import com.chess.mainwindow.game.*;      // Import Game class

public class ChatPanel extends JPanel implements Runnable {
    private JTextArea incoming;
    private JTextField outgoing;
    private BufferedReader reader;
    private PrintWriter writer;
    private JLabel statusLabel;
    private static final Color CHAT_BG = new Color(245, 245, 247);
    private static final Color MESSAGE_BG = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(0, 122, 255);

    // Font size constants
    private static final int CHAT_FONT_SIZE = 18;
    private static final int STATUS_FONT_SIZE = 14;
    private static final int BUTTON_FONT_SIZE = 16;

    public ChatPanel(PrintWriter writer) {
        this.writer = writer;
        // Use Board class for sizing
        setPreferredSize(new Dimension(350, Board.SQUARE_SIZE * Board.MAX_ROW));
        setBackground(CHAT_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        Game.chatpanel = this; // Assign to Game.chatpanel
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 10));

        // Status Label
        statusLabel = new JLabel("Chat Connected");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, STATUS_FONT_SIZE));
        statusLabel.setForeground(Color.GRAY);
        add(statusLabel, BorderLayout.NORTH);

        // Chat Area
        incoming = new JTextArea();
        incoming.setFont(new Font("Arial", Font.PLAIN, CHAT_FONT_SIZE));
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        incoming.setBackground(MESSAGE_BG);
        incoming.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(incoming);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(CHAT_BG);

        outgoing = new JTextField();
        outgoing.setFont(new Font("Arial", Font.PLAIN, CHAT_FONT_SIZE));
        outgoing.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);
        sendButton.addActionListener(new SendButtonListener());

        inputPanel.add(outgoing, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void launchClient() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                String message = outgoing.getText().trim();
                if (!message.isEmpty()) {
                    String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
                    String formattedMessage = String.format("[%s] %s", timestamp, message);
                    incoming.append(formattedMessage + "\n");
                    writer.println("c/" + message);
                    writer.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error sending message");
                statusLabel.setForeground(Color.RED);
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public void IncomingReader(String message) {
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        String formattedMessage = String.format("[%s] %s", timestamp, message);
        SwingUtilities.invokeLater(() -> {
            incoming.append(formattedMessage + "\n");
            incoming.setCaretPosition(incoming.getDocument().getLength());
        });
    }

    public void run() {
        // UI already initialized in constructor
    }
}
