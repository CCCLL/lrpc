package com.cccll.registry.zk;

import com.cccll.registry.ServiceRegistry;
import com.cccll.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 基于 zookeeper 实现服务注册
 *
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    /**
     *服务被注册进 zookeeper 的时候，将完整的服务名称 rpcServiceName（class name+group+version）作为根节点 ，
     * 子节点是对应的服务地址（ip+端口号），一个根节点可能会对应多个服务地址（相同服务被部署多份的情况）
     *
     * @param rpcServiceName       服务名称 （class name+group+version   例如：com.cccll.HelloServicetest1version1）
     *                                                                  class name : 服务接口名也就是类名比如：com.cccll.HelloService
     *                                                                  version :（服务版本）主要是为后续不兼容升级提供可能
     *                                                                  group :主要用于处理一个接口有多个类实现的情况
     * @param inetSocketAddress 提供服务的地址
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        //根节点下注册子节点：服务
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);

    }
}
