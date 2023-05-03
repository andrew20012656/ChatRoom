package com.learning.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientChatRoom extends JFrame {
    private JTextArea textArea = new JTextArea(10, 20);
    private JTextField textField = new JTextField(20);

    public ClientChatRoom() throws HeadlessException {
        super();
    }

    public void init() {
        this.setTitle("Client Chatroom");
        this.add(textArea, BorderLayout.CENTER);
        this.add(textField, BorderLayout.SOUTH);
        this.setBounds(300, 300, 300, 400);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageToSend = textField.getText();
                if(messageToSend.trim().length() == 0) {
                    return;
                }
                textField.setText("");
                textArea.append(messageToSend + "\n");
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when clicked on exit
        textArea.setEditable(false);
        textField.requestFocus(); // The cursor is placed in textField instead of textArea
        this.setVisible(true);
    }

    public static void main(String[] args) {
        ClientChatRoom cc1 = new ClientChatRoom();
        cc1.init();
    }
}
