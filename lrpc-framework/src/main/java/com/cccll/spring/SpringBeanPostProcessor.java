package com.cccll.spring;

import com.cccll.annotation.RpcService;
import com.cccll.entity.RpcServiceProperties;
import com.cccll.factory.SingletonFactory;
import com.cccll.provider.ServiceProvider;
import com.cccll.provider.ServiceProviderImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 在创建bean之前调用此方法，以查看是否对类进行了注解，如果类上有@RpcService，说明是我们的服务实现类，
 * 则进行注册服务相关操作。
 *
 * postProcessBeforeInitialization 方法是在bean的构造方法执行完，我们自己指定的初始化方法（例如：@Bean(initMethod = "init"）中指定的方法、
 * 实现InitializingBean接口的afterPropertiesSet方法、使用@PostConstruct注解的方法） 执行前执行。
 *
 * postProcessAfterInitialization 方法是在执行完我们自己指定的初始化方法之后执行。
 *
 * 这两个方法对要注册进IOC容器的所有bean都会执行，所以我们我可以写过滤条件，只对服务实现类执行
 *
 * 当这些方法都执行完毕后spring的容器才会创建
 * @author cccll
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {


    private final ServiceProvider serviceProvider;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //过滤条件，只对类上有 @RpcService 的bean进行服务注册相关操作
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                    .group(rpcService.group()).version(rpcService.version()).build();
            serviceProvider.publishService(bean, rpcServiceProperties);
        }
        return bean;
    }

}
