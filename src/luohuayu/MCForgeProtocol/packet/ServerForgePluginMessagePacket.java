package luohuayu.MCForgeProtocol.packet;

import java.io.IOException;

import org.spacehq.packetlib.io.NetInput;
import org.spacehq.packetlib.io.NetOutput;
import org.spacehq.packetlib.packet.Packet;

import luohuayu.MCForgeProtocol.MCForgeUtils;

public class ServerForgePluginMessagePacket implements Packet{
	private String channel;
	private byte[] data;

	@SuppressWarnings("unused")
	private ServerForgePluginMessagePacket() {}

	public ServerForgePluginMessagePacket(String channel, byte[] data) {
		this.channel=channel;
		this.data=data;
	}

	public String getChannel() {
		return this.channel;
	}

	public byte[] getData() {
		return this.data;
	}

	public void read(NetInput in) throws IOException {
		this.channel=in.readString();
		this.data=in.readBytes(MCForgeUtils.readVarShort(in));
	}

	public void write(NetOutput out) throws IOException {
		out.writeString(this.channel);
		out.writeShort(this.data.length);
		out.writeBytes(this.data);
	}

	public boolean isPriority() {
		return false;
	}
}
