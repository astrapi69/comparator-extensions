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
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
	 * Two strings and the sign of their comparison
	 *
	 * @param description
	 *            the name of the case
	 * @param object
	 *            the first string
	 * @param compareWithObject
	 *            the string to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record ComparedStrings(String description, String object, String compareWithObject,
		int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases with null, which comes before every object, and with hash codes that
	 * {@link String#hashCode()} defines: a string of one character has that character as its hash
	 * code, 97 for a and 98 for b, and Aa and BB share 2112
	 *
	 * @return the cases
	 */
	static Stream<ComparedStrings> comparedStrings()
	{
		final String a = "a";
		// another instance, equal to a and with its hash code
		final String otherA = new String(a);
		return Stream.of(new ComparedStrings("both null", null, null, 0),
			new ComparedStrings("the first null", null, a, -1),
			new ComparedStrings("the second null", a, null, 1),
			new ComparedStrings("the same instance", a, a, 0),
			new ComparedStrings("equal, two instances", a, otherA, 0),
			new ComparedStrings("the smaller hash code first", a, "b", -1),
			new ComparedStrings("the greater hash code first", "b", a, 1),
			new ComparedStrings("not equal, the same hash code", "Aa", "BB", 0));
	}

	/**
	 * Test method for {@link HashCodeComparator#compare(Object, Object)}: null before every object,
	 * otherwise the order of the hash codes, which does not tell two objects with the same hash
	 * code apart
	 *
	 * @param strings
	 *            the strings and the sign of their comparison
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("comparedStrings")
	public void testCompare(final ComparedStrings strings)
	{
		Assertions.assertEquals(strings.expectedSign(),
			Integer.signum(comparator.compare(strings.object(), strings.compareWithObject())));
	}

}
