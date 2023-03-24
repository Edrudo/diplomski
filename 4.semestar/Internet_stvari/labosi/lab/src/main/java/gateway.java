import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;


public class gateway {

    public static void main(String[] args) {
        String PORT = "COM1";
        int BAUD_RATE = 9600;

        MosquittoClient mqttClient = new MosquittoClient();

        // zigbee connection
        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

        try {
            myDevice.open();

            myDevice.addDataListener(new MyDataReceiveListener(mqttClient));
        } catch (XBeeException e) {
            e.printStackTrace();
            myDevice.close();
            System.exit(1);
        }

        System.out.println("Listening to messages");
    }
}