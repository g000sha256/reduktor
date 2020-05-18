-repackageclasses ''

-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Signature

-keepclassmembers class * {

    @com.google.gson.annotations.SerializedName <fields>;

}

-keepclassmembers class * implements android.os.Parcelable {

    public static final * CREATOR;

}