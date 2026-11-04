# AI code review

Review the diff and enough surrounding code/call sites to understand behavior. Prioritize
observable regressions, security/data risks and platform failures. Avoid speculative style
findings already handled by tooling. Do not require your preferred architecture everywhere.

## Review dimensions

| Area | Questions |
| --- | --- |
| Correctness | What concrete input/state/action makes this fail? |
| Domain | Does it preserve confirmed match/event/board/QR invariants? |
| KMP | Do APIs/dependencies and exported types work on all affected targets? |
| Concurrency | Can cancellation, duplicate events or stale results corrupt state? |
| Data/API | Are null/missing, retries, migration and older clients handled? |
| UI | Are states, back/IME/insets and accessibility behavior preserved? |
| Performance | Is expensive/repeated work on the critical path supported by evidence? |
| Security | Are trust, ownership and sensitive data boundaries maintained? |
| Verification | Do checks cover the failure mode rather than mirror the implementation? |

## Finding format

State severity, exact file/line, trigger, resulting impact and reasoning/evidence. Suggest a
focused correction where useful. Distinguish reproduced from inferred findings. Severity is
an impact assessment, not a numeric confidence score. Keep unresolved questions separate.

Do not claim an exhaustive audit or runtime verification from static inspection. If no
actionable findings are identified, say so with the reviewed scope and material limitations.


## Existing project review emphasis

Look for unnecessary complexity, recomposition, memory/observer leaks, threading/lifecycle
errors and Android/iOS interoperability failures. Suggest focused simpler alternatives
with reasons, prioritizing readability, maintainability and measured performance. Use
[AI_TOOLS](../AI_TOOLS.md) to select relevant existing tools; a configured example is not
proof of a working integration.
