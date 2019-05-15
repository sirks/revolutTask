package com.revolut.task.integration


import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE

abstract class WsHelper {
    final String ROOT = MockServer.HOST + ":" + MockServer.PORT + "/"
    final Client client

    WsHelper() {
        client = Client.create()
    }

    def stop() {
        client.destroy()
    }

    def <T> T post(String url, Object body, Class<T> returnType) {
        WebResource webResource = client.resource(url)
        ClientResponse clientResponse = webResource.type(APPLICATION_JSON_TYPE).post(ClientResponse.class, body)
        getBody(clientResponse, returnType)
    }

    def <T> T get(String url, Class<T> returnType){
        WebResource webResource = client.resource(url)
        ClientResponse clientResponse = webResource.type(APPLICATION_JSON_TYPE).get(ClientResponse.class)
        getBody(clientResponse, returnType)
    }

    static <T> T getBody(ClientResponse clientResponse, Class<T> returnType) {
        if (clientResponse.getStatus() >= 300) {
            return
        }
        if (clientResponse.hasEntity()) {
            clientResponse.getEntity(returnType)
        }
    }

}
