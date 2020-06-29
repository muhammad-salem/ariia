export interface SseEventHandler {
	name: string;

	handel(data: MessageEvent): void;
}
