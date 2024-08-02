package com.example.newsapplication.ui.search

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.MainActivity
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentSearchBinding
import com.example.newsapplication.models.Article
import com.example.newsapplication.ui.search.adapter.SearchQueryAdapter
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.hideBottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchQueryAdapter.Listener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var resultTV: TextView


    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchET = binding.searchET
        progressBar = binding.paginationProgressBar
        resultTV = binding.resultTV
        val bottomNavigationView =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        CoroutineScope(Dispatchers.Main).launch {
            hideBottomNavigationView(bottomNavigationView)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchET.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.searchET, InputMethodManager.SHOW_IMPLICIT)
        var job: Job? = null
        searchET.addTextChangedListener { editable ->
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchForNews(editable.toString())
                    }
                }
            }
        }
        setupRecyclerView()

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Resource.Loading -> showProgressBar()

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
                    spannableString.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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

    }

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        recyclerView = binding.searchResultRV
        recyclerView.apply {
            adapter = searchQueryAdapter
            layoutManager = LinearLayoutManager(requireContext())
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

//    private fun showResultMessage() {
//        resultTV.text = "Резултат(ы) по запросу ""
//    }
}