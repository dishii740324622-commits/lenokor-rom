package com.sayon.ai.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object SayonRepository {

    // Currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // List of all videos in feed
    private val _videoPosts = MutableStateFlow<List<VideoPost>>(emptyList())
    val videoPosts: StateFlow<List<VideoPost>> = _videoPosts.asStateFlow()

    // Comments map: videoId -> List<Comment>
    private val _commentsMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<Comment>>> = _commentsMap.asStateFlow()

    // Profiles map: userId -> User
    private val _otherUsers = MutableStateFlow<Map<String, User>>(emptyMap())
    val otherUsers: StateFlow<Map<String, User>> = _otherUsers.asStateFlow()

    init {
        // Populate initial demo community content
        seedInitialData()
    }

    private fun seedInitialData() {
        val creator1 = User(
            id = "user_creator_1",
            name = "سارة أحمد",
            username = "@sara_ai",
            email = "sara@sayon.ai",
            avatarUrl = "https://picsum.photos/seed/sara/200/200",
            bio = "مصممة مشاهد سينمائية بالذكاء الاصطناعي 🚀✨",
            followersCount = 125000, // 125K (Reaches 100K -> Verified Badge ✔️)
            followingCount = 180,
            isCurrentUser = false
        )

        val creator2 = User(
            id = "user_creator_2",
            name = "خالد العتيبي",
            username = "@khaled_vfx",
            email = "khaled@sayon.ai",
            avatarUrl = "https://picsum.photos/seed/khaled/200/200",
            bio = "صانع أفلام الخيال العلمي باستخدام Sayon AI Model 🤖🎥",
            followersCount = 2400000, // 2.4M (Reaches 100K -> Verified Badge ✔️)
            followingCount = 420,
            isCurrentUser = false
        )

        _otherUsers.value = mapOf(
            creator1.id to creator1,
            creator2.id to creator2
        )

        val posts = listOf(
            VideoPost(
                id = "vid_101",
                authorUserId = creator1.id,
                authorUsername = creator1.username,
                authorAvatarUrl = creator1.avatarUrl,
                prompt = "مدينة مستقبليّة مضاءة بالنيون في الليل مع سيارات تطير ومشاهد واقعية سينمائية 8K",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://picsum.photos/seed/sayon1/720/1280",
                durationSeconds = 18,
                likesCount = 1540,
                commentsCount = 38,
                isLikedByCurrentUser = false
            ),
            VideoPost(
                id = "vid_102",
                authorUserId = creator2.id,
                authorUsername = creator2.username,
                authorAvatarUrl = creator2.avatarUrl,
                prompt = "رائد فضاء يمشي على سطح كوكب مجهول بألوان زاهية وأصوات واقعية ثلاثية الأبعاد",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://picsum.photos/seed/sayon2/720/1280",
                durationSeconds = 18,
                likesCount = 2890,
                commentsCount = 112,
                isLikedByCurrentUser = true
            )
        )

        _videoPosts.value = posts

        _commentsMap.value = mapOf(
            "vid_101" to listOf(
                Comment(
                    id = "c1",
                    videoId = "vid_101",
                    userId = creator2.id,
                    username = creator2.username,
                    userAvatar = creator2.avatarUrl,
                    text = "فيديو رائع جداً والدقة واقعية بشكل مذهل!"
                )
            )
        )
    }

    // Login with Email or Google
    fun login(email: String, name: String = "مستخدم Sayon", username: String = "@user_name") {
        val user = User(
            id = "user_me_" + System.currentTimeMillis(),
            name = name,
            username = if (username.startsWith("@")) username else "@$username",
            email = email,
            avatarUrl = "https://picsum.photos/seed/myavatar/200/200",
            bio = "This is my bio. Welcome to my Sayon AI profile. #darktheme #tiktok",
            followersCount = 0, // Starts with 0 followers
            followingCount = 0, // Starts with 0 following
            isCurrentUser = true
        )
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    // Toggle Like on video
    fun toggleLike(videoId: String) {
        val updated = _videoPosts.value.map { post ->
            if (post.id == videoId) {
                val newLiked = !post.isLikedByCurrentUser
                val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                post.copy(isLikedByCurrentUser = newLiked, likesCount = newCount)
            } else post
        }
        _videoPosts.value = updated
    }

    // Add Comment
    fun addComment(videoId: String, commentText: String) {
        val currentUser = _currentUser.value ?: return
        val newComment = Comment(
            id = "c_" + UUID.randomUUID().toString(),
            videoId = videoId,
            userId = currentUser.id,
            username = currentUser.username,
            userAvatar = currentUser.avatarUrl,
            text = commentText
        )
        val currentComments = _commentsMap.value[videoId] ?: emptyList()
        val updatedComments = currentComments + newComment
        _commentsMap.value = _commentsMap.value + (videoId to updatedComments)

        // Increment comments count on post
        _videoPosts.value = _videoPosts.value.map { post ->
            if (post.id == videoId) {
                post.copy(commentsCount = post.commentsCount + 1)
            } else post
        }
    }

    // Delete Video Post
    fun deleteVideo(videoId: String) {
        _videoPosts.value = _videoPosts.value.filter { it.id != videoId }
    }

    // Add Newly Generated Video
    fun addGeneratedVideo(prompt: String, videoUrl: String, thumbnailUrl: String) {
        val user = _currentUser.value ?: return
        val newPost = VideoPost(
            id = "vid_" + System.currentTimeMillis(),
            authorUserId = user.id,
            authorUsername = user.username,
            authorAvatarUrl = user.avatarUrl,
            prompt = prompt,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            durationSeconds = 18,
            likesCount = 0,
            commentsCount = 0,
            isLikedByCurrentUser = false,
            createdAt = System.currentTimeMillis()
        )
        _videoPosts.value = listOf(newPost) + _videoPosts.value
    }

    // Toggle Follow status for other user
    fun toggleFollowOtherUser(userId: String) {
        val target = _otherUsers.value[userId] ?: return
        val newFollowingState = !target.isFollowing
        val newFollowersCount = if (newFollowingState) target.followersCount + 1 else target.followersCount - 1
        val updatedTarget = target.copy(
            isFollowing = newFollowingState,
            followersCount = newFollowersCount
        )

        _otherUsers.value = _otherUsers.value + (userId to updatedTarget)

        // Update current user's following count
        val me = _currentUser.value
        if (me != null) {
            val meNewFollowing = if (newFollowingState) me.followingCount + 1 else me.followingCount - 1
            _currentUser.value = me.copy(followingCount = meNewFollowing.coerceAtLeast(0))
        }
    }

    // Find post by ID for deep link loading
    fun getPostById(videoId: String): VideoPost? {
        return _videoPosts.value.find { it.id == videoId }
    }
}
