package com.cccll.registry.zk;

import com.cccll.registry.ServiceRegistry;
import com.cccll.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 基于 zookeeper 实现服务注册
 *
 * @author cccll
 * @createTime 2020年06月30日 10:56:00
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        //根节点下注册子节点：服务
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);

    }
}
