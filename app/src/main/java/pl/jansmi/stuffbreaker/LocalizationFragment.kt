package pl.jansmi.stuffbreaker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_localization.*
import pl.jansmi.stuffbreaker.adapter.ItemsAdapter
import pl.jansmi.stuffbreaker.database.entity.Box

class LocalizationFragment(val box: Box) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_localization, container, false)
    }

    override fun onStart() {
        super.onStart()

        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = ItemsAdapter(box)

        val recycler = view!!.findViewById<RecyclerView>(R.id.recycler).apply {
            this.setHasFixedSize(true) // necessary?
            this.layoutManager = viewManager
            this.adapter = viewAdapter
        }
    }

}