## Change log
----------------------

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
