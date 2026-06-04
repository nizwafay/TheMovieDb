# Network DTOs are parsed with Moshi reflection. Keep them stable under R8.
-keep class com.papay.themoviedb.core.network.**.*Dto { *; }
-keep class com.papay.themoviedb.core.network.**.*ResponseDto { *; }

# Retrofit creates implementations for API interfaces at runtime.
-keep interface com.papay.themoviedb.core.network.**.*ApiService { *; }

# Keep Kotlin metadata used by Moshi's reflective adapter.
-keep class kotlin.Metadata { *; }
