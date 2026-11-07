# comparator-extensions

**Java helpers for comparators: null-safe comparisons, a decorator that puts `null` first or
last, the sort order as a value, comparators for strings, dates and locales, comparator chains,
and comparators by a property or by a defined order.**

<div style="text-align: center">

[![Java CI with Gradle](https://github.com/astrapi69/comparator-extensions/actions/workflows/gradle.yml/badge.svg)](https://github.com/astrapi69/comparator-extensions/actions/workflows/gradle.yml)
[![Coverage Status](https://codecov.io/gh/astrapi69/comparator-extensions/branch/develop/graph/badge.svg)](https://codecov.io/gh/astrapi69/comparator-extensions)
[![Mutation Score](https://img.shields.io/endpoint?url=https%3A%2F%2Fraw.githubusercontent.com%2Fastrapi69%2Fcomparator-extensions%2Fgh-pages%2Fbadges%2Fmutation-score-develop.json)](https://pitest.org/)
[![Open Issues](https://img.shields.io/github/issues/astrapi69/comparator-extensions.svg?style=flat)](https://github.com/astrapi69/comparator-extensions/issues)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.astrapi69/comparator-extensions.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.astrapi69/comparator-extensions)
[![Javadoc](https://www.javadoc.io/badge/io.github.astrapi69/comparator-extensions.svg)](https://www.javadoc.io/doc/io.github.astrapi69/comparator-extensions)
[![MIT license](https://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/projects/jdk/25/)

</div>

comparator-extensions collects the comparator code that otherwise gets written again in every
project, and keeps it tested: every line and every branch is covered, and from 3.1 on PIT checks
that the tests notice when the code changes.

## Why comparator-extensions

It gathers what comes up wherever objects are compared and `null` may turn up: a `compare` that
accepts `null`, with `isGreater`, `isSmaller` and `isSame` on top; the null checks a `compare` or
an `equals` method starts with; a `SortOrder` that carries ascending or descending as a value, from
a setting or a request; comparators for strings, dates and locales that place `null` themselves; a
comparator for an order that a list gives; and `ChainableComparator`, the `ComparatorChain` of
Apache Commons Collections made by static `of` methods. Commons Collections is its one
dependency. silly-collection, silly-strings, file-worker and jobj-compare of this family build on
it.

If chaining, reversing and placing `null` is all you need, the JDK is enough: since Java 8
`Comparator` has `comparing`, `thenComparing`, `reversed`, `reverseOrder`, `nullsFirst` and
`nullsLast`. [Apache Commons Collections](https://commons.apache.org/proper/commons-collections/)
has `ComparatorUtils` and, in its `comparators` package, `FixedOrderComparator` for a defined order
and `NullComparator`, besides the `ComparatorChain` this library builds on. If you use
[Guava](https://github.com/google/guava) already, its `Ordering.explicit` gives a defined order that
refuses a value outside it, though Guava's own Javadoc calls `Ordering` obsolete for Java 8 and
later, in favour of `Comparator` and Guava's `Comparators`.

## Table of contents

- [Why comparator-extensions](#why-comparator-extensions)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Features](#features)
- [Quality](#quality)
- [Verifying the release signatures](#verifying-the-release-signatures)
- [Releasing](#releasing)
- [Contributing](#contributing)
- [How this project is made](#how-this-project-is-made)
- [License](#license)
- [Support this project](#support-this-project)
- [Similar projects](#similar-projects)
- [Credits](#credits)

## Requirements

**JDK 25 or newer**, at build and at runtime: from 3 on its jar holds Java 25 bytecode and does
not load on an older JVM. **2 is the last release for Java 17**, and 1.5 the last for Java 11; a
project below Java 25 stays on one of them. The one dependency is Apache Commons Collections
4.6.0, an API dependency: `ChainableComparator` extends its `ComparatorChain`, so the module
requires it `transitive`, and the pom and the Gradle module metadata put it on the compile class
path of a project that declares this library.

## Installation

Latest release on Maven Central: 3.1, for Java 25 (for Java 17: 2)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.astrapi69/comparator-extensions.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.astrapi69/comparator-extensions)

**Gradle** (version catalog, `gradle/libs.versions.toml`):

```toml
[versions]
comparator-extensions-version = "3.1"

[libraries]
comparator-extensions = { module = "io.github.astrapi69:comparator-extensions", version.ref = "comparator-extensions-version" }
```

```groovy
dependencies {
    implementation libs.comparator.extensions
}
```

**Gradle** (plain):

```groovy
dependencies {
    implementation "io.github.astrapi69:comparator-extensions:3.1"
}
```

**Maven**:

```xml
<dependency>
    <groupId>io.github.astrapi69</groupId>
    <artifactId>comparator-extensions</artifactId>
    <version>3.1</version>
</dependency>
```

The module name is `io.github.astrapisixtynine.comparator.extensions`.

3 publishes commons-collections4 for the runtime only (#36): a project on 3 that uses
`ChainableComparator`, or that is a module, declares `org.apache.commons:commons-collections4:4.6.0`
beside it. From 3.1 on the declaration above is enough.

## Quick start

The examples are checked: each block runs, with the values its comments name as assertions, in
[`ReadmeExamplesTest`](src/test/java/io/github/astrapi69/comparator/ReadmeExamplesTest.java).

**Null-safe comparisons.** `ComparatorExtensions.compare` compares two `Comparable`s and puts
`null` before any value; `isGreater`, `isSmaller` and `isSame` read its result.

```java
ComparatorExtensions.compare("a", "b");  // negative: "a" comes first
ComparatorExtensions.compare(null, "a"); // -1: null comes before any value
ComparatorExtensions.compare("a", null); // 1
ComparatorExtensions.isGreater(2, 1);    // true
ComparatorExtensions.isGreater(1, 1);    // false
ComparatorExtensions.isSmaller(null, 1); // true
ComparatorExtensions.isSame(1, 1);       // true
```

**In your own compare and equals methods.** `nullCheck` returns the result of a `compare` when
`null`, or the same object on both sides, decides it, and `null` when the two are left to compare;
`equalNullCheck` does the same for `equals`.

```java
ComparatorExtensions.nullCheck(null, "a");       // -1: null is the smaller
ComparatorExtensions.nullCheck(null, "a", true); // 1: null is the greater
ComparatorExtensions.nullCheck("a", "b");        // null: compare them
ComparatorExtensions.equalNullCheck("a", null);  // false
ComparatorExtensions.equalNullCheck("a", "b");   // null: compare them
```

**The sort order as a value.** A `SortOrderComparator` compares `Comparable`s in the `SortOrder`
it is given.

```java
List<Integer> numbers = new ArrayList<>(List.of(2, 3, 1));
numbers.sort(SortOrderComparator.of());                     // [1, 2, 3]: ascending by default
numbers.sort(SortOrderComparator.of(SortOrder.DESCENDING)); // [3, 2, 1]
```

**`null` first or last.** `NullCheckComparator` decorates a comparator, as the JDK's
`Comparator.nullsFirst` and `nullsLast` do, with the choice as a flag.

```java
Comparator<String> alphabetical = Comparator.naturalOrder();
List<String> names = new ArrayList<>(Arrays.asList("b", null, "a"));
names.sort(NullCheckComparator.of(alphabetical));       // [null, a, b]
names.sort(NullCheckComparator.of(alphabetical, true)); // [a, b, null]
```

**Comparators for strings, locales and dates.** They place `null` themselves, though not at the
same end: `StringComparator.of()` puts it last, `LocaleComparator.of()` and `DateComparator`
first; `StringComparator` and `LocaleComparator` take the choice too, `of(nullIsGreaterThan)`.

```java
List<String> words = new ArrayList<>(Arrays.asList("b", null, "a"));
words.sort(StringComparator.of());                     // [a, b, null]: null last
words.sort(StringComparator.of(SortOrder.DESCENDING)); // [b, a, null]

List<Locale> locales = new ArrayList<>(
    Arrays.asList(Locale.ENGLISH, null, Locale.GERMANY, Locale.GERMAN));
locales.sort(LocaleComparator.of()); // [null, de, de_DE, en]: by toString, null first

Date earlier = new Date(0);
Date later = new Date(1_000);
List<Date> dates = new ArrayList<>(Arrays.asList(later, null, earlier));
dates.sort(new DateComparator());           // [null, earlier, later]
dates.sort(new DescendingDateComparator()); // [later, earlier, null]
```

**Chains.** `ChainableComparator` is the `ComparatorChain` of Apache Commons Collections, made by
static `of` methods: each comparator decides where the ones before it found two elements equal,
and the `true` of `of(comparator, true)` reverses that comparator. As `ComparatorChain` says, a
chain is fixed once it has compared. The JDK's
`Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())` builds the first
chain below in one expression.

```java
ChainableComparator<String> byLength = ChainableComparator
    .of(Comparator.comparingInt(String::length));
byLength.addComparator(Comparator.naturalOrder()); // then alphabetically
List<String> words = new ArrayList<>(List.of("bb", "c", "ab", "a"));
words.sort(byLength); // [a, c, ab, bb]

ChainableComparator<String> longestFirst = ChainableComparator
    .of(Comparator.comparingInt(String::length), true);
longestFirst.addComparator(Comparator.naturalOrder());
words.sort(longestFirst); // [ab, bb, a, c]

// UnsupportedOperationException: a chain is fixed once it has compared
byLength.addComparator(Comparator.reverseOrder());
```

**By a property or a defined order.** `ComparatorFactory` makes a comparator from a `Function`
that reads a property, ascending, and from a list that gives the order.

```java
List<String> words = new ArrayList<>(List.of("ccc", "a", "bb"));
words.sort(ComparatorFactory.newBeanPropertyComparator(String::length)); // [a, bb, ccc]

List<String> priorities = List.of("high", "medium", "low");
List<String> tasks = new ArrayList<>(List.of("low", "high", "medium", "high"));
tasks.sort(ComparatorFactory.newDefinedOrderComparator(priorities)); // [high, high, medium, low]
```

## Features

| Package `io.github.astrapi69.comparator...` | What it holds |
|---|---|
| (root) | `ComparatorExtensions`: a null-safe `compare`, with or without a `SortOrder`, `isGreater`, `isSmaller` and `isSame`, and the null checks `nullCheck` and `equalNullCheck` for `compare` and `equals` methods. `NullCheckComparator`, a decorator that puts `null` first or last, with a builder; `SortOrderComparator` and the enum `SortOrder`; `ChainableComparator`, a `ComparatorChain` of Commons Collections made by static `of` methods; `CompareOrder`, the results of a `compare` as constants: `BEFORE` (-1), `EQUAL` (0) and `AFTER` (1) |
| `factory` | `ComparatorFactory`: comparators by a property, from a `Function`; by a defined order, from a `List`; for the values of a map, in their natural or in a random order; and in a random order of a list |
| `object` | `StringComparator`, with a `SortOrder`; `StringLengthComparator`, by length; `DateComparator` and `DescendingDateComparator`; `LocaleComparator`, by `toString`; `HashCodeComparator`, by `hashCode` |

The [Javadoc](https://www.javadoc.io/doc/io.github.astrapi69/comparator-extensions) has the
details.

## Quality

As of 3.1, measured by its release gate (JDK 25, the tree of `RELEASE-3.1`); each release takes
the numbers of its own gate:

| | |
|---|---|
| tests | 341 in 15 test classes, none skipped; the tests of one behaviour with many inputs are parameterized, a name for each case (#26) |
| line and branch coverage (JaCoCo) | 143 of 143 lines, 38 of 38 branches; the build fails below 98% of the lines or of the branches (`jacocoTestCoverageVerification` in `gradle/testing.gradle`) |
| mutation score (PIT) | 83 of 83 mutants killed, 100%, with PIT 1.30.1 and its default mutators; the run fails below 98% (`mutationThreshold` in `build.gradle`) |

The CI build runs the tests and the coverage check on every pull request, and the workflow
"Mutation testing" runs PIT on GitHub, on every pull request and every push to `develop` and
`main`, and publishes the mutation score of `develop` as the badge above. A pull request into
`develop` is merged only when both are green.

## Verifying the release signatures

The releases on Maven Central are signed with the OpenPGP key

```text
5B2F A6B1 1E29 8BC0 9287  F423 D8C4 0351 8C49 CA75
```

Since 3 they are signed by its signing subkey

```text
6D67 F844 2A6F CC96 BD7C  1B5E 9FCF 7C97 10E2 BD8D
```

valid until 2028-10-09; up to 2 they were signed by the primary key itself. The key, with the
subkey, is on keys.openpgp.org. Fetch it once, then verify the signature next to each file:

```sh
gpg --keyserver hkps://keys.openpgp.org --recv-keys 5B2FA6B11E298BC09287F423D8C403518C49CA75
gpg --verify comparator-extensions-3.jar.asc comparator-extensions-3.jar
```

gpg answers `Good signature` and prints the `Primary key fingerprint`, and for a release signed by
the subkey the `Subkey fingerprint`; both have to be the ones above. The warning that the key "is
not certified with a trusted signature" only says that you have not marked it as trusted in your
own keyring.

## Releasing

What a release consists of, in the order it is run:

1. **No open issue** but those of a later milestone: every issue of the release's milestone is
   closed, and a finding on the way is fixed in the release or, if it changes what callers rely
   on, assigned to the next major release.
2. **One pull request** with the CHANGELOG entry and `projectVersion` without `-SNAPSHOT`
   (`docs: changelog for X.Y, and the version that carries it`), the full build green on it.
3. **The release gate** on that pull request's tree: the tests and `jacocoTestCoverageVerification`
   with its floor in one run, `--rerun-tasks --no-build-cache`, on JDK 25, the toolchain of the
   build, with an empty local Maven repository (`-Dmaven.repo.local=<an empty directory>`, as
   `gradle/repositories.gradle` reads `mavenLocal()` before Central); javadoc prints no warning
   (the build runs it with `-Werror`), the class files are of Java 25 (major version 69), and the
   resolved dependencies hold no `-SNAPSHOT`. PIT does not run on a local machine: its numbers and
   its floor come from the run of "Mutation testing" on the pull request, and again from its run on
   the merge commit, whose tree is the pull request's tree. Its numbers go into the CHANGELOG entry
   and into the Quality section above, in the same pull request, before it is merged.
4. **Tag the merge commit** with `./gradlew tagRelease`, which names it `RELEASE-X.Y`, once its
   tree is the tree of the gate and the "Mutation testing" run on it has the numbers of the gate,
   and push the tag. `publish.yml` uploads to the Central Portal,
   where the maintainer releases it. A pushed tag is never moved or deleted; a failed run is fixed
   on the branch and run again.
5. **Verify on Central**: the jar, pom, `.module`, `-sources.jar` and `-javadoc.jar`, their sha1,
   and `gpg --verify` on the `.asc`.
6. **GitHub release** from the CHANGELOG entry.
7. **`main` follows the release** by a merge of the tag: on `main`,
   `git merge --no-ff "RELEASE-X.Y^{commit}" -m "Merge tag RELEASE-X.Y into main"`. Before `main`
   is pushed, `git diff RELEASE-X.Y main` has to be empty; if it is not, nothing is pushed. Never a
   force push: `main` is always the latest release, nothing else.
8. **Open the next cycle**: `projectVersion` to the next `-SNAPSHOT`, by a pull request, as
   `develop` takes no direct push.

## Contributing

Pull requests are welcome: fork
[astrapi69/comparator-extensions](https://github.com/astrapi69/comparator-extensions/fork) and
[open a pull request](https://github.com/astrapi69/comparator-extensions/pull/new/develop) against
`develop`.

Please add tests for your change. From 3.1 on, the coverage and mutation floors above hold for it
too (#21), and a `Makefile` runs the usual tasks (#28; `make help` lists them):

```shell
make build      # clean build: tests, coverage check, formatting check
make spotless   # format, and write the license headers
make pitest     # mutation testing on GitHub for the pushed branch, fails below the floor
```

PIT does not run on your machine: `make pitest` starts `mutation-testing.yml` on GitHub for the
current branch, which has to be pushed first, and prints how to follow the run; `make pitest-status`
shows the summary of the newest run of the branch: how many mutants were killed of how many, the
score, the floor and every mutant that survived.

The versions follow the
[Simplified Semantic Versioning](https://github.com/lightblueseas/mvn-parent-projects/wiki/Simplified-Semantic-Versioning)
guidelines, `<major>.<minor>.<patch>`. Questions, bug reports and feature requests belong on the
[issues page](https://github.com/astrapi69/comparator-extensions/issues).

## How this project is made

comparator-extensions was written by its maintainer, since 2022; release 3.1 is made mostly by AI
agents, under the maintainer's review. How AI is used in the projects of this profile:
[AI_USAGE.md](https://github.com/astrapi69/astrapi69/blob/main/AI_USAGE.md).

## License

MIT - see [LICENSE](LICENSE).

## Support this project

If comparator-extensions is useful to you, a star on
[GitHub](https://github.com/astrapi69/comparator-extensions) helps others find it. Sharing it or
reporting a bug helps just as much.

If you would like to contribute financially, the channels are on the repository's Sponsor button,
which reads [.github/FUNDING.yml](.github/FUNDING.yml): GitHub Sponsors, Liberapay, Ko-fi and PayPal.

## Similar projects

- [Guava](https://github.com/google/guava): Google's core libraries, with `Ordering` and
  `Comparators`
- [Apache Commons Collections](https://commons.apache.org/proper/commons-collections/):
  `ComparatorUtils` and the comparators of `org.apache.commons.collections4.comparators`, among
  them `ComparatorChain`, on which `ChainableComparator` builds
- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/): `ObjectUtils.compare`, a
  null-safe comparison of two `Comparable`s, and `CompareToBuilder`

## Credits

| | |
|---|---|
| [Maven Central](https://central.sonatype.com/artifact/io.github.astrapi69/comparator-extensions) | for hosting the artifacts of this open source project |
| [codecov.io](https://codecov.io) | for free code coverage reporting |
| [javadoc.io](https://www.javadoc.io) | for free javadoc hosting |
| [PIT](https://pitest.org/) | for mutation testing |

No animals were harmed in the making of this library.
