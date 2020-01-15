import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hb on 15/1/20.
 */
public class HomeScreen extends JPanel {

    JList listHistory;
    JButton btnSend;
    JTextField textInputField;
    private DefaultListModel<String> list = new DefaultListModel<>();

    private String adbPath;
    private PropertiesComponent propertiesComponent;
    private MyService myService;

    /**
     * Create the panel.
     */
    public HomeScreen(String adbPath) {
        setPreferredSize(new Dimension(320, 500));

        ImageIcon icon = new ImageIcon(HomeScreen.class.getResource("/icons/adbicon.png"));
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        JLabel iconLabel = new JLabel(icon);
        springLayout.putConstraint(SpringLayout.NORTH, iconLabel, 10, SpringLayout.NORTH, this);
        iconLabel.setPreferredSize(new Dimension(150, 150));
        iconLabel.setMinimumSize(new Dimension(50, 50));
        iconLabel.setMaximumSize(new Dimension(200, 200));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(iconLabel);

        textInputField = new JTextField();
        springLayout.putConstraint(SpringLayout.SOUTH, iconLabel, -6, SpringLayout.NORTH, textInputField);
        springLayout.putConstraint(SpringLayout.NORTH, textInputField, 156, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, iconLabel, 0, SpringLayout.WEST, textInputField);
        springLayout.putConstraint(SpringLayout.WEST, textInputField, 10, SpringLayout.WEST, this);
        add(textInputField);
        textInputField.setColumns(10);

        btnSend = new JButton("Send");
        springLayout.putConstraint(SpringLayout.NORTH, btnSend, 6, SpringLayout.SOUTH, iconLabel);
        springLayout.putConstraint(SpringLayout.EAST, iconLabel, 0, SpringLayout.EAST, btnSend);
        springLayout.putConstraint(SpringLayout.EAST, textInputField, -6, SpringLayout.WEST, btnSend);
        springLayout.putConstraint(SpringLayout.WEST, btnSend, -80, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.EAST, btnSend, -10, SpringLayout.EAST, this);
        add(btnSend);

        listHistory = new JBList();
        JBScrollPane jbScrollPane = new JBScrollPane(listHistory);

        listHistory.setBorder(new EmptyBorder(10, 10, 10, 10));
        springLayout.putConstraint(SpringLayout.SOUTH, btnSend, -8, SpringLayout.NORTH, jbScrollPane);
        springLayout.putConstraint(SpringLayout.SOUTH, textInputField, -8, SpringLayout.NORTH, jbScrollPane);
        springLayout.putConstraint(SpringLayout.NORTH, jbScrollPane, 194, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, jbScrollPane, 10, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, jbScrollPane, -10, SpringLayout.SOUTH, this);
        springLayout.putConstraint(SpringLayout.EAST, jbScrollPane, -10, SpringLayout.EAST, this);
        add(jbScrollPane);

        listHistory.setModel(list);

        this.adbPath = adbPath;
        initComponents();

    }

    private void initComponents() {
        propertiesComponent = PropertiesComponent.getInstance();
        myService = ServiceManager.getService(MyService.class);
        listHistory.setModel(list);
        listHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listHistory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                boolean isRightClick = SwingUtilities.isRightMouseButton(evt);
                int index = listHistory.locationToIndex(evt.getPoint());
                if (index != -1) {
                    if (isRightClick) {
                        deleteValue(index);
                    } else if (evt.getClickCount() == 2) {
                        runCommand(list.get(index));
                    }
                }
            }
        });

        listHistory.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int index = listHistory.getSelectedIndex();
                    deleteValue(index);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int index = listHistory.getSelectedIndex();
                    runCommand(list.get(index));
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        textInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!TextUtils.isEmpty(textInputField.getText())) {
                    runCommand(textInputField.getText());
                    saveValue();
                }
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!TextUtils.isEmpty(textInputField.getText())) {
                    runCommand(textInputField.getText());
                    saveValue();
                    textInputField.requestFocus();
                }
            }
        });

        for (String item : myService.items) {
            list.addElement(item);
        }
    }

    private void deleteValue(int index) {
        if (index != -1) {
            if (list.size() > index) {
                list.remove(index);
                if (list.size() > (index)) {
                    listHistory.setSelectedIndex(index);
                } else if (list.size() > (index - 1)) {
                    listHistory.setSelectedIndex(index - 1);
                }
            }
        }
    }

    private void saveValue() {
        String value = textInputField.getText();
        if (myService.items == null) {
            myService.items = new ArrayList<>();
        }
        myService.items.add(value);
        textInputField.setText("");
        list.add(0, value);
    }

    private void runCommand(String text) {
        //String[] clearAll = new String[]{adbPath, "shell", "input", "keyevent","--longpress", "$(echo 'KEYCODE_DEL %.0s' {1..250}"};
        String[] type = new String[]{adbPath, "shell", "input", "text", "$(echo \"" + text + "\" | sed 's/ /\\%s/g')"};

        //execute(clearAll);
        execute(type);
    }

    private void execute(String[] args) {
        try {
            Process proc = new ProcessBuilder(args).start();
            System.out.println("Run Succes");
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("Exception");
        }
    }
}
