package luohuayu.MCForgeProtocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.packet.Packet;
import org.spacehq.packetlib.packet.PacketProtocol;

import luohuayu.MCForgeProtocol.packet.ServerForgePluginMessagePacket;
import luohuayu.MCForgeProtocol.packet.ServerForgeUpdateTileEntityPacket;

public class MCForge {
	private MCForgeHandShake handshake;
	
	private HashMap<String,String> modList;
	private Session session;
	
	public MCForge(Session session,HashMap<String,String> modList) {
		this.modList=modList;
		this.session=session;
		this.handshake=new MCForgeHandShake(this,this.modList);
	}
	
	public void handle(Session session,Packet packet) {
		if(packet instanceof ServerForgePluginMessagePacket) {
			ServerForgePluginMessagePacket forgePacket=(ServerForgePluginMessagePacket)packet;
			this.session.callEvent(new PacketReceivedEvent(this.session,new ServerPluginMessagePacket(forgePacket.getChannel(),forgePacket.getData())));
			return;
		}else if(packet instanceof ServerForgeUpdateTileEntityPacket) {
			ServerForgeUpdateTileEntityPacket forgePacket=(ServerForgeUpdateTileEntityPacket)packet;
			if(forgePacket.getType()!=ServerForgeUpdateTileEntityPacket.Type.OTHER) {
				int type=forgePacket.getType().ordinal();
				this.session.callEvent(new PacketReceivedEvent(this.session,new ServerUpdateTileEntityPacket(0,forgePacket.getX(),forgePacket.getY(),forgePacket.getZ(),ServerUpdateTileEntityPacket.Type.values()[type],forgePacket.getNBT())));
			}
			return;
		}
		
		if(packet instanceof ServerPluginMessagePacket) {
			ServerPluginMessagePacket mcPacket=(ServerPluginMessagePacket)packet;
			if(mcPacket.getChannel().equals("FML|HS")||mcPacket.getChannel().equals("REGISTER")) this.handshake.handle(session,mcPacket);
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void modifyPacket(Session session,int id,Class<? extends Packet> packet) {
		try {
			PacketProtocol protocol=session.getPacketProtocol();
			Class<?> cls=protocol.getClass().getSuperclass();

			Field field=cls.getDeclaredField("incoming");
			field.setAccessible(true);

			Map<Integer, Class<? extends Packet>> incoming=null;
			incoming=(Map<Integer, Class<? extends Packet>>) field.get(protocol);
			
			incoming.put(id,packet);
			System.out.println(incoming.toString());
			
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
