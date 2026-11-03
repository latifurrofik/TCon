# TCon Remotion Shorts

Fondasi produksi YouTube Shorts TCon melalui GitHub Actions, tanpa perlu PC lokal.

## Spesifikasi
- 1080 × 1920, portrait 9:16, 30 FPS
- Durasi episode contoh: 50 detik
- Ilustrasi AI melalui AI Horde (komputasi sukarela komunitas; tanpa biaya API)
- Narasi Bahasa Indonesia melalui Edge TTS
- Render MP4 melalui Remotion di GitHub Actions
- Hasil berupa artifact untuk ditinjau manual

## Cara menjalankan dari Android
1. Buka tab **Actions** di repository GitHub.
2. Pilih **TCon cloud Shorts production**.
3. Tekan **Run workflow**.
4. Biarkan **Regenerate existing scene images** tidak dicentang untuk mempertahankan gambar yang sudah jadi.
5. Setelah selesai, unduh artifact **tcon-episode-01-preview**.

## Biaya dan batasan
Generator gambar memakai AI Horde, jaringan volunteer yang dapat digunakan gratis bahkan tanpa akun. Workflow memakai anonymous key secara default; anonymous request mendapat prioritas antrean terendah. Waktu tunggu tidak dijamin, ketersediaan worker berubah-ubah, dan request dapat timeout. Jika ingin prioritas lebih tinggi, buat akun AI Horde dan simpan API key sebagai GitHub Actions secret `AI_HORDE_API_KEY`. Jangan masukkan key ke source code.

GitHub Actions juga memiliki kuota penggunaan gratis sesuai jenis akun/repository. Edge TTS tidak memerlukan API key, tetapi merupakan layanan online dan ketersediaannya tidak dijamin.

## Approval dan upload
Workflow ini **hanya menghasilkan file preview**. Tidak ada langkah upload ke YouTube. Tinjau MP4 terlebih dahulu; jangan gunakan uploader lama karena belum memiliki approval gate yang aman.

## Pengembangan lokal
```bash
npm install
npm run dev
```
