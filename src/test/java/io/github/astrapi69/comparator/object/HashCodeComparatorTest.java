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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link HashCodeComparator}.
 *
 * @version 1.0
 * @author Asterios Raptis
 */
public class HashCodeComparatorTest
{

	/** For use of the result of the comparator. */
	int actual;

	/** The comparator. */
	Comparator<String> comparator;

	/**
	 * Sets up method will be invoked before every unit test method
	 */
	@BeforeEach
	public void setUp()
	{
		comparator = new HashCodeComparator<>();
	}

	/**
	 * Tear down method will be invoked after every unit test method
	 */
	@AfterEach
	public void tearDown()
	{
		comparator = null;
	}

	/**
	 * Test method for {@link HashCodeComparator#compare(Object, Object)} .
	 */
	@Test
	public void testCompare()
	{
		final String testString = "Albert";
		final String notExpected = "Asterios";
		final String expected = "Albert";

		Assertions.assertTrue(comparator.compare(testString, notExpected) == 1);

		Assertions.assertTrue(comparator.compare(notExpected, testString) == -1);

		Assertions.assertTrue(comparator.compare(testString, expected) == 0);
	}

}
