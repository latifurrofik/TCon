(() => {
  const $ = (id) => document.getElementById(id);
  const synth = window.speechSynthesis;
  const voiceA = $("voiceA");
  const voiceB = $("voiceB");
  const transcript = $("transcript");
  const status = $("status");
  const start = $("start");
  const pause = $("pause");
  const stop = $("stop");
  const turnCount = $("turn-count");
  let voices = [];
  let queue = [];
  let index = 0;
  let paused = false;
  let active = false;

  function loadVoices() {
    voices = synth ? synth.getVoices() : [];
    const idVoices = voices.filter(v => /^id(-|$)/i.test(v.lang));
    const options = idVoices.length ? idVoices : voices;
    [voiceA, voiceB].forEach((select, slot) => {
      const previous = select.value;
      select.innerHTML = "";
      if (!options.length) {
        select.add(new Option("Suara browser tidak tersedia", ""));
        return;
      }
      options.forEach((voice, i) => select.add(new Option(voice.name + " · " + voice.lang, voice.name)));
      const preferred = slot === 0 ? options[0] : (options.find(v => v.name !== options[0].name) || options[0]);
      select.value = options.some(v => v.name === previous) ? previous : preferred.name;
    });
    $("voice-note").textContent = idVoices.length
      ? idVoices.length + " suara Bahasa Indonesia terdeteksi. Pilih dua suara yang berbeda bila tersedia."
      : "Suara Bahasa Indonesia belum terdeteksi. Tambahkan/unduh suara Indonesia di pengaturan Android atau gunakan suara browser yang tersedia.";
  }

  function addMessage(speaker, cls, text) {
    if (transcript.querySelector(".empty-state")) transcript.innerHTML = "";
    const article = document.createElement("article");
    article.className = "message";
    const name = document.createElement("span");
    name.className = "speaker " + cls;
    name.textContent = speaker;
    const body = document.createElement("p");
    body.textContent = text;
    article.append(name, body);
    transcript.append(article);
    turnCount.textContent = (index + 1) + " giliran";
    article.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  function setStatus(label, running = false) {
    status.innerHTML = "<i></i> " + label;
    status.style.borderColor = running ? "#385747" : "";
    status.style.color = running ? "#a9e8c9" : "";
  }

  function buildDemo(topic) {
    return [
      { speaker:"MODERATOR", cls:"speaker-moderator", text:"Kita mulai diskusi. Topik hari ini: " + topic + " Saya akan meminta setiap peserta menyampaikan pandangan secara bergantian." },
      { speaker:"CHATGPT · PESERTA A", cls:"speaker-a", text:"Menurut saya, kecerdasan buatan lebih tepat dilihat sebagai alat yang mengubah cara kerja. Beberapa tugas rutin bisa otomatis, tetapi pekerjaan yang membutuhkan empati, tanggung jawab, dan penilaian manusia tetap penting." },
      { speaker:"GEMINI · PESERTA B", cls:"speaker-b", text:"Saya setuju sebagian, tetapi dampaknya bisa lebih luas. Bukan hanya tugas rutin; profesi yang pekerjaannya banyak berbasis informasi juga akan berubah. Karena itu, adaptasi keterampilan perlu dimulai sekarang." },
      { speaker:"MODERATOR", cls:"speaker-moderator", text:"Menarik. ChatGPT, bagaimana tanggapanmu terhadap kemungkinan perubahan pada pekerjaan berbasis informasi?" },
      { speaker:"CHATGPT · PESERTA A", cls:"speaker-a", text:"Perubahan itu nyata, tetapi tidak selalu berarti seluruh profesi hilang. Banyak pekerjaan akan dipecah menjadi tugas yang dikerjakan bersama AI dan tugas yang tetap membutuhkan manusia." },
      { speaker:"GEMINI · PESERTA B", cls:"speaker-b", text:"Saya ingin menambahkan bahwa manfaatnya tidak otomatis merata. Pendidikan, akses teknologi, dan kebijakan perusahaan akan menentukan siapa yang paling siap menghadapi perubahan." },
      { speaker:"MODERATOR", cls:"speaker-moderator", text:"Baik. Demo fondasi selesai. Pada tahap berikutnya, kalimat contoh ini akan diganti dengan respons langsung dari API AI." }
    ];
  }

  function speakNext() {
    if (!active || paused) return;
    if (index >= queue.length) {
      active = false;
      setStatus("Demo selesai");
      start.disabled = false;
      pause.disabled = true;
      stop.disabled = true;
      pause.textContent = "Ⅱ Jeda";
      return;
    }
    const item = queue[index];
    addMessage(item.speaker, item.cls, item.text);
    const utterance = new SpeechSynthesisUtterance(item.text);
    utterance.lang = "id-ID";
    const selectedName = item.cls === "speaker-b" ? voiceB.value : voiceA.value;
    const selected = voices.find(v => v.name === selectedName);
    if (selected) {
      utterance.voice = selected;
      utterance.lang = selected.lang;
    }
    utterance.rate = 0.96;
    utterance.pitch = item.cls === "speaker-b" ? 1.08 : 0.96;
    utterance.onend = () => {
      index += 1;
      if (active && !paused) window.setTimeout(speakNext, 650);
    };
    utterance.onerror = () => {
      index += 1;
      if (active && !paused) window.setTimeout(speakNext, 300);
    };
    synth.speak(utterance);
  }

  start.addEventListener("click", () => {
    if (!synth) {
      setStatus("TTS tidak didukung");
      $("voice-note").textContent = "Browser ini tidak mendukung Speech Synthesis. Coba Chrome Android.";
      return;
    }
    synth.cancel();
    const topic = $("topic").value.trim();
    if (!topic) {
      $("topic").focus();
      return;
    }
    queue = buildDemo(topic);
    index = 0;
    paused = false;
    active = true;
    transcript.innerHTML = "";
    turnCount.textContent = "0 giliran";
    start.disabled = true;
    pause.disabled = false;
    stop.disabled = false;
    pause.textContent = "Ⅱ Jeda";
    setStatus("Demo berjalan", true);
    speakNext();
  });

  pause.addEventListener("click", () => {
    if (!active) return;
    if (paused) {
      paused = false;
      synth.resume();
      pause.textContent = "Ⅱ Jeda";
      setStatus("Demo berjalan", true);
    } else {
      paused = true;
      synth.pause();
      pause.textContent = "▶ Lanjutkan";
      setStatus("Dijeda");
    }
  });

  stop.addEventListener("click", () => {
    if (synth) synth.cancel();
    active = false;
    paused = false;
    queue = [];
    start.disabled = false;
    pause.disabled = true;
    stop.disabled = true;
    pause.textContent = "Ⅱ Jeda";
    setStatus("Dihentikan");
  });

  if (synth) {
    loadVoices();
    synth.onvoiceschanged = loadVoices;
  } else {
    voiceA.innerHTML = "<option>Browser tidak mendukung TTS</option>";
    voiceB.innerHTML = "<option>Browser tidak mendukung TTS</option>";
  }
})();