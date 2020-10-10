package com.cccll.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 实现随机负载均衡策略
 *
 * @author cccll
 * @createTime 2020年07月25日 21:20:00
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
