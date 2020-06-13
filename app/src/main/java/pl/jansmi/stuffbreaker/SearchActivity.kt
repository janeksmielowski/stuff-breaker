package pl.jansmi.stuffbreaker

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_search.*
import pl.jansmi.stuffbreaker.adapters.SearchAdapter

class SearchActivity : AppCompatActivity() {
    private lateinit var viewAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

        val viewManager = LinearLayoutManager(this)
        viewAdapter = SearchAdapter(this)

        val recycler = findViewById<RecyclerView>(R.id.recycler).apply {
            this.setHasFixedSize(true) // necessary?
            this.layoutManager = viewManager
            this.adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewAdapter.updateQuery(query)
                return true
            }
        })
        return true
    }
}