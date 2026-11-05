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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The unit test class for the enum class {@link CompareOrder}.
 */
public class CompareOrderTest
{

	/**
	 * A constant of {@link CompareOrder} and its order
	 *
	 * @param description
	 *            the name of the case
	 * @param compareOrder
	 *            the constant
	 * @param order
	 *            its order, the result of a comparison that sorts an object so
	 */
	record OrderOfAConstant(String description, CompareOrder compareOrder, int order) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The three constants: after is 1, before is -1 and equal is 0, the signs a comparison returns
	 *
	 * @return the constants and their orders
	 */
	static Stream<OrderOfAConstant> ordersOfTheConstants()
	{
		return Stream.of(new OrderOfAConstant("AFTER: 1", CompareOrder.AFTER, 1),
			new OrderOfAConstant("BEFORE: -1", CompareOrder.BEFORE, -1),
			new OrderOfAConstant("EQUAL: 0", CompareOrder.EQUAL, 0));
	}

	/**
	 * Test method for {@link CompareOrder#getOrder()}
	 *
	 * @param constant
	 *            the constant and its order
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("ordersOfTheConstants")
	public void testGetOrder(final OrderOfAConstant constant)
	{
		assertEquals(constant.order(), constant.compareOrder().getOrder());
	}

}
