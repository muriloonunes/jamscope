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
import com.mno.jamscope.data.model.User
import com.mno.jamscope.databinding.FriendsListItemBinding
import com.mno.jamscope.databinding.SmallFriendListeningWidgetConfigureBinding
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.singlefriend.FriendListeningWidget
import com.mno.jamscope.features.widgets.singlefriend.startListeningUpdateWorker
import com.mno.jamscope.ui.viewmodel.ConfigWidgetScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendListeningWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var binding: SmallFriendListeningWidgetConfigureBinding
    private val configScreenWidgetViewModel: ConfigWidgetScreenViewModel by viewModels()
    private lateinit var adapter: FriendsAdapter
    private var selectedFriend: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        binding = SmallFriendListeningWidgetConfigureBinding.inflate(layoutInflater)
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
            this.selectedFriend = selectedFriend
        }
        binding.friendsRecyclerView.adapter = adapter

        configScreenWidgetViewModel.friends.observe(this) { friends ->
            adapter.submitList(friends)
        }

    }

    private fun saveConfiguration() {
        selectedFriend?.let {
            lifecycleScope.launch {
                val glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(appWidgetId)
                FriendListeningWidget().apply {
                    updateAppWidgetState(applicationContext, glanceId) {
                        WidgetDataStoreManager.saveFriend(it, selectedFriend!!)
                    }
                    update(applicationContext, glanceId)
                }
                startListeningUpdateWorker()
            }

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()

        } ?: run {
            Toast.makeText(this, getString(R.string.please_select_a_friend), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun cancelConfiguration() {
        setResult(RESULT_CANCELED)
        finish()
    }

    class FriendsAdapter(private val onFriendSelected: (User) -> Unit) :
        ListAdapter<User, FriendsAdapter.FriendViewHolder>(DIFF_CALLBACK) {

        private var selectedFriendPosition: Int? = null

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
            holder.bind(friend, position == selectedFriendPosition)
            holder.itemView.setOnClickListener {
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                val previousPosition = selectedFriendPosition
                selectedFriendPosition = currentPosition

                notifyItemChanged(previousPosition ?: -1)
                notifyItemChanged(currentPosition)

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