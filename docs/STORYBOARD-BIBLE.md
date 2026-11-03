# TCon Storyboard Bible v1.0
## Series: Disetir Media Sosial

**Status:** Proposed production standard  
**Version:** 1.0  
**Depends on:** `docs/VISUAL-PRODUCTION-BIBLE.md`  
**Applies to:** All 15 episodes of the *Disetir Media Sosial* YouTube Shorts series.

This document defines how an approved script becomes a timed, evidence-aware, animation-ready visual plan. The Visual Production Bible defines how the series looks; this Storyboard Bible defines what each shot communicates and how it moves.

## 1. Core storyboard principles

1. **Story first.** Every shot must serve a specific narration beat, not merely look attractive.
2. **One dominant idea per shot.** Avoid combining multiple unrelated metaphors in one frame.
3. **Show the mechanism.** When explaining an algorithmic or behavioral concept, depict the relationship or process—not just a generic phone.
4. **Make abstract ideas concrete.** Use a visual metaphor that viewers can understand quickly.
5. **Keep claims proportional to evidence.** Visuals must not imply secret intent, certainty, or direct control unless the narration and evidence support it.
6. **Design for motion.** Plan layers, movement, and transitions before generating assets.
7. **Optimize for mobile.** The main idea must read on a phone screen in a few seconds.
8. **Respect the series identity.** Apply the balanced editorial 2D, symbolic human, and hybrid-detail rules in the Visual Production Bible.

## 2. Production sequence and approval gates

The canonical episode workflow is:

1. Research the topic and collect reliable sources.
2. Write the narration and verify factual claims.
3. Approve the script.
4. Break the script into narration beats.
5. Create the storyboard and shot timing.
6. Review the storyboard for clarity, accuracy, repetition, and production feasibility.
7. Generate or select assets using the Visual Production Bible.
8. Review generated assets; uncertain items go to `PERLU_REVIEW`.
9. Assemble and animate scenes in Remotion.
10. Render a preview and run technical/editorial QA.
11. Obtain explicit user approval for that specific final render.
12. Only then may the video be uploaded to YouTube.

A script approval is not storyboard approval. Storyboard approval is not image approval. None of these approvals authorizes publishing. Never bypass the final per-render publishing approval.

## 3. Episode and shot structure

- Target runtime: 45–55 seconds; nominal planning target: 50 seconds.
- Typical structure: 5–8 shots, depending on narration density.
- A shot may contain multiple animation beats, but should retain one dominant visual idea.
- A new shot is warranted when the narration changes its central idea, location, scale, evidence, or emotional beat.
- Do not force exactly five scenes. The five-scene structure in the current `data/episode-01.json` is a useful legacy example, not a hard limit.
- Avoid shots so short that the viewer cannot understand them, and avoid holding a static composition without a purposeful movement or reveal.

## 4. Narrative arc for a 50-second Short

Use this as a flexible pacing guide, not a rigid formula:

| Approx. time | Narrative function | Viewer question | Storyboard job |
|---|---|---|---|
| 0–4 s | Hook | “Why should I care?” | Create a clear visual interruption, contradiction, or question |
| 4–11 s | Familiar situation | “Have I experienced this?” | Show an everyday, relatable behavior |
| 11–23 s | Mechanism | “What is happening?” | Explain the system or behavioral process |
| 23–36 s | Consequence / evidence | “What does it change?” | Show a grounded effect, trade-off, or evidence |
| 36–46 s | Insight | “What should I understand?” | Reframe the issue without exaggeration |
| 46–50 s | Closing | “What do I do with this?” | End with a memorable reflection or question |

Adjust timing to the script. Do not rush important explanations to preserve a template. Do not pad a weak script with extra visuals.

## 5. From narration to visual beats

For every narration paragraph or sentence:
- Identify the single key idea.
- Identify the concrete subject, action, and consequence.
- Decide whether it needs a literal scene, a conceptual metaphor, a diagram-like visual, or a combination.
- Select the most understandable visual representation.
- Determine what changes during the shot (reveal, movement, comparison, transformation, or reaction).
- Decide what the viewer should notice first and what they should understand by the end.

Do not create a new shot for every sentence automatically. Several connected sentences may share one shot if they explain the same process. Conversely, a single long sentence may need multiple shots if it contains distinct steps.

## 6. Shot design contract

Every storyboard shot must specify:

