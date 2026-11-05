package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.model.SearchCategory
import com.example.core.network.ApiRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name matches NexusSearch`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NexusSearch", appName)
  }

  @Test
  fun `verify repository search enforces privacy masking`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = ApiRepository(context)

    val results = repository.performSearch("Rajesh", SearchCategory.CONSENTED_DIRECTORY)
    assertTrue(results.isSuccess)

    val items = results.getOrNull()
    assertTrue(!items.isNullOrEmpty())

    val first = items!!.first()
    // Verify sensitive data is masked
    assertTrue(first.maskedNationalId.contains("XXXX"))
    assertTrue(first.maskedPhone.contains("***"))
  }
}
