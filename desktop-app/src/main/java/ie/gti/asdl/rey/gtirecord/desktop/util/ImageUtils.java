package ie.gti.asdl.rey.gtirecord.desktop.util;

import javax.swing.*;
import java.awt.*;

public class ImageUtils {
    // Resize icon method
    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
