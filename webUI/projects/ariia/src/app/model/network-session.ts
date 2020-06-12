export class NetworkSession {
    timer: number;
    remainingTime: number;
    totalLength: number;
    downloadLength: number;
    remainigLength: number;
    downloading: boolean;
    receiveTCP_old: number;
    receiveUDP_old: number;
    sendTCP_old: number;
    sendUDP_old: number;
    receiveTCP: number;
    receiveUDP: number;
    sendTCP: number;
    sendUDP: number;
    
    constructor() {
        this.timer = 0;
        this.remainingTime = 0;
        this.totalLength = 0;
        this.downloadLength = 0;
        this.remainigLength = 0;
        this.downloading = false;
        this.receiveTCP_old = 0;
        this.receiveUDP_old = 0;
        this.sendTCP_old = 0;
        this.sendUDP_old = 0;
        this.receiveTCP = 0;
        this.receiveUDP = 0;
        this.sendTCP = 0;
        this.sendUDP = 0;
    }

    update(session: NetworkSession) {
    	if(session){
			this.timer = session.timer;
		    this.remainingTime = session.remainingTime;
		    this.totalLength = session.totalLength;
		    this.downloadLength = session.downloadLength;
		    this.remainigLength = session.remainigLength;
		    this.downloading = session.downloading;
		    this.receiveTCP_old = session.receiveTCP_old;
		    this.receiveUDP_old = session.receiveUDP_old;
		    this.sendTCP_old = session.sendTCP_old;
		    this.sendUDP_old = session.sendUDP_old;
		    this.receiveTCP = session.receiveTCP;
		    this.receiveUDP = session.receiveUDP;
		    this.sendTCP = session.sendTCP;
		    this.sendUDP = session.sendUDP;
    	}
    }
}
