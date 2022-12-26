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
package io.github.astrapi69.comparator;

import java.util.Comparator;

/**
 * The enum {@link CompareOrder} that encapsulates the possible return values of the method
 * {@link Comparator#compare(Object, Object)}
 */
public enum CompareOrder
{
	/** The order to sort an object after. So the object is greater than the compared object */
	AFTER(1),
	/** The order to sort an object before. So the object is smaller than the compared object */
	BEFORE(-1),
	/** The order to sort an object as equal. So the object is the same as the compared object */
	EQUAL(0);

	/** The order */
	private final int order;

	CompareOrder(final int order)
	{
		this.order = order;
	}

	/**
	 * Gets the order
	 * 
	 * @return the order
	 */
	public int getOrder()
	{
		return order;
	}
}
