# AI Voice Meeting — Phase 1

Fondasi antarmuka mobile-first untuk proyek AI Voice Meeting di repository TCon.

## Jalankan
Buka `index.html` melalui static hosting atau server lokal. Untuk pengujian Android, gunakan Chrome dan pastikan suara Text-to-Speech Bahasa Indonesia tersedia di perangkat.

## Yang sudah tersedia
- UI responsif untuk layar Android.
- Input topik diskusi.
- Pemilih dua suara dari daftar suara browser.
- Demo percakapan lokal bergiliran.
- Tombol mulai, jeda, lanjutkan, dan berhenti.
- Transkrip demo.

## Batasan yang disengaja
Tahap ini **belum** menghubungkan OpenAI API atau Gemini API. Dialog yang terdengar masih contoh lokal, bukan jawaban AI sungguhan. Tidak ada API key atau biaya API pada tahap ini.

## Tahap berikutnya
1. Backend aman untuk menyimpan API key di environment variables.
2. Integrasi Gemini dan OpenAI secara terpisah.
3. Moderator yang mengatur giliran dan menjaga konteks.
4. TTS dua suara dan kontrol percakapan berkelanjutan.
5. Pengamanan biaya, batas penggunaan, dan penyimpanan sesi.

Jangan menaruh API key di frontend atau meng-commit file rahasia ke GitHub.
