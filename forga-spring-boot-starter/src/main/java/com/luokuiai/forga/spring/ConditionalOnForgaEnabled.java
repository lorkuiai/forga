package com.luokuiai.forga.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/** Matches when a host configuration bean declares {@link EnableForga}. */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnBean(annotation = EnableForga.class)
@interface ConditionalOnForgaEnabled { }
