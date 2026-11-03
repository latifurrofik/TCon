# Episode Thumbnail Manager

Each episode may contain one YouTube thumbnail directly in its folder: `thumbnail.jpg` or `thumbnail.png`. Do not create a subfolder or put episode thumbnails in `public/assets/library/`.

## Review lifecycle
- `missing`: no file.
- `pending_review`: registered, awaiting human visual review.
- `approved`: a human explicitly approved the exact registered file.
- `rejected`: reserved for a future explicit rejection workflow.

The metadata stores filename, SHA-256, dimensions, file size, aspect ratio, status and approval timestamp. If the image changes after registration, its hash no longer matches and it must be registered and reviewed again.

## Commands
Read-only inspection:
```bash
python scripts/manage_thumbnails.py --episode-dir data/episodes/episode_1
```
Register an existing image as pending:
```bash
python scripts/manage_thumbnails.py --episode-dir data/episodes/episode_1 --register
```
After visually reviewing the exact file, explicitly approve it:
```bash
python scripts/manage_thumbnails.py --episode-dir data/episodes/episode_1 --approve
```

Default mode is read-only. The tool never generates, moves, renames, deletes or overwrites image files. Approval is never automatic.
