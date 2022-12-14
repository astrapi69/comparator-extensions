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

import java.util.Comparator;

import io.github.astrapi69.comparator.NullCheckComparator;
import io.github.astrapi69.comparator.SortOrder;
import io.github.astrapi69.comparator.SortOrderComparator;

/**
 * The class {@link StringComparator} sorts String objects in ascending order as default.
 *
 * @author Asterios Raptis
 */
public class StringComparator extends SortOrderComparator<String>
{

	/**
	 * Instantiates a new {@link StringComparator} object
	 */
	public StringComparator()
	{
	}

	/**
	 * Instantiates a new {@link StringComparator} object
	 *
	 * @param sortOrder
	 *            the sort order
	 */
	public StringComparator(final SortOrder sortOrder)
	{
		super(sortOrder);
	}

	/**
	 * Factory method to create a new {@link StringComparator} object
	 *
	 * @return the new {@link StringComparator} object
	 */
	public static Comparator<String> of()
	{
		return StringComparator.of(true);
	}

	/**
	 * Factory method to create a new {@link StringComparator} object
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects are greater than a non-null objects
	 * @return the new {@link StringComparator} object
	 */
	public static Comparator<String> of(final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new StringComparator(), nullIsGreaterThan);
	}

	/**
	 * Factory method to create a new {@link StringComparator} object
	 *
	 * @param sortOrder
	 *            the sort order
	 * @return the new {@link StringComparator} object
	 */
	public static Comparator<String> of(final SortOrder sortOrder)
	{
		return StringComparator.of(sortOrder, true);
	}

	/**
	 * Factory method to create a new {@link StringComparator} object
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects are greater than a non-null objects
	 * @param sortOrder
	 *            the sort order
	 * @return the new {@link StringComparator} object
	 */
	public static Comparator<String> of(final SortOrder sortOrder, final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new StringComparator(sortOrder), nullIsGreaterThan);
	}

}
