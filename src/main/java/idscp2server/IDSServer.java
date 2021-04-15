package idscp2server;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.AisecDapsDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.AisecDapsDriverConfig;
import de.fhg.aisec.ids.idscp2.default_drivers.rat.dummy.RatProverDummy;
import de.fhg.aisec.ids.idscp2.default_drivers.rat.dummy.RatVerifierDummy;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTLSDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTlsConfiguration;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.AttestationConfig;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.Idscp2Configuration;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2Connection;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionAdapter;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionImpl;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionListener;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2MessageListener;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_server.Idscp2Server;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_server.Idscp2ServerFactory;
import de.fhg.aisec.ids.idscp2.idscp_core.drivers.SecureChannelDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2Exception;
import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2NotConnectedException;
import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2TimeoutException;
import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2WouldBlockException;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatProverDriverRegistry;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatVerifierDriverRegistry;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author I050368
 */
public class IDSServer{

    private static Logger LOG = LoggerFactory.getLogger(IDSServer.class);


	public static void main(String[] args) {
		
		startIDSServer();
	}
    
    
    public static void startIDSServer() {
    	
        try {
        	
            String host = "localhost";
           // host = "consumer-core";
            int port = 29292;
            String ksPath = "ssl/consumer-keystore.p12";
             
            String tsPath = "ssl/truststore.p12";
            
            
            // register rat drivers
            RatProverDriverRegistry.INSTANCE.registerDriver(
                    RatProverDummy.RAT_PROVER_DUMMY_ID, RatProverDummy::new, null
            );
            RatVerifierDriverRegistry.INSTANCE.registerDriver(
                    RatVerifierDummy.RAT_VERIFIER_DUMMY_ID, RatVerifierDummy::new, null
            );

            // create attestation config
            AttestationConfig localAttestationConfig = (new AttestationConfig.Builder())
                    .setSupportedRatSuite(new String[]{RatProverDummy.RAT_PROVER_DUMMY_ID})
                    .setExpectedRatSuite(new String[]{RatVerifierDummy.RAT_VERIFIER_DUMMY_ID})
                    .setRatTimeoutDelay(Long.parseLong("3600000"))
                    .build();

            // secure channel config
            NativeTlsConfiguration secureChannelConfig = (new NativeTlsConfiguration.Builder())
                    .setHost(host)
                    .setServerPort(port)
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStoreKeyType("RSA")
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .setCertificateAlias("1.0.1")
                    .build();


            // create daps config
            AisecDapsDriverConfig dapsDriverConfig = (new AisecDapsDriverConfig.Builder())
                    .setDapsUrl("https://daps.aisec.fraunhofer.de")
                    .setKeyAlias("1")
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .build();


            // create idscp configuration
            Idscp2Configuration serverConfiguration = (new Idscp2Configuration.Builder())
                    .setAttestationConfig(localAttestationConfig)
                    .setDapsDriver(new AisecDapsDriver(dapsDriverConfig))
                    .build();

            SecureChannelDriver<AppLayerConnection, NativeTlsConfiguration> secureChannelDriver = new NativeTLSDriver<>();
            Idscp2EndpointListenerImpl listImpl = new Idscp2EndpointListenerImpl();

            Idscp2ServerFactory serverFactory = new Idscp2ServerFactory(AppLayerConnection::new,listImpl,serverConfiguration, secureChannelDriver, secureChannelConfig);
                  
            Idscp2Server server = serverFactory.listen();
 
        } catch (Exception e) {
        	LOG.error("Failed to send message:" + e.getMessage());
            e.printStackTrace();
        }
    }





}
