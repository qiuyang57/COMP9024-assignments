package net.datastructures;

/**
 * Created by qiuya on 4/22/2017.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class testBall {
    public static void main(String[] args) {
        new testBall();
    }

    public testBall() {
        JPanel testPane = new JPanel();
        testPane.setBackground(Color.white);
        testPane.setLayout(new GridBagLayout());
        testPane.add(new MyBall(30,30,10));

        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(testPane);
        frame.pack();
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class MyBall extends JComponent
{
    private static final long serialVersionUID = 1L;
    public MyBall() { }
    public MyBall(int x, int y, int diameter)
    {
        super();
        this.setLocation(x, y);
        this.setSize(diameter, diameter);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillOval(0, 0, 100, 100);
    }
}
