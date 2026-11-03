import json
import tempfile
import unittest
from pathlib import Path

from scripts.manage_assets import inventory, load_registry


class AssetManagerTests(unittest.TestCase):
    def test_new_image_is_pending_review_and_not_moved(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            inbox = root / "inbox"
            inbox.mkdir()
            image = inbox / "tiny.png"
            image.write_bytes(
                b"\x89PNG\r\n\x1a\n" + b"\x00\x00\x00\rIHDR"
                + (12).to_bytes(4, "big") + (8).to_bytes(4, "big")
                + b"\x08\x06\x00\x00\x00" + b"\x00\x00\x00\x00"
            )
            # inventory expects paths relative to repository root; run from a
            # temporary registry and patch the module root for this isolated test.
            import scripts.manage_assets as manager
            old_root = manager.ROOT
            manager.ROOT = root
            try:
                registry = {"schema_version": 1, "library_name": "test", "assets": []}
                report = inventory(inbox, registry)
                self.assertEqual(len(report["new_assets"]), 1)
                record = report["new_assets"][0]
                self.assertEqual(record["category"], "PERLU_REVIEW")
                self.assertEqual(record["status"], "pending_review")
                self.assertEqual(record["dimensions"], {"width": 12, "height": 8})
                self.assertTrue(image.exists())
            finally:
                manager.ROOT = old_root

    def test_exact_duplicate_in_scan_is_reported_once(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            inbox = root / "inbox"
            inbox.mkdir()
            payload = b"\x89PNG\r\n\x1a\n" + b"\x00" * 24
            (inbox / "a.png").write_bytes(payload)
            (inbox / "b.png").write_bytes(payload)
            import scripts.manage_assets as manager
            old_root = manager.ROOT
            manager.ROOT = root
            try:
                report = inventory(inbox, {"assets": []})
                self.assertEqual(len(report["new_assets"]), 1)
                self.assertEqual(len(report["duplicates"]), 1)
                self.assertEqual(report["duplicates"][0]["match"], "same_scan")
            finally:
                manager.ROOT = old_root


if __name__ == "__main__":
    unittest.main()
