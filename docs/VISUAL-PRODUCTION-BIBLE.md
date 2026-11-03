# TCon Visual Production Bible v1.0
## Series: Disetir Media Sosial

**Status:** Approved visual standard  
**Version:** 2.0  
**Applies to:** The 15-episode *Disetir Media Sosial* YouTube Shorts series and its reusable visual assets.

This document is the source of truth for visual design, image generation, storyboard direction, and Remotion animation for this series. Episode-specific story decisions may vary, but must remain inside this visual identity unless a later version is explicitly approved.

## 1. Creative identity

The series explains how social media platforms and recommendation algorithms can shape attention, emotion, habits, choices, and perception.

The visual language is **simple 3D educational motion graphics**:
- informative, modern, approachable, and visually engaging;
- serious enough for critical topics, without making every scene gloomy or frightening;
- expressive and metaphorical, but easy to understand at a glance;
- visually rich where it helps the story, restrained where clarity matters.

Avoid both extremes: do not make the series uniformly dark and ominous, and do not make it look like a childish explainer or generic corporate infographic.

## 2. Human character system

**Approved direction: symbolic human figures.**

- Use anonymous, universal figures: silhouettes, simplified faceless characters, or minimal facial features only when the emotion cannot otherwise be communicated.
- Do not establish a single recurring protagonist across all episodes.
- Choose figure presentation (masculine, feminine, mixed group, or neutral) according to the scene, without implying a specific real person.
- Communicate emotion through posture, gesture, scale, spacing, and staging rather than detailed facial acting.
- Keep body proportions and rendering style consistent within a scene.
- People should remain relatable and human, not robotic by default.

## 3. 3D motion-graphics treatment

**Approved direction: simple, stylized 3D built procedurally with Three.js and React Three Fiber.**

- Prefer geometric forms, rounded primitives, simple symbolic objects, abstract spaces, and clean layered environments.
- Use readable materials, controlled roughness/metalness, soft shadows, gradients, and purposeful lighting.
- Keep scenes lightweight: a few focal objects, restrained particles, simple geometry, and limited material complexity.
- Use camera movement (push, pull, orbit, tracking) only when it clarifies the idea.
- Use depth, scale, and spatial relationships to explain abstract mechanisms.
- Avoid photorealism, detailed character modeling, anime styling, excessive polygons, noisy textures, and generic corporate infographic layouts.
- 2D typography, captions, labels, and interface-like overlays should be composed in Remotion above the 3D scene, not baked into textures.

The result should feel like a polished educational motion-graphics sequence: clearly three-dimensional, but intentionally simple and fast to render.

## 4. Color system

Core palette:
- Deep navy: `#101B2A` — primary dark foundation.
- Slate blue: `#263B49` — secondary depth and surfaces.
- Cyan: `#20B8CC` — technology, information, networks, and attention cues.
- Coral: `#F07859` — emotion, friction, urgency, and warning.
- Warm ivory: `#E9E5DC` — readable highlights and contrast.

Usage:
- Navy and slate establish the base, but lighter scenes are allowed.
- Cyan and coral are accents, not mandatory in every frame.
- Ivory is preferred for important readable text.
- Scene-specific colors may be introduced when the story requires them, while preserving a coherent overall grade.
- Maintain sufficient contrast for mobile viewing.

## 5. Composition and image-generation requirements

Default master frame: **1080 × 1920 px, portrait 9:16**.

Every generated scene should:
1. Communicate one dominant visual idea.
2. Have a clear focal subject and hierarchy.
3. Use foreground, midground, and background when useful.
4. Leave clean negative space when captions or titles will be overlaid.
5. Keep essential subjects away from the extreme edges and platform UI areas.
6. Remain legible on a phone screen at small display size.
7. Avoid unnecessary clutter and unrelated decorative symbols.

Do not generate baked-in captions, random lettering, fake interface copy, watermarks, or logos unless the storyboard explicitly requires legible text. Prefer adding typography in Remotion so it remains editable and consistent.

## 6. Visual metaphor language

