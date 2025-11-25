package me.phdljr;

import me.phdljr.controller.PaperDownloaderGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PaperDownloaderGUI::new);
    }
}
