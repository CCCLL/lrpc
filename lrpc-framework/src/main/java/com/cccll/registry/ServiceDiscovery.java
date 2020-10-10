package com.cccll.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现
 *
 */

public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 查找相应服务
     *
     * @param rpcServiceName rpc service name
     * @return service address
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
