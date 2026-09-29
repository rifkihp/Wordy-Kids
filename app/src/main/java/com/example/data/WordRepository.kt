package com.example.data

import androidx.compose.ui.graphics.Color

enum class Category(val displayName: String, val emoji: String, val badgeColor: Color) {
    ALL("All Words", "🌟", Color(0xFFFF7043)),
    ANIMALS("Animals", "🦁", Color(0xFFFFCA28)),
    TRANSPORT("Transport", "🚗", Color(0xFF42A5F5)),
    FLAGS("Flags", "🚩", Color(0xFFEF5350)),
    FRUITS("Fruits", "🍎", Color(0xFF66BB6A)),
    NATURE("Nature", "🌿", Color(0xFFAB47BC))
}

data class WordItem(
    val id: String,
    val word: String,
    val category: Category,
    val emoji: String,
    val hint: String,
    val countryCode: String? = null
)

object WordRepository {
    val wordList = listOf(
        // Animals
        WordItem("a1", "LION", Category.ANIMALS, "🦁", "King of the jungle with a big mane!"),
        WordItem("a2", "TIGER", Category.ANIMALS, "🐯", "Wild cat with orange and black stripes!"),
        WordItem("a3", "ELEPHANT", Category.ANIMALS, "🐘", "Huge animal with a long trunk!"),
        WordItem("a4", "MONKEY", Category.ANIMALS, "🐵", "Loves eating bananas and swinging on trees!"),
        WordItem("a5", "ZEBRA", Category.ANIMALS, "🦓", "Looks like a horse with black and white stripes!"),
        WordItem("a6", "GIRAFFE", Category.ANIMALS, "🦒", "Tallest animal with a very long neck!"),
        WordItem("a7", "PENGUIN", Category.ANIMALS, "🐧", "Cute bird that loves ice and swims fast!"),
        WordItem("a8", "PANDA", Category.ANIMALS, "🐼", "Black and white bear that eats bamboo!"),
        WordItem("a9", "DOLPHIN", Category.ANIMALS, "🐬", "Smart ocean animal that jumps high!"),
        WordItem("a10", "RABBIT", Category.ANIMALS, "🐰", "Hopping friend with long fluffy ears!"),
        WordItem("a11", "FROG", Category.ANIMALS, "🐸", "Green creature that croaks and hops!"),
        WordItem("a12", "KOALA", Category.ANIMALS, "🐨", "Sleepy tree climber from Australia!"),
        WordItem("a13", "OWL", Category.ANIMALS, "🦉", "Wise night bird with big round eyes!"),
        WordItem("a14", "FOX", Category.ANIMALS, "🦊", "Clever animal with a bushy red tail!"),
        WordItem("a15", "DOG", Category.ANIMALS, "🐶", "Man's best friend that barks playfully!"),
        WordItem("a16", "CAT", Category.ANIMALS, "🐱", "Soft furry pet that says meow!"),

        // Transport
        WordItem("t1", "CAR", Category.TRANSPORT, "🚗", "Four-wheeled vehicle for family trips!"),
        WordItem("t2", "BUS", Category.TRANSPORT, "🚌", "Large vehicle that carries many passengers!"),
        WordItem("t3", "TRAIN", Category.TRANSPORT, "🚂", "Runs on tracks and goes choo-choo!"),
        WordItem("t4", "AIRPLANE", Category.TRANSPORT, "✈️", "Flies high in the sky through the clouds!"),
        WordItem("t5", "ROCKET", Category.TRANSPORT, "🚀", "Shoots into outer space to reach the moon!"),
        WordItem("t6", "BICYCLE", Category.TRANSPORT, "🚲", "Has two wheels and pedals to ride!"),
        WordItem("t7", "HELICOPTER", Category.TRANSPORT, "🚁", "Flies using spinning blades on top!"),
        WordItem("t8", "SHIP", Category.TRANSPORT, "🚢", "Large boat traveling across deep oceans!"),
        WordItem("t9", "TRACTOR", Category.TRANSPORT, "🚜", "Strong farm vehicle with big wheels!"),
        WordItem("t10", "AMBULANCE", Category.TRANSPORT, "🚑", "Emergency vehicle with a siren!"),
        WordItem("t11", "SCOOTER", Category.TRANSPORT, "🛵", "Fun two-wheeled ride around town!"),
        WordItem("t12", "TAXI", Category.TRANSPORT, "🚕", "Yellow car you hire to go anywhere!"),

        // Flags & Countries
        WordItem("f1", "INDONESIA", Category.FLAGS, "🇮🇩", "Beautiful island nation with red and white flag!"),
        WordItem("f2", "JAPAN", Category.FLAGS, "🇯🇵", "Land of the rising sun with a red circle flag!"),
        WordItem("f3", "BRAZIL", Category.FLAGS, "🇧🇷", "Famous for samba and green-yellow flag!"),
        WordItem("f4", "CANADA", Category.FLAGS, "🇨🇦", "Flag features a red maple leaf!"),
        WordItem("f5", "FRANCE", Category.FLAGS, "🇫🇷", "Flag with blue, white, and red vertical stripes!"),
        WordItem("f6", "ITALY", Category.FLAGS, "🇮🇹", "Country of pizza with green, white, red flag!"),
        WordItem("f7", "KOREA", Category.FLAGS, "🇰🇷", "Flag has a yin-yang circle and black trigrams!"),
        WordItem("f8", "EGYPT", Category.FLAGS, "🇪🇬", "Home of ancient pyramids and golden eagle flag!"),
        WordItem("f9", "SPAIN", Category.FLAGS, "🇪🇸", "Flag with bright yellow and red colors!"),
        WordItem("f10", "MEXICO", Category.FLAGS, "🇲🇽", "Flag with green, white, red and an eagle!"),

        // Fruits
        WordItem("r1", "APPLE", Category.FRUITS, "🍎", "Sweet crunchy red fruit that keeps doctor away!"),
        WordItem("r2", "BANANA", Category.FRUITS, "🍌", "Yellow curved fruit loved by monkeys!"),
        WordItem("r3", "CHERRY", Category.FRUITS, "🍒", "Small round red fruit, often comes in pairs!"),
        WordItem("r4", "GRAPE", Category.FRUITS, "🍇", "Juicy purple berries that grow in bunches!"),
        WordItem("r5", "MANGO", Category.FRUITS, "🥭", "Tropical sweet fruit with a golden orange pulp!"),
        WordItem("r6", "ORANGE", Category.FRUITS, "🍊", "Citrus fruit packed with Vitamin C!"),
        WordItem("r7", "DONUT", Category.FRUITS, "🍩", "Ring-shaped sweet treat with colorful sprinkles!"),
        WordItem("r8", "STRAWBERRY", Category.FRUITS, "🍓", "Heart-shaped red berry with tiny seeds!"),
        WordItem("r9", "WATERMELON", Category.FRUITS, "🍉", "Big green fruit with juicy red inside!"),

        // Nature
        WordItem("n1", "SUN", Category.NATURE, "☀️", "Gives warmth and light to Earth during day!"),
        WordItem("n2", "MOON", Category.NATURE, "🌙", "Shines bright in the night sky!"),
        WordItem("n3", "STAR", Category.NATURE, "⭐", "Twinkles high above in the night!"),
        WordItem("n4", "FLOWER", Category.NATURE, "🌸", "Smells sweet and blooms in gardens!"),
        WordItem("n5", "TREE", Category.NATURE, "🌳", "Has green leaves and provides shade!"),
        WordItem("n6", "RAINBOW", Category.NATURE, "🌈", "Colorful arch in the sky after rain!"),
        WordItem("n7", "CLOUD", Category.NATURE, "☁️", "Fluffy white shapes floating in the sky!"),
        WordItem("n8", "OCEAN", Category.NATURE, "🌊", "Vast blue body of salty water with waves!")
    )
}
