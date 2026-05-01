package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class UiFactory {
    private UiFactory() {
    }

    public static JPanel pageContainer() {
        JPanel page = new JPanel();
        page.setBackground(Theme.BG);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBorder(new EmptyBorder(20, 20, 20, 20));
        return page;
    }

    public static JPanel pageHeader(String title, String subtitle) {
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_PAGE);
        t.setForeground(Theme.TEXT);
        t.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JLabel s = new JLabel(subtitle);
        s.setForeground(Theme.MUTED);
        s.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        head.add(t);
        head.add(javax.swing.Box.createVerticalStrut(4));
        head.add(s);
        return head;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        label.setFont(Theme.FONT_BOLD.deriveFont(18f));
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.MUTED);
        label.setFont(Theme.FONT);
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        return label;
    }

    public static JTextField input(String placeholder) {
        JTextField field = new JTextField(16);
        field.setText(placeholder);
        field.setForeground(new Color(141, 151, 196));
        field.setBackground(new Color(30, 39, 71));
        field.setCaretColor(Theme.TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(Theme.FONT);
        field.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        return field;
    }

    public static JPasswordField passwordInput() {
        JPasswordField field = new JPasswordField(16);
        field.setBackground(new Color(30, 39, 71));
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.TEXT);
        field.setEchoChar('\u2022');
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(Theme.FONT);
        field.setAlignmentX(JPasswordField.LEFT_ALIGNMENT);
        return field;
    }

    public static JPanel panelCard() {
        JPanel p = new JPanel();
        p.setBackground(Theme.PANEL);
        p.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        return p;
    }

    public static JPanel rowPanel() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        return row;
    }

    public static JScrollPane scrollWrap(java.awt.Component view) {
        JScrollPane pane = new JScrollPane(view);
        pane.setBorder(null);
        pane.getVerticalScrollBar().setUnitIncrement(14);
        pane.getViewport().setBackground(Theme.BG);
        pane.setOpaque(false);
        return pane;
    }

    public static JButton primaryButton(String text) {
        return baseButton(text, Theme.ACCENT, Color.WHITE, Theme.ACCENT);
    }

    public static JButton outlineButton(String text) {
        return baseButton(text, new Color(40, 51, 95), Color.WHITE, new Color(123, 136, 183));
    }

    public static JButton softButton(String text) {
        return baseButton(text, new Color(38, 50, 90), Theme.MUTED, new Color(88, 100, 147));
    }

    public static JButton dangerButton(String text) {
        return baseButton(text, new Color(91, 45, 63), new Color(255, 214, 223), Theme.DANGER);
    }

    public static JButton linkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    public static JPanel fillButton(JButton button) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(button, BorderLayout.CENTER);
        wrap.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        return wrap;
    }

    private static JButton baseButton(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(Theme.FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return btn;
    }
}
