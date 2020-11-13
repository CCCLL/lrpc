package com.cccll.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * 自定义包扫描器
 *
 * @author cccll
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        /**
         * ClassPathBeanDefinitionScanner 继承了 ClassPathScanningCandidateComponentProvider，
         * 在ClassPathScanningCandidateComponentProvider 中有两个TypeFilter集合，includeFilters、excludeFilters，
         * 满足任意includeFilters会被加载，同样的满足任意excludeFilters不会被加载。
         *
         * 此处是添加一个includeFilter，带有annoType类型注解或是继承annoType类型注解的注解的bean，都将被扫描进ioc容器
         */
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
