package city.windmill.fileViewer.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class MacAddress {
    private final byte[] mac;

    public MacAddress(byte[] mac) {
        this.mac = mac;
    }

    public MacAddress(String macAddress) {
        String[] hexs = macAddress.split("-");
        mac = new byte[hexs.length];
        for (int i = 0; i < hexs.length; i++) {
            mac[i] = Integer.valueOf(hexs[i]).byteValue();
        }
    }

    public byte[] getMac() {
        return mac;
    }

    //region Object
    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0").append(str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mac);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof MacAddress && Arrays.equals(((MacAddress) obj).getMac(), mac));
    }
    //endregion
}
