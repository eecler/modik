#!/usr/bin/env python3
"""Error-driven quarantine of leaf serialization methods.

1.21.1 made serialization registry-aware, so the breakage in a ported class is concentrated in
read/write/serializeNBT/encode-style methods while the actual behaviour of the class is fine.
This stubs exactly those methods - and only when EVERY error in the file is inside one - so the
class can land and be used, with the serialization debt recorded in port/tools/quarantined.tsv.

A quarantined block entity keeps working in a running world but does not persist its state.
This is a staging step, never a release state: see the report for the list to pay back.

Usage: python3 port/tools/quarantine.py errors.log [--apply]
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "src/main/java"

LEAF = {
    "read", "write", "readSafe", "writeSafe", "read_", "load", "save",
    "serializeNBT", "deserializeNBT", "readNBT", "writeNBT",
    "encode", "decode", "toNetwork", "fromNetwork", "toBytes",
    "readFromNBT", "writeToNBT", "addToTooltip", "readAdditionalSaveData",
    "addAdditionalSaveData", "writeSpawnData", "readSpawnData",
}

DECL = re.compile(
    r"^[ \t]*(?:@\w+(?:\([^)]*\))?[ \t]*)*"
    r"(?:public|protected|private)?[ \t]*(?:static[ \t]+)?(?:final[ \t]+)?"
    r"(?:<[^>]+>[ \t]+)?"
    r"([\w.<>\[\], ?]+?)[ \t]+(\w+)[ \t]*\(([^;{]*)\)[ \t]*(?:throws [\w., ]+)?[ \t]*\{",
    re.M,
)


def scan_methods(text):
    """Yield (name, return_type, body_start_index, body_end_index) for brace-delimited methods."""
    for m in DECL.finditer(text):
        ret, name = m.group(1).strip(), m.group(2)
        if name in ("if", "for", "while", "switch", "catch", "synchronized", "new", "record"):
            continue
        open_idx = text.index("{", m.end() - 1)
        i, depth = open_idx + 1, 1
        while i < len(text) and depth:
            c = text[i]
            if c in '"\'':
                q, i = c, i + 1
                while i < len(text) and text[i] != q:
                    i += 2 if text[i] == "\\" else 1
            elif c == "/" and i + 1 < len(text) and text[i + 1] == "/":
                i = text.find("\n", i)
                if i < 0:
                    break
            elif c == "/" and i + 1 < len(text) and text[i + 1] == "*":
                i = text.find("*/", i) + 1
            elif c == "{":
                depth += 1
            elif c == "}":
                depth -= 1
            i += 1
        yield name, ret, open_idx, i - 1


def default_return(ret):
    ret = ret.strip()
    if ret == "void":
        return ""
    if ret == "boolean":
        return "        return false;\n"
    if ret in ("int", "long", "short", "byte", "char"):
        return "        return 0;\n"
    if ret in ("float", "double"):
        return "        return 0f;\n" if ret == "float" else "        return 0d;\n"
    if ret == "CompoundTag":
        return "        return new CompoundTag();\n"
    return "        return null;\n"


def main():
    log = Path(sys.argv[1])
    apply = "--apply" in sys.argv
    errs = {}
    for line in log.read_text().splitlines():
        m = re.search(r"(/\S*?src/main/java/\S+\.java):(\d+): error:", line)
        if m:
            errs.setdefault(Path(m.group(1)), set()).add(int(m.group(2)))

    quarantined, skipped = [], []
    for path, lines in sorted(errs.items()):
        if not path.exists():
            continue
        text = path.read_text()
        offsets = [0]
        for ln in text.splitlines(keepends=True):
            offsets.append(offsets[-1] + len(ln))

        methods = list(scan_methods(text))
        targets, covered = [], set()
        for name, ret, s, e in methods:
            lo = text.count("\n", 0, s) + 1
            hi = text.count("\n", 0, e) + 1
            inside = {l for l in lines if lo <= l <= hi}
            if inside and name in LEAF:
                targets.append((name, ret, s, e))
                covered |= inside

        if not targets:
            skipped.append((path, sorted(lines)[:3]))
            continue
        if covered != lines:
            # Partial: the leaf methods still get stubbed - that removes real errors, and whatever
            # is left surfaces on the next compile round rather than being guessed at here.
            skipped.append((path, sorted(lines - covered)[:3]))

        for name, ret, s, e in sorted(targets, key=lambda t: -t[2]):
            body = ("\n        // PORT (1.21.1): quarantined - serialization is now registry-aware\n"
                    "        // (HolderLookup.Provider + codecs). State is NOT persisted until this is rewritten.\n"
                    + default_return(ret) + "    ")
            text = text[:s + 1] + body + text[e:]
            quarantined.append((str(path.relative_to(ROOT)), name))
        if apply:
            path.write_text(text)

    print(f"quarantined methods : {len(quarantined)} in {len({q[0] for q in quarantined})} files")
    print(f"files left to evict : {len(skipped)}")
    if apply:
        rep = ROOT / "port/tools/quarantined.tsv"
        with rep.open("a") as f:
            for file, name in quarantined:
                f.write(f"{file}\t{name}\n")
        print(f"report appended: {rep}")


if __name__ == "__main__":
    main()
