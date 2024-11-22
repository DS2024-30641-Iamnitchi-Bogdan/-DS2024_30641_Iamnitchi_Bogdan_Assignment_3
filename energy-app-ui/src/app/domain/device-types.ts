import {Role} from "./emus";

export interface Device {
  id: string; // Use string for UUID
  name: string;
  location: string;
  description: string;
  max_consumption: number;
  avg_consumption: number;
  user_id: string; // Use string for UUID
}

export interface DeviceUser {
  id: string;
  name: string;
  email: string;
}
