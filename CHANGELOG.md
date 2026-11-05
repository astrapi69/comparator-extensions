## Change log
----------------------

Version 3.1
-------------

3.1 restores what the code's names and Javadoc promise: where the code broke them, it is fixed, and each fix that a
caller can notice carries a **Note**. Nothing else a caller relies on changes. Measured by the release gate (JDK 25,
the tree of `RELEASE-3.1`): 341 tests in 15 test classes, none skipped; 143 of 143 lines and 38 of 38 branches
covered (JaCoCo, floor 98%); 83 of 83 mutants killed, 100% (PIT 1.30.1, floor 98%, in the workflow "Mutation
testing" on GitHub).

FIXED:

- `ComparatorFactory.newBeanPropertyComparator(function, nullsFirst, ascending)` sorts ascending, in the natural
  order of the property, for `ascending` true and descending for false, as its Javadoc says; it sorted the other way
  round. `nullsFirst` places the elements whose property is null first or last in either order, as before. The
  overload `newBeanPropertyComparator(function)` sorts as before, ascending with the nulls last, and its Javadoc says
  so (#22).
  **Note:** a caller that passes `ascending` to the three-argument overload gets the opposite order with 3.1; one that
  passed `false` to get an ascending order passes `true` now. No caller in the family uses that overload.
- `ComparatorExtensions.compare(object, compareWithObject, SortOrder.DESCENDING)` compares with the arguments swapped,
  `compare(compareWithObject, object)`, as `Collections.reverseOrder()` does, where it negated the ascending result.
  `Comparable.compareTo` may return any negative int, and `-Integer.MIN_VALUE` is `Integer.MIN_VALUE`, so for such a
  `compareTo` descending kept the smaller element first. `SortOrderComparator`, `StringComparator`,
  `StringLengthComparator` and `DescendingDateComparator` go through it. `String.compareTo` never returns
  `Integer.MIN_VALUE`, so the two string comparators sorted right before. `null` still comes first ascending and last
  descending (#23).
  **Note:** descending calls the `compareTo` of the second argument and returns its value, where it returned the
  negated `compareTo` of the first. For a `compareTo` that keeps the contract of `Comparable`, the sign changes only
  where it returned `Integer.MIN_VALUE`. The value can differ for a `compareTo` whose two results are not each other's
  negation; `String`, `Integer` and `Date` give the same values as before.
- `ComparatorFactory.newRandomComparator(list)` and `newRandomComparator(list, secureRandom)` shuffle a copy of the
  given list and compare by the index in the copy; they shuffled the given list itself, which their Javadoc did not
  say, and threw an `UnsupportedOperationException` for an unmodifiable list such as `List.of(...)` (#24).
  **Note:** the caller's list comes back as it was given. A caller that read the shuffled order from it gets it from
  the comparator instead, by sorting a copy of the elements with it; a later change of the given list no longer
  changes the order of the comparator
- the Javadoc of `StringLengthComparator`, its constructors and its `of` factories says what the code does: the longer
  string comes first in either sort order, and the `SortOrder` decides only between strings of equal length, so
  `of(SortOrder.DESCENDING)` still puts the longer string first and reverses only the order of strings of equal
  length; the factories say that null goes where their flag `nullIsGreaterThan` puts it, in either sort order. No
  behaviour changes; a parameterized test pins the order (#25)
- the Javadoc of `SortOrderComparator` says what the code does, where it contradicted it: the class is concrete and
  can be used as it is or extended, as `StringComparator` and `StringLengthComparator` do, where it called the class
  abstract; `of(SortOrder)` takes the given sort order, where it said the default one. No behaviour changes (#41)
- commons-collections4 is an API dependency (`api` in `gradle/dependencies.gradle`), as the module has required it
  `transitive` since #15: `ChainableComparator` extends its `ComparatorChain`. 3 published it for the runtime only: the
  pom with `<scope>runtime</scope>`, the Gradle module metadata in `runtimeElements` alone. So a project that declared
  comparator-extensions alone could not compile `ChainableComparator.of(...)` ("cannot access ComparatorChain"), and a
  module that requires this one did not compile at all ("module not found: org.apache.commons.collections4"). The pom
  now gives it the compile scope and `apiElements` lists it. `ModulePathTest` compiles its consumers against this
  project's `apiElements`, which is what a consumer resolves, instead of its own runtime class path, and
  `PublishedPomTest` reads the scope from the generated pom (#36).
  **Note:** callers get commons-collections4 4.6.0 on their compile class path from the one dependency line. A project
  on 3 that declared it itself to work around this can drop that line; one that locks its `compileClasspath` writes its
  locks again. The class files and the runtime class path are the same.
- `DateComparator` takes the `SortOrder` its class Javadoc promised: `new DateComparator(SortOrder.DESCENDING)` sorts
  descending, the later date first and null last, as `DescendingDateComparator` does. The class had only the
  no-argument constructor, and `new DateComparator(SortOrder.DESCENDING)` did not compile. The class keeps
  `compare(Date, Date)` and the members of 3 (#35).
  **Note:** `DateComparator` gains the constructor `DateComparator(SortOrder)` its Javadoc promised; `new
  DateComparator()` stays ascending, null first. As with any second constructor, `DateComparator::new` passed to a
  method overloaded for a `Supplier` and a `Function<SortOrder, ...>` no longer compiles; no code in the family does
  that.
- the Javadoc of the `of` factories of `StringComparator`, `StringLengthComparator` and `LocaleComparator` says what
  they return, a `NullCheckComparator` that decorates a new instance of the class, after the JDK's
  `Comparator.nullsFirst`. It said "the new `StringComparator` object" and so on. No behaviour changes; a
  parameterized test pins the type and the order (#46).
  **Note:** the objects these factories return never were instances of the named class: `StringLengthComparator.of()
  instanceof StringLengthComparator` is false, and a cast to it throws a `ClassCastException`, in 3 as in 3.1. Use the
  result as a `Comparator`.

CHANGED:

- `HELP.md` is removed: it was the help of the project template this repository was made from, how
  to rename a project cloned from it through an `InitialTemplateTest` this repository does not
  have, and how to set the OSSRH user name and password, which no build script reads since
  releases go through the Central Portal (#12). The secrets a release needs are listed at the top
  of `publish.yml` (#16)
- the README as silly-collection's (#27): what the library is for and when the JDK, Apache Commons
  Collections or Guava are enough, the requirements, the installation, a quick start whose
  examples run in `ReadmeExamplesTest`, the packages, the quality, how to verify the release
  signatures, how a release is made and how to contribute. It names 2 as the last release for
  Java 17, and 3 as the first release signed by the signing subkey
- mutation testing and a coverage floor (#21): the tests kill all 83 mutants of PIT 1.30.1 with its
  default mutators, 100%, up from 76 of 83, 91.6%, and the workflow "Mutation testing" fails below 98% (#33;
  `mutationThreshold` in `build.gradle`; PIT rounds the score half up to a whole percent before it compares), so from 3
  survivors on. The 7 that survived are killed by parameterized tests of what no test checked:
  `isGreater` and `isSmaller` of two equal objects, `newBeanPropertyComparator` with a null property, the
  order of `newRandomComparator(list, random)` and `newRandomMapValuesComparator` under a seeded
  `SecureRandom`, and `HashCodeComparator` with null. The build fails below 98% of the lines or of the
  branches as well (`jacocoTestCoverageVerification` in `gradle/testing.gradle`); the tests cover all 135
  lines and 38 branches. CI publishes the mutation score of each push to `develop` or `main` as a
  shields.io endpoint badge on the `gh-pages` branch (`badges/mutation-score-develop.json`,
  `badges/mutation-score-main.json`), also when the score fails the floor
- `DateComparator`, `DescendingDateComparator`, `HashCodeComparator` and `LocaleComparator` declare their public
  no-argument constructor with a Javadoc comment, so javadoc prints no warning; the binary interface stays the same,
  as the default constructor was public too (#32)
- a javadoc warning fails the build: the `javadoc` task runs with `-Werror` and `failOnError = true`, and `check`
  depends on it, so `./gradlew build` and CI run javadoc and fail on a warning in every locale, where a text search for
  "warning" missed the German "Warnung" (#32). **Note:** a javadoc error fails the build now too; `failOnError` was
  `false`, because the `javadocJar` line `javadoc.properties.failOnError = true` wrote into a copy of the task's
  properties
- mutation testing runs on GitHub only, in a workflow of its own, "Mutation testing"
  (`.github/workflows/mutation-testing.yml`): on a push to `develop` or `main`, on a pull request against them and on
  demand (`gh workflow run mutation-testing.yml --ref <branch>`). It runs `./gradlew pitest`, which fails below
  `mutationThreshold`, writes a summary of the run (killed of total, the score as PIT rounds it, the floor, every
  mutant not detected, also on a red run) and keeps PIT's report as the artifact `pitest-report`. The mutation badge
  and its publication moved with it out of `gradle.yml`, which builds and reports the coverage. Its check `pitest` and
  the build's `build` are required for a merge into `develop` (#33)
- a `Makefile` with the family's targets, as silly-io's (#28): `make help` lists them and is the default goal; `build`,
  `build-fast`, `test`, `clean`, `spotless`, `spotless-java`, `spotless-misc`, `jacoco-coverage`, `jacoco-report`,
  `jar`, `javadoc`, `dependencies`, `dependency-updates`, `version-catalog-format`, `version-catalog-update` and
  `publish-local` run the Gradle task of their name, and `tag-release` refuses a `-SNAPSHOT` version and wants
  `CONFIRM=yes`, as pushing the tag publishes. `make pitest` runs no PIT on the machine: it starts
  `mutation-testing.yml` on GitHub for the current branch, which has to be pushed, and `make pitest-status` prints the
  summary of the newest run of the branch. `publish-local` runs only when called by name, as the release gate needs an
  empty local Maven repository. No JDK is named: the Gradle toolchain builds with JDK 25
- the tests of one behaviour with many inputs are parameterized tests with a name for each case (#26):
  `@ParameterizedTest` tables over records, one row a case, in place of the methods that ran their cases one after
  the other and of the old methods of one case whose cases newer tables held; 19 `@Test` and 24 `@ParameterizedTest`
  methods with 341 invocations in 15 test classes, from 36 and 12 with 172. `HashCodeComparatorTest` has again the
  case #21 lost, two hash codes of opposite signs whose difference does not fit into an int, on which a `compare` that
  subtracts them fails. JaCoCo stays at 143 of 143 lines and 38 of 38 branches, PIT at 83 of 83 mutants. Tests only,
  no change to the library

Version 3
-------------

BREAKING:

- Java 25 is the minimum, up from 17: the family builds, tests and releases on JDK 25
  (astrapi69/mystic-crypt#234); the class files are major version 69 (#12)

CHANGED:

- built with Gradle 9.8.2, its wrapper jar checked against the published sha256; a `.gitattributes`
  keeps the line endings of the wrapper scripts (#12)
- the plugins spotless 7.0.0.BETA2 -> 8.10.4, versions 0.51.0 -> 0.67.0 under its new id
  `io.github.ben-manes.versions`, and version-catalog-update 0.8.4 -> 1.1.2, which chooses versions
  through a selector of its own: a stable version moves only to a stable one, and nothing to a
  snapshot (#12)
- the check that every test class left a result comes from the plugin
  `io.github.astrapi69.test-results-complete` 1.0 instead of the copied
  `gradle/test-results-complete.gradle` (#12)
- releases go to Maven Central through the Central Portal, as in the rest of the family: the
  `com.gradleup.nmcp` plugin uploads, the maintainer releases each deployment in the Portal, and a
  `RELEASE-*` tag runs the new `publish.yml`, which signs with the signing subkey that the secret
  `GPG_KEY_ID` names. An upload leaves only from CI, a release only from its tag. The OSSRH staging
  endpoint this project published to is shut down; its settings are gone, among them the switch that
  held the checksums to md5 and sha1, and snapshots resolve from the Central Portal's snapshot
  repository (#12)
- the module `requires transitive org.apache.commons.collections4`: `ChainableComparator` extends its
  `ComparatorChain`, so a module that requires this one now reads collections4 as well and can name
  `ComparatorChain`; 2 required it without `transitive` (#15)
- the runtime dependency commons-collections4 4.5.0-M2, a milestone, is raised to the stable 4.6.0; the
  only type this library uses from it, `ComparatorChain`, the superclass of `ChainableComparator`, has
  the same public and protected API in both (#11)
- raised by `versionCatalogUpdate`, test or build only: for the tests guava 33.3.0-jre -> 33.8.0-jre,
  junit-jupiter 5.11.0 -> 6.1.3, junit-platform-launcher 1.11.0 -> 6.1.3 and test-object 8.2 -> 9.1,
  and the plugin spotless 8.10.4 -> 8.11.0. None of them reaches a consumer: the runtime class path is
  commons-collections4 4.6.0 alone (#17)
- the grgit and hierynomus license plugins are removed: `tagRelease` runs `git tag` itself
  (`gradle/tagging.gradle`), and Spotless enforces the MIT header from
  `src/main/resources/license-header.txt`; the license plugin reported every Java file as missing its
  header and passed (#12)

Version 2
-------------

ADDED:

- new libs.versions.toml file for new automatic catalog versions update

CHANGED:

- rename of module to new name 'io.github.astrapisixtynine.comparator.extensions'
- major version to 2.*
- update to jdk version 17
- update gradle to new version 8.10
- update of gradle plugin dependency com.github.ben-manes.versions.gradle.plugin to new version 0.51.0
- update of gradle-plugin dependency 'org.ajoberstar.grgit:grgit-gradle' to new version 5.2.2
- update of gradle plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 7.0.0.BETA2
- update of dependency commons-collections4 version to 4.5.0-M2
- update of test dependency guava version to 33.3.0-jre
- update of test dependency test-objects to new version 8.2
- update of test dependency throwable to new version 3
- update of test dependency vintage-time to new version 6
- update of test dependency junit.jupiter.api to new patch version 5.11.0
- update of test dependency junit-platform-launcher to new version 1.11.0

Version 1.5
-------------

ADDED:

- new file importorder.properties that defines the order of the imports while formatting with spotless

CHANGED:

- update gradle to new version 7.6
- update of com.diffplug.spotless:spotless-plugin-gradle to new minor version 6.12.0
- update of com.github.ben-manes.versions.gradle.plugin to new minor version 0.44.0
- update of test dependency test-object to new minor version 7.2
- increase code coverage

Version 1.4
-------------

ADDED:

- new factory method for bean comparator for create a comparator from a bean function with null check and order

Version 1.3
-------------

ADDED:

- new dependency commons-collections4 in version 4.4
- new method for check if the first given object is smaller than the second given object
- new method for check if the first given object is greater than the second given object
- new method for check if the first given object is same as the second given object
- new factory method for create a comparator from a bean function for get a comparator like BeanPropertyComparator in module jobj-compare

CHANGED:

- update gradle to new version 7.5.1
- update of com.diffplug.spotless:spotless-plugin-gradle to new minor version 6.11.0
- replaced package.html files with package-info.java files
- moved ChainableComparator from module silly-collection to this module
- update of test dependency test-object to new version 7.1
- update of test dependency junit-jupiter-api to new version 5.9.1

Version 1.2
-------------

ADDED:

- missing exports in module-info.java for package 'io.github.astrapi69.comparator.object'

CHANGED:

- removed unused dependency lombok

Version 1.1
-------------

ADDED:

- new test dependency 'io.github.astrapi69:vintage-time' in version 5.4

CHANGED:

- moved comparator objects from module silly-collections to this module
- increase of code coverage to 100%

Version 1
-------------

ADDED:

- new CHANGELOG.md file created

Notable links:
[keep a changelog](http://keepachangelog.com/en/1.0.0/) Don’t let your friends dump git logs into changelogs
