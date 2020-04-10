package org.ariia.mvc.annoutation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Documented

@HttpMethod(method = RequestMethod.HEAD)
public @interface HeadRequest {
	/**
	 * value of the context
	 * value = "/context/a/b/c"
	 * @return
	 */
	String path();
	String[] headers()  default {};
	String[] produces() default {};
}
