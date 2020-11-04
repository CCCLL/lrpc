package com.cccll.remoting.transport.netty.client;

import com.cccll.extension.ExtensionLoader;
import com.cccll.factory.SingletonFactory;
import com.cccll.registry.ServiceDiscovery;
import com.cccll.remoting.dto.RpcRequest;
import com.cccll.remoting.dto.RpcResponse;
import com.cccll.remoting.transport.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * 基于 Netty 传输 RpcRequest。
 *
 * @author cccll
 */

@Slf4j
public class NettyClientTransport implements ClientTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyClientTransport() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 通过rpcRequest构建rpc服务名
        String rpcServiceName = rpcRequest.toRpcProperties().toRpcServiceName();
        // get server address
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        // 获取  server address 对应的 channel
        Channel channel = channelProvider.get(inetSocketAddress);
        // 此处的channel.isWritable()检查主要是利用Netty的高水位机制来实现流控
        if (channel != null && channel.isActive() && channel.isWritable()) {
            // 放入未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    //当调用CompletableFuture.get()被阻塞的时候,那么这个方法就是结束阻塞,并且get()获取发生的异常.
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

}