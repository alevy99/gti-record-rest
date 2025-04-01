package ie.gti.asdl.rey.gtirecord.desktop.ui;

import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public abstract class AbstractFrame extends JFrame {

    private final Logger logger = LoggerFactory.getLogger(AbstractFrame.class);

    private boolean isShownOnceOrMore = false;

    protected int getDefaultCloseOperationValue() {
        return JFrame.HIDE_ON_CLOSE;
    }

    @Getter
    private final FrameManager frameManager;

    public AbstractFrame(FrameManager frameManager) {
        super();
        this.frameManager = frameManager;
//        setLocationRelativeTo(this);
        init();
    }

    private void init() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                frameManager.showParent();
            }
        });

//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent default close action
//
//        // Add a window listener
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // Hide the frame instead of closing
//                setVisible(false);
//            }
//        });
    }

//    protected void onWindowClosing(WindowEvent e) {
//
//    }

    protected void shownFirstTime() {

    }

    protected void initForm() {
        setDefaultCloseOperation(getDefaultCloseOperationValue()); // Hide instead of closing
    }

    @Override
    public void setVisible(boolean b) {
        if (!isShownOnceOrMore) {
            isShownOnceOrMore = true;
            setLocationRelativeTo(this);
            shownFirstTime();
        }
        super.setVisible(b);
        if (!b) {
            onFormHidden();
        } else {
            onFormShown();
        }
    }

    protected void onFormHidden() {
        logger.debug("{} WINDOW IS HIDDEN", this.getClass().getSimpleName());
        //mainFrame.setVisible(true);
    }


    protected void onFormShown() {
        logger.debug("{} WINDOW IS VISIBLE", this.getClass().getSimpleName());
//        initTableData();
    }

    public void showForm() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            toFront();
        });
    }


//    protected boolean isShownOnce() {
//        return isShownOnce;
//    }
//
//    protected void setShownOnce(boolean shownOnce) {
//        isShownOnce = shownOnce;
//    }
}