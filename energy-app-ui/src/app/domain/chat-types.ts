export interface ChatUser {
  nickName: string;
  fullName: string;
  status: string;
  role: string;
  hasUnreadMessages?: boolean;
  isTyping?: boolean;
}

export interface ChatMessage {
  id: string;
  chatRoomId: string;
  senderId: string;
  recipientId: string;
  content: string;
  timestamp: Date;
  read: boolean;
  isMine: boolean;
}

export interface ChatMessageRequest {
  senderId: string;
  recipientId: string;
  content: string;
  timestamp: Date;
}
