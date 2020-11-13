package com.cccll;

import com.cccll.entity.RpcServiceProperties;
import com.cccll.extension.ExtensionLoader;
import com.cccll.proxy.RpcClientProxy;
import com.cccll.remoting.transport.ClientTransport;
import com.cccll.remoting.transport.socket.SocketRpcClient;

public class RpcFrameworkSimpleClientMain {
    public static void main(String[] args) {
        ClientTransport clientTransport = ExtensionLoader.getExtensionLoader(ClientTransport.class).getExtension("socketRpcClient");;
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
