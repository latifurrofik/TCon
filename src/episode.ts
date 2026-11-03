export type Scene = {
  start: number;
  end: number;
  eyebrow: string;
  headline: string;
  caption: string;
  visual: 'phone' | 'person' | 'feed' | 'question';
  narration: string;
  image: string;
  audio: string;
};

export const episode01 = {
  title: 'KAMU SADAR SEDANG DISETIR?',
  subtitle: 'Siapa yang mengendalikan perhatianmu?',
  scenes: [
    {start: 0, end: 240, eyebrow: 'TCON • ADA APA SEBENARNYA?', headline: 'KAMU SADAR\\nSEDANG DISETIR?', caption: 'Bukan setir mobil. Tapi perhatianmu.', visual: 'phone', narration: 'Pernah buka media sosial cuma untuk melihat sebentar, tapi tiba-tiba waktu sudah berlalu begitu lama? Bisa jadi perhatianmu sedang diarahkan.', image: 'images/episode-01/scene-01.webp', audio: 'audio/episode-01/scene-01.mp3'},
    {start: 240, end: 510, eyebrow: 'SETIAP HARI', headline: 'KAMU MEMBUKA\\nSATU APLIKASI', caption: 'Awalnya cuma ingin melihat sebentar.', visual: 'person', narration: 'Awalnya hanya satu notifikasi. Lalu satu video muncul, disusul video berikutnya. Tanpa rencana, jempol terus bergerak.', image: 'images/episode-01/scene-02.webp', audio: 'audio/episode-01/scene-02.mp3'},
    {start: 510, end: 810, eyebrow: 'LALU TANPA SADAR', headline: 'WAKTU BERJALAN\\nLEBIH LAMA', caption: 'Satu video berubah menjadi puluhan.', visual: 'feed', narration: 'Platform digital memakai sistem rekomendasi untuk memilih konten yang kemungkinan menarik perhatianmu. Sistem itu belajar dari apa yang kamu tonton dan lakukan.', image: 'images/episode-01/scene-03.webp', audio: 'audio/episode-01/scene-03.mp3'},
    {start: 810, end: 1110, eyebrow: 'ADA POLANYA', headline: 'APA YANG MUNCUL\\nBUKAN KEBETULAN', caption: 'Sistem rekomendasi memilih apa yang kamu lihat.', visual: 'question', narration: 'Bukan berarti semua pilihanmu dikendalikan. Tetapi urutan konten yang kamu lihat dapat memengaruhi apa yang kamu pikirkan, rasakan, dan perhatikan.', image: 'images/episode-01/scene-04.webp', audio: 'audio/episode-01/scene-04.mp3'},
    {start: 1110, end: 1500, eyebrow: 'PIKIRKAN INI', headline: 'KAMU YANG PEGANG\\nKENDALI?', caption: 'Atau perhatianmu yang sedang diarahkan?', visual: 'phone', narration: 'Jadi, lain kali saat membuka feed, berhenti sejenak dan tanyakan: aku memang ingin melihat ini, atau hanya terus mengikuti rekomendasi? Kamu yang pegang kendali.', image: 'images/episode-01/scene-05.webp', audio: 'audio/episode-01/scene-05.mp3'}
  ] satisfies Scene[]
};
