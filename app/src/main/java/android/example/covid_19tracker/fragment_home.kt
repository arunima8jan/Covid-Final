package android.example.covid_19tracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class fragment_home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchResults()
        refreshSwipe.setColorSchemeResources(R.color.home_color)
        refreshSwipe.setOnRefreshListener {
            if(isNetworkAvailable())
            {
                spin_kit.visibility=View.GONE
            titleHomeNew.visibility=View.VISIBLE
            home_container.visibility=View.GONE
                no_internet.visibility=View.GONE

            GlobalScope.launch {
                val response= withContext(Dispatchers.IO){

                    Client.api.clone().execute() }

                if(response.isSuccessful){
                    val data= Gson().fromJson(response.body?.string(),Response::class.java)
                    launch(Dispatchers.Main) {
                        refreshSwipe.isRefreshing=false
                        bindCombinedData(data.statewise[0])
                        titleHomeNew.visibility=View.GONE
                        home_container.visibility=View.VISIBLE

                    }
                }
            }

        }
            else
            {
                refreshSwipe.isRefreshing=false
                no_internet.visibility=View.VISIBLE
                titleHomeNew.visibility=View.VISIBLE
                home_container.visibility=View.GONE
            }
        }

        }




    private fun fetchResults() {
          spin_kit.visibility=View.VISIBLE
        titleHomeNew.visibility=View.VISIBLE
        home_container.visibility=View.GONE
        if(isNetworkAvailable())
        {
        GlobalScope.launch {
     val response= withContext(Dispatchers.IO){

         Client.api.clone().execute() }

     if(response.isSuccessful) {
         val data = Gson().fromJson(response.body?.string(), Response::class.java)
         launch(Dispatchers.Main) {
             bindCombinedData(data.statewise[0])
             spin_kit.visibility = View.GONE
             titleHomeNew.visibility = View.GONE
             home_container.visibility = View.VISIBLE

         }
     }
     }
    }
        else
        {
            no_internet.visibility=View.VISIBLE
            home_container.visibility=View.GONE
            titleHomeNew.visibility=View.VISIBLE
        }
    }



    private fun bindCombinedData(data: StatewiseItem) {
     val lastUpdatedTime=data.lastupdatedtime
        val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text="Last Updated: ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"
        confirmedTv.text=data.confirmed
        activeTv.text=data.active
        recoveredTv.text=data.recovered
        deceasedTv.text=data.deaths
        val arrayList:ArrayList<PieEntry> =ArrayList()
        arrayList.add(PieEntry((data.recovered)!!.toFloat(),"Recovered",1))
        arrayList.add(PieEntry((data.deaths)!!.toFloat(),"Deceased",2))
        arrayList.add(PieEntry((data.active)!!.toFloat(),"Active",3))

        val dataSet= PieDataSet(arrayList,"Covid-19 Results")
        val colorFirst=ContextCompat.getColor(requireContext(),R.color.home_reco)
        val colorSecond=ContextCompat.getColor(requireContext(),R.color.home_dead)
        val colorThird=ContextCompat.getColor(requireContext(),R.color.home_active)
        try {
            val activePercentage = (data.active)!!.toDouble() / (data.confirmed)!!.toDouble() * 100
            val recoveredPercentage = (data.recovered)!!.toDouble() / (data.confirmed)!!.toDouble() * 100
            val deathsPercentage = (data.deaths)!!.toDouble() / (data.confirmed)!!.toDouble() * 100

            active_percentage.text = String.format("%.2f", activePercentage).plus("%")
            recovered_percentage.text = String.format("%.2f", recoveredPercentage).plus("%")
            death_percentage.text = String.format("%.2f", deathsPercentage).plus("%")
            statusLayout.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dataSet.setDrawValues(false)
        dataSet.colors= mutableListOf(colorFirst,colorSecond,colorThird)
        pieChart.data= PieData(dataSet)
        pieChart.legend.isEnabled=false
        pieChart.setDrawSliceText(false)
        pieChart.description=null
        pieChart.holeRadius=70f
        pieChart.transparentCircleRadius=70f
        pieChart.animateXY(2000,2000)


    }

    private fun getTimeAgo(past: Date): String {
        val now =Date()
        val seconds= TimeUnit.MILLISECONDS.toSeconds(now.time-past.time)
        val minutes= TimeUnit.MILLISECONDS.toSeconds(now.time-past.time)
        val hours= TimeUnit.MILLISECONDS.toSeconds(now.time-past.time)
        return when{
            seconds<60->{
                "Few Seconds ago"
            }
            minutes<60->{
                "$minutes minutes ago"
            }
            hours<60->{
                "$hours hour ${minutes%60} min ago"
            }
            else->{
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(past).toString()
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

