
import os
import time
import json
from pathlib import Path
import whisper
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload
from google.auth.transport.requests import Request

# Config
VIDEOS_DIR = Path("videos")
TOKEN_FILE = "token.json"
CLIENT_SECRET = "client_secret.json"

def transcribe_video(video_path):
    print(f"Transcribing {video_path}...")
    model = whisper.load_model("base")
    result = model.transcribe(str(video_path))
    return result["text"][:2000]  # ambil 2000 char pertama

def generate_metadata(transcript):
    # SIMPLE AI GENERATOR - bisa lu ganti pakai OpenAI/Groq API
    # Untuk versi gratis tanpa API key, pakai template dari transcript
    title = transcript[:60].strip() + " | Auto Generated"
    # Bikin judul SEO dari kalimat pertama
    first_sentence = transcript.split('.')[0][:80]
    title = f"{first_sentence} - Wajib Nonton!"
    
    description = f"""
Video ini membahas:
{transcript[:500]}

---
Auto-upload by YT Agent Bot (Fly.io Free)
#shorts #fyp
"""
    tags = ["auto", "tutorial", "indonesia", "viral"]
    return title, description, tags

def get_youtube_service():
    creds = None
    if os.path.exists(TOKEN_FILE):
        creds = Credentials.from_authorized_user_file(TOKEN_FILE, ["https://www.googleapis.com/auth/youtube.upload"])
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            print("Butuh login YouTube pertama kali. Jalankan lokal dulu untuk dapat token.json")
            raise Exception("token.json not found")
    return build("youtube", "v3", credentials=creds)

def upload_video(file_path, title, description, tags):
    youtube = get_youtube_service()
    body = {
        "snippet": {
            "title": title[:95],
            "description": description,
            "tags": tags,
            "categoryId": "22"
        },
        "status": {
            "privacyStatus": "public",
            "selfDeclaredMadeForKids": False
        }
    }
    media = MediaFileUpload(str(file_path), chunksize=-1, resumable=True)
    request = youtube.videos().insert(part="snippet,status", body=body, media_body=media)
    response = request.execute()
    print(f"Uploaded! Video ID: {response['id']}")
    return response

if __name__ == "__main__":
    VIDEOS_DIR.mkdir(exist_ok=True)
    for video_file in VIDEOS_DIR.glob("*.mp4"):
        print(f"Processing: {video_file}")
        transcript = transcribe_video(video_file)
        title, desc, tags = generate_metadata(transcript)
        print(f"Generated Title: {title}")
        try:
            upload_video(video_file, title, desc, tags)
            # pindah ke folder done
            done_dir = Path("done")
            done_dir.mkdir(exist_ok=True)
            video_file.rename(done_dir / video_file.name)
        except Exception as e:
            print(f"Gagal upload {video_file}: {e}")
        time.sleep(5)
    print("Selesai, tidak ada video baru.")
