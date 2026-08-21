## RTK (Rust Token Killer)

### Golden Rule

- Always prefix commands with `rtk`.
- If RTK has a dedicated filter, it uses it. If not, it passes through unchanged.
- Even in command chains with `&&`, use `rtk` for each command segment.

```bash
# Wrong
git add . && git commit -m "msg" && git push

# Correct
rtk git add . && rtk git commit -m "msg" && rtk git push
```

### Build & Compile

```bash
rtk ./gradlew clean check
rtk ./gradlew :forga-core:compileJava
rtk ./gradlew :forga-core:checkstyleMain
```

### Test

```bash
rtk ./gradlew test
rtk ./gradlew :forga-core:test
rtk ./gradlew :forga-core:test --tests "com.luokuiai.forga.core.eval.AuthorizationEvaluatorTest"
rtk test ./gradlew clean check
```

### Git

```bash
rtk git status
rtk git log
rtk git diff
rtk git show
rtk git add
rtk git commit
rtk git push
rtk git pull
rtk git branch
rtk git fetch
```

### Files & Search

```bash
rtk ls <path>
rtk read <file>
rtk grep <pattern>
rtk find <pattern>
rtk diff <file>
```

### OpenSpec

```bash
rtk openspec status --change <change-id>
rtk openspec instructions apply --change <change-id> --json
rtk openspec validate <change-id> --strict --no-interactive
```

### Debug

```bash
rtk err <cmd>
rtk log <file>
rtk json <file>
rtk deps
rtk env
rtk summary <cmd>
rtk proxy <cmd>
```
