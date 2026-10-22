
import os
import time
from pathlib import Path
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload
from google.auth.transport.requests import Request

VIDEOS_DIR = Path("videos")
TOKEN_FILE = "token.json"

def generate_metadata_from_filename(filename):
    # Generate dari nama file, misal: cara-mancing-lele.mp4 -> Cara Mancing Lele - Wajib Nonton!
    name = Path(filename).stem.replace('-', ' ').replace('_', ' ').title()
    title = f"{name} - Auto Upload"
    description = f"""
Video: {name}

Ini adalah video {name}. Tonton sampai habis ya!

---
Auto-upload by YT Agent Bot
#shorts #fyp #indonesia
"""
    tags = [name.lower(), "tutorial", "indonesia", "viral", "auto"]
    return title[:95], description, tags

def get_youtube_service():
    if not os.path.exists(TOKEN_FILE):
        raise Exception("token.json not found - jalankan auth lokal dulu")
    creds = Credentials.from_authorized_user_file(TOKEN_FILE, ["https://www.googleapis.com/auth/youtube.upload"])
    if creds.expired and creds.refresh_token:
        creds.refresh(Request())
    return build("youtube", "v3", credentials=creds)

def upload_video(file_path, title, description, tags):
    youtube = get_youtube_service()
    body = {
        "snippet": {
            "title": title,
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
    print(f"Uploaded! ID: {response['id']}")
    return response

if __name__ == "__main__":
    VIDEOS_DIR.mkdir(exist_ok=True)
    print("Cek folder videos...")
    videos = list(VIDEOS_DIR.glob("*.mp4")) + list(VIDEOS_DIR.glob("*.mov")) + list(VIDEOS_DIR.glob("*.mkv"))
    if not videos:
        print("Tidak ada video baru.")
    for video_file in videos:
        print(f"Processing: {video_file.name}")
        title, desc, tags = generate_metadata_from_filename(video_file.name)
        print(f"Title: {title}")
        try:
            upload_video(video_file, title, desc, tags)
            done_dir = Path("done")
            done_dir.mkdir(exist_ok=True)
            video_file.rename(done_dir / video_file.name)
            print(f"Done: {video_file.name} moved to done/")
        except Exception as e:
            print(f"Gagal: {e}")
        time.sleep(5)
