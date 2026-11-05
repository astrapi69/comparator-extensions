# After the Makefile of astrapi69/silly-io (#28). No JDK is chosen here: the Gradle toolchain
# (build.gradle, java.toolchain, JDK 25 from gradle.properties projectSourceCompatibility) compiles,
# tests and writes the javadoc, so the JDK that runs Gradle only has to be one Gradle 9 runs on, 17 or
# later
GRADLE := ./gradlew

# the version a release tag would carry
VERSION := $(shell sed -n 's/^projectVersion=//p' gradle.properties)

.DEFAULT_GOAL := help

.PHONY: help build build-fast test clean spotless spotless-java spotless-misc pitest pitest-status \
	jacoco-coverage jacoco-report jar javadoc dependencies dependency-updates \
	version-catalog-format version-catalog-update publish-local tag-release

help:
	@echo "comparator-extensions - make targets"
	@echo ""
	@echo "  build                   clean build: tests + Jacoco + Spotless check (the everyday gate)"
	@echo "  build-fast              assemble without running tests"
	@echo "  test                    tests only"
	@echo "  clean                   remove build output"
	@echo ""
	@echo "  spotless                format and apply license headers (run before committing)"
	@echo "  spotless-java           Java sources only"
	@echo "  spotless-misc           everything else"
	@echo ""
	@echo "  pitest                  PIT on GitHub, not here: starts mutation-testing.yml for the current"
	@echo "                          branch, which has to be pushed, and prints how to follow the run"
	@echo "  pitest-status           the summary of the newest Mutation testing run of the current branch"
	@echo "  jacoco-coverage         verify the coverage thresholds"
	@echo "  jacoco-report           write the coverage report"
	@echo ""
	@echo "  jar                     the library jar"
	@echo "  javadoc                 the javadoc"
	@echo "  dependencies            print the dependency tree"
	@echo "  dependency-updates      report newer versions of dependencies and plugins"
	@echo "  version-catalog-format  sort and format gradle/libs.versions.toml"
	@echo "  version-catalog-update  write the newer versions into the catalog"
	@echo ""
	@echo "  publish-local           install into the local Maven repository, on purpose only: the"
	@echo "                          release gate needs an empty local Maven repository"
	@echo "  tag-release             create the RELEASE tag; pushing it publishes (CONFIRM=yes, no SNAPSHOT)"

build:
	$(GRADLE) clean build

build-fast:
	$(GRADLE) assemble -x test

test:
	$(GRADLE) test

clean:
	$(GRADLE) clean

spotless:
	$(GRADLE) spotlessApply

spotless-java:
	$(GRADLE) spotlessJavaApply

spotless-misc:
	$(GRADLE) spotlessMiscApply

# PIT runs on GitHub, not here: local PIT runs crashed the maintainer's machine on 2026-11-05
# (.github/workflows/mutation-testing.yml, #33; as astrapi69/silly-strings#59). This starts the workflow
# for the current branch, which has to be pushed as it is, since the run tests what GitHub holds, and
# prints how to follow the run; it does not wait for it. The summary of the run lists killed of total,
# the score, the floor and every surviving mutant, make pitest-status prints it.
pitest:
	@branch=$$(git rev-parse --abbrev-ref HEAD); \
	test "$$branch" != HEAD || { echo "pitest runs on GitHub for a branch, and HEAD is detached"; exit 1; }; \
	test "$$(git rev-parse HEAD)" = "$$(git rev-parse --verify --quiet "origin/$$branch")" || { \
		echo "pitest runs on GitHub: push $$branch first, PIT tests what origin/$$branch holds"; exit 1; }; \
	runs="gh run list --workflow mutation-testing.yml --branch $$branch --event workflow_dispatch --limit 1"; \
	before=$$($$runs --json databaseId --jq '.[0].databaseId // empty'); \
	gh workflow run mutation-testing.yml --ref "$$branch" || exit 1; \
	run=; \
	for attempt in 1 2 3 4 5 6 7 8 9 10; do \
		sleep 2; \
		run=$$($$runs --json databaseId --jq '.[0].databaseId // empty'); \
		test -n "$$run" && test "$$run" != "$$before" && break; \
		run=; \
	done; \
	test -n "$$run" || { \
		echo "mutation-testing.yml was started for $$branch; find its run with: $$runs"; exit 0; }; \
	echo "Mutation testing runs on GitHub for $$branch at $$(git rev-parse --short HEAD):"; \
	echo "  $$(gh run view "$$run" --json url --jq .url)"; \
	echo "follow it:     gh run watch $$run"; \
	echo "the result:    make pitest-status, the summary of the run"; \
	echo "PIT's report:  gh run download $$run -n pitest-report -D <directory>  (the artifact pitest-report)"

