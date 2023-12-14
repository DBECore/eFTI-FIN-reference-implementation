package com.ingroupe.efti.edeliveryapconnector.service;

import com.ingroupe.efti.edeliveryapconnector.dto.ApConfigDto;
import eu.domibus.plugin.ws.client.WebserviceClient;
import eu.domibus.plugin.ws.generated.WebServicePluginInterface;

import java.net.MalformedURLException;

public abstract class AbstractApService {

    protected WebServicePluginInterface initApWebService(final ApConfigDto apConfigDto) throws MalformedURLException {
        final WebserviceClient webserviceExample = new WebserviceClient(apConfigDto.getUrl(), true);
        return webserviceExample.getPort(apConfigDto.getUsername(), apConfigDto.getPassword());
    }
}
