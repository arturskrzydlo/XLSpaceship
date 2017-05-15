package com.xebia.exceptions;

import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */

//TODO: maybe something smarter ?
public class ClientErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler myErrorHandler = new DefaultResponseErrorHandler();

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body = IOUtils.toString(response.getBody());
        CustomClientException exception = new CustomClientException(response.getStatusCode(), body, body);
        throw exception;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return myErrorHandler.hasError(response);
    }
}
