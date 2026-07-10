# Room persistent library rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class androidx.room.util.TableInfo$Column { *; }
-keep class androidx.room.util.TableInfo$ForeignKey { *; }
-keep class androidx.room.util.TableInfo$Index { *; }

# Keep domain models if they are used for JSON serialization or Room entities
-keep class com.example.practica_desarrollomovil.domain.model.** { *; }
-keep class com.example.practica_desarrollomovil.data.local.entity.** { *; }
