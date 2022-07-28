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
package io.github.astrapi69.comparator;

import java.util.Comparator;

/**
 * The abstract class {@link SortOrderComparator} can be extended for adding the sort order
 * ascending or descending. The default sort order is ascending.
 *
 * @param <T>
 *            the generic type of the object to compare
 */
public class SortOrderComparator<T extends Comparable<T>> implements Comparator<T>
{

	/** The constant default sort order. */
	public static final SortOrder DEFAULT_SORT_ORDER = SortOrder.ASCENDING;
	/** The sort order. Default is ascending. */
	private SortOrder sortOrder = SortOrder.ASCENDING;

	/**
	 * Instantiates a new {@link SortOrderComparator} with the default sort order.
	 */
	public SortOrderComparator()
	{
		this(DEFAULT_SORT_ORDER);
	}

	/**
	 * Instantiates a new {@link SortOrderComparator} with the given sort order.
	 *
	 * @param sortOrder
	 *            the sort order
	 */
	public SortOrderComparator(final SortOrder sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	/**
	 * Factory method to create a new {@link SortOrderComparator} with the default sort order.
	 *
	 * @param <T>
	 *            the generic type of the object to compare
	 *
	 * @return the new {@link Comparator} object
	 */
	public static <T extends Comparable<T>> Comparator<T> of()
	{
		return new SortOrderComparator<T>();
	}

	/**
	 * Factory method to create a new {@link SortOrderComparator} with the default sort order.
	 *
	 * @param <T>
	 *            the generic type of the object to compare
	 * @param sortOrder
	 *            the sort order
	 *
	 * @return the new {@link Comparator} object
	 */
	public static <T extends Comparable<T>> Comparator<T> of(final SortOrder sortOrder)
	{
		return new SortOrderComparator<T>(sortOrder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final T object, final T compareWithObject)
	{
		return ComparatorExtensions.compare(object, compareWithObject, sortOrder);
	}

}
