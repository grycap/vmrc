/*******************************************************************************
 * Copyright 2012 I3M-GRyCAP
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.grycap.vmrc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtils {

	public static Class<?> getClassFromGenericInstance(Object object) {
		Type type = object.getClass().getGenericSuperclass();
		if (type instanceof Class && !(type instanceof ParameterizedType)) {
			return getClassFromGeneric((Class<?>)type);
		}
		return getClassFromParameterizedType(type);

	}

	public static Class<?> getClassFromGeneric(Class<?> clazz) {
		Type type = clazz.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			if (type.equals(Object.class)) return null;
			return getClassFromGeneric((Class<?>) type);
		}
		return getClassFromParameterizedType((ParameterizedType) type);
	}

	public static Class<?> getClassFromParameterizedType(Type type) {
		return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
	}
}
