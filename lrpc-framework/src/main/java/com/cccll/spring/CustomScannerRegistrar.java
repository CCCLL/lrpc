package com.cccll.spring;

import com.cccll.annotation.RpcScan;
import com.cccll.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 扫描和过滤指定的注解
 *
 * @author cccll
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE = "com.cccll.spring";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    /**
     * Spring启动时会自动调用：implements了ResourceLoaderAware接口类的实现方法：setResourceLoader()，
     * 将ResourceLoader注入进去,此处的ResourceLoader就是我们创建的ApplicationContext对象，
     * 因为ApplicationContext相关类或其父类都实现了ResourceLoader接口，或其子接口ResourcePatternResolver，
     * 后续代码会将此resourceLoader传递给我们自定义的包扫描器。
     * 实现以Aware后缀的接口，一般都是为了获得spring的一些数据
     * @param resourceLoader
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取RpcScan注解属性和值的map，然后转成AnnotationAttributes，AnnotationAttributes是Spring提供的注解的属性和值的包装类，底层也是map结构，它继承了LinkedHashMap
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null) {
            // 获取basePackage属性的值
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        //满足此条件则说明我们没有给@RpcScan的basePackage赋值
        if (rpcScanBasePackages.length == 0) {
            //得到标注@RpcScan的类的包名，作为被扫描的包（这里借用Spring Boot的思想，把标注@RpcScan的类放到最外层包下就可扫描所有子包下的类）
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        // 扫描标注 RpcService 注解的类
        CustomScanner rpcServiceScanner = new CustomScanner(beanDefinitionRegistry, RpcService.class);
        // 扫描标注 Component 注解的类
        CustomScanner springBeanScanner = new CustomScanner(beanDefinitionRegistry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
    }

}
