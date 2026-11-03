# AI Image Director — Android Companion Architecture

## Keputusan produk
Pengguna ingin memakai akun yang sudah login pada aplikasi resmi ChatGPT dan Gemini di perangkat Android. Jangan meminta API key dan jangan menganggap OAuth sebagai jalan untuk mengakses akun konsumen tersebut.

## Arsitektur yang dipilih
Bangun companion app Android native (Kotlin) sebagai pengendali alur kerja lintas aplikasi.

- ChatGPT tetap menjadi sutradara: menyusun prompt, memberi instruksi, dan mengevaluasi hasil.
- Gemini tetap menjadi generator gambar: menerima arahan melalui aplikasi Gemini dan menghasilkan/merevisi gambar.
- Pengguna tetap login sendiri di kedua aplikasi resmi.
- Companion app mengelola urutan kerja, status, riwayat prompt, tombol jeda/berhenti, dan handoff antar-aplikasi.
- Tidak ada API berbayar sebagai ketergantungan utama.

## Mekanisme Android yang perlu diuji
1. Android intents / package launch untuk membuka aplikasi resmi ChatGPT dan Gemini.
2. Android Sharesheet untuk mengirim prompt atau gambar antar-aplikasi jika target aplikasi menerima format share tersebut.
3. AccessibilityService hanya jika diperlukan dan setelah pengguna mengaktifkannya secara eksplisit, untuk membantu navigasi UI yang tidak memiliki intent resmi.
4. MediaProjection atau mekanisme pemilihan media sistem hanya bila diperlukan dan dengan persetujuan pengguna; jangan mengambil layar diam-diam.
5. TTS / SpeechRecognizer Android sebagai kontrol suara pendamping bila sesuai, bukan untuk menyamar sebagai API internal aplikasi AI.

## Batasan dan prinsip
- Tidak ada OAuth universal yang membuat aplikasi pihak ketiga dapat memakai sesi login ChatGPT/Gemini. OAuth hanya dapat dipakai jika penyedia menyediakan alur OAuth resmi untuk produk dan scope yang diperlukan.
- Jangan mengambil, menyimpan, atau membaca cookie, token sesi, password, atau data privat aplikasi ChatGPT/Gemini.
- Jangan menjanjikan automasi penuh sebelum diuji pada perangkat Android nyata. UI aplikasi pihak ketiga dapat berubah, dan aksesibilitas tidak menjamin semua kontrol dapat dioperasikan.
- Aksi penting seperti mengirim prompt, membuat gambar, dan membagikan hasil harus dapat dikonfirmasi atau dihentikan pengguna.
- Jangan mengirim permintaan ke API berbayar sebagai fallback diam-diam.

## Rencana fase
### A0 — Fondasi
- Simpan keputusan arsitektur ini.
- Pertahankan web workspace sebagai editor prompt dan riwayat sederhana.

### A1 — Android companion MVP
- Buat proyek Android native Kotlin.
- Deteksi/pastikan aplikasi ChatGPT dan Gemini tersedia.
- Tombol buka aplikasi dan handoff prompt melalui Sharesheet/deep link yang didukung.
- Simpan riwayat prompt lokal di perangkat.
- Belum ada AccessibilityService.

### A2 — Alur dua aplikasi berbantuan
- Uji alur nyata pada perangkat pengguna.
- Tambahkan kontrol aksesibilitas hanya untuk langkah yang terbukti tidak dapat dilakukan dengan intent/share.
- Sediakan indikator langkah, tombol lanjut, jeda, dan berhenti.

### A3 — Evaluasi gambar dan revisi
- Pengguna membagikan hasil gambar ke companion app atau ChatGPT.
- ChatGPT menyusun instruksi revisi; Gemini menerima instruksi berikutnya.
- Otomasi berulang hanya setelah jalur input/output yang stabil dan diizinkan terbukti.

## Kriteria selesai MVP
- Berjalan pada Android tanpa API key.
- Tidak meminta login ulang jika aplikasi resmi sudah login.
- Bisa menyalin/membagikan prompt ke Gemini dan membuka ChatGPT untuk evaluasi.
- Tidak mengakses kredensial atau sesi privat aplikasi lain.
- Selalu ada kontrol manual untuk melanjutkan atau menghentikan alur.
