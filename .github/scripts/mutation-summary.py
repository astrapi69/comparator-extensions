#!/usr/bin/env python3
"""Writes a Markdown summary of a PIT run: killed of total, the score as PIT rounds it, the floor of
build.gradle, and every mutant that was not detected. Reads PIT's mutations.xml; prints to stdout.

usage: mutation-summary.py <mutations.xml> <build.gradle>
"""
import re
import sys
import xml.etree.ElementTree as ET


def main(report, build_file):
    try:
        root = ET.parse(report).getroot()
    except (OSError, ET.ParseError) as error:
        print("## Mutation testing\n\nNo PIT report: " + str(error))
        return
    mutations = root.findall("mutation")
    detected = [m for m in mutations if m.get("detected") == "true"]
    total = len(mutations)
    killed = len(detected)
    # PIT rounds half up to a whole percent before it compares with mutationThreshold
    score = (100 * killed + total // 2) // total if total else 100
    floor = None
    with open(build_file, encoding="utf-8") as gradle:
        found = re.search(r"mutationThreshold\s*=\s*(\d+)", gradle.read())
        if found:
            floor = int(found.group(1))
    verdict = "passes" if floor is None or score >= floor else "FAILS"
    lines = ["## Mutation testing", ""]
    lines.append(f"{killed} of {total} mutants detected, {score}% as PIT rounds it; "
                 f"the floor is {floor if floor is not None else 'not set'}%, so the run {verdict}.")
    undetected = [m for m in mutations if m.get("detected") != "true"]
    if undetected:
        lines += ["", "| class | method | line | mutator | status |", "|---|---|---|---|---|"]
        for m in undetected:
            lines.append("| {} | {} | {} | {} | {} |".format(
                m.findtext("mutatedClass", "").rsplit(".", 1)[-1],
                m.findtext("mutatedMethod", ""),
                m.findtext("lineNumber", ""),
                m.findtext("description", "").replace("|", "\\|"),
                m.get("status", "")))
    else:
        lines += ["", "No mutant survived."]
    print("\n".join(lines))


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    main(sys.argv[1], sys.argv[2])
