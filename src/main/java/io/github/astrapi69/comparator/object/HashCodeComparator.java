/**
 * The MIT License
 *
 * Copyright (C) 2022 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator.object;

import java.io.Serializable;
import java.util.Comparator;

import io.github.astrapi69.comparator.ComparatorExtensions;

/**
 * The class {@link HashCodeComparator} compares two objects by their hash codes, the smaller hash
 * code first, and puts null before every object.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals. Two objects with the
 * same hash code compare as 0 even where {@link Object#equals(Object)} tells them apart, as the
 * strings {@code "Aa"} and {@code "BB"} (hash code 2112) or the longs {@code 0L} and {@code -1L}
 * (hash code 0). A {@link java.util.TreeSet} or a {@link java.util.TreeMap} ordered by it keeps one
 * of them only, and its {@code contains}, {@code get} and {@code remove} take the one for the
 * other, so it does not keep the general contract of {@link java.util.Set} or {@link java.util.Map}
 * (see {@link Comparator}). It is consistent with equals for a type whose unequal objects never
 * share a hash code, such as {@link Integer}
 *
 * @param <T>
 *            the generic type
 * @version 1.0
 * @author Asterios Raptis
 */
public class HashCodeComparator<T> implements Comparator<T>, Serializable
{

	/**
	 * The generated serialVersionUID.
	 */
	private static final long serialVersionUID = -6270968560769922192L;

	/**
	 * Instantiates a new {@link HashCodeComparator}, which compares objects by their hash code.
	 */
	public HashCodeComparator()
	{
	}

	/**
	 * Compares the given objects by their hash codes, null before every object
	 *
	 * @param object
	 *            One Object.
	 * @param compareWithObject
	 *            The other Object to compare.
	 * @return a negative integer, zero or a positive integer as the hash code of the first object
	 *         is less than, equal to or greater than the hash code of the second; null is less than
	 *         every object, and two nulls are equal
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(final T object, final T compareWithObject)
	{
		final Integer nullCheck = ComparatorExtensions.nullCheck(object, compareWithObject);
		if (nullCheck != null)
		{
			return nullCheck;
		}
		return ComparatorExtensions.compare(object.hashCode(), compareWithObject.hashCode());
	}

}
