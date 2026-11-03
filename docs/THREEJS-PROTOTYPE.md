# Three.js Educational Motion Prototype

## Purpose
A first 10-second vertical prototype for TCon using procedural 3D objects. It deliberately avoids AI-generated images and video. Three.js creates the scene; Remotion owns the timeline and final MP4 render.

## Composition
- ID: `TConThreePrototype`
- Canvas: 1080 × 1920, 30 fps, 300 frames (10 seconds)
- Visual: simple geometric nodes, rings, controlled lighting, slow camera push, and restrained motion
- Palette: TCon navy, slate, cyan, coral, and warm ivory
- Text: HTML/Remotion overlay, not baked into textures

## Deterministic animation
All animated transforms are derived from Remotion's `useCurrentFrame()`. Do not use React Three Fiber's `useFrame()`, browser timers, random values, or autonomous animation loops. This keeps frame seeking and rendering deterministic.

## Render
After dependencies are installed:
```bash
npm run typecheck
npm run render:three:prototype
```
The MP4 is written to `out/three-prototype.mp4`.

## Cloud rendering note
Three.js requires WebGL. GitHub-hosted Actions runners do not provide a physical GPU. The workflow therefore uses Chromium's software WebGL backend (SwiftShader) for the first validation render. This may be slower than GPU rendering. A successful TypeScript check alone does not prove that the MP4 rendered correctly; inspect the uploaded artifact.

## Scope limits
This is a visual feasibility prototype, not yet the production renderer for all 15 episodes. It does not generate scripts, voice, subtitles, episode-specific scenes, or upload to YouTube. No publication step is included.
