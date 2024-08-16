package com.example.newsapplication.view.search

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.MainActivity
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentSearchBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.clickAreaButton
import com.example.newsapplication.util.hideBottomNavigationView
import com.example.newsapplication.view.search.adapter.SearchQueryAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchQueryAdapter.Listener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var cleanBtn: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var resultTV: TextView
    private lateinit var appBarLayout: AppBarLayout

    private lateinit var connectionLiveData: NetworkConnectionLiveData


    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchET = binding.searchET
        cleanBtn = binding.cleanSearchText
        progressBar = binding.progressBar
        resultTV = binding.resultTV
        appBarLayout = binding.appBarLayout
        connectionLiveData = NetworkConnectionLiveData(requireContext())
        val bottomNavigationView =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        CoroutineScope(Dispatchers.Main).launch {
            hideBottomNavigationView(bottomNavigationView)
        }
        appBarLayoutBg()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cleanBtn.visibility = View.GONE
        searchET.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.searchET, InputMethodManager.SHOW_IMPLICIT)
        var job: Job? = null
        searchET.addTextChangedListener { editable ->
            job?.cancel()
            editable?.let {
                cleanBtn.visibility =
                    if (editable.isEmpty()) View.GONE else View.VISIBLE
                if (editable.toString().isNotEmpty()) {
                    job = lifecycleScope.launch {
                        delay(500L)
                        connectionLiveData.observe(viewLifecycleOwner) { isConnected ->
                            if (isConnected) {
                                viewModel.searchForNews(editable.toString().trim())
                            }
                        }
                    }
                }
            }
        }
        setupRecyclerView()

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        searchQueryAdapter.differ.submitList(newsResponse.articles)
                    }
                    resultTV.visibility = View.VISIBLE

                    val queryText = searchET.text.toString()
                    val resultText = "Result(s) on request \"$queryText\""
                    val spannableString = SpannableString(resultText)
                    val startIndex = resultText.indexOf(queryText)
                    val endIndex = startIndex + queryText.length
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    resultTV.text = spannableString

                    hideProgressBar()
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e("MyLog", "An error occurred: $message")
                    }
                }

                else -> {}

            }
        }

        cleanBtn.setOnClickListener {
            searchET.setText("")
        }
        clickAreaButton(cleanBtn)

    }

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        recyclerView = binding.searchResultRV
        recyclerView.apply {
            adapter = searchQueryAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onClick(item: Article) {
        val action = SearchFragmentDirections.actionSearchFragmentToWebViewFragment(item)
        findNavController().navigate(action)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    private fun appBarLayoutBg() {
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // AppBarLayout is collapsed
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            } else if (verticalOffset == 0) {
                // AppBarLayout is expanded
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            } else {
                // AppBarLayout is in the middle of collapsing/expanding
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            }
        }
    }
}