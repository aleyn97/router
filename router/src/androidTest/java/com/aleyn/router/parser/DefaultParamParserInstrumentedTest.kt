package com.aleyn.router.parser

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultParamParserInstrumentedTest {

    @Test
    fun parse_default_returns_single_char() {
        val fragment = TestFragment().apply {
            arguments = Bundle().apply {
                putString("letter", "A")
            }
        }

        val value = DefaultParamParser.parseDefault(fragment, "letter", Char::class.java)

        assertEquals('A', value)
    }

    @Test
    fun parse_default_returns_null_for_multi_char_string() {
        val fragment = TestFragment().apply {
            arguments = Bundle().apply {
                putString("letter", "AB")
            }
        }

        val value = DefaultParamParser.parseDefault(fragment, "letter", Char::class.java)

        assertNull(value)
    }

    @Test
    fun parse_any_supports_generic_collections_and_nested_objects() {
        val payload = mapOf(
            "teamA" to listOf(User("Alice", 18), User("Bob", 20)),
            "teamB" to listOf(User("Cindy", 21))
        )
        val json = """{"teamA":[{"name":"Alice","age":18},{"name":"Bob","age":20}],"teamB":[{"name":"Cindy","age":21}]}"""
        val fragment = TestFragment().apply {
            arguments = Bundle().apply {
                putString("users", json)
            }
        }

        val value = DefaultParamParser.parseAny<Map<String, List<User>>>(fragment, "users", "users")

        assertEquals(payload, value)
    }

    private class TestFragment : Fragment()

    private data class User(
        val name: String,
        val age: Int
    )
}
