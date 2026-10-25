# LiquidGlass TV — Android TV launcher starter

Launcher Android native sederhana untuk diuji pada Sharp AQUOS-TVX22A (Android TV 11).

## Fitur
- Home screen gelap dengan panel transparan bergaya glass.
- Jam dan tanggal.
- Mendeteksi aplikasi yang dapat diluncurkan dari sistem (Leanback/Launcher).
- Navigasi dasar dengan remote: arah, OK, Down untuk semua aplikasi, Back untuk kembali.
- Tidak meminta izin internet dan tidak mengumpulkan data.

## Build APK
1. Buka proyek ini di Android Studio versi yang mendukung Android Gradle Plugin 8.6.1.
2. Pastikan Android SDK Platform 35 dan JDK 17 tersedia.
3. Tunggu Gradle sync selesai.
4. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK hasil build ada di `app/build/outputs/apk/debug/app-debug.apk`.

## Instalasi uji
- Salin APK ke USB atau gunakan metode instalasi Android TV yang kamu kuasai.
- Izinkan instalasi dari sumber yang sesuai hanya jika kamu memahami sumber APK.
- Jalankan **LiquidGlass TV** dari daftar aplikasi untuk mengujinya terlebih dahulu.

## Menjadikan launcher utama
Manifest sudah mendaftarkan intent HOME, tetapi beberapa firmware Android TV tidak menampilkan pemilih launcher atau tetap memaksa launcher bawaan. Coba dahulu tanpa mengubah sistem. Jika menu pemilih Home tersedia, pilih LiquidGlass TV. Jangan menonaktifkan launcher bawaan sebelum launcher pengganti terbukti berfungsi.

## Batasan versi awal
- Ini adalah starter project/source code, bukan APK yang sudah dibangun atau diuji pada perangkat fisik.
- Efek kaca menggunakan panel transparan ringan, bukan blur real-time, untuk mengurangi beban GPU.
- Dock saat ini menampilkan enam aplikasi pertama menurut urutan alfabet. Pengurutan favorit yang bisa dikustomisasi, wallpaper, HDMI input, dan penanganan tombol Home khusus belum diterapkan.
- Android 11 membatasi visibilitas paket; daftar aplikasi dapat berbeda sesuai firmware dan aplikasi yang terpasang.

## Referensi
- Material TV Launcher: https://github.com/0xRampey/material-tv-launcher
- Android TV app basics: https://developer.android.com/training/tv/get-started/create
