// package declaration

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
