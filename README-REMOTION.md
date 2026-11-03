# TCon Remotion Shorts

Fondasi produksi YouTube Shorts TCon melalui GitHub Actions, tanpa perlu PC lokal.

## Spesifikasi
- 1080 × 1920, portrait 9:16, 30 FPS
- Durasi episode contoh: 50 detik
- Visual utama: komposisi motion graphics Remotion; ilustrasi AI Horde opsional
- Narasi Bahasa Indonesia melalui Edge TTS
- Subtitle bertiming dari word boundaries
- Render MP4 melalui Remotion di GitHub Actions
- Hasil berupa artifact untuk ditinjau manual

## Cara menjalankan dari Android
1. Buka tab **Actions** di repository GitHub.
2. Pilih **TCon cloud Shorts production**.
3. Tekan **Run workflow**.
4. Untuk percobaan paling stabil, biarkan **Try free AI Horde scene images** tidak dicentang. Video memakai visual motion graphics bawaan Remotion.
5. Jika ingin mencoba ilustrasi AI Horde, centang opsi tersebut. Jika layanan gagal atau gambar tidak lengkap, workflow otomatis kembali ke visual motion graphics.
6. Setelah selesai, unduh artifact **tcon-episode-01-preview**.

## Biaya dan batasan
AI Horde adalah layanan komunitas gratis dengan antrean dan ketersediaan worker yang tidak dijamin. Workflow tidak lagi bergantung pada AI Horde untuk menyelesaikan render. Log workflow 3 November 2026 menunjukkan AI Horde ditolak Cloudflare dengan HTTP 403, Error 1010 `browser_signature_banned`; ini merupakan blokir akses dari sisi layanan, bukan bukti kuota gambar habis. Jangan mengulang request otomatis ketika layanan mengirim error tersebut.

Jika ilustrasi AI Horde diaktifkan dan gagal, render beralih ke visual motion graphics bawaan. Mode fallback tetap merupakan preview yang harus ditinjau; bukan pengganti otomatis untuk ilustrasi final yang sudah disetujui.

GitHub Actions memiliki kuota penggunaan gratis sesuai jenis akun/repository. Edge TTS tidak memerlukan API key, tetapi merupakan layanan online dan ketersediaannya tidak dijamin.

## Approval dan upload
Workflow ini **hanya menghasilkan file preview**. Tidak ada langkah upload ke YouTube. Tinjau MP4 terlebih dahulu; jangan gunakan uploader lama karena belum memiliki approval gate yang aman.

## Pengembangan lokal
```bash
npm install
npm run dev
```
