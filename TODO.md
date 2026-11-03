# TCon — To Do List

Dokumen ini adalah backlog utama pengembangan TCon. Semua pekerjaan dimulai dengan status **Belum dikerjakan**, kecuali diperbarui berdasarkan hasil implementasi dan pengujian nyata.

## Aturan pengerjaan

- Kerjakan berdasarkan prioritas; jangan menganggap kode selesai hanya karena sudah masuk GitHub.
- Setiap fitur harus diuji dan memiliki kriteria selesai yang jelas.
- Kembangkan fitur melalui branch `feat/*` atau `fix/*`, lalu gabungkan ke `main` setelah pemeriksaan.
- Jangan menghapus branch atau menggabungkan perubahan besar sebelum dampaknya diperiksa.
- Rahasia, token, password, dan API key tidak boleh disimpan di source code.
- Untuk tindakan eksternal yang berdampak—terutama publikasi YouTube—wajib ada persetujuan pemilik.

## P0 — Audit dan stabilisasi fondasi

- [ ] Audit struktur dan isi branch `main`; catat modul, entry point, dependensi, dan kondisi build.
- [ ] Inventarisasi seluruh branch; tandai sudah merged, masih terbuka, tertinggal, atau tidak lagi diperlukan.
- [ ] Tinjau Pull Request yang masih terbuka, khususnya perbaikan rendering Shorts tanpa layanan gambar AI; putuskan berdasarkan hasil tes, bukan asumsi.
- [ ] Tinjau `feat/ai-voice-meeting-foundation`; tentukan bagian yang layak dipindahkan ke arsitektur aplikasi utama.
- [ ] Tetapkan struktur direktori, standar penamaan, versi, dan aturan kontribusi.
- [ ] Pastikan workflow GitHub Actions dapat membangun dan menguji proyek secara konsisten.
- [ ] Dokumentasikan cara build, instalasi, konfigurasi, dan pemulihan jika build gagal.

**Kriteria selesai P0:** kondisi repository terdokumentasi, keputusan setiap branch tercatat, dan build utama dapat direproduksi.

## P1 — Android MVP TCon

- [ ] Pastikan proyek Android native dapat dibangun menjadi APK dari branch utama.
- [ ] Uji instalasi dan pembukaan APK pada perangkat Android.
- [ ] Buat beranda TCon dengan navigasi modul.
- [ ] Buat halaman pengaturan dan informasi versi aplikasi.
- [ ] Simpan preferensi dan draft secara lokal dengan penanganan error.
- [ ] Tambahkan pemeriksaan versi dan alur pembaruan APK yang aman; instalasi tetap mengikuti izin Android.
- [ ] Tambahkan pencatatan error dasar tanpa merekam data sensitif.

**Kriteria selesai P1:** APK terpasang dan fungsi dasar teruji di HP Android.

## P2 — AI Image Director

- [ ] Buat alur kerja ChatGPT sebagai sutradara prompt dan Gemini sebagai pembuat gambar.
- [ ] Sediakan editor prompt, tombol salin, dan handoff ke aplikasi resmi Gemini.
- [ ] Sediakan cara membawa hasil gambar kembali ke ChatGPT untuk evaluasi.
- [ ] Simpan riwayat prompt dan revisi secara lokal bila diizinkan pengguna.
- [ ] Uji alur pembuatan dan revisi gambar secara manual di Android.
- [ ] Dokumentasikan batas integrasi aplikasi resmi; jangan mengasumsikan login akun memberi akses API atau kendali otomatis.

**Kriteria selesai P2:** alur ChatGPT → Gemini → evaluasi/revisi dapat digunakan dan diuji di perangkat.

## P3 — TCon Studio: produksi YouTube

- [ ] Buat modul perencanaan konten dan kalender produksi.
- [ ] Dukung dua format: YouTube Shorts dan video long-form.
- [ ] Buat alur ide, riset, naskah, narasi, storyboard, dan daftar aset.
- [ ] Integrasikan workflow Remotion untuk merender video.
- [ ] Buat pemeriksaan hasil render: durasi, rasio, audio, subtitle, dan keberadaan file.
- [ ] Buat modul judul, deskripsi, tag, thumbnail, dan metadata.
- [ ] Simpan proyek dan aset per episode agar mudah dilacak.

**Kriteria selesai P3:** satu proyek video dapat diproses dari brief sampai file hasil render yang lolos pemeriksaan.

## P4 — YouTube Upload dan persetujuan publikasi

