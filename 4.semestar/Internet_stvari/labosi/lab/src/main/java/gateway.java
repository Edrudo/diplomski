import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;


public class gateway {

    public static void main(String[] args) {
        String PORT = "COM9";
        int BAUD_RATE = 115200;


        // zigbee connection
        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

        try {
            MqttClient mqttClient = new MqttClient();
            myDevice.open();

            MqttClientListener listener = new MqttClientListener(mqttClient, myDevice);
            myDevice.addDataListener(new MyDataReceiveListener(mqttClient));

        } catch (XBeeException | MqttException e) {
            e.printStackTrace();
            myDevice.close();
            System.exit(1);
        }

        System.out.println("Listening to messages");
    }
}