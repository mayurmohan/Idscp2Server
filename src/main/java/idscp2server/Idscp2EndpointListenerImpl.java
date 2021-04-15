package idscp2server;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;

public class Idscp2EndpointListenerImpl implements Idscp2EndpointListener {

	public Idscp2EndpointListenerImpl() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onConnection(AppLayerConnection connection) {
			// TODO Auto-generated method stub
		connection.addIdsMessageListener(new MessageListener());
	}

}
