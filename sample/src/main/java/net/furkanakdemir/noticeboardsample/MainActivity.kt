package net.furkanakdemir.noticeboardsample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import net.furkanakdemir.noticeboard.ChangeType.ADDED
import net.furkanakdemir.noticeboard.ChangeType.CHANGED
import net.furkanakdemir.noticeboard.ChangeType.DEPRECATED
import net.furkanakdemir.noticeboard.ChangeType.FIXED
import net.furkanakdemir.noticeboard.ChangeType.REMOVED
import net.furkanakdemir.noticeboard.ChangeType.SECURITY
import net.furkanakdemir.noticeboard.DisplayOptions
import net.furkanakdemir.noticeboard.NoticeBoard
import net.furkanakdemir.noticeboard.Source
import net.furkanakdemir.noticeboard.data.model.Release
import net.furkanakdemir.noticeboard.data.model.Release.Change

@Suppress("LongMethod", "ComplexMethod", "TooManyFunctions")
class MainActivity : AppCompatActivity() {

    private lateinit var displayOptionsDialog: AlertDialog
    private lateinit var recyclerView: RecyclerView
    private lateinit var sampleAdapter: SampleAdapter

    private var currentDisplayOptions: DisplayOptions = DisplayOptions.ACTIVITY
    private var title: String = TITLE_NOTICEBOARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupRecyclerView()

        buildDisplayOptionsDialog()
    }

    private fun buildDisplayOptionsDialog() {
        val builderSingle = AlertDialog.Builder(this)
        builderSingle.setTitle(TITLE_DISPLAY_OPTIONS_DIALOG)

        val menuItems = arrayOf(
            getString(R.string.action_display_activity),
            getString(R.string.action_display_dialog)
        )

        builderSingle.setSingleChoiceItems(menuItems, 0) { dialog, which ->
            currentDisplayOptions = when (which) {
                0 -> DisplayOptions.ACTIVITY
                1 -> DisplayOptions.DIALOG
                else -> throw IllegalArgumentException("Invalid index $which")
            }

            dialog.dismiss()
        }

        builderSingle.setNegativeButton(TEXT_DISPLAY_OPTIONS_DIALOG_CLOSE, null)
        displayOptionsDialog = builderSingle.create()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sample, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_display_options -> {
                showDisplayOptionsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDisplayOptionsDialog() {

        if (!displayOptionsDialog.isShowing) {
            displayOptionsDialog.show()
        }
    }

    private fun setupRecyclerView() {

        sampleAdapter = SampleAdapter {
            when (it) {
                SourceType.DYNAMIC.type -> openDynamic()
                SourceType.XML.type -> openXml()
                SourceType.JSON.type -> openJson()
                SourceType.EMPTY_JSON.type -> openEmptyFileJson()
                SourceType.EMPTY_ARRAY_JSON.type -> openEmptyArrayJson()
                SourceType.INVALID_JSON.type -> openInvalidJson()
                SourceType.EMPTY_XML.type -> openEmptyFileXml()
                SourceType.EMPTY_ARRAY_XML.type -> openEmptyArrayXml()
                SourceType.INVALID_XML.type -> openInvalidXml()
            }
        }

        recyclerView = findViewById<RecyclerView>(R.id.main_recyclerview).apply {
            setHasFixedSize(true)
            adapter = sampleAdapter
            addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }

        sampleAdapter.samples = SourceType.values().map {
            it.type
        }.toMutableList()
    }

    private fun setupToolbar() {
        setSupportActionBar(main_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = TITLE_NOTICEBOARD_SAMPLE
    }

    private fun openDynamic() {
        val changes = listOf(
            Release(
                "30 Sep 2019", "1.0.0",
                listOf(
                    Change("New Login Page", ADDED),
                    Change("Crash in Payment", CHANGED),
                    Change("Old theme will be removed", DEPRECATED),
                    Change("Tutorial page is removed", REMOVED),
                    Change("Crash in Payment", FIXED),
                    Change("HTTPS only requests", SECURITY)
                )
            )
        )

        val myColorProvider = CustomColorProvider(this)

        NoticeBoard(this).pin {
            displayIn(currentDisplayOptions)
            title(title)
            source(Source.Dynamic(changes))
            colorProvider(myColorProvider)
        }
    }

    private fun openInvalidXml() {
        val filepath = "sample_invalid.xml"

        NoticeBoard(this).pin {
            source(Source.Xml(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openEmptyArrayXml() {

        val filepath = "sample_empty.xml"

        NoticeBoard(this).pin {
            source(Source.Xml(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openEmptyFileXml() {

        val filepath = "sample_empty_file.xml"

        NoticeBoard(this).pin {
            source(Source.Xml(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openJson() {

        val filepath = "sample.json"

        NoticeBoard(this).pin {
            source(Source.Json(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openEmptyArrayJson() {

        val filepath = "sample_empty.json"

        NoticeBoard(this).pin {
            source(Source.Json(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openEmptyFileJson() {

        val filepath = "sample_empty_file.json"

        NoticeBoard(this).pin {
            source(Source.Json(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openInvalidJson() {

        val filepath = "sample_invalid.json"

        NoticeBoard(this).pin {
            source(Source.Json(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    private fun openXml() {
        val filepath = "sample.xml"

        NoticeBoard(this).pin {
            source(Source.Xml(filepath))
            title(title)
            displayIn(currentDisplayOptions)
        }
    }

    companion object {
        private const val TITLE_NOTICEBOARD_SAMPLE = "NoticeBoards"
        private const val TITLE_DISPLAY_OPTIONS_DIALOG = "Display Options"
        private const val TEXT_DISPLAY_OPTIONS_DIALOG_CLOSE = "Close"

        private const val TITLE_NOTICEBOARD = "Change Logs"
    }

    override fun onBackPressed() {
        // TODO Remove when memory leak is fixed
        if (isTaskRoot && supportFragmentManager.backStackEntryCount == 0) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    enum class SourceType(val type: String) {
        DYNAMIC("Dynamic"),
        XML("Xml"),
        JSON("Json"),
        EMPTY_JSON("Empty Json"),
        EMPTY_ARRAY_JSON("Empty Array Json"),
        INVALID_JSON("Invalid Json"),
        EMPTY_XML("Empty Xml"),
        EMPTY_ARRAY_XML("Empty Array Xml"),
        INVALID_XML("Invalid Xml")
    }
}
