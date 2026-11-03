import json
import tempfile
import unittest
from pathlib import Path

from scripts.manage_thumbnails import inspect_thumbnail, update_metadata

def png(w=1080,h=1920):
    return b"\x89PNG\r\n\x1a\n"+b"\x00\x00\x00\rIHDR"+w.to_bytes(4,"big")+h.to_bytes(4,"big")+b"\x08\x06\x00\x00\x00"+b"\x00\x00\x00\x00"

class ThumbnailTests(unittest.TestCase):
    def test_missing(self):
        with tempfile.TemporaryDirectory() as d:
            self.assertEqual(inspect_thumbnail(Path(d))["status"],"missing")
    def test_register_pending_and_keep_file(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d); image=p/"thumbnail.png"; image.write_bytes(png())
            (p/"metadata.json").write_text('{"episode_number":1}')
            update_metadata(p,inspect_thumbnail(p),False)
            data=json.loads((p/"metadata.json").read_text())
            self.assertEqual(data["thumbnail"]["status"],"pending_review")
            self.assertTrue(image.exists())
    def test_changed_file_cannot_be_approved(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d); image=p/"thumbnail.png"; image.write_bytes(png())
            (p/"metadata.json").write_text('{"episode_number":1}')
            update_metadata(p,inspect_thumbnail(p),False); image.write_bytes(png(1000,1000))
            with self.assertRaisesRegex(ValueError,"changed since registration"):
                update_metadata(p,inspect_thumbnail(p),True)
    def test_both_names_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d); (p/"thumbnail.jpg").write_bytes(png()); (p/"thumbnail.png").write_bytes(png())
            with self.assertRaisesRegex(ValueError,"both"):
                inspect_thumbnail(p)

if __name__=="__main__": unittest.main()
