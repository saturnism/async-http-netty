/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of async-http-netty.
 *
 * async-http-netty is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * async-http-netty is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with async-http-netty. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package com.johnstok.http.netty.test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.johnstok.http.Version;
import com.johnstok.http.async.Request;
import com.johnstok.http.async.Response;

/**
 * HTTP Request that sends a 'Hello World!" response.
 *
 * @author Keith Webster Johnston.
 */
public class HelloWorldRequest
    implements
        Request {

    private Response _response;
    private final Logger log = Logger.getLogger(getClass().getName());


    @Override
    public void onRequestLine(final String method, final String uri, final Version version) {
        log.info(method+" "+uri+" "+version);
    }


    @Override
    public void onHeaders(final Map<String, List<String>> headers) {
        log.info(headers.toString());
    }


    @Override
    public void onEnd(final Map<String, List<String>> trailers) {
        _response.writeStatusLine(new Version(1, 1), 200, "OK");
        _response.writeHeaders(new HashMap<String, List<String>>());
        _response.writeBody(ByteBuffer.wrap("Hello World!".getBytes()));
        _response.writeEnd(null);
    }


    @Override
    public void onBody(final ByteBuffer bytes) {

    }


    @Override
    public void onBegin(final Response response) {
        _response=response;
    }
}
