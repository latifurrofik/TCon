# TCon Remotion Shorts

Fondasi renderer video vertikal TCon. Modul ini berdiri terpisah dari bot uploader Python yang lama.

## Spesifikasi
- 1080 × 1920, portrait 9:16
- 30 FPS, 50 detik (1.500 frame)
- React + Remotion 4
- Tema gelap, aksen kuning, motion graphics simbolik
- Episode awal: **Kamu Sadar Sedang Disetir?**

## Menjalankan di lingkungan Node.js
```bash
npm install
npm run dev
```

Render:
```bash
mkdir -p out
npm run render:episode-01
```

Preview still:
```bash
npm run still
```

## Batasan tahap ini
Ini baru template visual demonstrasi dengan teks dan simbol vektor. Belum ada generator ilustrasi AI, TTS, subtitle berbasis audio, musik/SFX, cloud rendering, antrean approval, atau integrasi upload. Jangan menjalankan uploader lama untuk file produksi sebelum approval gate diterapkan.
