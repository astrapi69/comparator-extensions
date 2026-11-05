## Change log
----------------------

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
