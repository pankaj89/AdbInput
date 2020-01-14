import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.ArrayList;

@State(name = "pref", storages = {@Storage("yourName.xml")})
class MyService implements PersistentStateComponent<MyService> {

    public ArrayList<String> items = new ArrayList<String>(); ;

    public MyService getState() {
        return this;
    }

    public void loadState(MyService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}