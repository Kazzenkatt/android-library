-keep class com.github.axet.androidlibrary.widgets.SearchView {*;}

-keep public class android.support.v7.widget.AppCompatButton {*;} # bugged AppCompatButton with 'final' variables. crash api 25.3.1

-dontwarn de.innosystec.unrar.**
-dontwarn org.apache.commons.**
