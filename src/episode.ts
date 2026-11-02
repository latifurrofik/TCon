export type Scene = {
  start: number;
  end: number;
  eyebrow: string;
  headline: string;
  caption: string;
  visual: 'phone' | 'person' | 'feed' | 'question';
};

export const episode01 = {
  title: 'KAMU SADAR SEDANG DISETIR?',
  subtitle: 'Siapa yang mengendalikan perhatianmu?',
  scenes: [
    {start: 0, end: 240, eyebrow: 'TCON • ADA APA SEBENARNYA?', headline: 'KAMU SADAR\nSEDANG DISETIR?', caption: 'Bukan setir mobil. Tapi perhatianmu.', visual: 'phone'},
    {start: 240, end: 510, eyebrow: 'SETIAP HARI', headline: 'KAMU MEMBUKA\nSATU APLIKASI', caption: 'Awalnya cuma ingin melihat sebentar.', visual: 'person'},
    {start: 510, end: 810, eyebrow: 'LALU TANPA SADAR', headline: 'WAKTU BERJALAN\nLEBIH LAMA', caption: 'Satu video berubah menjadi puluhan.', visual: 'feed'},
    {start: 810, end: 1110, eyebrow: 'ADA POLANYA', headline: 'APA YANG MUNCUL\nBUKAN KEBETULAN', caption: 'Sistem rekomendasi memilih apa yang kamu lihat.', visual: 'question'},
    {start: 1110, end: 1500, eyebrow: 'PIKIRKAN INI', headline: 'KAMU YANG PEGANG\nKENDALI?', caption: 'Atau perhatianmu yang sedang diarahkan?', visual: 'phone'}
  ] satisfies Scene[]
};
