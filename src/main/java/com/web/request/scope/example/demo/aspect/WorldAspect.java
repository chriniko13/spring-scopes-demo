package com.web.request.scope.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class WorldAspect {

    @Pointcut(value = "@annotation(com.web.request.scope.example.demo.annotation.World)")
    public void interceptWorldAnnotatedMethods() {
    }

    @Around(value = "interceptWorldAnnotatedMethods()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        System.out.println("WorldAspect#around --- Before");
        Object result = proceedingJoinPoint.proceed();
        System.out.println("WorldAspect#around --- After");
        return result;

    }

}
