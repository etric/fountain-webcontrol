package com.e3k.fountain.webcontrol.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextArea;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public class TestTextArea extends JTextArea implements FocusListener, KeyListener{
   
    private boolean f = true;
    
    public TestTextArea() {
        setWrapStyleWord(true);
        setLineWrap(true);
        setColumns(20);
        setRows(5);
        addFocusListener(TestTextArea.this);
        addKeyListener(TestTextArea.this);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (f) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.drawString("Введите байты, разделенные пробелами...", 2, 11);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        f = false;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getDocument().getLength() == 0) {
            f = true;
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        String text = getText();
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && text.length() > 0 && text.charAt(text.length()-1) == ' ') {
            setText(text.substring(0, text.length() - 1));
            e.consume();
        } else
        if (e.getKeyChar() == ' ' && text.length() > 0 && text.charAt(text.length()-1) != ' ') {
            setText(text.concat(" "));
            e.consume();
        } else
        if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
            if (Integer.parseInt(text.substring(text.lastIndexOf(' ') + 1, getCaret().getDot()) + e.getKeyChar()) > 255) {
                setText(text + ' ' + e.getKeyChar());
                e.consume();
            }
        } else {
            e.consume();
        }
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}