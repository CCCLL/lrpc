package com.cccll.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 */

public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 获取远程服务地址
     *
     * @param rpcServiceName 服务名称
     * @return 远程服务地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
