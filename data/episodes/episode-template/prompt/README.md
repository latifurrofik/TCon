# Prompt Episode

File `visual-prompts.json` dibuat oleh `scripts/generate_prompts.py` dari `../naskah/storyboard.json`.

Jalankan dari root repository:

```bash
python scripts/generate_prompts.py --episode data/episodes/episode_N/naskah/storyboard.json
```

Prompt yang dihasilkan berstatus `pending`. Review dahulu sebelum digunakan pada penyedia gambar. Generator ini hanya menyusun prompt; tidak membuat gambar.
