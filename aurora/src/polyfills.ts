import { bootstrapZone } from '@ibyar/aurora';

bootstrapZone('manual');


declare global {
    interface Array<T> {
        at(index: number): T | undefined;
    }
}

function at(this: Array<any>, index: number) {
    if (index >= 0) {
        return this[index];
    }
    index = this.length + index;
    return this[index];
}
