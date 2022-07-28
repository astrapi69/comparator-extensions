/**
 * The MIT License
 *
 * Copyright (C) 2015 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator.factory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class TestDataFactory
{
	/**
	 * Factory method for create a map for and count elements of the given collection
	 *
	 * @param <K>
	 *            the generic type of the elements
	 * @param counterMap
	 *            the counter Map
	 * @param elements
	 *            the elements
	 * @return the new map ready to count elements
	 */
	public static <K> Map<K, Integer> newCounterMap(Map<K, Integer> counterMap,
		Collection<K> elements)
	{
		Objects.requireNonNull(counterMap);
		for (K element : elements)
		{
			if (counterMap.containsKey(element))
			{
				counterMap.merge(element, 1, Integer::sum);
				continue;
			}
			counterMap.put(element, 0);
		}
		return counterMap;
	}

}
