# Prompt Episode 1

Manifest prompt episode ini dibuat dari `../naskah/storyboard.json`.

Jalankan dari root repository:

```bash
python scripts/generate_prompts.py --episode data/episodes/episode_1/naskah/storyboard.json
```

Output: `data/episodes/episode_1/prompt/visual-prompts.json`.

Semua prompt baru berstatus `pending` dan wajib direview. Generator tidak membuat gambar atau menghubungi provider gambar.
