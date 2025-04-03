package ie.gti.asdl.rey.gtirecord.desktop.ui;

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
        init();
    }

    private void init() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                frameManager.showParent();
            }
        });
    }

    protected void shownFirstTime() {

    }

    protected void initFrame() {
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
            onFrameHidden();
        } else {
            onFrameShown();
        }
    }

    protected void resetFrame() {
        // Do nothing
    }

    protected void onFrameHidden() {
        logger.debug("{} WINDOW IS HIDDEN", this.getClass().getSimpleName());
        resetFrame();
    }


    protected void onFrameShown() {
        logger.debug("{} WINDOW IS VISIBLE", this.getClass().getSimpleName());
    }

    public void showFrame() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            toFront();
        });
    }

}