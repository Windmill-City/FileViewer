package city.windmill.fileViewer.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class WakeOnLan {
    public static void wake(MacAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket(0, getBroadcast());
        socket.send(CreatePacket(address));
        socket.close();
    }

    private static DatagramPacket CreatePacket(MacAddress address) {
        byte[] mac = address.getMac();

        byte[] bytes = new byte[6 + 16 * mac.length];
        //FF x 6
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        //Mac Address x 16
        for (int i = 6; i < bytes.length; i += mac.length) {
            System.arraycopy(mac, 0, bytes, i, mac.length);
        }
        return new DatagramPacket(bytes, bytes.length);
    }

    public static InetAddress getBroadcast() throws SocketException, UnknownHostException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        return interfaceAddress.getBroadcast();
                    }
                }
            }
        }
        return InetAddress.getByName("255.255.255.255");
    }
}
