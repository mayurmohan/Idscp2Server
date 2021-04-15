package idscp2server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.app_layer.listeners.IdsMessageListener;
import de.fraunhofer.iais.eis.Message;

public class MessageListener implements IdsMessageListener  {

    private Logger LOG = LoggerFactory.getLogger(MessageListener.class);
	@Override
	public void onMessage(AppLayerConnection arg0, Message arg1, byte[] data) {
		// TODO Auto-generated method stub
		
		LOG.info("received message:" + String.valueOf(data));
		
	}



}
