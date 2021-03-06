package com.khs.sherpa.parser;

/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/



import com.khs.sherpa.annotation.Param;

public class DoubleParamPaser implements ParamParser<Double> {

	public boolean isValid(Class<?> clazz) {
		return (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class));
	}

	public Double parse(String value, Param annotation, Class<?> clazz) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(value+" must be double ");
		}
	}

}
