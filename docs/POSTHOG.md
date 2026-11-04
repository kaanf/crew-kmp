# PostHog product analytics plan

## Purpose and status

This document is the proposed first, intentionally small PostHog rollout for the
Crew Android/iOS app. It is a planning document; PostHog has not been added to the
app, no project key has been configured, and the events below are not yet emitted.
The aim is to learn where users get stuck and which parts of the social match/task
experience they use, without collecting sensitive content or adding broad tracking.

Sentry remains the tool for crashes and API errors. PostHog is for aggregate product
behavior and funnels; its event counts should not be used to diagnose an individual
failure.

## First questions to answer

1. Do users reach the main screen after opening/signing in?
2. Which task categories are chosen, and where do users abandon starting a match?
3. Do users complete a match after starting one?
4. Which main areas (events, quests, profile) are visited?

## Initial event set

Use a small, explicit allowlist of events. Prefer tracking meaningful user actions
over every tap or every recomposition.

| Event | When to emit | Safe properties to consider |
|---|---|---|
| `app_opened` | App enters foreground (session-level deduplication) | `app_version`, `platform` |
| `screen_viewed` | User navigates to a meaningful destination | `screen_name` from a fixed allowlist, `entry_point` from a fixed allowlist |
| `task_selected` | User confirms/selects a task in a match flow | `task_category`, `selection_position` when meaningful |
| `match_started` | Match successfully starts, not when the start button is merely tapped | `task_category`, `match_mode` if these are non-identifying enums |
| `match_completed` | Match reaches its successful terminal state | `task_category`, coarse `duration_bucket` |
| `match_abandoned` | User explicitly exits an active/incomplete match | `task_category`, coarse `phase` |
| `quest_opened` | User opens the quest area | `entry_point` |

Keep property values bounded and non-identifying. Do not send task text, event names,
free-form strings, participant names, email, phone, photos, QR contents, access/session
tokens, precise location, request/response bodies, or Sentry data. Avoid adding a
property simply because it is available in UI state.

## UX behavior and click analysis

The first version should use the explicit events above and a few meaningful screen
views. It should not enable blanket click autocapture or session replay. Those features
can record more interface or user-entered content than needed, and are not required to
answer the initial questions. If a later UX investigation needs a particular interaction,
add a named event for that action and review its payload first.

## Identity, consent, and environment

- Start with PostHog's anonymous distinct ID; do not create identity from email/name.
- If authenticated-user analysis is needed, decide and document a pseudonymous stable
  user identifier and account merge behavior before implementing `identify`.
- Keep local/debug builds disabled by default or clearly separated from production
  analytics, so developer testing does not pollute product reports.
- Before production collection, confirm the app's privacy disclosure, applicable consent
  requirements, opt-out behavior, retention, project region/hosting, and PostHog free-tier
  limits. These are owner/product decisions; this plan does not assert legal compliance.
- Keep the PostHog project key in local/build configuration and CI secrets as appropriate;
  a public client key is not a server secret, but it should not be committed as a
  personal/local configuration value. Never put a server personal API key in the app.

## Implementation direction

Crew is Kotlin Multiplatform with shared Compose UI and thin Android/iOS hosts. On
implementation, first confirm the currently maintained PostHog SDK and its Android,
iOS, and Kotlin Multiplatform support. Prefer a shared analytics interface and one
shared event vocabulary, with platform initialization/adapters only where SDK support
requires them. Keep analytics calls at explicit state/action success boundaries rather
than in composable rendering. Analytics failures must never interrupt app behavior.

The event mapping should be checked against the current task selection and match state
transitions in `feature/game/presentation`, plus navigation in `composeApp`, before code
is changed. The table is a proposed product schema, not a claim that every listed
transition already has a single authoritative callback.

## First useful PostHog views

After events have arrived, create only these initial reports:

1. **Activation funnel:** `app_opened` → `screen_viewed` for the main screen →
   `task_selected` → `match_started`.
2. **Match completion funnel:** `match_started` → `match_completed`, with
   `match_abandoned` as a separate breakdown.
3. **Task choice trend:** `task_selected` by `task_category` over time.
4. **Area usage:** `screen_viewed` by allowlisted `screen_name`.

Validate event volume and property values with a development/test account before
interpreting these reports. Small samples are directional and do not establish why a
user behaved a certain way; pair quantitative findings with usability observation.

## Rollout and acceptance checks

1. Confirm PostHog project/region, current free-tier terms, consent/opt-out decision, and
   app key configuration.
2. Implement initialization, privacy defaults, and a small shared tracking interface.
3. Add the initial event set at verified state transitions; do not add autocapture or
   replay in this rollout.
4. Verify Android and iOS events in a development environment, check that properties
   contain only the allowlisted values, and confirm analytics outages do not affect flows.
5. Build the four reports above and compare them with a small manual test flow before
   enabling production collection.

## Out of scope for the first rollout

- Session replay, heatmaps, blanket autocapture, and arbitrary tap logging
- Experiments/feature flags, surveys, push attribution, and marketing attribution
- Backend event tracking or user/session correlation with Sentry
- Sending personal data or gameplay content to analytics
- Any claim that installation, consent requirements, costs, or SDK compatibility have
  been verified; those need checking against current PostHog documentation during
  implementation
