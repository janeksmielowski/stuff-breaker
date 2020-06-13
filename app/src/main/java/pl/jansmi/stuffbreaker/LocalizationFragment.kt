package pl.jansmi.stuffbreaker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.adapters.ItemsAdapter
import pl.jansmi.stuffbreaker.database.entity.Box

class LocalizationFragment(
    val box: Box,
    val switchContent: (box: Box) -> Unit,
    val renderItems: Boolean
) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_localization, container, false)
    }

    override fun onStart() {
        super.onStart()

        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = ItemsAdapter(context!!, box, switchContent, renderItems)

        val recycler = view!!.findViewById<RecyclerView>(R.id.recycler).apply {
            this.setHasFixedSize(true) // necessary?
            this.layoutManager = viewManager
            this.adapter = viewAdapter
        }
    }

}