| Field | Requirement |
|---|---|
| Shot ID | Stable episode-local ID, e.g. `E01-S01` |
| Time | Start and end in seconds; no gaps or accidental overlaps |
| Narration | Exact narration segment synchronized to the shot |
| Story beat | The idea this shot communicates |
| Viewer takeaway | What the viewer should understand |
| Visual description | Concrete description of subject, action, and environment |
| Metaphor / evidence | Why this visual is appropriate; cite source IDs for factual visuals |
| Composition | Focal point, camera angle, foreground, midground, background |
| Asset plan | Required reusable and episode-specific assets |
| Layer plan | Which elements must be separate for animation |
| Motion | Primary movement, secondary movement, camera movement, easing |
| Transition | How this shot enters and leaves |
| On-screen text | Short optional headline/caption; do not duplicate full narration |
| Sound | Optional sound cue and its story purpose |
| Prompt brief | Image-generation brief aligned with Visual Production Bible |
| QA notes | Accuracy, legibility, risks, and open questions |

All timestamps use seconds from the start of the video. Shot timing must cover the full runtime without unintended gaps. Narration must fit naturally; do not accelerate speech unnaturally to satisfy timings.

## 7. Visual storytelling patterns

Choose a pattern based on the idea. Do not use one pattern for every episode.

- **Everyday behavior:** a recognizable action, followed by a reveal of what is happening around it.
- **Cause and effect:** show a sequence of connected steps, with clear directional movement.
- **Before and after:** compare two states using consistent framing.
- **Scale contrast:** a small human figure against a large system or environment.
- **Feedback loop:** show an action, system response, and the next action as a readable cycle.
- **Choice and trade-off:** show competing paths or outcomes without falsely implying only one option exists.
- **Information flow:** show how signals, content, or recommendations are selected and presented.
- **Reflection:** reduce visual complexity and give the viewer space to process the insight.

Use visual metaphors from the Visual Production Bible only when they clarify the narration. Avoid defaulting to strings controlling people, puppet imagery, or sinister machines unless that is genuinely the most accurate and useful metaphor.

## 8. Shot composition and mobile readability

- Establish one primary focal point per shot.
- Use a clear foreground / midground / background hierarchy where it adds depth.
- Keep key faces, hands, phones, and symbols away from extreme edges and platform UI overlays.
- Keep important visual information large enough to read on a mobile screen.
- Leave negative space for captions and brief labels.
- Do not put critical text over visually busy areas.
- Avoid tiny interface details, dense diagrams, and many floating icons.
- Use recognizable abstract interface shapes without copying a real platform UI unnecessarily.
- Avoid embedded generated text; add final typography in Remotion.

## 9. Asset planning and layer separation

For each shot, classify asset needs using the existing library:
- `TOKOH`: symbolic human figures and groups.
- `BACKGROUND`: environments and atmospheric spaces.
- `OBJEK`: phones, clocks, furniture, screens, and props.
- `SIMBOL`: networks, loops, paths, attention, filters, and other concepts.
- `ELEMEN`: light, particles, shadows, and motion accents.

Specify whether each asset is:
- reusable from the central library;
- newly generated for the episode;
- a derivative of an existing asset;
- a composite that cannot sensibly be separated;
- missing and therefore requires an Asset Request.

Layering guidance:
- Separate the subject from the background when parallax or independent movement is planned.
- Separate moving objects from the character when their motion differs.
- Keep shadows and glow separate when they need independent animation.
- Do not over-split a simple image into unnecessary layers.
- Preserve source files and give derivatives new IDs.
- Do not silently replace or delete approved assets.

## 10. Motion direction for Remotion

Every shot should normally have one primary motion and may have one restrained secondary motion.

Supported motion vocabulary:
- camera: push-in, pull-back, pan, tracking, tilt, static hold;
- subject: enter, exit, turn, gesture, shift, scale, reveal;
- object: float, slide, pulse, rotate, stack, scatter, connect, fade;
- environment: parallax drift, light shift, particle movement, subtle depth movement;
- typography: reveal, emphasis, mask, short kinetic movement.

Rules:
- Use motion to direct attention or explain a relationship.
- Use smooth easing; avoid mechanical constant-speed movement unless intentional.
- Do not animate every layer simultaneously.
- Keep fast motion for hooks, disruptions, and meaningful reveals.
- Use slower motion for explanation and reflection.
- Use transitions motivated by composition, direction, shape, or narrative.
- Avoid gratuitous glitch, spin, shake, and zoom effects.

## 11. On-screen text and captions

