package com.cccll.loadbalance;

import java.util.List;

/**
 * 负载均衡策略的抽象类
 *
 * @author cccll
 * @createTime 2020年07月25日 21:06:00
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses);
    }

    protected abstract String doSelect(List<String> serviceAddresses);

}
