package yot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlaceholderPage extends JPanel {
    public PlaceholderPage(String title, String subtitle) {
        JPanel page = UiFactory.pageContainer();
        page.add(UiFactory.pageHeader(title, subtitle));
        page.add(Box.createVerticalStrut(14));

        JPanel box = UiFactory.panelCard();
        box.setLayout(new GridBagLayout());
        box.setPreferredSize(new Dimension(400, 460));
        JLabel text = new JLabel("<html><center>" + title
                + " Page Placeholder<br/>Sidebar navigation is already connected.</center></html>");
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setForeground(Theme.MUTED);
        box.add(text);
        page.add(box);
        page.add(Box.createVerticalGlue());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        add(page);
    }
}
