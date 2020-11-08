package com.cccll.registry;

import com.cccll.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 获取远程服务地址
     *
     * @param rpcServiceName 服务名称
     * @return 远程服务地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
