KtLint rules
============

Common KtLint rules for Technoir Lab projects. The convention plugins add
this ruleset automatically.

## Rules

| Rule ID                        | Enabled by default | Auto-correct |
|--------------------------------|--------------------|--------------|
| `technoirlab:forbidden-import` | Yes                | No           |

### Forbidden import

Reports an import when its fully qualified import path matches a configured
whole-name glob pattern. The rule is enabled by default, but its import list is
empty by default, so it reports nothing until configured.

Configure a comma-separated list in `.editorconfig`:

```editorconfig
[*.{kt,kts}]
ktlint_technoirlab_forbidden-import_imports = java.util.Date, kotlinx.coroutines.internal.*, *.experimental.*
```

Matching has the following behavior:

- Matching is case-sensitive and covers the entire import path.
- `*` matches zero or more characters, including periods. For example,
  `example.*` matches both `example.Type` and `example.deep.Type`.
- Every character other than `*` is treated literally; `?`, brackets, and other
  regular-expression syntax have no special meaning.
- Surrounding whitespace is trimmed from each comma-separated pattern.
- Empty and whitespace-only patterns are ignored.
- Aliased imports are matched by their underlying path, not by the alias.
- Multiple matching patterns still produce one finding for an import.

For example, the configuration above reports each of these imports:

```kotlin
import java.util.Date
import kotlinx.coroutines.internal.Symbol
import example.experimental.Feature
```

The diagnostic is:

```text
The import `<fully-qualified-import>` is forbidden.
```

Disable the rule with the standard KtLint rule property:

```editorconfig
[*.{kt,kts}]
ktlint_technoirlab_forbidden-import = disabled
```
