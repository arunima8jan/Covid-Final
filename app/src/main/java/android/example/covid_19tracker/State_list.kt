package android.example.covid_19tracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_state_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class State_list : Fragment() {
    lateinit var stateAdapter: StateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_state_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchResults()
        refreshApp()
    }
    private fun fetchResults() {
        spin_kit.visibility = View.VISIBLE
        list.visibility = View.GONE
        header.visibility = View.GONE
        if (isNetworkAvailable()) {
            no_internet.visibility=View.GONE
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) {

                    Client.api.clone().execute()
                }

                if (response.isSuccessful) {

                    val data = Gson().fromJson(response.body?.string(), Response::class.java)
                    launch(Dispatchers.Main) {
                        spin_kit.visibility = View.GONE
                        list.visibility = View.VISIBLE
                        header.visibility = View.VISIBLE
                        bindStateWiseData(data.statewise.subList(1, data.statewise.size))
                    }
                }
            }
        }
        else
        {
            no_internet.visibility=View.VISIBLE
            list.visibility = View.GONE
            header.visibility = View.GONE

        }
    }

    private fun bindStateWiseData(subList: List<StatewiseItem>) {
        stateAdapter=StateAdapter(subList)
        list.adapter=stateAdapter

    }
    private fun refreshApp()
    {     refresh.setColorSchemeResources(R.color.state_color)
        refresh.setOnRefreshListener {
        if(isNetworkAvailable()) {
            no_internet.visibility=View.GONE
            list.visibility = View.GONE
            header.visibility = View.GONE
            spin_kit.visibility = View.GONE
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) {

                    Client.api.clone().execute()
                }

                if (response.isSuccessful) {

                    val data = Gson().fromJson(response.body?.string(), Response::class.java)
                    launch(Dispatchers.Main) {
                        refresh.isRefreshing = false
                        list.visibility = View.VISIBLE
                        header.visibility = View.VISIBLE
                        bindStateWiseData(data.statewise.subList(1, data.statewise.size))
                    }
                }
            }
        }
            else
        {
            no_internet.visibility=View.VISIBLE
            refresh.isRefreshing = false
            list.visibility = View.GONE
            header.visibility = View.GONE


        }
    }

    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false

        } else false
    }

}
