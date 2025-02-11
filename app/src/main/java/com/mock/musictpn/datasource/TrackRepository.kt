package com.mock.musictpn.datasource

import androidx.lifecycle.LiveData
import com.mock.musictpn.datasource.local.TrackDataSource
import com.mock.musictpn.datasource.local.dao.PlayListDao
import com.mock.musictpn.datasource.network.ApiContract
import com.mock.musictpn.datasource.network.IMusicService
import com.mock.musictpn.model.album.AlbumList
import com.mock.musictpn.model.genre.GenreList
import com.mock.musictpn.model.search.SearchResult
import com.mock.musictpn.model.track.Track
import com.mock.musictpn.model.track.TrackList
import retrofit2.Response
import javax.inject.Inject

class TrackRepository @Inject constructor(
    private val source: TrackDataSource,
    private val apiService: IMusicService,
    private val dao: PlayListDao
) {
    fun fetchTracksLocal(): List<Track> {
        return source.fetchTracksLocal()
    }

    suspend fun getAlbumBanner(): Response<AlbumList> {
        return apiService.getTopTrendingAlbums(ApiContract.RANGE_WEEK, 5)
    }

    suspend fun getTopTracksTrending(): Response<TrackList> {
        return apiService.getTopTracksTrending(ApiContract.RANGE_YEAR, 50)
    }

    suspend fun getTracksByAlbumId(albumId: String): Response<TrackList> {
        return apiService.getTracksByAlbumId(albumId)
    }

    suspend fun getTracksByGenreId(genreId: String): Response<TrackList> {
        return apiService.getTracksByGenreId(genreId)
    }

    suspend fun getGenres(): Response<GenreList> {
        return apiService.getGenreList()
    }

    suspend fun getAlbums(): Response<AlbumList> {
        return apiService.getTopTrendingAlbums(ApiContract.RANGE_MONTH, 20)
    }

    suspend fun searchByKeyword(name: String): Response<SearchResult> {
        return apiService.searchByKeyword(name)
    }


    suspend fun insertTrack(track: Track, playList: Int): Long {
        val favor = track.apply {
            if (track.playListId == PlayListDao.ID_LIST_FAVORITE) {
                this.localId = 0
            }
            this.playListId = playList
        }
        return dao.insertTrack(favor)
    }

    suspend fun deleteTrack(track: Track): Int {
        return dao.deleteTrack(track)
    }

    fun getHistoryTracks(): TrackList {
        return dao.getListByID(PlayListDao.ID_LIST_HISTORY)
    }

    fun getFavoriteTracks(): LiveData<TrackList> {
        return dao.getListByIDLiveData(PlayListDao.ID_LIST_FAVORITE)
    }

    suspend fun getOlderTracks(id: Int): List<Track> {
        return dao.filterHistory(id)
    }

    fun getAllHistory(rows: Int): LiveData<List<Track>>{
        return dao.getAllHistory(rows)
    }

    suspend fun deleteByUrl(url: String, playList: Int) {
        dao.deleteByUrl(url, playList)
    }

}