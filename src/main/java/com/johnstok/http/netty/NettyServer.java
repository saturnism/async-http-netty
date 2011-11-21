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
package com.johnstok.http.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import com.johnstok.http.RequestFactory;
import com.johnstok.http.Server;


/**
 * The Netty server implementation.
 *
 * @author Keith Webster Johnston.
 */
public class NettyServer
    implements
        Server {

    // TODO: Remove Chunk Aggregation
    // TODO: Send trailers with request#onEnd()

    private Channel _channel;


    /** {@inheritDoc} */
    @Override
    public void listen(final InetSocketAddress address,
                       final RequestFactory requestFactory) {
        if (null!=_channel) {
            throw new IllegalStateException("Server is already listening.");
        }
        ServerBootstrap _bootstrap = new ServerBootstrap(
            new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        _bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override  public ChannelPipeline getPipeline() {
                final ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler", new AsyncHttpUpstreamHandler(requestFactory));
                return pipeline;
            }
        });
        _channel = _bootstrap.bind(address);
    }


    /** {@inheritDoc} */
    @Override
    public void close() {
        if (null==_channel) {
            throw new IllegalStateException("Server is not listening.");
        }
        _channel.close().awaitUninterruptibly();
        _channel = null;
    }
}