# the summary mutation-testing.yml wrote into the log of the newest run of the current branch, whatever
# started it: a push, a pull request or make pitest
pitest-status:
	@branch=$$(git rev-parse --abbrev-ref HEAD); \
	test "$$branch" != HEAD || { echo "pitest-status looks for the runs of a branch, and HEAD is detached"; exit 1; }; \
	run=$$(gh run list --workflow mutation-testing.yml --branch "$$branch" --limit 1 \
		--json databaseId --jq '.[0].databaseId // empty'); \
	test -n "$$run" || { echo "no Mutation testing run of $$branch yet; make pitest starts one"; exit 1; }; \
	gh run view "$$run" --json event,headSha,status,conclusion,createdAt,url --jq \
		'"Mutation testing of \(.headSha[0:7]) (\(.event), \(.createdAt)): \(.status) \(.conclusion)\n  \(.url)"'; \
	test "$$(gh run view "$$run" --json status --jq .status)" = completed || { \
		echo "the summary follows when the run is complete: gh run watch $$run"; exit 0; }; \
	summary=$$(gh run view "$$run" --log | awk -F '\t' ' \
		{ line = $$0; sub(/^[^\t]*\t[^\t]*\t[^ ]*Z ?/, "", line) } \
		step == "" && line ~ /^## Mutation testing/ { step = $$2 } \
		step != "" { if ($$2 != step) exit; print line }'); \
	test -n "$$summary" || { echo "the log of run $$run holds no summary: the run stopped before it"; exit 1; }; \
	printf '\n%s\n\n' "$$summary"; \
	echo "PIT's report:  gh run download $$run -n pitest-report -D <directory>  (the artifact pitest-report)"

jacoco-coverage:
	$(GRADLE) jacocoTestCoverageVerification

jacoco-report:
	$(GRADLE) jacocoTestReport

jar:
	$(GRADLE) jar

javadoc:
	$(GRADLE) javadoc

dependencies:
	$(GRADLE) dependencies

dependency-updates:
	$(GRADLE) dependencyUpdates

version-catalog-format:
	$(GRADLE) versionCatalogFormat

version-catalog-update:
	$(GRADLE) versionCatalogUpdate

# Called by name only; no other target runs it. gradle/repositories.gradle asks mavenLocal() before
# Maven Central, so what lies in the local Maven repository stands in for what Central serves, for this
# library and for its dependencies, and the release gate needs an empty one (README.md, "Releasing").
publish-local:
	$(GRADLE) publishToMavenLocal

# Tagging is publishing: .github/workflows/publish.yml triggers on 'RELEASE-*' tag pushes and
# uploads to the Central Portal (README.md, "Releasing"). Decide whether to upload BEFORE the tag, not
# after it. An upload leaves only from CI, so there is no target that uploads from here.
tag-release:
	@case "$(VERSION)" in *-SNAPSHOT) \
		echo "tag-release refuses $(VERSION): a release tag carries a release version (README.md, \"Releasing\")"; \
		exit 1;; esac
	@test "$(CONFIRM)" = "yes" || { \
		echo "tag-release creates a RELEASE tag, and pushing it triggers publish.yml, which uploads"; \
		echo "to the Central Portal. Re-run with CONFIRM=yes if that is intended."; \
		exit 1; }
	$(GRADLE) tagRelease
