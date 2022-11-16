package com.example.clase15_terremotoapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListFragment : Fragment() {
    private var listadoTerremotos = mutableListOf<Terremoto>()
    private var listadoTerremotosDataBase = mutableListOf<Terremoto>()
    private lateinit var dataBase: TerremotoDataBase

    private val job = Job()
    private lateinit var adapter: TerremotoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = myView.findViewById(R.id.listrecyclerview)
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBase= getDataBase(requireActivity().application)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = TerremotoAdapter()
        recyclerView.adapter = adapter

        getTerremotosDataBase()

        getTerremotos()

        adapter.onItemClickListener = { terremoto ->
            navigateToDetail(terremoto)
        }

        adapter.submitList(listadoTerremotosDataBase)
    }

    private fun navigateToDetail(terremoto: Terremoto) {
        val bundle = Bundle()
        bundle.putParcelable("terremoto", terremoto)
        val detailFragment = DetailFragment()
        detailFragment.arguments = bundle
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainerView, detailFragment)
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getTerremotos() {
        CoroutineScope(Dispatchers.IO + job).launch {
            val call = getRefrofit().create(ApiService::class.java).getAllWeek()
            val response = call.body()

            activity?.runOnUiThread {
                listadoTerremotos.clear()
                if (call.isSuccessful) {
                    listadoTerremotos = (response?.features?.map { feature ->
                        feature.toTerromoto() } ?: emptyList()) as MutableList<Terremoto>
                        insertAllTerremotos(listadoTerremotos)
                        adapter.submitList(listadoTerremotos)
                } else {
                    val error = call.errorBody().toString()
                    Log.i("RETROFIT", error)
                }
            }
        }
    }

    private fun insertAllTerremotos(listadoTerremotos: MutableList<Terremoto>) {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.terremotoDao.insertAll(listadoTerremotos)
        }
    }

    private fun getTerremotosDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            listadoTerremotosDataBase = dataBase.terremotoDao.getAllTerremotos()
        }
    }

    private fun getRefrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}