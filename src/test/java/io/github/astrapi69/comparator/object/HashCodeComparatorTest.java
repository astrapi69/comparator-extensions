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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
	 * code, 97 for a and 98 for b, Aa and BB share 2112, and polygenelubricants has
	 * {@link Integer#MIN_VALUE}, whose difference to the hash code of a does not fit into an int
	 *
	 * @return the cases
	 */
	static Stream<ComparedStrings> comparedStrings()
	{
		final String a = "a";
		// another instance, equal to a and with its hash code
		final String otherA = new String(a);
		// "polygenelubricants".hashCode() is Integer.MIN_VALUE, -2147483648: a compare that
		// subtracts the hash codes overflows with it and returns the wrong sign in both orders,
		// MIN_VALUE - 97 is 2147483551 and 97 - MIN_VALUE is -2147483551
		final String minimalHashCode = "polygenelubricants";
		return Stream.of(new ComparedStrings("both null", null, null, 0),
			new ComparedStrings("the first null", null, a, -1),
			new ComparedStrings("the second null", a, null, 1),
			new ComparedStrings("the same instance", a, a, 0),
			new ComparedStrings("equal, two instances", a, otherA, 0),
			new ComparedStrings("the smaller hash code first", a, "b", -1),
			new ComparedStrings("the greater hash code first", "b", a, 1),
			new ComparedStrings("not equal, the same hash code", "Aa", "BB", 0),
			new ComparedStrings("the hash code Integer.MIN_VALUE first", minimalHashCode, a, -1),
			new ComparedStrings("the hash code Integer.MIN_VALUE second", a, minimalHashCode, 1));
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

	/**
	 * Test for the note of {@link HashCodeComparator} that it imposes orderings inconsistent with
	 * equals: a {@link TreeSet} and a {@link TreeMap} ordered by it keep one of two strings with
	 * the same hash code only, Aa and BB, and take the one for the other (#54)
	 */
	@Test
	public void testSortedSetAndMapTakeOneObjectForAnotherWithTheSameHashCode()
	{
		final TreeSet<String> set = new TreeSet<>(comparator);
		Assertions.assertTrue(set.add("Aa"), "Aa is added");
		Assertions.assertFalse(set.add("BB"), "BB is not added");
		Assertions.assertEquals(List.of("Aa"), new ArrayList<>(set), "the set keeps Aa only");
		Assertions.assertTrue(set.contains("BB"), "contains takes Aa for BB");
		Assertions.assertTrue(set.remove("BB"), "remove takes Aa for BB");
		Assertions.assertTrue(set.isEmpty(), "Aa is removed");

		final TreeMap<String, Integer> map = new TreeMap<>(comparator);
		map.put("Aa", 1);
		map.put("BB", 2);
		Assertions.assertEquals(List.of("Aa"), new ArrayList<>(map.keySet()), "the key Aa only");
		Assertions.assertEquals(2, map.get("Aa"), "the value of BB under the key Aa");
	}

}
