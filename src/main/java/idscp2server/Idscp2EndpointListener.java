package idscp2server;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;

public interface Idscp2EndpointListener{

	public void onConnection( AppLayerConnection connection );
}
