# TCon media asset pipeline — Episode 01

## 1. Generate narration
Use Python 3.10+:
```bash
python -m pip install -r requirements-media.txt
python scripts/generate_voice.py
```
Default voice: `id-ID-GadisNeural`; change with `--voice id-ID-ArdiNeural`.
This uses Microsoft's Edge TTS service. It is not an official guaranteed-free production API; availability and terms may change. Do not send confidential text.

The script creates MP3 files in `public/audio/episode-01/` and a duration manifest at `data/episode-01-audio.json`.

## 2. Generate five scene illustrations
Use each scene's `visual_prompt` in `data/episode-01.json`. Generate portrait 9:16 images with no text or logos. Save as `public/images/episode-01/scene-01.png` through `scene-05.png`.

Image generation is not automated yet because no image-provider credentials or connected generation endpoint have been configured.

## 3. Render
Without generated assets: `npm run render:episode-01`.
After all five images and five MP3 files exist: `npm run render:episode-01:assets`.

The Remotion composition now supports generated scene images and per-scene Indonesian narration as optional inputs. The default remains the built-in vector preview until assets exist.

## 4. Current limits
Narration clips start at each scene boundary and may be cut if longer than that scene. Check the audio duration manifest before final render. Automatic timing adjustment and word-level subtitles are not implemented yet. No YouTube upload occurs here.
