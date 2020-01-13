import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by hb on 13/1/20.
 */
public class ScreenUI {

    private String adbPath;

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
        etTextTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand();
            }
        });
        btnSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand();
            }
        });
        if (TextUtils.isEmpty(adbPath)) {
            etAdbPathTextField.setVisible(true);
        } else {
            etAdbPathTextField.setVisible(false);
        }
    }

    private void runCommand(){
        if (TextUtils.isEmpty(adbPath)) {
            adbPath = etAdbPathTextField.getText();
        }

        //String[] clearAll = new String[]{adbPath, "shell", "input", "keyevent","--longpress", "$(echo 'KEYCODE_DEL %.0s' {1..250}"};
        String[] type = new String[]{adbPath, "shell", "input", "text", "$(echo \"" + etTextTextField.getText() + "\" | sed 's/ /\\%s/g')"};

        //execute(clearAll);
        execute(type);
    }

    private void execute(String[] args){
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
}
