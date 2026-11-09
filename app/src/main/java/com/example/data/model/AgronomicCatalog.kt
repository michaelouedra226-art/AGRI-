package com.example.data.model

data class CropCatalogItem(
    val id: String,
    val name: String,
    val scientificName: String,
    val category: String, // Céréales, Légumineuses, Maraîchage, Tubercules
    val seedRatePerHa: String,
    val spacing: String,
    val cycleDays: String,
    val soilSuitability: String,
    val wateringNeeds: String,
    val fertilization: String,
    val keyDiseases: List<String>,
    val harvestingTips: String,
    val cropCategoryCode: String // cereal, legume, vegetable, tuber
)

data class LivestockCatalogItem(
    val id: String,
    val species: String,
    val localName: String,
    val category: String,
    val feedingGuide: String,
    val housingGuide: String,
    val sanitaryCalendar: String,
    val commonRisks: String,
    val livestockCategoryCode: String // poultry, sheep, cattle
)

data class MarketPriceItem(
    val product: String,
    val market: String,
    val region: String,
    val priceFcfa: Long,
    val unit: String,
    val trend: String, // "hausse", "stable", "baisse"
    val dateUpdated: String
)

object AgronomicData {
    val crops = listOf(
        CropCatalogItem(
            id = "corn",
            name = "Maïs",
            scientificName = "Zea mays",
            category = "Céréales",
            seedRatePerHa = "20 à 25 kg / hectare",
            spacing = "80 cm entre lignes x 25 à 30 cm entre poquets (1 plant/poquet)",
            cycleDays = "80 à 110 jours (variétés Barka, Espoir, FBC6)",
            soilSuitability = "Sols profonds, bien drainés, riches en matière organique",
            wateringNeeds = "500 à 800 mm bien répartis. Très sensible au stress hydrique à la floraison",
            fertilization = "Fumier (5 à 10 t/ha) au labour. NPK 14-23-14 ou 15-15-15 (150 kg/ha au semis/15j) + Urée (100 kg/ha en 2 fractions)",
            keyDiseases = listOf("Chenille légionnaire d'automne", "Charbon du maïs", "Helminthosporiose", "Striga hermonthica"),
            harvestingTips = "Récolter lorsque les spathes brunissent et que le point noir apparaît à la base du grain. Sécher à moins de 13% d'humidité.",
            cropCategoryCode = "cereal"
        ),
        CropCatalogItem(
            id = "sorghum",
            name = "Sorgho",
            scientificName = "Sorghum bicolor",
            category = "Céréales",
            seedRatePerHa = "10 à 12 kg / hectare",
            spacing = "80 cm x 40 cm (démarier à 2-3 brins par poquet)",
            cycleDays = "90 à 120 jours (variétés Sariaso, Kapèlga)",
            soilSuitability = "Tolère les sols pauvres et argilo-sablonneux, sols ferrugineux",
            wateringNeeds = "400 à 600 mm. Très résistant à la sécheresse grâce au système racinaire profond",
            fertilization = "Compost en poquet (technique Zaï) + 100 kg/ha de NPK",
            keyDiseases = listOf("Anthracnose", "Mildiou du sorgho", "Punaises des panicules", "Striga"),
            harvestingTips = "Couper les panicules dès maturité complète, battage et séchage soigné pour éviter les moisissures.",
            cropCategoryCode = "cereal"
        ),
        CropCatalogItem(
            id = "millet",
            name = "Mil perlé",
            scientificName = "Pennisetum glaucum",
            category = "Céréales",
            seedRatePerHa = "5 à 8 kg / hectare",
            spacing = "80 cm à 1 m entre poquets (démarier à 3 plants)",
            cycleDays = "75 à 90 jours (variétés IKMP-5, SOSAT-C88)",
            soilSuitability = "Sols sablonneux sahéliens légers, faible fertilité",
            wateringNeeds = "300 à 500 mm. La culture céréalière la plus rustique face à l'aridité",
            fertilization = "Fumier décomposé en cordons pierreux ou zaï + 50 kg NPK",
            keyDiseases = listOf("Mineuse de l'épi", "Charbon", "Oiseaux granivores"),
            harvestingTips = "Récolte manuelle des chandelles. Stockage traditionnel en greniers aérés traités aux feuilles de neem.",
            cropCategoryCode = "cereal"
        ),
        CropCatalogItem(
            id = "cowpea",
            name = "Niébé (Haricot)",
            scientificName = "Vigna unguiculata",
            category = "Légumineuses",
            seedRatePerHa = "15 à 20 kg / hectare",
            spacing = "50 cm x 25 cm (pur) ou associé au sorgho/mil",
            cycleDays = "60 à 75 jours (variétés KVx, Tvx)",
            soilSuitability = "Tous types de sols, améliore la terre en fixant l'azote de l'air",
            wateringNeeds = "350 à 550 mm. Tolérant aux coups de chaleur",
            fertilization = "Engrais phosphaté (Superphosphate simple ou triple 50 kg/ha). Éviter l'excès d'azote.",
            keyDiseases = listOf("Pucerons noirs (Aphis craccivora)", "Thrips floricoles", "Bruches du niébé en stockage"),
            harvestingTips = "Récolter au fur et à mesure que les gousses sèchent. Traitement PICS (sacs hermétiques) contre les charançons.",
            cropCategoryCode = "legume"
        ),
        CropCatalogItem(
            id = "tomato",
            name = "Tomate",
            scientificName = "Solanum lycopersicum",
            category = "Maraîchage",
            seedRatePerHa = "200 à 300 g / hectare (en pépinière)",
            spacing = "80 cm entre lignes x 40 à 50 cm sur la ligne",
            cycleDays = "75 à 90 jours après repiquage (variétés Mongal, Nadira, Petomech)",
            soilSuitability = "Sols limoneux profonds et meubles, pH 6.0 à 6.8",
            wateringNeeds = "Arrosage régulier à la fraîcheur, idéalement goutte-à-goutte. Ne pas mouiller le feuillage.",
            fertilization = "Fumure de fond abondante (20 t/ha compost) + NPK maraîcher + apport de Calcium pour éviter le cul noir",
            keyDiseases = listOf("Mildiou", "Flétrissement bactérien", "Nématodes galligènes", "Mouche blanche (TYLCV)"),
            harvestingTips = "Cueillir au stade 'tournant' ou rouge ferme tôt le matin avec le pédoncule. Ranger en caissettes aérées.",
            cropCategoryCode = "vegetable"
        ),
        CropCatalogItem(
            id = "onion",
            name = "Oignon",
            scientificName = "Allium cepa",
            category = "Maraîchage",
            seedRatePerHa = "4 à 5 kg / hectare",
            spacing = "20 cm entre lignes x 10 cm entre plants",
            cycleDays = "90 à 110 jours après repiquage (variété Violet de Galmi)",
            soilSuitability = "Sols légers, bien aérés, sans cailloux",
            wateringNeeds = "Besoins modérés mais constants. Stopper l'irrigation 2 semaines avant la récolte.",
            fertilization = "Compost bien mûr + NPK 12-14-19 + apport de soufre pour le piquant et la conservation",
            keyDiseases = listOf("Thrips de l'oignon", "Pourriture blanche", "Alternariose"),
            harvestingTips = "Récolter quand 50% des fanes sont tombées. Ressuyage obligatoire au champ sous ombrage 10-15 jours.",
            cropCategoryCode = "vegetable"
        )
    )

