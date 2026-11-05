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
 * The class {@link StringLengthComparator} compares {@link String} objects by their length: the
 * longer string comes first, in either sort order. The sort order decides between strings of equal
 * length only: {@link SortOrder#ASCENDING}, the default, puts them in their natural order, and
 * {@link SortOrder#DESCENDING} in the reverse of it. So {@link SortOrder#DESCENDING} still puts the
 * longer string first and reverses only the order of strings of equal length:
 * {@code "Leon", "Alex", "Billy"} sorts to {@code "Billy", "Alex", "Leon"} with
 * {@link SortOrder#ASCENDING} and to {@code "Billy", "Leon", "Alex"} with
 * {@link SortOrder#DESCENDING}. This differs from {@code Comparator.comparingInt(String::length)},
 * which puts the shorter string first, and its {@code reversed()}, which puts the longer one first:
 * there the order decides the order of the lengths, here the sort order does not.
 *
 * @author Asterios Raptis
 */
public class StringLengthComparator extends SortOrderComparator<String>
{

	/**
	 * Instantiates a new {@link StringLengthComparator} with the default sort order
	 * {@link SortOrder#ASCENDING}: the longer string comes first, and strings of equal length come
	 * in their natural order.
	 */
	public StringLengthComparator()
	{
	}

	/**
	 * Instantiates a new {@link StringLengthComparator} with the given sort order, which decides
	 * between strings of equal length only: the longer string comes first in either sort order, and
	 * strings of equal length come in their natural order with {@link SortOrder#ASCENDING} and in
	 * the reverse of it with {@link SortOrder#DESCENDING}.
	 *
	 * @param sortOrder
	 *            the sort order of strings of equal length
	 */
	public StringLengthComparator(final SortOrder sortOrder)
	{
		super(sortOrder);
	}

	/**
	 * Factory method to create a new {@link StringLengthComparator} with the default sort order
	 * {@link SortOrder#ASCENDING}, in which null is greater than a non-null string: the longer
	 * string comes first, strings of equal length come in their natural order, and null comes last.
	 *
	 * @return the new {@link StringLengthComparator} object
	 */
	public static Comparator<String> of()
	{
		return StringLengthComparator.of(true);
	}

	/**
	 * Factory method to create a new {@link StringLengthComparator} with the default sort order
	 * {@link SortOrder#ASCENDING}: the longer string comes first, strings of equal length come in
	 * their natural order, and null comes last or first as the given flag says.
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null is greater than a non-null string
	 * @return the new {@link StringLengthComparator} object
	 */
	public static Comparator<String> of(final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new StringLengthComparator(), nullIsGreaterThan);
	}

	/**
	 * Factory method to create a new {@link StringLengthComparator} with the given sort order,
	 * which decides between strings of equal length only, and in which null is greater than a
	 * non-null string: the longer string comes first in either sort order, strings of equal length
	 * come in their natural order with {@link SortOrder#ASCENDING} and in the reverse of it with
	 * {@link SortOrder#DESCENDING}, and null comes last in either sort order.
	 *
	 * @param sortOrder
	 *            the sort order of strings of equal length
	 * @return the new {@link StringLengthComparator} object
	 */
	public static Comparator<String> of(final SortOrder sortOrder)
	{
		return StringLengthComparator.of(sortOrder, true);
	}

	/**
	 * Factory method to create a new {@link StringLengthComparator} with the given sort order,
	 * which decides between strings of equal length only: the longer string comes first in either
	 * sort order, strings of equal length come in their natural order with
	 * {@link SortOrder#ASCENDING} and in the reverse of it with {@link SortOrder#DESCENDING}, and
	 * null comes last or first as the given flag says, in either sort order.
	 *
	 * @param sortOrder
	 *            the sort order of strings of equal length
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null is greater than a non-null string
	 * @return the new {@link StringLengthComparator} object
	 */
	public static Comparator<String> of(final SortOrder sortOrder, final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new StringLengthComparator(sortOrder), nullIsGreaterThan);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final String object, final String compareWithObject)
	{
		final int compareWithObjectLength = compareWithObject.length();
		final int objectLength = object.length();
		if (objectLength == compareWithObjectLength)
		{
			return super.compare(object, compareWithObject);
		}
		return compareWithObjectLength - objectLength;
	}

}
