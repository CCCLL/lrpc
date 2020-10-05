package com.cccll.proxy;

import com.cccll.entity.RpcServiceProperties;
import com.cccll.remoting.dto.RpcMessageChecker;
import com.cccll.remoting.dto.RpcRequest;
import com.cccll.remoting.dto.RpcResponse;
import com.cccll.remoting.transport.ClientTransport;
import com.cccll.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 动态代理类.
 * 当动态代理对象调用一个方法时，它实际上调用下面的invoke方法。
 * 正是由于动态代理，客户端调用的远程方法就像调用本地方法一样（中间过程被屏蔽）
 *
 * @author cccll
 * @createTime 2020年06月15日 23:01:00
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    /**
     *
     * 用于向服务器发送请求。有两种实现：socket和netty
     */
    private final ClientTransport clientTransport;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(ClientTransport clientTransport, RpcServiceProperties rpcServiceProperties) {
        this.clientTransport = clientTransport;
        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }


    public RpcClientProxy(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
    }

    /**
     * 获取代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 当你使用代理对象调用方法时，实际上会调用此方法。
     * 代理对象是你通过 getProxy方法获得的对象。
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        //构建rpc请求体
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        //根据clientTransport的实际类型，会以不同方式发送rpc请求体，然后得到其rpcResponse，返回
        if (clientTransport instanceof NettyClientTransport) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) clientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        if (clientTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) clientTransport.sendRpcRequest(rpcRequest);
        }
        RpcMessageChecker.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }
}