Use visual metaphors when they make abstract mechanisms understandable. Possible motifs include:
- attention: spotlight, beam, magnifying frame, narrowing field of view;
- recommendation systems: branching paths, streams, ranking lanes, connected nodes;
- repetition and habit: loops, cycles, recurring paths, repeated environmental cues;
- influence and control: strings, gravity, invisible forces, directional currents;
- filter bubbles: walls, domes, tunnels, isolated rooms, selective windows;
- engagement and reward: pulses, counters, sparks, rising signals;
- overload: layered notifications, crowded signals, fragmented focus.

These are a vocabulary, not mandatory clichés. Do not repeat the same smartphone-with-floating-icons composition across episodes. Metaphors must be relevant to the actual narration and should not falsely imply that an algorithm literally performs a depicted action.

## 7. Asset layering and library conventions

TCon's reusable asset library uses these categories:
- `TOKOH`: symbolic people, silhouettes, groups.
- `BACKGROUND`: rooms, streets, abstract spaces, digital environments.
- `OBJEK`: phones, screens, furniture, clocks, everyday props.
- `SIMBOL`: conceptual metaphors, networks, loops, control, attention.
- `ELEMEN`: light, particles, shadows, motion accents, effects.
- `PERLU_REVIEW`: assets whose category, quality, rights, or suitability is uncertain.

Asset rules:
- Preserve every original upload or generated source; never overwrite an approved asset.
- Give each new asset a stable unique ID.
- Derivatives (cutouts, recolors, crops, alternate poses) receive their own IDs and reference their source asset.
- Use transparent PNG or another alpha-capable format for isolated elements; use WebP or PNG for full-frame backgrounds as appropriate.
- Mark inseparable multi-part artwork as a composite asset.
- Store source, license/provenance, prompt or creation notes, tags, dimensions, transparency, and review status in metadata.
- Auto-file only when classification confidence is high. Otherwise route to `PERLU_REVIEW`.
- Never treat an asset as approved merely because it was generated or uploaded.

## 8. Shorts format and editorial pacing

Default delivery:
- Platform: YouTube Shorts.
- Aspect ratio: 9:16.
- Resolution: 1080 × 1920.
- Frame rate: 30 fps.
- Target duration: approximately 45–55 seconds (nominal target: 50 seconds).
- Language: Bahasa Indonesia.
- Narration: one narrator by default.
- Captions: added in Remotion, synchronized to narration, concise, high contrast, and inside safe areas.

Nominal 50-second pacing guide (flexible to the script):
| Time | Story function | Visual direction |
|---|---|---|
| 00–04 s | Hook | Immediate visual question, striking scale contrast, or unexpected metaphor |
| 04–12 s | Context | Establish a familiar situation |
| 12–25 s | Explanation | Show the mechanism with clear visual examples |
| 25–38 s | Conflict / evidence | Introduce consequence, contrast, or important fact |
| 38–46 s | Insight | Resolve the idea or reveal what viewers may have missed |
| 46–50 s | Closing | Memorable reflection or bridge to the next episode |

Do not stretch narration or scenes merely to fill the target duration. The edit should follow the strongest natural rhythm of the script.

## 9. Motion and camera language

The video must feel animated, not like a sequence of static images. Use motion with editorial purpose:
- parallax between separated layers to create depth;
- slow push-in or pull-back for emphasis and reflection;
- pan or tracking movement to redirect attention;
- independent object animation for notifications, interface fragments, symbols, and props;
- restrained motion typography for key phrases;
- transitions motivated by shape, direction, color, or story meaning.

Motion principles:
- Every shot should have a clear focal point and one primary movement.
- Avoid animating every element at once.
- Use easing and natural acceleration/deceleration; avoid constant robotic movement.
- Reserve fast movement for hooks, disruptions, or meaningful changes.
- Use quieter motion for explanation and reflection.
- Avoid excessive zooms, spins, glitch effects, and decorative transitions.

## 10. Typography, captions, and sound

