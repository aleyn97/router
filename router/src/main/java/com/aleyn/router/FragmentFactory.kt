package com.aleyn.router

import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import java.lang.reflect.InvocationTargetException


/**
 * Create a new instance of a Fragment with the given class name. This uses
 * [.loadFragmentClass] and the empty
 * constructor of the resulting Class by default.
 *
 * @param className   The class name of the fragment to instantiate.
 * @return Returns a new fragment instance.
 * @throws Fragment.InstantiationException If there is a failure in instantiating
 * the given fragment class.  This is a runtime exception; it is not
 * normally expected to happen.
 */
internal fun instantiate(className: String): Fragment {
    return try {
        val cls = loadFragmentClass(className)
        cls.getConstructor().newInstance()!!
    } catch (e: InstantiationException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e
        )
    } catch (e: IllegalAccessException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e
        )
    } catch (e: NoSuchMethodException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": could not find Fragment constructor", e
        )
    } catch (e: InvocationTargetException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": calling Fragment constructor caused an exception", e
        )
    }
}

private val sClassCacheMap = SimpleArrayMap<ClassLoader, SimpleArrayMap<String, Class<*>>>()

/**
 * Determine if the given fragment name is a support library fragment class.
 *
 * @param className   Class name of the fragment to load
 * @return Returns the parsed Class
 */
@Throws(ClassNotFoundException::class)
internal fun loadClass(
    className: String
): Class<*> {
    val classLoader = Fragment::class.java.classLoader
    var classMap = sClassCacheMap[classLoader]
    if (classMap == null) {
        classMap = SimpleArrayMap()
        sClassCacheMap.put(classLoader, classMap)
    }
    var clazz = classMap[className]
    if (clazz == null) {
        // Class not found in the cache, see if it's real, and try to add it
        clazz = Class.forName(className, false, classLoader)
        classMap.put(className, clazz)
    }
    return clazz!!
}

/**
 * Determine if the given fragment name is a valid Fragment class.
 *
 * @param className   Class name of the fragment to test
 * @return true if `className` is `androidx.fragment.app.Fragment`
 * or a subclass, false otherwise.
 */
internal fun isFragmentClass(
    className: String
): Boolean {
    return try {
        val clazz = loadClass(className)
        Fragment::class.java.isAssignableFrom(clazz)
    } catch (e: ClassNotFoundException) {
        false
    }
}

/**
 * Parse a Fragment Class from the given class name. The resulting Class is kept in a global
 * cache, bypassing the [Class.forName] calls when passed the same
 * class name again.
 *
 * @param className   The class name of the fragment to parse.
 * @return Returns the parsed Fragment Class
 * @throws Fragment.InstantiationException If there is a failure in parsing
 * the given fragment class.  This is a runtime exception; it is not
 * normally expected to happen.
 */
@Suppress("UNCHECKED_CAST")
internal fun loadFragmentClass(
    className: String
): Class<out Fragment?> {
    return try {
        loadClass(className) as Class<out Fragment?>
    } catch (e: ClassNotFoundException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": make sure class name exists", e
        )
    } catch (e: ClassCastException) {
        throw Fragment.InstantiationException(
            "Unable to instantiate fragment " + className
                    + ": make sure class is a valid subclass of Fragment", e
        )
    }
}