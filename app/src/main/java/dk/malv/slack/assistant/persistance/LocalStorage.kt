package dk.malv.slack.assistant.persistance

import android.content.SharedPreferences
import android.location.Location
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A class for managing local storage using SharedPreferences.
 *
 * @property sharedPref The instance of SharedPreferences to be used for local storage operations
 */
class LocalStorage @Inject constructor(private val sharedPref: SharedPreferences) {

    /**
     * Save an integer value to SharedPreferences.
     *
     * @param key The key under which the value will be saved
     * @param value The integer value to be saved
     */
    fun saveInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    /**
     * Retrieve an integer value from SharedPreferences.
     *
     * @param key The key under which the value is saved
     * @param defaultValue The default value to be returned if the key is not found
     * @return The retrieved integer value, or the default value if the key is not found
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    /**
     * Save a string value to SharedPreferences.
     *
     * @param key The key under which the value will be saved
     * @param value The string value to be saved
     */
    fun saveString(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    /**
     * Retrieve a string value from SharedPreferences.
     *
     * @param key The key under which the value is saved
     * @param defaultValue The default value to be returned if the key is not found
     * @return The retrieved string value, or the default value if the key is not found
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Save a boolean value to SharedPreferences.
     *
     * @param key The key under which the value will be saved
     * @param value The boolean value to be saved
     */
    fun saveBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    /**
     * Retrieve a boolean value from SharedPreferences.
     *
     * @param key The key under which the value is saved
     * @param defaultValue The default value to be returned if the key is not found
     * @return The retrieved boolean value, or the default value if the key is not found
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    /**
     * Save a float value to SharedPreferences.
     *
     * @param key The key under which the value will be saved
     * @param value The float value to be saved
     */
    fun saveFloat(key: String, value: Float) {
        with(sharedPref.edit()) {
            putFloat(key, value)
            apply()
        }
    }

    /**
     * Retrieve a float value from SharedPreferences.
     *
     * @param key The key under which the value is saved
     * @param defaultValue The default value to be returned if the key is not found
     * @return The retrieved float value, or the default value if the key is not found
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPref.getFloat(key, defaultValue)
    }

    /**
     * Save a Map of String, String to SharedPreferences.
     *
     * @param key The key under which the map will be saved
     * @param map The map of String, String to be saved
     */
    fun saveStringMap(key: String, map: Map<String, String>) {
        with(sharedPref.edit()) {
            putString(key, Json.encodeToString(map))
            apply()
        }
    }

    /**
     * Save a List of String to SharedPreferences.
     *
     * @param key The key under which the list will be saved
     * @param list The list of String to be saved
     */
    fun saveStringList(key: String, list: List<String>) {
        with(sharedPref.edit()) {
            putString(key, Json.encodeToString(list))
            apply()
        }
    }

    /**
     * Retrieve a List of String from SharedPreferences.
     *
     * @param key The key under which the list is saved
     * @return The retrieved list of String, or an empty list if the key is not found
     */
    fun getStringList(key: String): List<String> {
        val json = sharedPref.getString(key, null)
        return if (json != null) {
            Json.decodeFromString<List<String>>(json)
        } else {
            emptyList()
        }
    }

    /**
     * Modify a value in the List of String stored in SharedPreferences.
     *
     * @param key The key under which the list is saved
     * @param index The index in the list to be modified
     * @param newValue The new value to be set at the specified index
     */
    fun modifyStringListValue(key: String, newValue: String) {
        val list = getStringList(key).toMutableList()
        list.add(newValue)
        saveStringList(key, list)
    }

    fun remove(key: String) {
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }
}

// region Extensions

fun LocalStorage.saveLocation(key: String, location: Location?) {
    if (location == null) {
        remove(key + "_lat")
        remove(key + "_long")
        return
    }

    saveFloat(key + "_lat", location.latitude.toFloat())
    saveFloat(key + "_long", location.longitude.toFloat())
}

fun LocalStorage.getLocation(key: String): Location? {
    val lat = getFloat(key + "_lat")
    val long = getFloat(key + "_long")
    if (lat == 0f && long == 0f) return null

    val location = Location("")
    location.latitude = lat.toDouble()
    location.longitude = long.toDouble()
    return location
}

// endregion