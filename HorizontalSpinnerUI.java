//package declaration

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer.UIResource;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;



// Tweaked setBounds() and getPreferredSize()
// Copied HandlerLayoutManager from private class BasicUIManager.Handler
// Made HandlerLayoutManager an inner class for access to buttons

// It works fine without, but if you want to maintain consistency with orientation
// changing at runtime, override mySpinner.setComponentOrientation and
// mySpinner.applyComponentOrientation
// to call initButtonListeners() when the component orientation changes, if ever:
// See end of the file for example.
public class HorizontalSpinnerUI extends BasicSpinnerUI{

    Component westButton;
    Component eastButton;
    protected static final Dimension zeroSize = new Dimension(0,0);
    
    @Override
    public void installUI(JComponent c) {
	this.spinner = (JSpinner)c;
	installDefaults();
	installListeners();
        westButton  =
                createTheArrowButton(SwingConstants.WEST, "West");
	spinner.add(westButton);
	spinner.add(createEditor(), "Editor");
        eastButton  =
                createTheArrowButton(SwingConstants.EAST, "East");
	spinner.add(eastButton);
        // last item in installKeyboardActions() sets action map, hard to access it
        installKeyboardActions();

        // Remove default Handler listener and install anew.
        initButtonListeners();
    }

    private Component createTheArrowButton(int direction, String name) {
	JButton b = new BasicArrowButton(direction);
        b.setName(name);
	Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
	if (buttonBorder instanceof UIResource) {
	    // Wrap the border to avoid having the UIResource be replaced by
	    // the ButtonUI. This is the opposite of using BorderUIResource.
	    b.setBorder(new CompoundBorder(buttonBorder, null));
	} else {
	    b.setBorder(buttonBorder);
	}
        b.setInheritsPopupMenu(true);
	return b;
    }

    @Override
    protected LayoutManager createLayout() {
      LayoutManager handlerLayout = new HandlerLayoutManager();
      return handlerLayout;
    }



    public void initButtonListeners(){
        ComponentOrientation orient = this.spinner.getComponentOrientation();
        if(orient.equals(ComponentOrientation.UNKNOWN) ||
                orient.equals(ComponentOrientation.LEFT_TO_RIGHT)){
            installPreviousButtonListeners(westButton);
            installNextButtonListeners(eastButton);
        }else{if(orient.equals(ComponentOrientation.RIGHT_TO_LEFT)){
            installPreviousButtonListeners(eastButton);
            installNextButtonListeners(westButton);
        }
        }
    }
    
    @Override
    protected void installPreviousButtonListeners(Component c) {
        uninstallArrowButtonListeners(c);
        c.setName("Spinner.previousButton");
        super.installPreviousButtonListeners(c);
        //untested - may be needed if app is using spinner's action map
        if (c instanceof JButton) {
            ActionListener[] actionListeners = ((JButton)c).getActionListeners();
            for(int a=0;a<actionListeners.length;a++){
                // Hack
                if (actionListeners[a].getClass().toString().indexOf("ArrowButtonHandler") >-1){
                    spinner.getActionMap().put("decrement", (AbstractAction)actionListeners[a]);
                }
            }
        } 
    }
    
    @Override
    protected void installNextButtonListeners(Component c) {
        uninstallArrowButtonListeners(c);
        c.setName("Spinner.nextButton");
        super.installNextButtonListeners(c);
        //untested - may be needed if app is using spinners action map
        if (c instanceof JButton) {
            ActionListener[] actionListeners = ((JButton)c).getActionListeners();
            for(int a=0;a<actionListeners.length;a++){
                // Hack
                if (actionListeners[a].getClass().toString().indexOf("ArrowButtonHandler") >-1){
                    spinner.getActionMap().put("increment", (AbstractAction)actionListeners[a]);
                }
            }
        }
    }

    protected void uninstallArrowButtonListeners(Component c){
        if(c != null){
            MouseListener[] mouseListeners = c.getMouseListeners();
            for(int m=0;m<mouseListeners.length;m++){
                // Hack
                if (mouseListeners[m].getClass().toString().indexOf("ArrowButtonHandler") >-1){
                    c.removeMouseListener(mouseListeners[m]);
                }
            }
            if (c instanceof JButton) {
                ActionListener[] actionListeners = ((JButton)c).getActionListeners();
                for(int a=0;a<actionListeners.length;a++){
                    // Hack
                    if (actionListeners[a].getClass().toString().indexOf("ArrowButtonHandler") >-1){
                        ((JButton)c).removeActionListener(actionListeners[a]);
                    }
                }
            }
        }
    }

class HandlerLayoutManager implements LayoutManager{
	private Component editor = null;

