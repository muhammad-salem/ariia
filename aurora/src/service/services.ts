import { DataService } from "./data.service";
import { HttpClient } from "./http.service";
import { ItemService } from "./item.service";
import { LogService } from "./log.service";
import { RangeService } from "./range.service";
import { SseService } from "./sse.service";


export const httpClient = new HttpClient();

export const sseService = new SseService();

export const rangeService = new RangeService();

export const itemService = new ItemService(httpClient);

export const logService = new LogService(httpClient);

export const dataService = new DataService(sseService, itemService);
