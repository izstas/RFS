package me.izstas.rfs.server.config.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation binds a method parameter to the path part matched by the Ant pattern.
 *
 * Example: for {@code @RequestMapping("/xyz/**")} and the requested URL {@code /xyz/abc/def}, the value will be {@code abc/def}
 *
 * @see org.springframework.util.AntPathMatcher#extractPathWithinPattern(String, String)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathWithinPattern {
}
