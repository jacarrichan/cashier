/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类. 提供访问私有变量,获取泛型类型Class, 提取集合中元素的属性, 转换字符串到对象等Util函数.
 */
public final class ReflectionUtils {

	private ReflectionUtils() {

	}

	// /**
	// * 调用Getter方法.
	// */
	// public static Object invokeGetterMethod(Object obj, String propertyName)
	// throws Exception {
	// String getterMethodName = "get" + StringUtils.capitalize(propertyName);
	// return invokeMethod(obj, getterMethodName, new Class[] {}, new Object[]
	// {});
	// }

	// /**
	// * 调用Setter方法.使用value的Class来查找Setter方法.
	// */
	// public static void invokeSetterMethod(Object obj, String propertyName,
	// Object value) throws Exception {
	// invokeSetterMethod(obj, propertyName, value, null);
	// }

	// /**
	// * 调用Setter方法.
	// *
	// * @param propertyType
	// * 用于查找Setter方法,为空时使用value的Class替代.
	// */
	// public static void invokeSetterMethod(Object obj, String propertyName,
	// Object value, Class<?> propertyType) throws Exception {
	// Class<?> type = propertyType != null ? propertyType : value.getClass();
	// String setterMethodName = "set" + StringUtils.capitalize(propertyName);
	// invokeMethod(obj, setterMethodName, new Class[] { type }, new Object[] {
	// value });
	// }

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 * @param obj 对象
	 * @param fieldName 字段名
	 * @return 字段值
	 * @throws IllegalAccessException when params is null
	 * @throws IllegalArgumentException when field is null
	 */
	public static Object getFieldValue(final Object obj, final String fieldName)
			throws IllegalArgumentException, IllegalAccessException {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		result = field.get(obj);
		return result;
	}

	// /**
	// * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	// *
	// * @throws IllegalAccessException
	// * @throws IllegalArgumentException
	// */
	// public static void setFieldValue(final Object obj, final String
	// fieldName, final Object value) throws IllegalArgumentException,
	// IllegalAccessException {
	// Field field = getAccessibleField(obj, fieldName);
	//
	// if (field == null) {
	// throw new IllegalArgumentException("Could not find field [" + fieldName +
	// "] on target [" + obj + "]");
	// }
	//
	// field.set(obj, value);
	// }

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问. 如向上转型到Object仍无法找到, 返回null.
	 * @param obj 对象
	 * @param fieldName 字段名
	 * @return 字段对象
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			}
			catch (NoSuchFieldException e) {
				// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 直接调用对象方法(无参数）, 无视private/protected修饰符. 用于一次性调用的情况.
	 * @param obj 对象
	 * @param methodName 方法名
	 * @return 返回值
	 * @throws Exception when
	 */
	public static Object invokeMethod(final Object obj, final String methodName) throws Exception {
		return invokeMethod(obj, methodName, new Class[] {}, new Object[] {});
	}

	/**
	 * 执行方法
	 * @param obj 对象
	 * @param methodName 方法名
	 * @param args 请求参数
	 * @return 方法返回值
	 * @throws Exception when
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Object... args) throws Exception {
		Class[] parameterTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = args[i].getClass();
		}
		return invokeMethod(obj, methodName, parameterTypes, args);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符. 用于一次性调用的情况.
	 * @param obj 对象
	 * @param methodName 方法名
	 * @param parameterTypes 请求参数类型
	 * @param args 请求参数
	 * @return 方法返回值
	 * @throws InvocationTargetException when
	 * @throws IllegalArgumentException when
	 * @throws IllegalAccessException when
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
			final Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		return method.invoke(obj, args);
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问. 如向上转型到Object仍无法找到, 返回null. 用于方法需要被多次调用的情况.
	 * 先使用本函数先取得Method,然后调用Method.invoke(Object obj,Object...args)
	 * @param obj 对象
	 * @param methodName 方法名
	 * @param parameterTypes 请求参数类型
	 * @return 方法对象
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {

		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

				method.setAccessible(true);

				return method;

			}
			catch (NoSuchMethodException e) {
				// NOSONAR
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	// /**
	// * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. eg. public
	// UserDao
	// * extends HibernateDao<User>
	// *
	// * @param clazz
	// * The class to introspect
	// * @return the first generic declaration, or Object.class if cannot be
	// * determined
	// */
	// @SuppressWarnings("unchecked")
	// public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
	// return getSuperClassGenricType(clazz, 0);
	// }

	// /**
	// * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
	// *
	// * 如public UserDao extends HibernateDao<User,Long>
	// *
	// * @param clazz
	// * clazz The class to introspect
	// * @param index
	// * the Index of the generic ddeclaration,start from 0.
	// * @return the index generic declaration, or Object.class if cannot be
	// * determined
	// */
	// public static Class getSuperClassGenricType(final Class clazz, final int
	// index) {
	//
	// Type genType = clazz.getGenericSuperclass();
	//
	// if (!(genType instanceof ParameterizedType)) {
	// logger.warn(clazz.getSimpleName() +
	// "'s superclass not ParameterizedType");
	// return Object.class;
	// }
	//
	// Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
	//
	// if (index >= params.length || index < 0) {
	// logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() +
	// "'s Parameterized Type: " + params.length);
	// return Object.class;
	// }
	// if (!(params[index] instanceof Class)) {
	// logger.warn(clazz.getSimpleName() +
	// " not set the actual class on superclass generic parameter");
	// return Object.class;
	// }
	//
	// return (Class) params[index];
	// }

	// /**
	// * 提取集合中的对象的属性(通过getter函数), 组合成List.
	// *
	// * @param collection
	// * 来源集合.
	// * @param propertyName
	// * 要提取的属性名.
	// */
	// @SuppressWarnings("unchecked")
	// public static List convertElementPropertyToList(final Collection
	// collection, final String propertyName) throws Exception {
	// List list = new ArrayList();
	//
	// for (Object obj : collection) {
	// list.add(PropertyUtils.getProperty(obj, propertyName));
	// }
	//
	// return list;
	// }

	// /**
	// * 提取集合中的对象的属性(通过getter函数), 组合成由分割符分隔的字符串.
	// *
	// * @param collection
	// * 来源集合.
	// * @param propertyName
	// * 要提取的属性名.
	// * @param separator
	// * 分隔符.
	// */
	// @SuppressWarnings("unchecked")
	// public static String convertElementPropertyToString(final Collection
	// collection, final String propertyName, final String separator) throws
	// Exception {
	// List list = convertElementPropertyToList(collection, propertyName);
	// return StringUtils.join(list, separator);
	// }

	// /**
	// * 转换字符串到相应类型.
	// *
	// * @param value
	// * 待转换的字符串
	// * @param toType
	// * 转换目标类型
	// */
	// public static Object convertStringToObject(String value, Class<?> toType)
	// throws Exception {
	// return ConvertUtils.convert(value, toType);
	// }

}
