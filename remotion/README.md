# TCon Remotion Shorts

Fondasi awal template YouTube Shorts vertikal 1080×1920, 30 fps, dengan gaya ilustrasi editorial 2D gelap dan aksen kuning.

## Prasyarat
- Node.js 20 atau lebih baru
- npm
- Lingkungan desktop/server yang didukung Remotion untuk rendering Chromium

## Menjalankan
```bash
cd remotion
npm install
npm run studio
```

Render contoh:
```bash
npm run render:ep01
```

Output berada di `remotion/out/episode-01.mp4`.

## Status saat ini
Ini baru scaffold visual dan data episode contoh. Ilustrasi AI per adegan, TTS Bahasa Indonesia, musik/SFX, subtitle tersinkron, sistem approval, dan upload YouTube belum diintegrasikan. Placeholder lingkaran pada template harus diganti aset ilustrasi final.

## Keputusan arsitektur
Rendering tidak dipaksakan berjalan di Android/Termux. Android dipakai untuk mengelola repository, memeriksa hasil, dan menyetujui publikasi. Render dijalankan pada runner/lingkungan yang kompatibel setelah jalur gratis dan batas kuotanya diuji.