    val livestock = listOf(
        LivestockCatalogItem(
            id = "poultry_local",
            species = "Poulets locaux (Pondeuses/Chair locales)",
            localName = "Poulet bicyclette / Volailles du Faso",
            category = "Aviculture",
            feedingGuide = "Mélange brisures de maïs/mil (60%), son de céréales (20%), farine de poisson/termites séchés (15%), coquilles d'huîtres pilées (5%). Eau propre et fraîche en permanence.",
            housingGuide = "Poulailler surélevé, bien ventilé avec grillage moustiquaire, perchoirs en bois, nids de ponte propres avec paille propre.",
            sanitaryCalendar = "Vaccin Newcastle (I-2 thermorésistant) à J7, J21 puis tous les 3 mois. Déparasitage interne mensuel. Poudre insecticide anti-poux.",
            commonRisks = "Peste aviaire (Newcastle), variole aviaire, coccidiose, prédateurs nocturnes.",
            livestockCategoryCode = "poultry"
        ),
        LivestockCatalogItem(
            id = "sheep_sahel",
            species = "Moutons du Sahel / Djallonké",
            localName = "Brebis et béliers d'embouche",
            category = "Petits ruminants",
            feedingGuide = "Fanes d'arachide et de niébé, tiges de maïs hachées, son de blé, tourteau de coton (200-300g/jour), bloc de sel minéralisé à lécher.",
            housingGuide = "Bergerie sèche et abritée des pluies et du vent froid, litière propre renouvelée souvent pour éviter le piétin.",
            sanitaryCalendar = "Vaccin contre la Peste des Petits Ruminants (PPR) une fois par an. Vermifugation au début et à la fin de l'hivernage.",
            commonRisks = "PPR, parasitose gastro-intestinale, météorisation (ballonnement), piétin.",
            livestockCategoryCode = "sheep"
        ),
        LivestockCatalogItem(
            id = "cattle_sahel",
            species = "Bovins (Zébus Peulh et Gourounsi)",
            localName = "Bœufs et vaches",
            category = "Gros ruminants",
            feedingGuide = "Pâturage naturel + foin de brousse, complément d'embouche : tourteau de coton, drêche de bière locale, paille traitée à l'urée.",
            housingGuide = "Parc clôturé avec zone d'ombre permanente, abreuvoirs cimentés nettoyés chaque semaine.",
            sanitaryCalendar = "Vaccination annuelle contre la Péripneumonie Contagieuse Bovine (PPCB) et le Charbon symptomatique. Déparasitage.",
            commonRisks = "PPCB, trypanosomiase (zone sud/mouches tsé-tsé), tiques, fièvre aphteuse.",
            livestockCategoryCode = "cattle"
        )
    )

    val marketPrices = listOf(
        MarketPriceItem("Maïs blanc", "Marché Sankaryare", "Ouagadougou", 20500, "Sac de 100 kg", "stable", "Cours de référence"),
        MarketPriceItem("Mil perlé", "Marché Central", "Bobo-Dioulasso", 24000, "Sac de 100 kg", "hausse", "Cours de référence"),
        MarketPriceItem("Sorgho blanc", "Marché de Koudougou", "Centre-Ouest", 21000, "Sac de 100 kg", "stable", "Cours de référence"),
        MarketPriceItem("Niébé blanc", "Marché de Pouytenga", "Centre-Est", 38500, "Sac de 100 kg", "hausse", "Cours de référence"),
        MarketPriceItem("Tomate fraîche", "Marché de Ouahigouya", "Nord", 18000, "Caisse de 50 kg", "baisse", "Cours de référence"),
        MarketPriceItem("Oignon violet", "Marché Central", "Bobo-Dioulasso", 28000, "Sac de 100 kg", "stable", "Cours de référence"),
        MarketPriceItem("Poulet de race locale", "Gare de Ouagadougou", "Centre", 3500, "L'unité (sujet vif)", "hausse", "Cours de référence")
    )
}
