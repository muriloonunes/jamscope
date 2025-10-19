package com.mno.jamscope.domain.usecase.friend

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendRecentTracksUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(username: String): Resource<List<Track>> {
        return repository.getRecentTracks(username)
    }
}