        // The buttons do not change, we only switch their listeners
	public void addLayoutComponent(String name, Component c) {
	    if ("East".equals(name)) {
		eastButton = c;
	    }
	    else if ("West".equals(name)) {
		westButton = c;
	    }
	    else if ("Editor".equals(name)) {
		editor = c;
	    }
	}

	public void removeLayoutComponent(Component c) {
	    if (c == eastButton) {
		eastButton = null;
	    }
	    else if (c == westButton) {
		westButton = null;
	    }
	    else if (c == editor) {
		editor = null;
	    }
	}

	private Dimension preferredSize(Component c) {
	    return (c == null) ? zeroSize : c.getPreferredSize();
	}

	public Dimension preferredLayoutSize(Container parent) {
	    Dimension nextD = preferredSize(eastButton);
	    Dimension previousD = preferredSize(westButton);
	    Dimension editorD = preferredSize(editor);
	    /* Force the editors height to be a multiple of 2
	     */
	    editorD.height = ((editorD.height + 1) / 2) * 2;
	    Dimension size = new Dimension(editorD.width, editorD.height);
	    size.width += 2*(Math.max(nextD.width, previousD.width));
	    Insets insets = parent.getInsets();
	    size.width += insets.left + insets.right;
	    size.height += insets.top + insets.bottom;
	    return size;
	}

	public Dimension minimumLayoutSize(Container parent) {
	    return preferredLayoutSize(parent);
	}

	private void setBounds(Component c, int x, int y, int width, int height) {
	    if (c != null) {
		c.setBounds(x, y, width, height);
	    }
	}

	public void layoutContainer(Container parent) {
	    int width  = parent.getWidth();
	    int height = parent.getHeight();
	    Insets insets = parent.getInsets();
	    Dimension nextD = preferredSize(eastButton);
	    Dimension previousD = preferredSize(westButton);
	    int buttonWidth = Math.max(nextD.width, previousD.width);
	    int editorHeight = height - (insets.top + insets.bottom);
	    // The arrowButtonInsets value is used instead of the JSpinner's
	    // insets if not null. Defining this to be (0, 0, 0, 0) causes the
	    // buttons to be aligned with the outer edge of the spinner's
	    // border, and leaving it as "null" places the buttons completely
	    // inside the spinner's border.
	    Insets buttonInsets = UIManager.getInsets("Spinner.arrowButtonInsets");
	    if (buttonInsets == null) {
		buttonInsets = insets;
	    }

	    /* Deal with the spinner's componentOrientation property.
	     */
	    int editorWidth;
	    if (parent.getComponentOrientation().isLeftToRight()) {
		editorWidth = width - insets.left - (2*buttonWidth) - buttonInsets.right;
                setBounds(westButton,
                    insets.left,
                    insets.top,
                    buttonWidth,
                    editorHeight);
                setBounds(editor,
                    insets.left+ buttonWidth,
                    insets.top,
                    editorWidth,
                    editorHeight);
                setBounds(eastButton,
                    insets.left+ buttonWidth + editorWidth,
                    insets.top,
                    buttonWidth,
                    editorHeight);
            }else{if (!parent.getComponentOrientation().isLeftToRight()) {
                editorWidth = width - insets.right - (2*buttonWidth) - buttonInsets.left;
                setBounds(westButton,
                    width-buttonWidth-editorWidth-buttonWidth,
                    insets.top,
                    buttonWidth,
                    editorHeight);
                setBounds(editor,
                    width-buttonWidth-editorWidth,
                    insets.top,
                    editorWidth,
                    editorHeight);
                setBounds(eastButton,
                    width-buttonWidth,
                    insets.top,
                    buttonWidth,
                    editorHeight);
                }
            }
       }
    }
}

/*
 *    class MyJSpinner extends JSpinner{

        @Override
        public void setComponentOrientation(ComponentOrientation o){
            System.out.println("setComponentOrientation ");
            super.setComponentOrientation(o);
            if( super.getUI() instanceof HorizontalSpinnerUI){
                ((HorizontalSpinnerUI) super.getUI()).initButtonListeners();
            }
        }
        @Override
        public void applyComponentOrientation(ComponentOrientation o){
            System.out.println("applyComponentOrientation ");
            super.applyComponentOrientation( o);
            if( super.getUI() instanceof HorizontalSpinnerUI){
                ((HorizontalSpinnerUI) super.getUI()).initButtonListeners();
            }
        }
    }
 */
