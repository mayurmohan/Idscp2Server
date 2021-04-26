package idscp2server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.app_layer.listeners.GenericMessageListener;

public class MessageListener implements GenericMessageListener  {

    private Logger LOG = LoggerFactory.getLogger(MessageListener.class);
    private Exchange exchange;
    private Processor processor;
    private ExceptionHandler handler;
	@Override
	public void onMessage(AppLayerConnection arg0, String arg1, byte[] data) {
		// TODO Auto-generated method stub

		String payload= new String(data);
		LOG.info("received message:" + payload);
        try {
    		exchange.getIn().setBody(payload);
            // send message to next processor in the route
        	processor.process(exchange);
         
        } catch (Exception e) {
			// TODO Auto-generated catch block
        	handler.handleException("Error processing exchange", exchange, exchange.getException());
		} finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
            	handler.handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
	}

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public ExceptionHandler getHandler() {
		return handler;
	}

	public void setHandler(ExceptionHandler handler) {
		this.handler = handler;
	}



}
