import { Item } from './item';

export class MetalinkItem extends Item {
    mirrors: string[] = [];

    constructor(item: MetalinkItem) {
    	super(item);
        if (item) {
            this.mirrors = item.mirrors;
        }
    }

    update(item: MetalinkItem) {
        super.update(item);
        this.mirrors = item.mirrors;
    }

}
