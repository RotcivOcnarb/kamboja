package com.mygdx.game.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;

public class MySteamCallback implements SteamUserStatsCallback{

	@Override
	public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
		
		
	}

	@Override
	public void onUserStatsStored(long gameId, SteamResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserStatsUnloaded(SteamID steamIDUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName,
			int curProgress, int maxProgress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries,
			int numEntries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaderboardScoreUploaded(boolean success, SteamLeaderboardHandle leaderboard, int score,
			boolean scoreChanged, int globalRankNew, int globalRankPrevious) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGlobalStatsReceived(long gameId, SteamResult result) {
		// TODO Auto-generated method stub
		
	}






}
