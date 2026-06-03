# Design Tokens — Atlan Performance

Canonical token source. Shared constants live in `shared/.../design/`; platform layers mirror them
(`androidApp/.../design/`, `iosApp/AtlanPerformance/Design/`). Keep all three in sync.

## Colors

| Token | Hex | Use |
|---|---|---|
| Abyss | `#0B2A3C` | Primary dark brand surface |
| AbyssDeep | `#061A26` | Deepest dark surface (Wet Mode background) |
| Tide | `#0E8A9A` | Science, calm progress, selected state, Why affordances |
| TideDeep | `#0A6F7D` | Tide pressed / emphasis |
| TideSoft | `#BFE0E5` | Selected fill (soft) |
| TidePale | `#DDEEF1` | Selected option background |
| Coral | `#FF6A3D` | Rare. Productive action / completion / high-signal accent |
| CoralBright | `#FF7E50` | Wet Mode Complete zone, onboarding warmth |
| CoralDeep | `#E55428` | Coral pressed |
| Foam | `#ECF7F8` | Primary light surface; Wet Mode text |
| FoamWarm | `#F4FAFB` | Onboarding light background |
| Paper | `#FBFCFC` | Primary light surface |

### Usage rules
- Abyss / AbyssDeep = primary dark surfaces. Foam / Paper = primary light surfaces.
- Tide = science / calm progress / "Why".
- Coral is rare: productive action, completion, high-signal accents only.
- **Do not** use Coral for errors, guilt, or missed-session states.
- **Do not** create red failure states for training disruption.

## Typography
Platform-native implementation; preserve this hierarchy:
- **Display** — calm editorial display type (doc ref: Fraunces; native fallback: New York / bundled display).
- **Body** — high-legibility sans (doc ref: Manrope; native fallback: SF Pro / Roboto).
- **Numeric** — tabular numeric where available.
- **Wet Mode** — extra-large, high-contrast numbers.

iOS: SwiftUI `Font` wrappers, stub custom font names, Dynamic Type-friendly text styles.
Android: Compose typography tokens, stub custom font resources, scalable text sizes.
Spanish strings wrap; never shrink to fit.

## Spacing
`xs 4 · sm 8 · md 12 · lg 16 · xl 24 · 2xl 32 · 3xl 48 · 4xl 64`

## Radii
`sm 4 · md 8 · lg 16 · sheet 24 · phone 32 · pill 999`

## Motion
Calm, low-velocity transitions. Respect Reduce Motion / reduced-motion settings — provide non-animated
alternatives for chart and sheet transitions.
