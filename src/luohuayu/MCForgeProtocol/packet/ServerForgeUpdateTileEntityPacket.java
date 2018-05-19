package luohuayu.MCForgeProtocol.packet;

import java.io.IOException;
import org.spacehq.mc.protocol.util.NetUtil;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.packetlib.io.NetInput;
import org.spacehq.packetlib.io.NetOutput;
import org.spacehq.packetlib.packet.Packet;
public class ServerForgeUpdateTileEntityPacket implements Packet {
	private int x;
	private int y;
	private int z;
	private Type type;
	private CompoundTag nbt;

	@SuppressWarnings("unused")
	private ServerForgeUpdateTileEntityPacket() {}

	public ServerForgeUpdateTileEntityPacket(int breakerEntityId, int x, int y, int z, Type type, CompoundTag nbt) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.nbt = nbt;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Type getType() {
		return this.type;
	}

	public CompoundTag getNBT() {
		return this.nbt;
	}

	public void read(NetInput in) throws IOException {
		this.x = in.readInt();
		this.y = in.readShort();
		this.z = in.readInt();
		int type = in.readUnsignedByte() - 1;
		if(type>5||type<0) type=5;
		this.type = Type.values()[type];
		this.nbt = NetUtil.readNBT(in);
	}

	public void write(NetOutput out) throws IOException {
		out.writeInt(this.x);
		out.writeShort(this.y);
		out.writeInt(this.z);
		out.writeByte(this.type.ordinal() + 1);
		NetUtil.writeNBT(out, this.nbt);
	}

	public boolean isPriority() {
		return false;
	}

	public static enum Type {
		MOB_SPAWNER,  COMMAND_BLOCK,  BEACON,  SKULL,  FLOWER_POT,  OTHER;
		private Type() {}
	}
}
