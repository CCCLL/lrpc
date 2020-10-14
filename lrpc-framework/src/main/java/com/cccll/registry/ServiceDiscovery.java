package com.cccll.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 */

public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 查找相应服务
     *
     * @param rpcServiceName 服务名称
     * @return 提供服务的地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
