## Change log
----------------------

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
