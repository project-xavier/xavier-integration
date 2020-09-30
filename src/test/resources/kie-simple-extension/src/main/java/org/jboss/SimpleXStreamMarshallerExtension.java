package org.jboss;

import org.kie.server.api.marshalling.xstream.XStreamMarshaller;
import org.kie.server.api.marshalling.xstream.XStreamMarshallerExtension;

public class SimpleXStreamMarshallerExtension implements XStreamMarshallerExtension {

    @Override
    public void extend(XStreamMarshaller marshaller) {
        System.out.println("SETTING MARSHALLER IGNOREUNKNOWNELEMENTS ++++++++++++++++++++++++++++");
        marshaller.getXstream().ignoreUnknownElements();
    }

}
