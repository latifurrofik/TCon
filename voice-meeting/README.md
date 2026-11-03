# AI Voice Meeting — TCon

Fondasi aplikasi web mobile-first untuk diskusi suara dua AI dari Android.

## Phase 1 — UI dan demo suara
- UI responsif.
- Pemilih suara browser.
- Tombol mulai, jeda, lanjutkan, dan berhenti.

## Phase 2 — Integrasi API
- Backend FastAPI terpisah.
- Endpoint koneksi OpenAI dan Gemini.
- API key disimpan sebagai environment variables server.
- Satu putaran: ChatGPT membuka, Gemini menanggapi, suara dibacakan browser.

## Menjalankan frontend
Buka index.html melalui static hosting atau server lokal. Isi alamat backend pada panel Koneksi AI jika frontend dan backend tidak berada di origin yang sama.

## Menjalankan backend
Lihat backend/README.md. Instal dependensi dari backend/requirements.txt, atur environment variables, lalu jalankan Uvicorn pada port 8000.

## Catatan biaya
Gemini API menyediakan free tier untuk model tertentu dengan batas kuota dan ketersediaan yang dapat berubah. OpenAI API memiliki billing terpisah dari langganan ChatGPT. Untuk benar-benar tanpa biaya API, integrasi OpenAI harus dibiarkan nonaktif dan gunakan Gemini free tier saja.

## Catatan keamanan
Jangan pernah menaruh API key di frontend atau repository. Backend saat ini belum memiliki autentikasi dan rate limiter; gunakan lokal/pribadi untuk pengujian, jangan publikasikan backend sebelum pengamanan tambahan diterapkan.

## Tahap berikutnya
1. Moderator otomatis yang mengatur giliran.
2. Percakapan berkelanjutan dengan tombol jeda/berhenti.
3. Pengamanan API, pembatasan biaya, dan riwayat sesi.
