package our.stuff.networking;

public class PacketManager
{
	
	public static final byte TYPE_PING = 0;
	public static final byte TYPE_CONNECT = 1;
	public static final byte TYPE_ACCEPT = 2;
	public static final byte TYPE_CHAT = 3;
	
	public static byte[] genPacketData(byte packetType)
	{
		return genPacketData(packetType, new byte[64]);
	}
	
	public static byte[] genPacketData(byte packetType, byte[] data)
	{
		data[0] = packetType;
		return data;
	}
	
	public static byte[] genChatPacket(String message)
	{
		byte[] txtBytes = message.getBytes();
		return genPacketData(TYPE_CHAT, txtBytes);
	}
	
}