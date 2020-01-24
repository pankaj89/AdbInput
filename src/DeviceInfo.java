import com.android.ddmlib.IDevice;

/**
 * Created by hb on 23/1/20.
 */
public class DeviceInfo {
    DeviceInfo(IDevice iDevice) {
        id = iDevice.getSerialNumber();
        manufacturer = iDevice.getProperty("ro.product.manufacturer");
        model = iDevice.getProperty("ro.product.model");
    }

    String id;
    String manufacturer;
    String model;

    public String getFullName() {
        return getFirstCaps(manufacturer) + " " + model;
    }

    String getFirstCaps(String name) {
        try {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        } catch (Exception e) {
            return name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeviceInfo) {
            return ((DeviceInfo) obj).id.equals(id);
        }
        return super.equals(obj);
    }
}
