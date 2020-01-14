import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by hb on 13/1/20.
 */
public class ScreenUI {

    private String adbPath;
    private PropertiesComponent propertiesComponent;
    private MyService myService;
    private DefaultListModel<String> list = new DefaultListModel<>();

    public ScreenUI(Project project, ToolWindow toolWindow, String adbPath) {
        this.adbPath = adbPath;
        initComponents();
    }

    public JPanel getContent() {
        return panel1;
    }

    private void createUIComponents() {
    }

    private void initComponents() {
        propertiesComponent = PropertiesComponent.getInstance();
        myService = ServiceManager.getService(MyService.class);
        listHistory.setModel(list);
        listHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*listHistory.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()) {
                    int position = listHistory.getSelectedIndex();
                    if (position != -1)
                        runCommand(list.get(listHistory.getSelectedIndex()));
                }
            }
        });*/

        listHistory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                boolean isRightClick = SwingUtilities.isRightMouseButton(evt);
                JList jlist = (JList) evt.getSource();
                int index = jlist.locationToIndex(evt.getPoint());
                if (index != -1) {
                    if (isRightClick) {
                        list.remove(index);
                    } else if (evt.getClickCount() == 2) {
                        // Double-click detected
                        runCommand(list.get(index));
                    }
                }
            }
        });

        etTextTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand(etTextTextField.getText());
                saveValue();
            }
        });
        btnSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand(etTextTextField.getText());
            }
        });
        if (TextUtils.isEmpty(adbPath)) {
            etAdbPathTextField.setVisible(true);
        } else {
            etAdbPathTextField.setVisible(false);
        }

        for (String item : myService.items) {
            System.out.print("Item : " + item);
        }
        list.addAll(myService.items);
    }

    private void saveValue() {
        String value = etTextTextField.getText();
        if (myService.items == null) {
            myService.items = new ArrayList<>();
        }
        myService.items.add(value);
        etTextTextField.setText("");
        list.add(0, value);
    }

    private void runCommand(String text) {
        if (TextUtils.isEmpty(adbPath)) {
            adbPath = etAdbPathTextField.getText();
        }

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

    private JPanel panel1;
    private JTextField etTextTextField;
    private JButton btnSendButton;
    private JTextField etAdbPathTextField;
    private JList<String> listHistory;
}
