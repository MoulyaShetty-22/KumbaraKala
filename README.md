# 🏺 KumbaraKala — Android App
**Karnataka's Clay Heritage, Reimagined**
MindMatrix VTU Internship Programme · Project 22

---

## ✅ Setup Instructions

### Step 1 — Open in Android Studio
1. Extract the ZIP file
2. Open **Android Studio** (Hedgehog or later)
3. Click **File → Open** and select the `KumbaraKala` folder
4. Wait for Gradle sync to complete (may take 2–5 minutes on first run)

---

### Step 2 — Add Your Gemini API Key (REQUIRED)

The app uses **Google Gemini 2.0 Flash** for AI features. You need a free API key.

**Get a free key:**
1. Go to [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Click **Create API Key**
3. Copy the key

**Add to the project:**
1. Open the file `local.properties` in the project root
2. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual key:
```
GEMINI_API_KEY=AIzaSy...your_key_here...
sdk.dir=/path/to/your/Android/Sdk
```
3. Also update `sdk.dir` to your Android SDK path (Android Studio usually fills this automatically)

> ⚠️ Never commit your API key to Git. The `local.properties` file is gitignored by default.

---

### Step 3 — Run the App
1. Connect an Android device (API 26+) or start an emulator
2. Click the **Run ▶** button in Android Studio
3. The app will install and launch

---

## 📱 App Features

| Feature | How to Use |
|---|---|
| **Add Product** | Home → FAB (+) → Fill name, price, category, take/pick photo |
| **Generate Benefit Card** | Product Detail → ✨ Generate Benefit Card tab → Gemini Vision AI generates card |
| **Object Story** | Product Detail → 📖 Story tab → Generate Story |
| **Care Guide** | Product Detail → 🛡 Care Guide tab → Generate Care Guide |
| **Share on WhatsApp** | Benefit Card screen → 💬 Share on WhatsApp |
| **Save to Gallery** | Benefit Card screen → 💾 Save to Gallery |
| **Read Aloud (TTS)** | Benefit Card screen → 🔊 Read Aloud |
| **Language Toggle** | Product Detail → EN/KN button (switches Gemini output language) |
| **Artisan Bio** | Bottom Nav → Artisan tab → Fill profile, phone number |
| **Earnings Calculator** | Artisan tab → Enter pots/week & price → Calculate |
| **Add Workshop** | Bottom Nav → Workshops → FAB (+) |
| **Book via WhatsApp** | Workshops → Book via WhatsApp button |

---

## 🏗️ Architecture

```
KumbaraKala/
├── data/
│   ├── model/          # Product, Artisan, Workshop, GeminiModels
│   ├── db/             # Room DAOs, AppDatabase, RetrofitClient, GeminiApiService
│   └── repository/     # ProductRepository, ArtisanRepository, WorkshopRepository, GeminiRepository
├── viewmodel/          # ProductViewModel, ArtisanViewModel, WorkshopViewModel
└── ui/
    ├── home/           # HomeFragment + ProductAdapter
    ├── product/        # AddEditProductActivity, ProductDetailActivity
    ├── card/           # BenefitCardActivity (Canvas card generation)
    ├── artisan/        # ArtisanFragment
    ├── workshop/       # WorkshopFragment, WorkshopAdapter, AddWorkshopActivity
    └── MainActivity
```

- **Language:** Kotlin
- **Architecture:** MVVM + LiveData + ViewModel
- **Database:** Room DB (3 tables: products, artisan, workshops)
- **AI:** Google Gemini 2.0 Flash (text + vision multimodal)
- **Networking:** Retrofit 2 + OkHttp
- **Image Loading:** Glide
- **QR Code:** ZXing
- **Min SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34 (Android 14)

---

## 🎨 Colour Palette

| Colour | Hex | Usage |
|---|---|---|
| Deep Terracotta | `#7B2D00` | Primary, toolbar, FAB |
| Warm Brown | `#A03A10` | Accent, secondary |
| Cream | `#F9EDE5` | Background |
| Cream Surface | `#FFF5EE` | Cards, surfaces |
| Brown Text | `#3E1F00` | Body text |

---

## 📋 Checklist (PRD Success Criteria)

- [x] Benefit Card generated via Gemini Vision — real image sent to API
- [x] Card contains product image background + text overlay via Canvas API
- [x] Artisan name and phone auto-populate on cards and WhatsApp messages
- [x] Story from Object Memory — first-person narrative from clay item perspective
- [x] Use It Right Care Guide — product-specific, cached locally after first generation
- [x] Full CRUD on products — persisted to Room DB across restarts
- [x] Workshop screen — all fields shown, WhatsApp booking, Fully Booked badge
- [x] Earnings Calculator — direct vs middleman weekly income comparison
- [x] Language toggle — English / Kannada via Gemini re-call
- [x] TTS Read Aloud — en_IN and kn_IN locales
- [x] Save card to Gallery — MediaStore API
- [x] QR code on card — ZXing from artisan WhatsApp number
- [x] Daily AI clay fact — cached per day, shown on home screen banner
- [x] Terracotta colour palette throughout all screens
- [x] Min SDK API 26+

---

## 🔑 Key Files

| File | Purpose |
|---|---|
| `local.properties` | Put your Gemini API key here |
| `GeminiRepository.kt` | All Gemini API prompts |
| `BenefitCardActivity.kt` | Canvas card rendering + share/save/TTS |
| `AppDatabase.kt` | Room DB setup |

---

*KumbaraKala · Empowering Karnataka's Clay Artisans Through Technology*