- Keep on-screen text short; narration carries the detailed explanation.
- Use a consistent sans-serif type family and a small hierarchy of sizes and weights.
- Maintain strong contrast and safe margins for Shorts UI.
- Captions must be readable on mobile and must not cover the main subject.
- Use sound effects to reinforce actions and transitions, not to decorate every movement.
- Background music should support the emotional arc while remaining clearly below narration.
- Use only audio with documented usage rights or a suitable license.

## 11. Prompt template

Use this as a reusable direction for procedural 3D scenes. Replace the scene description and shot-specific direction for every storyboard frame. Use image-generation prompts only when a scene genuinely requires an external raster asset; 3D geometry is the default visual source.

```text
Create a portrait 9:16 (1080x1920) simple stylized 3D educational motion-graphics scene
for an Indonesian YouTube Shorts series about social media and recommendation algorithms.

VISUAL IDENTITY:
Balanced editorial mood: modern, informative, approachable, thoughtful.
Use simple geometric 3D forms, symbolic objects, clear spatial depth,
controlled lighting, clean materials, and restrained camera movement.
Core palette: deep navy #101B2A, slate #263B49, cyan #20B8CC,
coral #F07859, warm ivory #E9E5DC. Use accents selectively.

SCENE:
[Describe one specific visual idea grounded in the episode narration.]

COMPOSITION:
[Define focal subject, camera angle, foreground, midground, background,
negative space, and mobile-safe placement.]

STORY MEANING:
[Explain what the viewer should understand from this image.]

ANIMATION READINESS:
Build the scene from independently controllable 3D objects where practical.
Keep the focal silhouette readable and avoid unnecessary overlaps.
Compose captions and typography in Remotion, not inside 3D textures.

RESTRICTIONS:
No photorealism, no anime, no random text,
no watermark, no unrelated logos, no generic decorative clutter.
```

## 12. Quality-control checklist

Before an image is accepted:
- [ ] Does it communicate the intended narration beat?
- [ ] Does it follow the simple 3D educational motion-graphics direction?
- [ ] Does it feel balanced rather than uniformly dark or childish?
- [ ] Is the focal point immediately understandable?
- [ ] Is the composition readable in portrait mobile format?
- [ ] Are there no unwanted words, watermarks, or logos?
- [ ] Can its important elements be animated or composited?
- [ ] Are source and usage rights documented?
- [ ] Is it free of accidental similarity to a real person's identity?
- [ ] Has it been assigned a unique asset ID and review status?

Before a video is approved:
- [ ] Does motion support the story rather than distract?
- [ ] Are captions synchronized and readable?
- [ ] Is narration clear above music and effects?
- [ ] Is the final render 9:16, 1080 × 1920, and 30 fps?
- [ ] Has the full video been reviewed before publication?

## 13. Governance and versioning

- This v2.0 standard applies to the *Disetir Media Sosial* series.
- Episode storyboards may adapt composition, lighting, and accent colors to the subject.
- A change to the core 3D direction, palette, or video format requires a new approved version.
- Generated assets are proposals until reviewed; uncertain assets stay in `PERLU_REVIEW`.
- No image or video is published to YouTube automatically. Publishing requires explicit user approval for that specific final render.


## 14. Three.js and Remotion implementation rules (v2.0)

- Use Three.js through `@remotion/three` and React Three Fiber for procedural 3D scenes.
- Wrap 3D scenes in `ThreeCanvas` with explicit `width` and `height`.
- Drive every animation from Remotion `useCurrentFrame()` and `interpolate()`; do not use React Three Fiber `useFrame()`, timers, random animation, or autonomous render loops.
- Keep narration, subtitles, title typography, and timeline orchestration in Remotion.
- Prefer geometry and materials over generated images for reusable abstract visuals.
- For GitHub Actions on Remotion 4, use a software-compatible Chromium WebGL backend such as `--gl=swiftshader`; expect slower renders and inspect the MP4 artifact.
- Treat the first prototype as a feasibility test. Do not replace the existing production composition until the prototype renders correctly and is reviewed.
