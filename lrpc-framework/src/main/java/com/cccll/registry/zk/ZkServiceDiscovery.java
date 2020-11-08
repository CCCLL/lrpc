package com.cccll.registry.zk;

import com.cccll.enumeration.RpcErrorMessage;
import com.cccll.exception.RpcException;
import com.cccll.loadbalance.LoadBalance;
import com.cccll.loadbalance.RandomLoadBalance;
import com.cccll.registry.ServiceDiscovery;
import com.cccll.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于 zookeeper 实现服务发现
 *
 * @author cccll
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //获取rpcServiceName下所有子节点（服务地址），相同服务被部署多份就有多个，如果此服务只部署一台，则此集合中只有一个服务地址
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
