package yot.ui;

import java.awt.Color;
import java.awt.Font;

public final class Theme {
    private Theme() {
    }

    public static final Color BG = new Color(11, 16, 32);
    public static final Color PANEL = new Color(26, 35, 66);
    public static final Color PANEL_ALT = new Color(33, 43, 79);
    public static final Color TEXT = new Color(233, 237, 255);
    public static final Color MUTED = new Color(174, 184, 232);
    public static final Color ACCENT = new Color(111, 139, 255);
    public static final Color ACCENT_ALT = new Color(72, 215, 194);
    public static final Color DANGER = new Color(255, 122, 152);
    public static final Color BORDER = new Color(70, 83, 126);

    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_PAGE = new Font("Segoe UI", Font.BOLD, 28);
}
