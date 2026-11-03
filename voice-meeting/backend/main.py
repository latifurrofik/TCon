import os
from typing import Literal

import httpx
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "").strip()
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "").strip()
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
ALLOWED_ORIGINS = [
    item.strip()
    for item in os.getenv("ALLOWED_ORIGINS", "http://localhost:8000").split(",")
    if item.strip()
]

app = FastAPI(title="AI Voice Meeting API", version="0.2.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOWED_ORIGINS,
    allow_credentials=False,
    allow_methods=["GET", "POST"],
    allow_headers=["Content-Type"],
)


class ChatMessage(BaseModel):
    role: Literal["user", "assistant"]
    content: str = Field(min_length=1, max_length=4000)


class ChatRequest(BaseModel):
    topic: str = Field(min_length=1, max_length=500)
    prompt: str = Field(min_length=1, max_length=3000)
    history: list[ChatMessage] = Field(default_factory=list, max_length=12)


def provider_status() -> dict:
    return {
        "openai": {"configured": bool(OPENAI_API_KEY), "model": OPENAI_MODEL},
        "gemini": {"configured": bool(GEMINI_API_KEY), "model": GEMINI_MODEL},
    }


@app.get("/api/health")
async def health():
    return {"ok": True, "service": "ai-voice-meeting", "providers": provider_status()}


async def ask_openai(request: ChatRequest) -> str:
    if not OPENAI_API_KEY:
        raise HTTPException(status_code=503, detail="OPENAI_API_KEY belum dipasang di server.")
    messages = [
        {
            "role": "system",
            "content": (
                "Kamu adalah peserta diskusi AI berbahasa Indonesia. Berikan jawaban lisan "
                "yang alami, ringkas, bernalar, dan relevan. Tanggapi argumen sebelumnya; "
                "jangan mengaku sebagai moderator. Hindari markdown, daftar panjang, dan "
                "kalimat pembuka yang berulang."
            ),
        }
    ]
    messages.extend(
        {"role": item.role, "content": item.content}
        for item in request.history[-12:]
    )
    messages.append({
        "role": "user",
        "content": f"Topik diskusi: {request.topic}\\n\\n{request.prompt}",
    })
    payload = {
        "model": OPENAI_MODEL,
        "messages": messages,
        "temperature": 0.7,
        "max_tokens": 350,
    }
    try:
        async with httpx.AsyncClient(timeout=45) as client:
            response = await client.post(
                "https://api.openai.com/v1/chat/completions",
                headers={"Authorization": f"Bearer {OPENAI_API_KEY}"},
                json=payload,
            )
            response.raise_for_status()
            data = response.json()
            answer = data["choices"][0]["message"]["content"]
            if not isinstance(answer, str) or not answer.strip():
                raise ValueError("Empty response")
            return answer.strip()
    except httpx.HTTPStatusError as exc:
        if exc.response.status_code == 429:
            raise HTTPException(status_code=429, detail="Kuota atau batas laju OpenAI tercapai.")
        if exc.response.status_code in (401, 403):
            raise HTTPException(status_code=502, detail="Autentikasi OpenAI ditolak. Periksa API key.")
        raise HTTPException(status_code=502, detail=f"OpenAI mengembalikan HTTP {exc.response.status_code}.")
    except (httpx.TimeoutException, httpx.RequestError):
        raise HTTPException(status_code=504, detail="OpenAI tidak merespons tepat waktu.")
    except (KeyError, IndexError, ValueError):
        raise HTTPException(status_code=502, detail="Format respons OpenAI tidak dikenali.")


async def ask_gemini(request: ChatRequest) -> str:
    if not GEMINI_API_KEY:
        raise HTTPException(status_code=503, detail="GEMINI_API_KEY belum dipasang di server.")
    contents = []
    for item in request.history[-12:]:
        contents.append({
            "role": "model" if item.role == "assistant" else "user",
            "parts": [{"text": item.content}],
        })
    contents.append({
        "role": "user",
        "parts": [{"text": f"Topik diskusi: {request.topic}\n\n{request.prompt}"}],
    })
    url = (
        "https://generativelanguage.googleapis.com/v1beta/models/"
        f"{GEMINI_MODEL}:generateContent"
    )
    try:
        async with httpx.AsyncClient(timeout=45) as client:
            response = await client.post(
                url,
                headers={"x-goog-api-key": GEMINI_API_KEY},
                json={
                    "systemInstruction": {
                        "parts": [{
                            "text": (
                                "Kamu adalah peserta diskusi AI berbahasa Indonesia. "
                                "Jawab secara alami seperti berbicara, bernalar, ringkas, "
                                "dan tanggapi argumen peserta lain. Kamu bukan moderator. "
                                "Hindari markdown dan pembuka berulang."
                            )
                        }]
                    },
                    "contents": contents,
                    "generationConfig": {"temperature": 0.7, "maxOutputTokens": 350},
                },
            )
            response.raise_for_status()
            data = response.json()
            answer = "".join(
                part.get("text", "")
                for part in data["candidates"][0]["content"]["parts"]
            ).strip()
            if not answer:
                raise ValueError("Empty response")
            return answer
    except httpx.HTTPStatusError as exc:
        if exc.response.status_code == 429:
            raise HTTPException(status_code=429, detail="Kuota atau batas laju Gemini tercapai.")
        if exc.response.status_code in (400, 401, 403):
            raise HTTPException(status_code=502, detail="Gemini menolak permintaan. Periksa API key/model.")
        raise HTTPException(status_code=502, detail=f"Gemini mengembalikan HTTP {exc.response.status_code}.")
    except (httpx.TimeoutException, httpx.RequestError):
        raise HTTPException(status_code=504, detail="Gemini tidak merespons tepat waktu.")
    except (KeyError, IndexError, ValueError, TypeError):
        raise HTTPException(status_code=502, detail="Format respons Gemini tidak dikenali.")


@app.post("/api/chat/{provider}")
async def chat(provider: Literal["openai", "gemini"], request: ChatRequest):
    if provider == "openai":
        answer = await ask_openai(request)
        return {"provider": "openai", "model": OPENAI_MODEL, "text": answer}
    answer = await ask_gemini(request)
    return {"provider": "gemini", "model": GEMINI_MODEL, "text": answer}
