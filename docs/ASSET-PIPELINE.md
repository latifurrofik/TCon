# TCon media asset pipeline — Episode 01

## 1. Generate narration (free tier / subject to service availability)
Use Python 3.10+:
```bash
python -m pip install -r requirements-media.txt
python scripts/generate_voice.py
``
Default voice: `id-ID-GadisNeural`. Change with `--voice id-ID-ArdiNeural`.
This uses Microsoft's Edge TTS service; it is not an official guaranteed-free production API and availability/terms may change. Do not send confidential text.

The script creates one MP3 per scene in `public/audio/episode-01/` and a duration manifest at `data/episode-01-audio.json`.

## 2. Generate scene illustrations
Use each scene's `visual_prompt` in `data/episode-01.json` with an image generator. Generate one portrait 9:16 image per scene, with no text or logos. Save as:
- `public/images/episode-01/scene-01.png`
- `public/images/episode-01/scene-02.png`
- ... through scene-05.png

Keep the same visual style and character design across all five scenes. Image generation is not automated yet because no image-provider credentials or connected generation endpoint have been configured.

## 3. Important
The current Remotion composition still uses its built-in vector illustrations. Audio and image assets are generated separately and are not yet wired into the composition. Next step is to connect these assets, calculate scene timing from narration duration, and add a preview/approval queue. Nothing in this pipeline uploads to YouTube.
