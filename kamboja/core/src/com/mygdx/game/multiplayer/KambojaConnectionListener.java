package com.mygdx.game.multiplayer;
public interface KambojaConnectionListener {

	public void receiveUDP(KambojaPacket data);
	public void receiveTCP(KambojaPacket data);
	public void connected();
	public void connectionFailed(String message);
	public void disconnected();
	public boolean clientTriesToConnect();
}
