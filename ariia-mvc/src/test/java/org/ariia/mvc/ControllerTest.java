package org.ariia.mvc;

import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.ariia.mvc.processing.ProxySwitcher;
import org.junit.Test;

public class controllerTest {
	
	/**
	 * repo
	 */
	
	
	@Test
	public void test1() {
		ItemController item = new ItemController();
        ProxySwitcher controller = new ProxySwitcher(item);
        controller.getMarkers().forEach(System.out::println);
        controller.getMarkers().forEach(m -> {
        	try {
        		for (Parameter parameter : m.getMethod().getParameters()) {
        			System.out.print(parameter.getName());
        			System.out.print('\t');
        			System.out.print(parameter.getAnnotatedType());
        			System.out.print('\t');
        			System.out.print(Arrays.toString(parameter.getAnnotations()));
        			System.out.print('\t');
        			System.out.print(parameter.isSynthetic());
        			System.out.print('\n');
        		}
//				System.out.println(m.getMethod().invoke(item, 0, new Item()));
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
	}

}
