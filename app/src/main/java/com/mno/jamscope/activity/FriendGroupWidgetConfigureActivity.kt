package com.mno.jamscope.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mno.jamscope.R
import com.mno.jamscope.databinding.FriendGroupWidgetConfigureBinding
import com.mno.jamscope.databinding.FriendsListItemBinding
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.viewmodel.ConfigWidgetScreenViewModel
import com.mno.jamscope.widget.WidgetDataStoreManager
import com.mno.jamscope.widget.friendgroup.FriendGroupWidget
import com.mno.jamscope.widget.friendgroup.startGroupUpdateWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendGroupWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var binding: FriendGroupWidgetConfigureBinding
    private val configScreenWidgetViewModel: ConfigWidgetScreenViewModel by viewModels()
    private lateinit var adapter: FriendsAdapter
    private var selectedFriends: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        binding = FriendGroupWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.saveButton.setOnClickListener { saveConfiguration() }
        binding.cancelButton.setOnClickListener { cancelConfiguration() }

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FriendsAdapter { selectedFriend ->
            if (this.selectedFriends.contains(selectedFriend)) {
                selectedFriends.remove(selectedFriend)
                return@FriendsAdapter
            }
            if (this.selectedFriends.size >= 4) {
                Toast.makeText(this, getString(R.string.max_four_friends_warning), Toast.LENGTH_SHORT).show()
                return@FriendsAdapter
            }
            this.selectedFriends.add(selectedFriend)
        }
        binding.friendsRecyclerView.adapter = adapter

        configScreenWidgetViewModel.friends.observe(this) { friends ->
            adapter.submitList(friends)
        }

    }

    private fun saveConfiguration() {
        if (selectedFriends.size < 2) {
            Toast.makeText(this, getString(R.string.please_select_group_friend), Toast.LENGTH_SHORT)
                .show()
        } else {
            lifecycleScope.launch {
                val glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(appWidgetId)
                FriendGroupWidget().apply {
                    updateAppWidgetState(applicationContext, glanceId) {
                        WidgetDataStoreManager.saveFriendsGroup(it, selectedFriends)
                    }
                    update(applicationContext, glanceId)
                }
                startGroupUpdateWorker()
            }
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    private fun cancelConfiguration() {
        setResult(RESULT_CANCELED)
        finish()
    }

    class FriendsAdapter(private val onFriendSelected: (User) -> Unit) :
        ListAdapter<User, FriendsAdapter.FriendViewHolder>(DIFF_CALLBACK) {

        private val selectedFriends: MutableList<User> = mutableListOf()

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
                override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                    return oldItem == newItem
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
            val binding = FriendsListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return FriendViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
            val friend = getItem(position)
            val isSelected = selectedFriends.contains(friend) // Verifica se está selecionado
            holder.bind(friend, isSelected)

            holder.itemView.setOnClickListener {
                if (selectedFriends.contains(friend)) {
                    selectedFriends.remove(friend)
                } else {
                    if (selectedFriends.size >= 4) {
                        Toast.makeText(
                            holder.itemView.context,
                            holder.itemView.context.getString(R.string.max_four_friends_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    selectedFriends.add(friend)
                }
                notifyItemChanged(position)
                onFriendSelected(friend)
            }
        }

        class FriendViewHolder(private val binding: FriendsListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(friend: User, isSelected: Boolean) {
                binding.friendName.text = friend.realname.ifEmpty { friend.name }
                val imageUrl = friend.image.firstOrNull { it.size == "large" }?.url ?: ""
                Glide.with(binding.friendImage.context)
                    .load(imageUrl)
                    .error(R.drawable.baseline_account_circle_24)
                    .circleCrop()
                    .into(binding.friendImage)

                val context = binding.root.context
                val selectedColor = ContextCompat.getColor(context, R.color.selected_color)
                val unselectedColor = ContextCompat.getColor(context, R.color.transparent)

                binding.root.setBackgroundColor(
                    if (isSelected) selectedColor else unselectedColor
                )
            }
        }
    }
}