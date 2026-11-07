"""Writes the mutation score of a PIT report as a shields.io endpoint badge.

Usage: mutation-score-badge.py <mutations.xml> <badge.json> <build.gradle>

The score is the share of the mutants that the tests detected, rounded as PIT rounds the score it
checks against mutationThreshold, so the badge shows the number the build was held to. A score
below mutationThreshold of build.gradle is red whatever its number, as the run fails then; a score
at or above it, or any score of a build.gradle without a floor, takes the color of its number
(#68). The badge follows https://shields.io/badges/endpoint-badge; README.md reads it from the
gh-pages branch, to which .github/workflows/gradle.yml publishes it on a push to develop or master.
"""

import json
import math
import re
import sys
import xml.etree.ElementTree as ElementTree
from pathlib import Path

# the lowest score of each color, from the best down
COLORS = [(95, "brightgreen"), (90, "green"), (80, "yellowgreen"), (70, "yellow"), (60, "orange")]


def floor_of(build_file):
    """mutationThreshold of the given build.gradle, read as mutation-summary.py reads it, or None"""
    found = re.search(r"mutationThreshold\s*=\s*(\d+)", build_file.read_text(encoding="utf-8"))
    return int(found.group(1)) if found else None


def color_of(score, floor):
    if floor is not None and score < floor:
        return "red"
    for minimum, color in COLORS:
        if score >= minimum:
            return color
    return "red"


def badge_of(report, floor):
    mutations = ElementTree.parse(report).getroot().findall("mutation")
    if not mutations:
        raise SystemExit(f"{report} holds no mutation, so it gives no score")
    detected = sum(1 for mutation in mutations if mutation.get("detected") == "true")
    # as the score PIT checks mutationThreshold against, PercentageCalculator.getPercentage(long,
    # long, int) with the precision 0: half up, not half to even, and no more than 99 while a mutant
    # survives. Its other overload, Math.round of a float, is the one of the console and the HTML
    # report, and differs in some cases (15 of 24 detected: 62 there, 63 here and for the floor)
    score = math.floor(100 * detected / len(mutations) + 0.5)
    if detected < len(mutations):
        score = min(score, 99)
    print(f"{detected} of {len(mutations)} mutants detected: {score}%")
    return {
        "schemaVersion": 1,
        "label": "mutation score",
        "message": f"{score}%",
        "color": color_of(score, floor),
    }


def main(arguments):
    if len(arguments) != 3:
        raise SystemExit(__doc__)
    report, badge, build_file = (Path(argument) for argument in arguments)
    badge.parent.mkdir(parents=True, exist_ok=True)
    badge.write_text(json.dumps(badge_of(report, floor_of(build_file))) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main(sys.argv[1:])
