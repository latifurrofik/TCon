# AI Voice Meeting API

Backend FastAPI untuk menghubungkan frontend dengan OpenAI API dan Gemini API.

## Menjalankan lokal

1. Buat virtual environment Python.
2. Instal dependensi:

```bash
pip install -r requirements.txt
```

3. Atur environment variables `OPENAI_API_KEY` dan/atau `GEMINI_API_KEY`.
4. Jalankan dari folder `voice-meeting/backend`:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

Dokumentasi endpoint tersedia di `/docs`.

## Endpoint

- `GET /api/health`: status backend dan apakah masing-masing API key tersedia (tidak pernah mengembalikan key).
- `POST /api/chat/openai`: meminta respons OpenAI.
- `POST /api/chat/gemini`: meminta respons Gemini.

Contoh body:

```json
{
  "topic": "Apakah AI menggantikan pekerjaan manusia?",
  "prompt": "Berikan pandangan pembuka sebagai peserta diskusi.",
  "history": []
}
```

## Keamanan

- API key hanya dibaca dari environment variables server.
- Jangan masukkan API key di JavaScript, HTML, atau Git.
- Batasi `ALLOWED_ORIGINS` ke domain frontend saat deploy.
- Endpoint ini belum memiliki autentikasi pengguna atau rate limiter. Jangan membuka backend ke internet publik sebelum menambahkan keduanya.
- Respons dibatasi 350 token dan riwayat maksimal 12 pesan untuk mengendalikan biaya.
