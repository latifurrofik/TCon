# Episode Workspace

Setiap episode memiliki satu folder dengan pola `episode_N` (contoh: `episode_1`, `episode_2`). Folder ini adalah sumber kerja utama untuk materi editorial episode.

## Struktur standar

```text
episode_N/
├── metadata.json
├── README.md
├── naskah/
│   ├── naskah.md
│   └── storyboard.json
├── prompt/
│   ├── README.md
│   └── visual-prompts.json
├── assets/
├── audio/
└── output/
```

## Aturan

- `metadata.json`: metadata publikasi dan status produksi.
- `naskah/naskah.md`: naskah narasi Bahasa Indonesia yang mudah dibaca dan diedit.
- `naskah/storyboard.json`: data mesin untuk scene, timing, narasi per scene, dan arahan visual. Ini menjadi input generator prompt.
- `prompt/visual-prompts.json`: manifest prompt hasil generator; status awal setiap prompt adalah pending review.
- `assets/`: aset khusus episode. Aset reusable tetap berada di `public/assets/library/`; jangan menggandakan tanpa alasan.
- `audio/`: voice-over dan audio khusus episode.
- `output/`: preview dan hasil render episode. File render besar sebaiknya disimpan sebagai GitHub Actions artifact, bukan di-commit ke Git.
- Folder dan file template memakai placeholder; jangan menganggap placeholder sebagai materi episode yang sudah disetujui.

Buat episode baru dengan menyalin `data/episodes/episode-template/`, lalu ganti nomor, metadata, naskah, storyboard, dan prompt. Jangan menghapus sumber episode lama sebelum seluruh referensi pipeline dipindahkan dan diuji.
