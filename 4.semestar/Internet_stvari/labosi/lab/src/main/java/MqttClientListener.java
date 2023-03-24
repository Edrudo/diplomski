import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.utils.HexUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClientListener {
    XBeeDevice myDevice;
    MqttClient mqttClient;

    private static final String DATA_TO_SEND = "actuate";
    byte[] dataToSend = DATA_TO_SEND.getBytes();
    private static final String REMOTE_NODE_IDENTIFIER = "REMOTE";

    public MqttClientListener(MqttClient mqttClient, XBeeDevice myDevice) throws MqttException {
        this.myDevice = myDevice;
        this.mqttClient = mqttClient;

        IMqttMessageListener listener = new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Obtain the remote XBee device from the XBee network.
                XBeeNetwork xbeeNetwork = myDevice.getNetwork();
                RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);
                if (remoteDevice == null) {
                    System.out.println("Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
                    System.exit(1);
                }

                System.out.format("Sending data to %s >> %s | %s... ", remoteDevice.get64BitAddress(),
                        HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)),
                        new String(dataToSend));

                myDevice.sendData(remoteDevice, dataToSend);
            }
        };

        this.mqttClient.client.subscribe("actuate", listener);
        System.out.println("Mqtt client listener connected");
    }
}
