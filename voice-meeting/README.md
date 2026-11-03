# AI Image Director — TCon

Aplikasi pendamping mobile-first untuk alur kerja ChatGPT sebagai sutradara visual dan Gemini sebagai pembuat gambar.

## Tujuan produk
- ChatGPT membantu merumuskan ide, komposisi, gaya, dan prompt gambar.
- Gemini menerima instruksi melalui aplikasi Gemini dan menghasilkan atau merevisi gambar.
- Pengguna dapat mengulang proses evaluasi dan revisi sampai hasil sesuai.

## Android MVP saat ini
1. Buka ChatGPT dan minta dibuatkan arahan/prompt gambar.
2. Tempel prompt ke kolom pada halaman ini.
3. Tekan **Salin instruksi**, lalu **Buka Gemini**.
4. Di Gemini, gunakan percakapan suara atau chat, tempel instruksi, dan minta Gemini membuat gambar.
5. Untuk revisi, bawa hasil atau deskripsikan kekurangannya ke ChatGPT. Salin arahan revisi ke Gemini.

Halaman ini adalah jembatan salin-tempel. Ia belum mengendalikan mikrofon, aplikasi Gemini, atau proses generate gambar secara otomatis.

## Batas teknis yang perlu diketahui
- Akun ChatGPT dan Gemini yang digunakan di aplikasi masing-masing tidak otomatis memberi aplikasi pihak ketiga akses API.
- Langganan ChatGPT tidak termasuk kredit OpenAI API.
- Halaman web Android tidak dapat secara aman membaca percakapan suara Gemini atau mengambil hasil gambar dari aplikasi Gemini.
- Tombol Buka Gemini hanya membuka alamat aplikasi/web Gemini; perilaku pembukaan aplikasi bergantung pada Android dan browser.
- Otomatisasi lintas aplikasi yang benar-benar hands-free kemungkinan memerlukan aplikasi Android native dengan izin Accessibility, dan tetap perlu diuji terhadap kebijakan serta perubahan UI aplikasi Gemini.

## Tahap pengembangan
1. Android MVP: workspace prompt, salin instruksi, dan handoff ke Gemini.
2. Riwayat prompt dan revisi agar instruksi sebelumnya tidak hilang.
3. Evaluasi hasil gambar dengan upload/berbagi gambar kembali ke ChatGPT.
4. Riset opsi otomasi Android native. Jangan mengklaim integrasi langsung sebelum diuji pada perangkat nyata.

## Menjalankan
Buka file index.html melalui static hosting atau server lokal. Tidak membutuhkan API key untuk MVP ini.

Folder backend/ masih berisi eksperimen integrasi API sebelumnya dan tidak dipakai oleh MVP Android saat ini.