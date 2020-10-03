package com.cccll.registry.zk;

import com.cccll.enumeration.RpcErrorMessage;
import com.cccll.exception.RpcException;
import com.cccll.registry.ServiceDiscovery;
import com.cccll.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于zookeeper的服务发现
 *
 * @author cccll
 * @createTime 2020年07月09日 19:16:00
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    //private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        //this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);
        log.info("成功找到服务地址:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
