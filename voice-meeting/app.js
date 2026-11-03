(() => {
  const $ = (id) => document.getElementById(id);
  const synth = window.speechSynthesis;
  const voiceA = $("voiceA");
  const voiceB = $("voiceB");
  const transcript = $("transcript");
  const start = $("start");
  const pause = $("pause");
  const stop = $("stop");
  const apiStatus = $("api-status");
  const apiBaseInput = $("apiBase");
  const checkApi = $("checkApi");
  let voices = [];
  let active = false;
  let paused = false;
  let runId = 0;
  let currentUtterance = null;
  let turn = 0;
  let configured = {openai:false, gemini:false};

  function baseUrl() {
    return apiBaseInput.value.trim().replace(/\/$/, "") || window.location.origin;
  }

  function setStatus(label, running = false) {
    $("status").innerHTML = "<i></i> " + label;
    $("status").style.borderColor = running ? "#385747" : "";
    $("status").style.color = running ? "#a9e8c9" : "";
  }

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
      options.forEach(voice => select.add(new Option(voice.name + " · " + voice.lang, voice.name)));
      const preferred = slot === 0 ? options[0] : (options.find(v => v.name !== options[0].name) || options[0]);
      select.value = options.some(v => v.name === previous) ? previous : preferred.name;
    });
    $("voice-note").textContent = idVoices.length
      ? idVoices.length + " suara Bahasa Indonesia terdeteksi. Pilih dua suara berbeda jika tersedia."
      : "Suara Bahasa Indonesia belum terdeteksi. Atur suara TTS di Android atau gunakan suara browser yang tersedia.";
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
    turn += 1;
    $("turn-count").textContent = turn + " giliran";
    article.scrollIntoView({behavior:"smooth", block:"nearest"});
  }

  async function requestJson(path, options = {}) {
    const response = await fetch(baseUrl() + path, {
      ...options,
      headers: {"Content-Type":"application/json", ...(options.headers || {})}
    });
    let data = {};
    try { data = await response.json(); } catch (_) {}
    if (!response.ok) throw new Error(data.detail || ("HTTP " + response.status));
    return data;
  }

  async function checkConnection() {
    checkApi.disabled = true;
    apiStatus.textContent = "Memeriksa…";
    try {
      const data = await requestJson("/api/health");
      configured = {
        openai: !!data.providers?.openai?.configured,
        gemini: !!data.providers?.gemini?.configured
      };
      const a = configured.openai ? "ChatGPT API key tersedia" : "ChatGPT API key belum diatur";
      const b = configured.gemini ? "Gemini API key tersedia" : "Gemini API key belum diatur";
      apiStatus.textContent = a + " · " + b;
      apiStatus.style.color = configured.openai && configured.gemini ? "#63d6aa" : "#f0c77b";
      if (configured.openai && configured.gemini) {
        setStatus("Kedua AI terhubung");
      } else {
        setStatus("API belum lengkap");
      }
      return configured.openai && configured.gemini;
    } catch (error) {
      apiStatus.textContent = "Backend tidak terjangkau";
      apiStatus.style.color = "#ff9aa8";
      setStatus("Backend belum terhubung");
      $("voice-note").textContent = error.message + ". Pastikan backend aktif dan alamatnya benar.";
      return false;
    } finally {
      checkApi.disabled = false;
    }
  }

  function waitIfPaused(id) {
    return new Promise(resolve => {
      const poll = () => {
        if (id !== runId || !active) return resolve(false);
        if (!paused) return resolve(true);
        window.setTimeout(poll, 150);
      };
      poll();
    });
  }

  async function speak(text, speaker, id) {
    if (!synth) return;
    const canContinue = await waitIfPaused(id);
    if (!canContinue) throw new Error("Diskusi dihentikan.");
    return new Promise((resolve, reject) => {
      const utterance = new SpeechSynthesisUtterance(text);
      currentUtterance = utterance;
      const isGemini = speaker === "gemini";
      const selectedName = isGemini ? voiceB.value : voiceA.value;
      const selected = voices.find(v => v.name === selectedName);
      utterance.lang = selected?.lang || "id-ID";
      if (selected) utterance.voice = selected;
      utterance.rate = 0.96;
      utterance.pitch = isGemini ? 1.08 : 0.96;
      utterance.onend = () => { currentUtterance = null; resolve(); };
      utterance.onerror = event => {
        currentUtterance = null;
        if (event.error === "canceled" || event.error === "interrupted") reject(new Error("Audio dihentikan."));
        else resolve();
      };
      synth.speak(utterance);
      if (paused) synth.pause();
    });
  }

  async function ask(provider, topic, prompt, history) {
    const path = provider === "openai" ? "/api/chat/openai" : "/api/chat/gemini";
    return requestJson(path, {
      method:"POST",
      body:JSON.stringify({topic, prompt, history})
    });
  }

  async function runMeeting(id) {
    const topic = $("topic").value.trim();
    const moderatorOpening = "Kita mulai diskusi dengan topik: " + topic + ". ChatGPT, silakan sampaikan pandangan pembuka. Setelah itu Gemini akan menanggapi.";
    addMessage("MODERATOR", "speaker-moderator", moderatorOpening);
    await speak(moderatorOpening, "openai", id);

    setStatus("ChatGPT sedang berpikir", true);
    const first = await ask("openai", topic, "Sampaikan pandangan pembuka sebagai peserta diskusi. Maksimal tiga paragraf pendek.", []);
    if (id !== runId || !active) return;
    addMessage("CHATGPT · PESERTA A", "speaker-a", first.text);
    await speak(first.text, "openai", id);

    setStatus("Gemini sedang menanggapi", true);
    const second = await ask("gemini", topic, "Tanggapi pandangan ChatGPT berikut. Setujui atau sanggah dengan alasan, lalu tambahkan sudut pandang baru.\n\nPandangan ChatGPT: " + first.text, [
      {role:"user", content:"Sampaikan pandangan pembuka tentang topik: " + topic},
      {role:"assistant", content:first.text}
    ]);
    if (id !== runId || !active) return;
    addMessage("GEMINI · PESERTA B", "speaker-b", second.text);
    await speak(second.text, "gemini", id);

    const closing = "Terima kasih. Tahap integrasi API berhasil: ChatGPT telah membuka diskusi dan Gemini telah menanggapi. Moderator otomatis dan putaran tanpa batas akan dibuat pada tahap berikutnya.";
    addMessage("MODERATOR", "speaker-moderator", closing);
    await speak(closing, "openai", id);
    if (id !== runId || !active) return;
    active = false;
    setStatus("Satu putaran selesai");
    start.disabled = false;
    pause.disabled = true;
    stop.disabled = true;
  }

  start.addEventListener("click", async () => {
    if (!synth) {
      setStatus("TTS tidak didukung");
      return;
    }
    const topic = $("topic").value.trim();
    if (!topic) { $("topic").focus(); return; }
    start.disabled = true;
    checkApi.disabled = true;
    setStatus("Memeriksa API…");
    const ready = await checkConnection();
    checkApi.disabled = false;
    if (!ready) {
      start.disabled = false;
      $("voice-note").textContent = "Atur kedua API key di environment server terlebih dahulu. Tidak perlu memasukkan key di halaman ini.";
      return;
    }
    synth.cancel();
    runId += 1;
    const id = runId;
    active = true;
    paused = false;
    turn = 0;
    transcript.innerHTML = "";
    $("turn-count").textContent = "0 giliran";
    pause.disabled = false;
    stop.disabled = false;
    pause.textContent = "Ⅱ Jeda";
    try {
      await runMeeting(id);
    } catch (error) {
      if (id === runId) {
        active = false;
        synth.cancel();
        setStatus("Terjadi kendala");
        addMessage("SISTEM", "speaker-moderator", error.message);
        start.disabled = false;
        pause.disabled = true;
        stop.disabled = true;
      }
    }
  });

  pause.addEventListener("click", () => {
    if (!active) return;
    paused = !paused;
    if (paused) {
      synth.pause();
      pause.textContent = "▶ Lanjutkan";
      setStatus("Dijeda");
    } else {
      synth.resume();
      pause.textContent = "Ⅱ Jeda";
      setStatus("Diskusi berjalan", true);
    }
  });

  stop.addEventListener("click", () => {
    runId += 1;
    active = false;
    paused = false;
    if (synth) synth.cancel();
    currentUtterance = null;
    start.disabled = false;
    pause.disabled = true;
    stop.disabled = true;
    pause.textContent = "Ⅱ Jeda";
    setStatus("Dihentikan");
  });

  checkApi.addEventListener("click", checkConnection);
  if (synth) {
    loadVoices();
    synth.onvoiceschanged = loadVoices;
  } else {
    voiceA.innerHTML = "<option>Browser tidak mendukung TTS</option>";
    voiceB.innerHTML = "<option>Browser tidak mendukung TTS</option>";
  }
})();