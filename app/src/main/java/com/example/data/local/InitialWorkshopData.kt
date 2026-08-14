package com.example.data.local

object InitialWorkshopData {
    fun getInitialItems(): List<WorkshopItemEntity> {
        return listOf(
            // A-STL: مواد الحدادة
            WorkshopItemEntity(
                sku = "A-STL-001",
                name = "كمر حديد مجرى UPN 120",
                categoryCode = "A-STL",
                categoryName = "مواد الحدادة",
                storageLocation = "المنطقة A - حامل الكمرات R-01",
                technicalSpecs = "مقطع UPN، ارتفاع 120 مم، طول 6 متر، صلب إنشائي St-37",
                quantity = 14,
                unit = "حبة (6 م)",
                defaultQuantity = "1 حبة",
                minQuantityAlert = 4,
                notes = "خاص بهياكل القواعد والجمالونات"
            ),
            WorkshopItemEntity(
                sku = "A-STL-002",
                name = "ماسورة حديد أسود غير ملحومة 2 بوصة",
                categoryCode = "A-STL",
                categoryName = "مواد الحدادة",
                storageLocation = "المنطقة A - رف الأنابيب P-03",
                technicalSpecs = "قطر 2 بوصة (Sched 40)، سمك 3.9 مم، طول 6 م",
                quantity = 28,
                unit = "ماسورة",
                defaultQuantity = "1 ماسورة",
                minQuantityAlert = 6,
                notes = "استخدامات التصنيع والضغط العالي"
            ),
            WorkshopItemEntity(
                sku = "A-STL-003",
                name = "لوح صاج حديد مدرفل على الساخن 3 مم",
                categoryCode = "A-STL",
                categoryName = "مواد الحدادة",
                storageLocation = "المنطقة A - طبلية الألواح S-02",
                technicalSpecs = "أبعاد 1250 * 2500 مم، سمك 3 مم، صلب كربوني",
                quantity = 9,
                unit = "لوح",
                defaultQuantity = "1 لوح",
                minQuantityAlert = 3,
                notes = "قص بالبلازما وتشكيل القواعد"
            ),

            // B-PLV: مستلزمات السباكة
            WorkshopItemEntity(
                sku = "B-PLV-001",
                name = "محبس بليّة نحاسي صناعي 1 بوصة PN25",
                categoryCode = "B-PLV",
                categoryName = "مستلزمات السباكة",
                storageLocation = "المنطقة B - رف الصمامات V-04",
                technicalSpecs = "مقاس 1 بوصة، سن داخلي/داخلي، ضغط 25 بار، يد حديد مغطاة",
                quantity = 18,
                unit = "قطعة",
                defaultQuantity = "1 قطعة",
                minQuantityAlert = 5,
                notes = "خطوط الهواء المضغوط وشبكات التبريد"
            ),
            WorkshopItemEntity(
                sku = "B-PLV-002",
                name = "كوع حديد مجلفن 90 درجة 1.5 بوصة",
                categoryCode = "B-PLV",
                categoryName = "مستلزمات السباكة",
                storageLocation = "المنطقة B - صندوق الوصلات F-08",
                technicalSpecs = "مقاس 1.5 بوصة، سن BSPT، مجلفن على الساخن ضد الصدأ",
                quantity = 35,
                unit = "قطعة",
                defaultQuantity = "1 قطعة",
                minQuantityAlert = 10,
                notes = "توصيلات شبكات السباكة المعدنية"
            ),
            WorkshopItemEntity(
                sku = "B-PLV-003",
                name = "جلبة تخفيض سن نحاس 1 بوصة إلى 3/4 بوصة",
                categoryCode = "B-PLV",
                categoryName = "مستلزمات السباكة",
                storageLocation = "المنطقة B - درج الوصلات النحاسية B-02",
                technicalSpecs = "سن خارجي 1 بوصة * سن داخلي 3/4 بوصة، سبيكة نحاس أصفر CW617N",
                quantity = 42,
                unit = "قطعة",
                defaultQuantity = "1 قطعة",
                minQuantityAlert = 12,
                notes = "لتحويل مقاسات التوصيل السريع"
            ),

            // C-TLS: أدوات العمل اليدوية والصغيرة
            WorkshopItemEntity(
                sku = "C-TLS-001",
                name = "صاروخ جلخ وقطع يدوي 7 بوصة 2200W",
                categoryCode = "C-TLS",
                categoryName = "أدوات العمل اليدوية والصغيرة",
                storageLocation = "المنطقة C - دولاب الصواريخ والعدد E-01",
                technicalSpecs = "قدرة 2200 واط، 8500 دورة/دقيقة، مقاس القرص 180 مم (7 بوصة)، 220V",
                quantity = 4,
                unit = "جهاز",
                defaultQuantity = "1 جهاز",
                minQuantityAlert = 2,
                notes = "لأعمال تجهيز وتنعيم درز اللحام"
            ),
            WorkshopItemEntity(
                sku = "C-TLS-002",
                name = "قدمة ورنية رقمية (كليبر) 150 مم قياس دقيق",
                categoryCode = "C-TLS",
                categoryName = "أدوات العمل اليدوية والصغيرة",
                storageLocation = "المنطقة C - دولاب أدوات القياس M-01",
                technicalSpecs = "نطاق 0-150 مم، دقة 0.01 مم، ستانلس ستيل مصلد، شاشة LCD",
                quantity = 5,
                unit = "علبة",
                defaultQuantity = "1 طقم",
                minQuantityAlert = 2,
                notes = "أدوات القياس المعتمدة لمراقبة الجودة"
            ),
            WorkshopItemEntity(
                sku = "C-TLS-003",
                name = "طقم مفاتيح بلدي ومشرشر من 6 إلى 32 مم",
                categoryCode = "C-TLS",
                categoryName = "أدوات العمل اليدوية والصغيرة",
                storageLocation = "المنطقة C - لوح التعليق الجداري W-03",
                technicalSpecs = "كروم فاناديوم (Cr-V)، 26 قطعة، معيار DIN 3113",
                quantity = 3,
                unit = "طقم",
                defaultQuantity = "1 طقم",
                minQuantityAlert = 1,
                notes = "للصيانة الميكانيكية وتجميع الهياكل"
            ),

            // D-WLD: ماكينات اللحام ومستهلكاتها
            WorkshopItemEntity(
                sku = "D-WLD-001",
                name = "سلك لحام كهربائي AWS E7018 مقاس 3.2 مم",
                categoryCode = "D-WLD",
                categoryName = "ماكينات اللحام ومستهلكاتها",
                storageLocation = "المنطقة D - فرن وأرفف الأسلاك W-12",
                technicalSpecs = "قطر 3.2 مم، طول 350 مم، هيدروجين منخفض، عبوة 5 كجم",
                quantity = 22,
                unit = "عبوة (5 كجم)",
                defaultQuantity = "1 عبوة",
                minQuantityAlert = 6,
                notes = "للحام الإنشائي عالي الإجهاد والأنابيب"
            ),
            WorkshopItemEntity(
                sku = "D-WLD-002",
                name = "بكرة سلك لحام ميج ER70S-6 مقاس 1.0 مم",
                categoryCode = "D-WLD",
                categoryName = "ماكينات اللحام ومستهلكاتها",
                storageLocation = "المنطقة D - مستودع الميج والسلك M-04",
                technicalSpecs = "سلك صلب مكسو نحاس، قطر 1.0 مم، وزن البكرة 15 كجم",
                quantity = 11,
                unit = "بكرة (15 كجم)",
                defaultQuantity = "1 بكرة",
                minQuantityAlert = 3,
                notes = "خاص بماكينات اللحام نصف الأوتوماتيكية MIG/MAG"
            ),
            WorkshopItemEntity(
                sku = "D-WLD-003",
                name = "بنسة لحام احترافية 500 أمبير شديدة التحمل",
                categoryCode = "D-WLD",
                categoryName = "ماكينات اللحام ومستهلكاتها",
                storageLocation = "المنطقة D - رف ملحقات اللحام A-05",
                technicalSpecs = "تيار تشغيل 500A، مقبض عازل حراري مقاوم للصدمات، نحاس نقي",
                quantity = 8,
                unit = "قطعة",
                defaultQuantity = "1 قطعة",
                minQuantityAlert = 2,
                notes = "كابلات التوصيل الأرضي والكهربائي"
            ),

            // E-CNS: المواد الاستهلاكية والعدّة
            WorkshopItemEntity(
                sku = "E-CNS-001",
                name = "قرص قطع حديد صلب 9 بوصة (230 مم)",
                categoryCode = "E-CNS",
                categoryName = "المواد الاستهلاكية والعدّة",
                storageLocation = "المنطقة E - رف الأقراص الاستهلاكية D-01",
                technicalSpecs = "مقاس 230 * 2.5 * 22.23 مم، أكسيد ألمونيوم معزز بشبكتين فايبر",
                quantity = 65,
                unit = "قرص",
                defaultQuantity = "5 أقراص",
                minQuantityAlert = 15,
                notes = "معدل استهلاك يومي عالي لصواريخ القطع"
            ),
            WorkshopItemEntity(
                sku = "E-CNS-002",
                name = "مسمار صلب مسدس 8.8 مقاس M12 * 60 مم مع صامولة",
                categoryCode = "E-CNS",
                categoryName = "المواد الاستهلاكية والعدّة",
                storageLocation = "المنطقة E - درج المسامير والصواميل N-14",
                technicalSpecs = "قطر M12، طول 60 مم، صلب 8.8 عالي المقاومة، سن متري 1.75 مم",
                quantity = 180,
                unit = "طقم (مسمار+صامولة+وردة)",
                defaultQuantity = "10 أطقم",
                minQuantityAlert = 50,
                notes = "تثبيت كمرات وركائز المصنع"
            ),
            WorkshopItemEntity(
                sku = "E-CNS-003",
                name = "عبوة رذاذ مانع التصاق طرطشة اللحام (Anti-Spatter)",
                categoryCode = "E-CNS",
                categoryName = "المواد الاستهلاكية والعدّة",
                storageLocation = "المنطقة E - خزانة الكيماويات والدهانات C-03",
                technicalSpecs = "سعة 400 مل، تركيبة مائية خالية من السيليكون قابلة للطلاء بعدها",
                quantity = 16,
                unit = "عبوة رذاذ",
                defaultQuantity = "1 عبوة",
                minQuantityAlert = 4,
                notes = "لحماية فوهات شعلات الميج والأسطح المجاورة"
            )
        )
    }
}
