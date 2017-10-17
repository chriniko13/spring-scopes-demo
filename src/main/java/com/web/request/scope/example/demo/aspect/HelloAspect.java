package com.web.request.scope.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Aspect
@Component
public class HelloAspect {

    @Pointcut(value = "@annotation(com.web.request.scope.example.demo.annotation.Hello)")
    public void interceptHelloAnnotatedMethods() {
    }

    @Around(value = "interceptHelloAnnotatedMethods()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        System.out.println("HelloAspect#around --- Before");
        Object result = proceedingJoinPoint.proceed();
        System.out.println("HelloAspect#around --- After");
        return result;

    }

}
