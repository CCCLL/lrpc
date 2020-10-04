package com.cccll.loadbalance;

import java.util.List;

/**
 * 负载均衡策略的接口
 *
 * @author cccll
 * @createTime 2020年07月25日 20:56:00
 */
public interface LoadBalance {
    /**
     * 从现有服务地址列表中选择一个
     *
     * @param serviceAddresses 服务地址列表
     * @return 选中的服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
