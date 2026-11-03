# Episode Workspace

Setiap episode memiliki folder `episode_N` sebagai sumber kerja utama.

## Struktur standar
```text
episode_N/
├── metadata.json
├── README.md
├── thumbnail.jpg (atau thumbnail.png)
├── naskah/
├── prompt/
├── assets/
├── audio/
└── output/
```

## Aturan
- `metadata.json` menyimpan metadata publikasi dan status produksi, termasuk review thumbnail.
- Simpan maksimal satu thumbnail YouTube di root folder episode sebagai `thumbnail.jpg` atau `thumbnail.png`, bukan di subfolder atau `public/assets/library/`.
- Status thumbnail: `missing` → `pending_review` → `approved`. Approval hanya setelah pemeriksaan visual manusia; perubahan file membatalkan approval.
- Gunakan `python scripts/manage_thumbnails.py --episode-dir data/episodes/episode_N`; lihat `docs/THUMBNAIL-MANAGER.md`.
- `naskah/naskah.md`: narasi Bahasa Indonesia.
- `naskah/storyboard.json`: scene, timing, narasi per scene dan arahan visual.
- `prompt/visual-prompts.json`: manifest prompt; status awal pending review.
- `assets/`: aset visual khusus episode selain thumbnail. Aset reusable tetap di `public/assets/library/`.
- `audio/`: voice-over dan audio khusus episode.
- `output/`: preview dan render; file besar sebaiknya menjadi GitHub Actions artifact.
- Placeholder bukan materi yang sudah disetujui.

Buat episode baru dengan menyalin `data/episodes/episode-template/`. Jangan hapus sumber legacy sebelum semua referensi pipeline dipindahkan dan diuji.
