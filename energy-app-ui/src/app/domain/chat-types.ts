export interface ChatUser {
  nickName: string;
  fullName: string;
  status: string;
}

export interface ChatMessageResponse {
  id: string;
  chatRoomId: string;
  senderId: string;
  recipientId: string;
  content: string;
  timestamp: string;
  isSeen: boolean;
  isMine: boolean;
}

export interface ChatMessageRequest {
  senderId: string;
  recipientId: string;
  content: string;
  timestamp: string;
}
