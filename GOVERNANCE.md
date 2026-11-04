# Project ownership and governance

## Confirmed decisions

Confirmed by the project owner on 2026-11-04:

- The project belongs to the owner and remains closed source.
- The owner is the decision point for product, architecture and release planning.
- Current application code is the design reference. Figma may be connected later.
- The backend is the sibling `../crew-api` repository; its outdated README is not evidence.
- Copilot is not used. No Copilot adapter is needed.
- Development uses task branches into `dev`, followed by release PRs into `main`.
  The owner merges; agents prepare, validate, commit, push and open task PRs by default.
  See [BRANCH_STRATEGY](docs/BRANCH_STRATEGY.md) for merge methods and setup gaps.

These decisions do not create a release authorization, grant repository permissions,
select a license or establish external contribution terms. The owner has approved a
temporary shared support/security address, documented in [SUPPORT](SUPPORT.md) and
[SECURITY](SECURITY.md); no public maintainer handle has been specified.

## Responsibilities and remaining operational decisions

| Responsibility | Owner / operational status |
| --- | --- |
| Product, technical and repository decisions | Project owner, confirmed |
| Release and store distribution | Project owner; procedure, monitoring and recovery still being planned |
| Support and security reports | Owner-approved temporary mailbox documented in SUPPORT/SECURITY; handling/monitoring and response expectations remain open |
| Community conduct handling | Need and policy adoption not decided; CODE_OF_CONDUCT remains a draft |

There is no established external-contributor governance process. No committee, alternate
reviewer or community reporting channel is assumed. The conduct draft describes how people
would treat one another and how complaints would be handled; it is not a code-quality rule.

## Technical decisions

Prefer established implementation patterns. Record consequential proposals with their
alternatives and evidence, and accepted significant technical changes under
[decisions](docs/decisions/README.md). Product target behavior can be accepted before
implementation; label the gap explicitly instead of claiming existing compliance.

Actual access controls, branch protections and required reviews live in repository settings
and remain unverified. AI assistants prepare changes/evidence within the authorized task;
a generated answer is not approval for publication or remote configuration changes.

Open operational questions: [OPEN_QUESTIONS](docs/ai/OPEN_QUESTIONS.md).
