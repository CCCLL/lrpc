package com.cccll.loadbalance;

import java.util.List;

/**
 * 负载均衡策略的接口
 *
 * @author cccll
 */
public interface LoadBalance {
    /**
     * 在已有服务提供地址列表中选择一个
     *
     * @param serviceAddresses 服务地址列表
     * @return 目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
