package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.virgilsecurity.ethreenexmodemo.R
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.ChatControlActivity
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.thread.ThreadFragment
import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoConversation
import com.nexmo.client.NexmoMember
import kotlinx.android.synthetic.main.fragment_threads_list.*


/**
 * ThreadsListFragment
 */
class ThreadsListFragment : Fragment() {

    private val adapter = ThreadsListRVAdapter()
    private lateinit var presenter: ThreadsListPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        adapter.setOnClickListener {
            (activity!! as ChatControlActivity).changeFragment(ThreadFragment.instance(it))
        }
        presenter = ThreadsListPresenter(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_threads_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showHamburger()
        rvThreads.layoutManager = LinearLayoutManager(activity)
        rvThreads.adapter = adapter

        if (NexmoClient.get() == null) {
            presenter.initNexmo(::onInitNexmoSuccess, ::onInitNexmoError)
        } else {
            presenter.requestThreads(::onGetThreadsSuccess, ::onGetThreadsError)
            presenter.listenNewThreads(::onThreadAddedSuccess, ::onThreadAddedError)
        }
    }

    private fun showHamburger() {
        (activity as ChatControlActivity).showBackButton(false)
    }

    private fun onInitNexmoSuccess() {
        presenter.requestThreads(::onGetThreadsSuccess, ::onGetThreadsError)
        presenter.listenNewThreads(::onThreadAddedSuccess, ::onThreadAddedError)
    }

    private fun onInitNexmoError(throwable: Throwable) {
        Toast.makeText(activity, throwable.message, Toast.LENGTH_SHORT).show()
    }

    private fun onThreadAddedSuccess(nexmoConversation: NexmoConversation) {
        adapter.addItem(nexmoConversation)
    }

    private fun onThreadAddedError(throwable: Throwable) {
        Toast.makeText(activity, throwable.message, Toast.LENGTH_SHORT).show()
    }

    private fun onGetThreadsSuccess(threads: Collection<NexmoConversation>) {
        adapter.setItems(threads)
    }

    private fun onGetThreadsError(throwable: Throwable) {
        Toast.makeText(activity, throwable.message, Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()

        presenter.disposeAll()
    }

    companion object {
        fun instance() = ThreadsListFragment()
    }
}