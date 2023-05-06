/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Class to manage the XBee received data that was sent by other modules in the 
 * same network.
 * 
 * <p>Acts as a data listener by implementing the 
 * {@link IDataReceiveListener} interface, and is notified when new 
 * data for the module is received.</p>
 * 
 * @see IDataReceiveListener
 *
 */
public class MyDataReceiveListener implements IDataReceiveListener {
	MqttClient mqttClient;

	public MyDataReceiveListener(MqttClient mqttClient){
		this.mqttClient = mqttClient;
	}

	String getTemperature(XBeeMessage xbeeMessage){
		return "";
	}
	String getHumidity(XBeeMessage xbeeMessage){
		return "";
	}
	String getAcceleration(XBeeMessage xbeeMessage){
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(com.digi.xbee.api.models.XBeeMessage)
	 */
	@Override
	public void dataReceived(XBeeMessage xbeeMessage) {
		System.out.format("From %s >> %s | %s%n", xbeeMessage.getDevice().get64BitAddress(),
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())),
				new String(xbeeMessage.getData()));

		/*XBee64BitAddress device = xbeeMessage.getDevice().get64BitAddress();
		String message = new String(xbeeMessage.getData());
		String hexMessage = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData()));

		String temp = getTemperature(xbeeMessage);
		String hum = getHumidity(xbeeMessage);
		String acc = getAcceleration(xbeeMessage);

		try {
			mqttClient.client.publish("device/acceleration", new MqttMessage(acc.getBytes()));
			mqttClient.client.publish("device/humidity", new MqttMessage(hum.getBytes()));
			mqttClient.client.publish("device/temperature", new MqttMessage(temp.getBytes()));
		} catch (MqttException e) {
			e.printStackTrace();
		}*/
	}
}