- [ ] Rancang koneksi resmi YouTube Data API dengan OAuth Google.
- [ ] Buat proses menghubungkan dan mencabut akses akun YouTube.
- [ ] Siapkan unggah sebagai private atau status aman terlebih dahulu.
- [ ] Tampilkan pratinjau video dan metadata sebelum publikasi.
- [ ] Wajibkan persetujuan eksplisit pemilik sebelum video dipublikasikan atau dijadwalkan tayang.
- [ ] Catat status unggah, ID video, jadwal, dan pesan kegagalan.
- [ ] Uji batas kuota API, penanganan retry, dan unggah terputus.

**Kriteria selesai P4:** video dapat diunggah melalui integrasi resmi dan tidak pernah dipublikasikan tanpa persetujuan.

## P5 — Automation Engine dan AI Agents

- [ ] Rancang workflow engine modular: trigger, langkah, kondisi, jadwal, dan hasil.
- [ ] Buat katalog agen AI beserta peran, input, output, dan batas kewenangannya.
- [ ] Buat antrean tugas, status eksekusi, retry, dan pembatalan.
- [ ] Tambahkan approval manusia untuk tindakan eksternal atau berisiko.
- [ ] Pisahkan antarmuka Android dari proses backend/worker agar pekerjaan berat tidak bergantung pada aplikasi yang terus terbuka.
- [ ] Evaluasi kebutuhan hosting dan biaya sebelum memilih layanan cloud atau server.

**Kriteria selesai P5:** workflow sederhana dapat dijalankan, dipantau, dihentikan, dan meminta persetujuan sesuai aturan.

## P6 — Data, memori, keamanan, dan operasional

- [ ] Tentukan penyimpanan lokal dan kebutuhan database jarak jauh berdasarkan use case.
- [ ] Buat model data untuk pengguna, proyek, tugas, workflow, aset, dan riwayat.
- [ ] Terapkan pengelolaan rahasia dan izin akses dengan prinsip least privilege.
- [ ] Buat backup, ekspor, dan pemulihan data.
- [ ] Tambahkan log audit untuk perubahan konfigurasi dan tindakan penting.
- [ ] Buat dashboard status, laporan, dan analitik operasional.
- [ ] Dokumentasikan privasi, retensi data, dan prosedur insiden.

**Kriteria selesai P6:** data dan akses dikelola dengan aman, dapat dipulihkan, dan aktivitas penting dapat diaudit.

## P7 — Social Media Command Center (monitoring multi-platform)

- [ ] Rancang dashboard terpusat untuk seluruh akun media sosial yang dihubungkan pengguna.
- [ ] Prioritaskan integrasi resmi Meta (Facebook dan Instagram) serta YouTube; tambahkan platform lain setelah API dan izin aksesnya dievaluasi.
- [ ] Buat alur OAuth/koneksi akun, pemilihan aset atau halaman yang dikelola, pemeriksaan izin, dan pencabutan akses.
- [ ] Tampilkan metrik yang tersedia per platform, seperti views, reach, impressions, engagement, watch time, pengikut, dan subscriber; jangan mengasumsikan semua metrik tersedia di setiap platform.
- [ ] Buat ringkasan performa lintas akun dan perbandingan antarperiode dengan definisi metrik yang jelas.
- [ ] Pantau postingan, komentar, pesan, dan notifikasi hanya jika API serta izin aplikasi mengizinkannya.
- [ ] Buat sinkronisasi terjadwal, penanganan rate limit, token kedaluwarsa, kegagalan koneksi, dan status data terakhir diperbarui.
- [ ] Buat peringatan untuk perubahan performa atau aktivitas penting dengan ambang batas yang dapat diatur pengguna.
- [ ] Terapkan kontrol privasi, penyimpanan token yang aman, pembatasan akses, dan log audit untuk setiap akun terhubung.
- [ ] Dokumentasikan keterbatasan API, proses persetujuan aplikasi platform, kuota, dan biaya operasional.

**Kriteria selesai P7:** pengguna dapat menghubungkan akun secara resmi dan memantau data yang memang tersedia melalui API dari satu dashboard TCon.


- Target perangkat utama: Android.
- Strategi biaya: prioritaskan opsi gratis dan biaya rendah; verifikasi batas kuota sebelum mengandalkan layanan.
- Produk: satu aplikasi TCon modular, bukan aplikasi terpisah untuk setiap fitur.
- Konten: dukung Shorts dan long-form.
- Publikasi YouTube: otomatisasi proses boleh dilakukan, tetapi publikasi menunggu persetujuan pemilik.
- Status dokumen ini tidak menyatakan fitur-fitur di atas sudah tersedia; ini adalah daftar penugasan ke depan.

## Log progres

| Tanggal | Perubahan |
|---|---|
| 2026-11-03 | Membuat backlog awal TCon di branch `main`. |
