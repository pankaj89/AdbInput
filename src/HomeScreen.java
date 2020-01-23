import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.http.util.TextUtils;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.color.ColorUtil;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by hb on 15/1/20.
 */
public class HomeScreen extends JPanel {

    private JList listHistory;
    private JButton btnSend;
    private JTextField textInputField;
    private JComboBox deviceListComboBox;
    private DefaultListModel<String> list = new DefaultListModel<>();
    private JLabel labelAbout;

    private String adbPath;
    private PropertiesComponent propertiesComponent;
    private MyService myService;

//    public HomeScreen(String adbPath) {
//    	
//    }

    /**
     * Create the panel.
     */
    public HomeScreen(String adbPath) {
        setPreferredSize(new Dimension(320, 500));

        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        labelAbout = new JLabel("About");
        add(labelAbout);

        JXImageView iconLabel = new JXImageView();

        try {

            try {
                Field field = iconLabel.getClass().getDeclaredField("checkerPaint");
                field.setAccessible(true);

                Color c = new Color(1f, 0f, 0f, 0f);
                field.set(iconLabel, ColorUtil.getCheckerPaint(c, c, 1));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            BufferedImage image = ImageIO.read(getClass().getResource("/icons/adb_icon_t.png"));
            iconLabel.setImage(image);
            iconLabel.setDragEnabled(false);
            iconLabel.setScale(0.4);
            iconLabel.setEditable(false);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        springLayout.putConstraint(SpringLayout.NORTH, iconLabel, 10, SpringLayout.NORTH, this);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(iconLabel);

        deviceListComboBox = new ComboBox();


        textInputField = new JTextField();
        springLayout.putConstraint(SpringLayout.SOUTH, iconLabel, -6, SpringLayout.NORTH, textInputField);
        springLayout.putConstraint(SpringLayout.NORTH, textInputField, 156, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, iconLabel, 0, SpringLayout.WEST, textInputField);
        springLayout.putConstraint(SpringLayout.WEST, textInputField, 10, SpringLayout.WEST, this);
        add(textInputField);
        textInputField.setColumns(10);

        btnSend = new JButton("Send");
        btnSend.setFocusable(false);
        springLayout.putConstraint(SpringLayout.NORTH, btnSend, 6, SpringLayout.SOUTH, iconLabel);
        springLayout.putConstraint(SpringLayout.EAST, iconLabel, 0, SpringLayout.EAST, btnSend);
        springLayout.putConstraint(SpringLayout.EAST, textInputField, -6, SpringLayout.WEST, btnSend);
        springLayout.putConstraint(SpringLayout.WEST, btnSend, -80, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.EAST, btnSend, -10, SpringLayout.EAST, this);
        add(btnSend);

        listHistory = new JBList();
        JBScrollPane jbScrollPane = new JBScrollPane(listHistory);


        listHistory.setBorder(new EmptyBorder(10, 10, 10, 10));
        springLayout.putConstraint(SpringLayout.NORTH, labelAbout, 0, SpringLayout.NORTH, iconLabel);
        springLayout.putConstraint(SpringLayout.EAST, labelAbout, -8, SpringLayout.EAST, iconLabel);
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

    public class SampleDialogWrapper extends DialogWrapper {

        public SampleDialogWrapper() {
            super(true); // use current window as parent
            init();
            setTitle("About");
        }

        class OkAction extends AbstractAction {
            SampleDialogWrapper sampleDialogWrapper;

            OkAction(SampleDialogWrapper sampleDialogWrapper) {
                super("Ok");
                this.sampleDialogWrapper = sampleDialogWrapper;
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sampleDialogWrapper.dispose();
            }
        }

        @NotNull
        @Override
        protected Action[] createActions() {
//            return super.createActions();
            return new Action[]{new OkAction(this)};
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
            dialogPanel.setPreferredSize(new Dimension(350, 100));

            JLabel label = new JLabel("Developed by", SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            dialogPanel.add(label);

            JLabel labelName = new JLabel("Pankaj Sharma", SwingConstants.CENTER);
            labelName.setHorizontalAlignment(SwingConstants.CENTER);
            labelName.setVerticalAlignment(SwingConstants.CENTER);
            dialogPanel.add(labelName);

            return dialogPanel;
        }
    }

    private void initComponents() {
        propertiesComponent = PropertiesComponent.getInstance();
        myService = ServiceManager.getService(MyService.class);
        listHistory.setModel(list);
        listHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        labelAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SampleDialogWrapper().show();
            }
        });
        listHistory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
//                boolean isRightClick = SwingUtilities.isRightMouseButton(evt);
                int index = listHistory.locationToIndex(evt.getPoint());
                if (index != -1) {
                    if (evt.getClickCount() == 2) {
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
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (listHistory.getSelectedIndex() == 0) {
                        textInputField.requestFocus();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        textInputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    listHistory.requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        textInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextUtils.isEmpty(textInputField.getText())) {
                    runCommand(textInputField.getText());
                    saveValue();
                }
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextUtils.isEmpty(textInputField.getText())) {
                    runCommand(textInputField.getText());
                    saveValue();
                    textInputField.requestFocus();
                }
            }
        });

        for (String item : myService.items) {
            list.addElement(item);
        }

        getDeviceList();
    }

    private void deleteValue(int index) {
        if (index != -1) {
            if (list.size() > index) {
                list.remove(index);
                myService.items.remove(index);
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
        if (!myService.items.contains(value)) {
            myService.items.add(value);
            list.add(0, value);
        }
        textInputField.setText("");
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

            InputStreamReader isReader = new InputStreamReader(proc.getInputStream());
            //Creating a BufferedReader object
            BufferedReader reader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }


            System.out.println("Run Succes");
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("Exception");
        }
    }

    private ArrayList<DeviceInfo> devices = new ArrayList<DeviceInfo>();

    private void getDeviceList() {
        AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge.createBridge(adbPath, false);
        AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            @Override
            public void deviceConnected(IDevice iDevice) {
                DeviceInfo deviceInfo = new DeviceInfo(iDevice);
                System.out.println("Device Connected : " + deviceInfo.getFullName());
            }

            @Override
            public void deviceDisconnected(IDevice iDevice) {
                DeviceInfo deviceInfo = new DeviceInfo(iDevice);
                System.out.println("Device Disconnected : " + deviceInfo.getFullName());
                if (devices.contains(deviceInfo)) {
                    devices.remove(deviceInfo);
                }
            }

            @Override
            public void deviceChanged(IDevice iDevice, int i) {
                DeviceInfo deviceInfo = new DeviceInfo(iDevice);
                System.out.println("Device Changed : " + deviceInfo.getFullName());
                if (devices.contains(deviceInfo)) {
                    int index = devices.indexOf(deviceInfo);
                    devices.set(index, deviceInfo);
                } else {
                    devices.add(deviceInfo);
                }
            }
        });
    }
}
