# Room, Firebase, Coil ve Compose kendi kurallarını artifact'larıyla birlikte getiriyor;
# buraya yalnızca R8'in kendi başına göremediği durumlar yazılır.

# Crash raporlarının okunabilir kalması için satır numarası; kaynak dosya adı gizlenir.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# kotlinx.serialization: @Serializable sınıfın serializer'ı isimle (Companion.serializer())
# bulunur, çağrısı kodda görünmez. R8 kullanılmıyor sanıp siler.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *** descriptor; }
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    static **$* *;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor, sınıf yolunda varsa kullandığı opsiyonel bağımlılıkları referans eder; yoksa da çalışır.
-dontwarn org.slf4j.**
-dontwarn io.ktor.network.sockets.**
-dontwarn kotlinx.coroutines.debug.**
