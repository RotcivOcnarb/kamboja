package com.mygdx.game.multiplayer;

public class DataIdentifier {

	public static final byte PLAYER_CONNECTED = 0x00;
	public static final byte PLAYER_DISCONNECTED= 0x01;
	public static final byte SERVER_PLAYER_MAIN_MENU_INFO = 0x02;
	
	public static final byte SERVER_CONNECT = 0x03;
	public static final byte SERVER_DISCONNECT = 0x04;

	public static final byte CLIENT_PLAYER_MAIN_MENU_INFO = 0x05;
	
	public static final int PLAYER_SELECT = 0;
	public static final int MAP_SELECT = 1;
	public static final int GAME_STATE = 2;

}
