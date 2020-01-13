import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hb on 13/1/20.
 */
public class MyToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        String etAdbPath = getAdbPath(project);
        ScreenUI myToolWindow = new ScreenUI(project, toolWindow, etAdbPath);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private String getAdbPath(Project project) {
        try {
            Properties properties = new Properties();
            File file = new File(project.getBasePath() + "/local.properties");
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
            String sdkDir = properties.getProperty("sdk.dir");
            String ndkDir = properties.getProperty("ndk.dir");

            return sdkDir + "/platform-tools/adb";
        } catch (Exception exception) {
            System.out.println("Error");
        }
        return "";
    }
}
