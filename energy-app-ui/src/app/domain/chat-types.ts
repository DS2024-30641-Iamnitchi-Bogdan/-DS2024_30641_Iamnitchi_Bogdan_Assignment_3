export interface ChatUser {
  nickName: string;
  fullName: string;
  status: string;
}

export interface ChatMessage {
  id: string;
  chatRoomId: string;
  senderId: string;
  recipientId: string;
  content: string;
  timestamp: string;
  isSeen: boolean;
  isMine: boolean;
}
