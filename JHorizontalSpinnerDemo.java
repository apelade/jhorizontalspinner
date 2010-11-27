// package declaration

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class JHorizontalSpinnerDemo {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JHorizontalSpinnerDemo();
            }
        });
    }

        public JHorizontalSpinnerDemo(){
        //initialize the screen etc:
        JFrame frame = new JFrame("JHorizontalSpinner Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout());
        JHorizontalSpinner horizontalSpinner = new JHorizontalSpinner();
       // horizontalSpinner.setPreferredSize(new Dimension(150, horizontalSpinner.getPreferredSize().height));
        JSpinner verticalSpinner = new JSpinner();
        toolBar.add(horizontalSpinner);
        toolBar.addSeparator();
        toolBar.add(verticalSpinner);

        frame.add(toolBar, BorderLayout.SOUTH);
       // frame.add()
        frame.pack();
        frame.setVisible(true);

    }

}
