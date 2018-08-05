package Othello;

import javax.swing.JApplet;
import java.awt.*;
import javax.swing.*;


/**
 *
 * @author Aditya Garg
 */

public class OthelloSwingApplet extends JApplet{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void init() {

        getContentPane().setLayout(new GridLayout(1, 1));

        JButton button = new JButton("Start Othello");
        getContentPane().add(button);

        button.addActionListener(
            new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPushed(evt);  
            }  
        }
        );

    }

    private void buttonPushed(java.awt.event.ActionEvent evt) {
        OthelloTwoPlayerGame f = new OthelloTwoPlayerGame();
    }

}


