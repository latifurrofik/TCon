(() => {
  const prompt = document.getElementById("prompt");
  const copyButton = document.getElementById("copyPrompt");
  const openButton = document.getElementById("openGemini");
  const status = document.getElementById("status");
  const feedback = document.getElementById("feedback");
  const GEMINI_URL = "https://gemini.google.com/app";
  function setStatus(message, good = false) {
    status.innerHTML = "<i></i> " + message;
    status.style.color = good ? "#a9e8c9" : "";
    status.style.borderColor = good ? "#385747" : "";
  }
  async function copyText(value) {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(value);
      return;
    }
    prompt.focus();
    prompt.select();
    const copied = document.execCommand("copy");
    if (!copied) throw new Error("Salin otomatis tidak tersedia. Tekan lama pada teks lalu pilih Salin.");
  }
  copyButton.addEventListener("click", async () => {
    const value = prompt.value.trim();
    if (!value) {
      feedback.textContent = "Tempelkan instruksi gambar terlebih dahulu.";
      prompt.focus();
      return;
    }
    try {
      await copyText(value);
      setStatus("Instruksi disalin", true);
      feedback.textContent = "Instruksi sudah disalin. Buka Gemini dan tempelkan ke percakapan suara atau chat.";
    } catch (error) {
      setStatus("Gagal menyalin");
      feedback.textContent = error.message;
    }
  });
  openButton.addEventListener("click", () => {
    window.open(GEMINI_URL, "_blank", "noopener");
    setStatus("Membuka Gemini");
    feedback.textContent = "Jika aplikasi Gemini tidak terbuka otomatis, lanjutkan melalui browser atau pilih aplikasi Gemini saat diminta Android.";
  });
  prompt.addEventListener("input", () => {
    setStatus(prompt.value.trim() ? "Instruksi siap" : "Siap");
  });
})();