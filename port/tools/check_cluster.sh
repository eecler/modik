#!/bin/bash
# PORT tooling: type-check one cluster in isolation.
#   port/tools/check_cluster.sh core/chemistry/storage [more dirs...]
# Uses -sourcepath, so only the files the cluster actually needs get pulled in - other sessions'
# half-ported clusters in src/main/java cannot make this red.
set -u
cd "$(dirname "$0")/../.."
JH=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
[ -f build/compile-classpath.txt ] || JAVA_HOME=$JH ./gradlew dumpCompileClasspath -q
OUT=$(mktemp -d)
FILES=()
for d in "$@"; do
    while IFS= read -r f; do FILES+=("$f"); done < <(find "src/main/java/petrolpark/mc/destroy/$d" -name '*.java')
done
$JH/bin/javac -nowarn -proc:none \
    -cp "$(cat build/compile-classpath.txt)" \
    -sourcepath src/main/java \
    -d "$OUT" "${FILES[@]}" 2>&1 | grep -v '^Note:'
rm -rf "$OUT"
