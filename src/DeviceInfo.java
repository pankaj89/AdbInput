import com.android.ddmlib.IDevice;

/**
 * Created by hb on 23/1/20.
 */
public class DeviceInfo {
    DeviceInfo(IDevice iDevice){
        id = iDevice.getSerialNumber();
        brand = iDevice.getProperty("ro.product.brand");
        model= iDevice.getProperty("ro.product.model");
    }
    String id;
    String brand;
    String model;

    public String getFullName(){
        return brand+" "+model;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DeviceInfo){
            return ((DeviceInfo)obj).id.equals(id);
        }
        return super.equals(obj);
    }
}
