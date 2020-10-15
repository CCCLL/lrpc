package com.cccll.config;

import com.cccll.registry.zk.util.CuratorUtils;
import com.cccll.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 当服务端（provider）关闭的时候做一些事情比如取消注册所有服务
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
