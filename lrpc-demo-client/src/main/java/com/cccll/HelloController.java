package com.cccll;

import com.cccll.annotation.RpcReference;
import org.springframework.stereotype.Component;


@Component
public class HelloController {

    @RpcReference(version = "version2", group = "test2")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(10000);
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(1000);
            helloService.hello(new Hello("111", "222"));
        }
    }
}
