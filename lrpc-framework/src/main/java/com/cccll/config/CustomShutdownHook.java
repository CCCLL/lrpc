package com.cccll.config;

import com.cccll.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 当服务器关闭时，做一些处理，比如清除注册的所有服务
 *
 * @author cccll
 * @createTime 2020年07月02日 20:32:00
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //清除注册的所有服务
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
            //shutDown所有的线程池
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
