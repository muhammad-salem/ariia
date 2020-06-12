export class NetworkSession {

	timer = 0;
	remainingTime = 0;
	
	downloading = false;
	
	totalLength = 0;
	downloadLength = 0;
	remainigLength = 0;
	speedOfTCPReceive = 0;
	speedOfTCPSend = 0;
	speedOfUDPReceive = 0;
	speedOfUDPSend = 0;
	
	receiveTCP = 0;
	receiveUDP = 0;
	sendTCP = 0;
	sendUDP = 0;
	
	totalReceive = 0;
	totalSend = 0;
	total = 0;
    

    update(session: NetworkSession) {
    	if(session){
    		this.timer = session.timer ;
			this.remainingTime = session.remainingTime ;
			
			this.downloading = session.downloading;
			
			this.totalLength = session.totalLength;
			this.downloadLength = session.downloadLength;
			this.remainigLength = session.remainigLength;
			
			this.speedOfTCPReceive = session.speedOfTCPReceive ;
			this.speedOfTCPSend = session.speedOfTCPSend;
			this.speedOfUDPReceive = session.speedOfUDPReceive ;
			this.speedOfUDPSend = session.speedOfUDPSend ;
			
			this.receiveTCP = session.receiveTCP;
			this.receiveUDP = session.receiveUDP;
			this.sendTCP = session.sendTCP;
			this.sendUDP = session.sendUDP ;
			
			this.totalReceive = session.totalReceive;
			this.totalSend = session.totalSend;
			this.total = session.total ;
			
    	}
    }
}
