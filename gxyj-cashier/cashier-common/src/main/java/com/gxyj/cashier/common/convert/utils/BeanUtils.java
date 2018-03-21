/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Bean工具类.
 *
 * @author Danny
 */
public final class BeanUtils {

	public static Object instantiateClass(Class<?> clazz) throws RuntimeException {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}

		if (clazz.isInterface()) {
			throw new RuntimeException("Specified class is an interface");
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor((Class[]) null), null);
		}
		catch (NoSuchMethodException ex) {
			throw new RuntimeException("No default constructor found", ex);
		}
	}

	public static Object instantiateClass(Constructor<?> ctor, Object[] args)
			throws RuntimeException {
		if (ctor == null) {
			throw new IllegalArgumentException("Constructor must not be null");
		}

		try {
			if (!Modifier.isPublic(ctor.getModifiers())
					|| !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
				ctor.setAccessible(true);
			}
			return ctor.newInstance(args);
		}
		catch (InstantiationException ex) {
			throw new RuntimeException("Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(
					"Has the class definition changed? Is the constructor accessible?",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new RuntimeException("Illegal arguments for constructor", ex);
		}
		catch (InvocationTargetException ex) {
			throw new RuntimeException("Constructor threw exception",
					ex.getTargetException());
		}
	}

	private BeanUtils() {

	}
}
