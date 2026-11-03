# AI Voice Meeting API

Backend FastAPI untuk menghubungkan frontend dengan OpenAI API dan Gemini API.

## Menjalankan lokal
1. Masuk ke folder voice-meeting/backend.
2. Instal dependensi: pip install -r requirements.txt.
3. Atur environment variables OPENAI_API_KEY dan GEMINI_API_KEY pada terminal atau panel environment server.
4. Jalankan: uvicorn main:app --host 0.0.0.0 --port 8000.
5. Dokumentasi endpoint tersedia di /docs.

## Endpoint
- GET /api/health: status backend dan apakah masing-masing API key tersedia (key tidak pernah dikembalikan).
- POST /api/chat/openai: meminta respons OpenAI.
- POST /api/chat/gemini: meminta respons Gemini.

Contoh body JSON: { topic, prompt, history }.

## Environment variables
Lihat .env.example. Default Gemini memakai gemini-3.7-flash; ketersediaan dan kuota gratis bergantung pada project Google AI Studio. Default OpenAI memakai gpt-4o-mini; penggunaan OpenAI API ditagih terpisah dari langganan ChatGPT.

## Keamanan penting
- API key hanya dibaca dari environment variables server.
- Jangan masukkan API key di JavaScript, HTML, atau Git.
- Batasi ALLOWED_ORIGINS ke domain frontend saat deploy.
- Endpoint belum memiliki autentikasi pengguna atau rate limiter. Jalankan lokal/pribadi dahulu. Jangan buka backend ke internet publik sebelum menambahkan autentikasi dan pembatasan request.
- Respons dibatasi 350 token dan riwayat maksimal 12 pesan untuk mengendalikan biaya.
