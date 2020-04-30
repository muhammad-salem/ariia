export class NetworkSession {

    timer: number;
    totalLength: number;
    downloadLength: number;
    remainigLength: number;
    downloading:boolean;
    // receiveTCP_old: number;
    // receiveUDP_old: number;
    // sendTCP_old: number;
    // sendUDP_old: number;
    receiveTCP: number;
    receiveUDP: number;
    sendTCP: number;
    sendUDP: number;
    
    constructor() {
        this.timer = 0;
        this.totalLength = 0;
        this.downloadLength = 0;
        this.remainigLength = 0;
        this.downloading = false;
//      this.receiveTCP_old = 0;
//      this.receiveUDP_old = 0;
//      this.sendTCP_old = 0;
//      this.sendUDP_old = 0;
        this.receiveTCP = 0;
        this.receiveUDP = 0;
        this.sendTCP = 0;
        this.sendUDP = 0;
    }

}
