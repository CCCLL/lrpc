package com.cccll.registry;

import com.cccll.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 *
 * @author cccll
 */
@SPI
public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param rpcServiceName       服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
