package net.furkanakdemir.noticeboard.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_notice_board.*
import net.furkanakdemir.noticeboard.NoticeBoard.Companion.KEY_TITLE
import net.furkanakdemir.noticeboard.R
import net.furkanakdemir.noticeboard.config.ConfigRepository
import net.furkanakdemir.noticeboard.data.model.Release
import net.furkanakdemir.noticeboard.di.DaggerInjector
import net.furkanakdemir.noticeboard.result.EventObserver
import net.furkanakdemir.noticeboard.util.mapper.Mapper
import javax.inject.Inject

class NoticeBoardActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var noticeBoardAdapter: NoticeBoardAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var noticeBoardViewModel: NoticeBoardViewModel

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var viewMapper: Mapper<List<Release>, List<NoticeBoardItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerInjector.component?.inject(this)

        setContentView(R.layout.activity_notice_board)

        setupToolbar()

        setupRecyclerView()

        noticeBoardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NoticeBoardViewModel::class.java)

        noticeBoardViewModel.releaseLiveData.observe(this, Observer {
            noticeBoardAdapter.releaseList = it.toMutableList()
            showContent()
        })

        noticeBoardViewModel.eventLiveData.observe(this, EventObserver {
            messageTextView.text = it
            showMessage()
        })

        noticeBoardViewModel.getChanges()
    }

    private fun showMessage() {
        messageTextView.visibility = VISIBLE
        recyclerView.visibility = GONE
    }

    private fun showContent() {
        messageTextView.visibility = GONE
        recyclerView.visibility = VISIBLE
    }

    private fun setupRecyclerView() {
        noticeBoardAdapter = NoticeBoardAdapter(configRepository.getColorProvider())

        recyclerView = findViewById<RecyclerView>(R.id.change_recyclerview).apply {
            setHasFixedSize(true)
            adapter = noticeBoardAdapter
        }
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.notice_board_toolbar)
        setSupportActionBar(toolbar)

        val title = intent.getStringExtra(KEY_TITLE)
            ?: throw IllegalStateException("field $KEY_TITLE missing in Intent")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        fun createIntent(context: Context, title: String): Intent {
            val intent = Intent(context, NoticeBoardActivity::class.java)
            intent.putExtra(KEY_TITLE, title)
            return intent
        }
    }
}