- Narration is the primary information channel.
- Headlines should be short and add emphasis rather than repeat the entire narration.
- Captions must match the spoken words and be synchronized.
- Keep captions within safe margins and avoid covering the focal subject.
- Use the series typography system consistently.
- Use Bahasa Indonesia with natural spelling and punctuation.
- Do not render important text inside AI-generated images; compose it in Remotion.

## 12. Evidence and ethical representation

- Maintain a source list for factual claims in each episode.
- When a shot depicts a study, statistic, platform feature, or causal relationship, link it to the relevant source or claim ID in the episode data.
- Distinguish established findings from hypotheses, examples, and interpretation.
- Do not depict recommendation systems as having human-like intentions.
- Do not imply that every viewer sees the same feed or experiences the same effect.
- Avoid presenting correlation as causation.
- Do not invent UI features, statistics, or scientific mechanisms for visual convenience.
- If the narration is uncertain, the visual should preserve that uncertainty rather than turning it into certainty.

## 13. Prompt handoff to image generation

The storyboard does not ask the image model to invent the story. It supplies the shot's approved visual brief.

Each image-generation request must include:
1. Episode ID and shot ID.
2. The exact story beat and viewer takeaway.
3. Subject, action, and environment.
4. Composition and camera angle.
5. Foreground / midground / background requirements.
6. Required reusable asset references or visual descriptions.
7. Elements that must remain separate or transparent.
8. Negative space and mobile-safe placement.
9. The Visual Production Bible style constraints.
10. Explicit exclusions: unwanted text, watermark, unrelated logos, photorealism, 3D, anime, or clutter.

Generate a clean base image first. If a shot needs independently animated layers, generate or derive those elements separately and register each asset. Do not assume a single flattened image can provide true independent object animation.

## 14. Storyboard data contract

The canonical machine-readable episode format should preserve the current root fields while adding explicit storyboard metadata. Recommended top-level structure:

```json
{
  "id": "episode-01",
  "series_id": "disetir-media-sosial",
  "title": "Episode title",
  "language": "id-ID",
  "format": {
    "width": 1080,
    "height": 1920,
    "fps": 30,
    "duration_seconds": 50
  },
  "visual_standard": "docs/VISUAL-PRODUCTION-BIBLE.md",
  "storyboard_standard": "docs/STORYBOARD-BIBLE.md",
  "sources": [],
  "scenes": [
    {
      "id": "E01-S01",
      "start": 0,
      "end": 5,
      "story_beat": "One clear idea",
      "viewer_takeaway": "What the viewer should understand",
      "narration": "Exact approved narration segment",
      "visual": {
        "description": "Subject, action, environment",
        "metaphor": "Why this visual is suitable",
        "composition": "Focal point and spatial layers",
        "camera": "Camera direction"
      },
      "assets": [],
      "motion": {
        "primary": "slow-push-in",
        "secondary": [],
        "transition_in": "cut",
        "transition_out": "motivated-match"
      },
      "on_screen_text": [],
      "sound_cues": [],
      "image_prompt_brief": "",
      "qa": {
        "accuracy_review": "pending",
        "visual_review": "pending"
      }
    }
  ]
}
```

This is a recommended contract, not a claim that the current Remotion code already consumes every field. Update the schema, renderer, and tests in a separate implementation change before relying on new fields in production.

## 15. Storyboard review checklist

Before approving a storyboard:
- [ ] The visual sequence follows the narration's actual logic.
- [ ] Every shot has one dominant idea and a clear viewer takeaway.
- [ ] The hook creates curiosity without misleading the viewer.
- [ ] The mechanism is visually understandable.
- [ ] Metaphors are accurate and not repeated unnecessarily.
- [ ] Shot timings cover the complete runtime.
- [ ] Narration fits naturally within the timing.
- [ ] Asset needs and reusable assets are identified.
- [ ] Layer separation supports the planned motion.
- [ ] Captions and text have mobile-safe space.
- [ ] Factual visuals are traceable to sources or claim IDs.
- [ ] No scene implies stronger claims than the evidence supports.
- [ ] The storyboard follows the approved Visual Production Bible.

## 16. Versioning and governance

- This v1.0 standard applies to the *Disetir Media Sosial* series.
- The Visual Production Bible remains the authority for style and visual identity.
- This document remains the authority for shot planning and visual storytelling.
- Changes to shot requirements, data contract, or approval gates require a versioned update.
- New data fields must not be treated as renderer-supported until implemented and tested.
- The user must approve the final render before any YouTube upload.
