package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.virgilsecurity.ethreenexmodemo.R
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.ChatControlActivity
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.thread.ThreadFragment
import com.android.virgilsecurity.ethreenexmodemo.ui.toolbar.Toolbar
import com.nexmo.client.NexmoConversation
import com.nexmo.client.NexmoMember
import kotlinx.android.synthetic.main.activity_chat_control.*
import kotlinx.android.synthetic.main.fragment_threads_list.*

/**
 * ThreadsListFragment
 */
class ThreadsListFragment : Fragment() {

    private val presenter = ThreadsListPresenter()
    private val adapter = ThreadsListRVAdapter()
    private lateinit var toolbar: Toolbar

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        adapter.setOnClickListener {
            (activity!! as ChatControlActivity).changeFragment(ThreadFragment.instance(it))
        }
        rvThreads.layoutManager = LinearLayoutManager(activity)
        rvThreads.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_threads_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = toolbarThreadsList as Toolbar
        toolbar.setTitle("Threads List")
        toolbar.showHamburgerButton()
        toolbar.setOnToolbarItemClickListener {
            when (it.id) {
                R.id.ivHamburger -> {
                    dlBase.openDrawer(Gravity.START)
                }
            }
        }

        presenter.requestThreads(::onGetThreadsSuccess, ::onGetThreadsError)
        presenter.listenNewThreads {
            adapter.addItem(it)
        }
        presenter.listenInvites(::onNewInviteJoinSuccess, ::onNewInviteJoinError)
    }

    private fun onGetThreadsSuccess(threads: Collection<NexmoConversation>) {
        adapter.setItems(threads)
    }

    private fun onGetThreadsError(throwable: Throwable) {
        Toast.makeText(activity, throwable.message, Toast.LENGTH_SHORT).show()
    }

    private fun onNewInviteJoinSuccess(nexmoMember: NexmoMember) {
        Toast.makeText(activity, "Joined chat with " + nexmoMember.user.displayName, Toast.LENGTH_SHORT).show()
    }

    private fun onNewInviteJoinError(throwable: Throwable) {
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