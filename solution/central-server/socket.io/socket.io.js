/**
 * socket.io.js
 * ============
 * Server-side code for Cinevault Chat.
 * Manages namespaces, chat room joining, message broadcasting, chat creation, typing indicators,
 * and system notifications.
 */
const setupNamespace = (io, namespaceName) => {
    const namespace = io.of('/' + namespaceName);
    // In-memory storage for chat history per room.
    const chats = {};
    chats['General'] = []; // Initialize the default chat room.

    namespace.on('connection', (socket) => {
        console.log('User connected to /' + namespaceName + ' ' + socket.id);

        // Send the current chat list to the newly connected client.
        socket.emit('chatListUpdate', Object.keys(chats));

        /**
         * Handle joining a chat room.
         * Expects an object containing 'chatId' and 'username'.
         */
        socket.on('joinChat', ({ chatId, username }) => {
            // If the socket is already in a room, notify others that the user has left.
            if (socket.currentChatId) {
                socket.to(socket.currentChatId).emit('message', {
                    username: 'System',
                    text: socket.username + ' has left the room.',
                    timestamp: new Date().toLocaleTimeString(),
                });
                socket.leave(socket.currentChatId);
            }
            // Join the new room.
            socket.join(chatId);
            socket.currentChatId = chatId;
            socket.username = username;
            console.log('Socket '+ socket.id + 'joined chat: ' + chatId);

            // Initialize chat history for the room if it doesn't exist.
            if (!chats[chatId]) {
                chats[chatId] = [];
            }
            // Send the existing chat history to the client.
            socket.emit('chatHistory', chats[chatId]);
            // Notify other users in the room that a new user has joined.
            socket.to(chatId).emit('message', {
                username: 'System',
                text: username + ' has joined the room.',
                timestamp: new Date().toLocaleTimeString(),
            });
        });

        /**
         * Handle new message events.
         * Data should include 'chatId', 'username', 'message', and 'timestamp'.
         */
        socket.on('newMessage', (data) => {
            const { chatId, username, message, timestamp } = data;
            const chatMessage = { username, text: message, timestamp };

            // Append the new message to the chat history.
            if (chats[chatId]) {
                chats[chatId].push(chatMessage);
            } else {
                chats[chatId] = [chatMessage];
            }
            // Broadcast the message to all clients in the room except the sender.
            socket.broadcast.to(chatId).emit('message', chatMessage);
        });

        /**
         * Handle new chat room creation.
         */
        socket.on('newChat', (chatName) => {
            if (!chats.hasOwnProperty(chatName)) {
                chats[chatName] = [];
                // Broadcast updated chat list to all connected clients.
                namespace.emit('chatListUpdate', Object.keys(chats));
                console.log('New chat created: ' + chatName);
            }
        });

        /**
         * Handle typing indicator events.
         */
        socket.on('typing', (data) => {
            socket.to(data.chatId).emit('typing', { username: data.username });
        });
        socket.on('stopTyping', (data) => {
            socket.to(data.chatId).emit('stopTyping', { username: data.username });
        });

        /**
         * Handle socket disconnection.
         * If the user was in a room, broadcast a leave notification.
         */
        socket.on('disconnect', () => {
            if (socket.currentChatId && socket.username) {
                socket.to(socket.currentChatId).emit('message', {
                    username: 'System',
                    text: socket.username + ' has left the room.',
                    timestamp: new Date().toLocaleTimeString(),
                });
            }
            console.log('User disconnected: ' + socket.id);
        });
    });
};

module.exports = (io) => {
    ['general', 'movies', 'actors', 'countries', 'genres'].forEach(ns => setupNamespace(io, ns));
};