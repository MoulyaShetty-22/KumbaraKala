package com.kumbara.kala.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kumbara.kala.data.model.Artisan
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.data.model.Workshop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class, Artisan::class, Workshop::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun artisanDao(): ArtisanDao
    abstract fun workshopDao(): WorkshopDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "kumbara_kala_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch { seedDatabase(database) }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            db.artisanDao().insertArtisan(Artisan(
                id = 1, name = "Ravi Kumbara", village = "Udupi, Karnataka",
                yearsOfCraft = 25,
                biography = "Third generation Kumbara potter from Udupi. My hands have shaped clay for over 25 years, continuing a tradition passed down by my grandfather.",
                phone = "", heritageTags = "Kumbara,Karnataka,Clay,Udupi"
            ))

            db.productDao().insertProduct(Product(
                name = "Clay Water Pot", price = 350.0, category = "Health",
                imagePath = "ASSET:product_water_pot",
                benefitCard = "Clay water pots naturally cool water through evaporation and release beneficial minerals, maintaining an alkaline pH that aids digestion and boosts immunity. Drinking from clay is a centuries-old Ayurvedic practice known to balance Pitta dosha.|||Terracotta is micro-porous — water seeps through tiny pores and evaporates, cooling contents by 6–8°C with zero electricity. Clay also neutralises water acidity naturally, making it safer than plastic bottles that leach BPA.|||The Kumbara potters of Karnataka have crafted these water vessels for over 3,000 years, passing down wheel-throwing and kiln-firing techniques through generations in villages across Udupi and Dharwad.",
                story = "I was born from the red earth along the banks of the Swarna river near Udupi. Skilled hands kneaded and shaped me on a spinning wheel, coaxing my round belly into being over many patient hours.\n\nI was fired for eight long hours in a wood kiln alongside dozens of my brothers and sisters, emerging stronger, redder, and full of quiet purpose.\n\nNow I sit in your kitchen, cooling your water the way nature intended — no electricity, no plastic, no chemicals. Just ancient earth wisdom working silently for you.\n\nEvery sip from me carries the minerals of Karnataka soil and the warmth of a potter's dedication. I am not just a pot. I am a living tradition.",
                careGuide = "HOW TO USE:\n• Fill with fresh drinking water daily\n• Keep in a cool shaded spot for best cooling effect\n• Soak for 24 hours before very first use\n\nHOW TO MAINTAIN:\n• Clean with a soft brush and plain water — no soap inside\n• Sun-dry once a week to prevent moss buildup\n• Do not scrub with steel wool or harsh cleaners\n\nDO'S:\n✓ Soak 24 hours before first use\n✓ Keep covered with a clay or wooden lid\n✓ Place on a wooden or cloth surface\n\nDON'TS:\n✗ Do not refrigerate — the clay cracks\n✗ Do not use detergent or soap inside\n✗ Do not place on metal or wet surfaces"
            ))

            db.productDao().insertProduct(Product(
                name = "Clay Curry Pot", price = 550.0, category = "Traditional",
                imagePath = "ASSET:product_curry_pot",
                benefitCard = "Clay curry pots cook food at an even gentle heat that preserves up to 100% more nutrients compared to metal cookware. The alkaline clay neutralises acidity in tomato curries and adds calcium, iron and magnesium to every meal.|||Clay distributes heat evenly through thick walls, preventing hot spots that destroy vitamins and enzymes. The alkaline surface balances pH of acidic ingredients, making slow-cooked food healthier and richer in minerals.|||Karnataka's Kumbara artisans have perfected the curry pot over generations. Districts like Dharwad, Hassan and Udupi each have distinct pot shapes suited to local recipes — from coastal fish curry to inland bisibelebath.",
                story = "I was shaped from deep black clay found near the foothills of the Western Ghats — clay that has absorbed centuries of monsoon rains and forest minerals.\n\nThe potter's thick-walled hands built me slowly and deliberately, walls designed to hold heat evenly and never let your curry lose its soul.\n\nI spent a full night in a blazing kiln at over 900 degrees, and emerged darkened, hardened, and ready for a lifetime of service.\n\nEvery dish slow-cooked inside me carries a memory of Karnataka's earth. Your rasam will taste deeper. Your sambar will smell richer. Your rice will carry a warmth that no steel pot can ever give.",
                careGuide = "HOW TO USE:\n• Always heat on low to medium flame only — never high\n• Add a little oil or water before first use on heat\n• Ideal for slow cooking curries, dal, rice and gravies\n• Use a heat diffuser between the flame and pot\n\nHOW TO MAINTAIN:\n• Wash with warm water and a soft cloth after fully cooling\n• Dry upside down in open air after washing\n• Season monthly by rubbing inside with coconut oil\n\nDO'S:\n✓ Season with oil before very first use\n✓ Allow to cool naturally before washing\n✓ Store in a dry airy place\n\nDON'TS:\n✗ Do not use on high flame — it cracks\n✗ Do not pour cold water into a hot pot\n✗ Do not put in dishwasher"
            ))

            db.productDao().insertProduct(Product(
                name = "Clay Diya (Lamp)", price = 30.0, category = "Eco",
                imagePath = "ASSET:product_diya",
                benefitCard = "Clay diyas burn mustard or sesame oil cleanly, releasing negative ions that purify indoor air. Research shows oil lamps reduce airborne bacteria by up to 94% compared to paraffin wax candles which release toxic soot and chemicals.|||Terracotta is 100% biodegradable — a clay diya returned to soil after use causes zero environmental harm and enriches the earth with minerals. The clay body absorbs excess oil for a steadier, longer-lasting flame.|||The humble diya is the most iconic product of Karnataka's Kumbara community. Made by the millions every Diwali season, each one is hand-pressed in a simple mould and sun-dried within a single day — pure traditional efficiency.",
                story = "I am the smallest member of the Kumbara family, yet I carry the greatest responsibility — light.\n\nI was pressed into shape by a potter's thumb in a busy courtyard in Dharwad one October morning, while the air already smelled of festival. I dried under the afternoon sun alongside hundreds of my brothers and sisters.\n\nWhen you fill me with oil and touch a flame to my cotton wick, something ancient happens. I do not just give light — I give warmth, memory, and the feeling that darkness will not last.\n\nI will return to the earth when my work is done. But the light I gave you — that stays forever.",
                careGuide = "HOW TO USE:\n• Fill 3/4 with mustard oil, sesame oil or pure ghee\n• Soak cotton wick in oil for 5 minutes before lighting\n• Place on a stable flat surface, away from curtains\n\nHOW TO MAINTAIN:\n• Wipe out ash and residue with a dry cloth after oil finishes\n• Store in a cool dry box when not in use\n• Reuse multiple times throughout a festival season\n\nDO'S:\n✓ Use natural oils — mustard, sesame or pure ghee\n✓ Place on a metal or clay tray to catch drips\n✓ Compost after use — 100% biodegradable\n\nDON'TS:\n✗ Do not leave unattended when lit\n✗ Do not use synthetic oils or kerosene\n✗ Do not pour water to extinguish — let oil burn out naturally"
            ))
        }
    }
}
