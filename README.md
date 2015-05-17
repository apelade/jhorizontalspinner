# jhorizontalspinner

Old project imported from Google Code

DESCRIPTION:
- Arrow buttons pointing outward from a central editor field.
- Pluggable UI can be set on an existing JSpinner, or use JHorizontalSpinner wrapper class.
- Respects language-related ComponentOrientation by switching the previous and next button listeners:

  - previous-editor-next in left-to-right ComponentOrientation.
  - next-editor-previous in right-to-left ComponentOrientation.
  - HorizontalSpinnerUI.java - most of the code. It overrides BasicSpinnerUI for horizontal layout.
  - JHorizontalSpinner.java - a thin JSpinner wrapper for ctor to setUI() and ComponentOrientation methods.
  - JHorizontalSpinnerDemo.java - a main class to show it. No action, just view.


STATUS:
Experimental code, Java 6, works. No testing on deeper integration with Swing, eg. super's ActionMap. 


USAGE:
Declaring: A couple ways to do it:
Instantiate the JHorizontalSpinner directly:
`JSpinner horzSpin = new JHorizontalSpinner();`
Or, after creating a JSpinner, call setUI() on it:
```
public SpinnerMenuItem(String labelStr, int initialValue){
    super();
    spinner = new JSpinner();
    spinner.setValue(new Integer(100)); //sets the amount of space for the textfield
    spinner.addChangeListener(this);
    add(spinner);
    spinner.getEditor().setPreferredSize(spinner.getEditor().getPreferredSize());
    spinner.setValue(new Integer(initialValue));
    super.setText(labelStr);
    spinner.setUI(new HorizontalSpinnerUI());// calls installUI, which calls initButtonListeners()
}
```
Updating: needed if you want it to change as ComponentOrientation (language) changes:
- You could add a listener for changes of orientation.
- Or when needed, you could call `((HorizontalSpinnerUI)spinner.getUI()).initButtonListeners()` to refresh which one is previous or next, like some kind of global refresh.
- Or you can override one of this component's containers set of ComponentOrientation methods to call it as in JHorizontalSpinner.
- Or you can use JHorizontalSpinner directly.
```
import java.awt.ComponentOrientation;
import javax.swing.JSpinner;

class JHorizontalSpinner extends JSpinner{

    public JHorizontalSpinner(){
        super();
        setUI(new HorizontalSpinnerUI());
    }

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
```
