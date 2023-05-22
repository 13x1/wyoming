set -x

ITEMS=(
  main/java/cx/lexi/wyoming/ConfigModel.java
  main/resources
)

mv src src_disabled

for item in "${ITEMS[@]}"; do
  mkdir -p src/"$(dirname "$item")"
  cp -r src_disabled/"$item" src/"$item"
done

./gradlew build

rm -rf src
mv src_disabled src