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
import java.util.Date;

import io.github.astrapi69.comparator.ComparatorExtensions;
import io.github.astrapi69.comparator.SortOrder;

/**
 * The class {@link DescendingDateComparator} sorts {@link Date} objects in descending order, the
 * later date first and null last, as {@code new DateComparator(SortOrder.DESCENDING)} does.
 *
 * @deprecated use {@link DateComparator#DateComparator(SortOrder)} with
 *             {@link SortOrder#DESCENDING}, which sorts the same: the later date first and null
 *             last
 */
@Deprecated(since = "3.2")
public class DescendingDateComparator implements Comparator<Date>
{

	/**
	 * Instantiates a new {@link DescendingDateComparator}, which sorts {@link Date} objects in
	 * descending order, the later date first and null last.
	 */
	public DescendingDateComparator()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final Date date, final Date compareWithDate)
	{
		return ComparatorExtensions.compare(date, compareWithDate, SortOrder.DESCENDING);
	}

}
