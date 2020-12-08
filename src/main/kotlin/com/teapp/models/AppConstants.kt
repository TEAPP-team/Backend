package main.kotlin.com.teapp.models

enum class AppStrings(val value:String) {
    COOKIE_NAME("TEAPP_COOKIE"),
    ID_NAME("id"),
    LOGIN_NAME("login"),
    PASSWORD_NAME("password"),
    EMPTY_STRING("");
}

enum class AppInts(val value: Int) {
    ID_INDEX(0),
    LOGIN_INDEX(1),
    PASSWORD_INDEX(2);
}