package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.RiddleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameResource() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tricky Mind", appName)
  }

  @Test
  fun testLoadRiddlesFromAssets() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = RiddleRepository(context)
    val riddles = repository.getAllRiddles()

    assertTrue("Riddles should not be empty", riddles.isNotEmpty())
    assertEquals(66, riddles.size)

    val first = riddles[0]
    assertTrue(first.question.contains("sheep"))
    assertEquals(4, first.options.size)
    assertEquals(1, first.answer)
  }

  @Test
  fun testCoinsPersistence() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = RiddleRepository(context)
    val initialCoins = repository.getCoins()

    val addedCoins = repository.addCoins(20)
    assertEquals(initialCoins + 20, addedCoins)

    val spentSuccess = repository.spendCoins(10)
    assertTrue(spentSuccess)
    assertEquals(initialCoins + 10, repository.getCoins())

    val overSpend = repository.spendCoins(999999)
    assertFalse(overSpend)
  }
}
