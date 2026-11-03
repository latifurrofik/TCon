import tempfile
import unittest
from pathlib import Path

from PIL import Image

from scripts.analyze_assets import build_report, difference_hash, filename_hints, hamming_distance


class AssetIntelligenceTests(unittest.TestCase):
    def test_filename_category_is_only_a_low_confidence_hint(self):
        hint = filename_hints(Path("person_character.png"))
        self.assertEqual(hint["suggested_category"], "TOKOH")
        self.assertEqual(hint["category_confidence"], "low")
        self.assertEqual(filename_hints(Path("untitled.png"))["suggested_category"], "PERLU_REVIEW")

    def test_identical_images_have_zero_hash_distance(self):
        image = Image.new("RGB", (32, 32), "white")
        self.assertEqual(difference_hash(image), difference_hash(image.copy()))
        self.assertEqual(hamming_distance("0000000000000000", "0000000000000000"), 0)

    def test_report_flags_similar_images_and_is_read_only(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            inbox = root / "public" / "assets" / "inbox"
            inbox.mkdir(parents=True)
            Image.new("RGB", (32, 32), "white").save(inbox / "background_a.png")
            Image.new("RGB", (32, 32), "white").save(inbox / "background_b.png")
            report = build_report(inbox, root, threshold=0)
            self.assertEqual(report["scanned_images"], 2)
            self.assertEqual(len(report["possible_similar_pairs"]), 1)
            self.assertFalse(report["safety"]["registry_modified"])
            self.assertTrue((inbox / "background_a.png").exists())
            self.assertTrue((inbox / "background_b.png").exists())


if __name__ == "__main__":
    unittest.main()
