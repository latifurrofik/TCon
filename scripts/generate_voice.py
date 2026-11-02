#!/usr/bin/env python3
"""Generate Indonesian narration clips with Edge TTS.

Usage:
  python -m pip install -r requirements-media.txt
  python scripts/generate_voice.py
  python scripts/generate_voice.py --voice id-ID-ArdiNeural
"""
import argparse
import asyncio
import json
from pathlib import Path

import edge_tts
from mutagen.mp3 import MP3

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data" / "episode-01.json"
OUT = ROOT / "public" / "audio" / "episode-01"
DEFAULT_VOICE = "id-ID-GadisNeural"

async def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--voice", default=DEFAULT_VOICE)
    parser.add_argument("--rate", default="+0%")
    parser.add_argument("--pitch", default="+0Hz")
    args = parser.parse_args()

    episode = json.loads(DATA.read_text(encoding="utf-8"))
    OUT.mkdir(parents=True, exist_ok=True)
    results = []
    for index, scene in enumerate(episode["scenes"], start=1):
        text = scene["narration"].strip()
        target = OUT / f"scene-{index:02d}.mp3"
        communicate = edge_tts.Communicate(text, args.voice, rate=args.rate, pitch=args.pitch)
        await communicate.save(str(target))
        duration = MP3(str(target)).info.length
        results.append({
            "scene": index,
            "file": f"audio/episode-01/scene-{index:02d}.mp3",
            "duration_seconds": round(duration, 2),
            "narration": text
        })
        print(f"OK scene {index}: {duration:.2f}s -> {target}")
    manifest = ROOT / "data" / "episode-01-audio.json"
    manifest.write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Manifest saved: {manifest}")

if __name__ == "__main__":
    asyncio.run(main())
