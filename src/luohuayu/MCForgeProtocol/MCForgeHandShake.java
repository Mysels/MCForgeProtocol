package luohuayu.MCForgeProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.io.stream.StreamNetOutput;

import luohuayu.MCForgeProtocol.packet.ServerForgePluginMessagePacket;
import luohuayu.MCForgeProtocol.packet.ServerForgeUpdateTileEntityPacket;

public class MCForgeHandShake{
	private MCForge forge;
	private HashMap<String,String> modList;

	public MCForgeHandShake(MCForge forge,HashMap<String,String> modList) {
		this.forge=forge;
		this.modList=modList;
	}
	
	public void handle(Session session,ServerPluginMessagePacket packet) {
		String channel=packet.getChannel();
		byte[] data=packet.getData();
		
		if(!channel.equals("FML|HS")&&!channel.equals("REGISTER")) return;
		switch(channel) {
			case "FML|HS":
				
				int packetID=data[0];
				
				switch(packetID) {
					case 0: //Hello
						forge.modifyPacket(session,63,ServerForgePluginMessagePacket.class);
						forge.modifyPacket(session,53,ServerForgeUpdateTileEntityPacket.class);
						
						sendPluginMessage(session,"FML|HS",new byte[]{0x01, 0x02});
						
						//ModList
						ByteArrayOutputStream buf=new ByteArrayOutputStream();
						StreamNetOutput out=new StreamNetOutput(buf);
						try {
							out.writeVarInt(2);
							out.writeByte(modList.size());
							modList.forEach((k, v) -> {
								try {
									out.writeString(k);
									out.writeString(v);
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
						sendPluginMessage(session,"FML|HS",buf.toByteArray());
						break;
					case 2: //ModList
						sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x02}); //ACK(WAITING SERVER DATA)
						break;
					case 3: //RegistryData
						sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x03}); //ACK(WAITING SERVER COMPLETE)
						break;
					case -1: //HandshakeAck
						int ackID=data[1];
						switch(ackID) {
							case 2: //WAITING CACK
								sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x04}); //PENDING COMPLETE
							case 3: //COMPLETE
								sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x05}); //COMPLETE
						}
					default:
				}
				break;
			case "REGISTER":
				sendPluginMessage(session,"REGISTER",data);
				break;
			default:
		}
	}

	private void sendPluginMessage(Session session,String channel,byte[] data) {
		session.send(new ClientPluginMessagePacket(channel,data));
	}
}
